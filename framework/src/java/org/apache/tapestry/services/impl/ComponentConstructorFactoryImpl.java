// Copyright 2004 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.services.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.order.Orderer;
import org.apache.hivemind.service.ClassFactory;
import org.apache.tapestry.enhance.EnhancedClassValidator;
import org.apache.tapestry.enhance.EnhancementOperationImpl;
import org.apache.tapestry.enhance.EnhancementWorker;
import org.apache.tapestry.event.ResetEventListener;
import org.apache.tapestry.services.ComponentConstructor;
import org.apache.tapestry.services.ComponentConstructorFactory;
import org.apache.tapestry.spec.IComponentSpecification;

/**
 * Implementation of the {@link org.apache.tapestry.services.ComponentConstructorFactory}service
 * interface.
 * 
 * @author Howard M. Lewis Ship
 * @since 3.1
 */
public class ComponentConstructorFactoryImpl implements ComponentConstructorFactory,
        ResetEventListener
{
    private ErrorLog _errorLog;

    private ClassFactory _classFactory;

    private ClassResolver _classResolver;

    private List _contributions;

    private List _workers;

    private EnhancedClassValidator _validator;

    /**
     * Map of {@link org.apache.tapestry.services.ComponentConstructor}keyed on
     * {@link org.apache.tapestry.spec.IComponentSpecification}.
     */

    private Map _cachedConstructors = Collections.synchronizedMap(new HashMap());

    public void resetEventDidOccur()
    {
        _cachedConstructors.clear();
    }

    public void initializeService()
    {
        Orderer orderer = new Orderer(_errorLog, "worker");

        Iterator i = _contributions.iterator();
        while (i.hasNext())
        {
            EnhancementWorkerContribution c = (EnhancementWorkerContribution) i.next();

            orderer.add(c.getWorker(), c.getId(), c.getAfter(), c.getBefore());
        }

        _workers = orderer.getOrderedObjects();
    }

    public ComponentConstructor getComponentConstructor(IComponentSpecification specification,
            String className)
    {
        ComponentConstructor result = (ComponentConstructor) _cachedConstructors.get(specification);

        if (result == null)
        {
            Class baseClass = _classResolver.findClass(className);

            EnhancementOperationImpl eo = new EnhancementOperationImpl(_classResolver,
                    specification, baseClass, _classFactory);

            Iterator i = _workers.iterator();
            while (i.hasNext())
            {
                EnhancementWorker w = (EnhancementWorker) i.next();

                w.performEnhancement(eo);
            }

            result = eo.getConstructor();

            // TODO: This should be optional to work around that IBM JVM bug.
            // Also, to some degree, it should be passed into EnhancementOperationImpl,
            // as it generally only needs to be done if a enhanced class
            // is fabricated.

            _validator.validate(baseClass, result.getComponentClass(), specification);

            _cachedConstructors.put(specification, result);
        }

        return result;
    }

    public void setClassFactory(ClassFactory classFactory)
    {
        _classFactory = classFactory;
    }

    public void setClassResolver(ClassResolver classResolver)
    {
        _classResolver = classResolver;
    }

    public void setContributions(List contributions)
    {
        _contributions = contributions;
    }

    public void setValidator(EnhancedClassValidator validator)
    {
        _validator = validator;
    }

    public void setErrorLog(ErrorLog errorLog)
    {
        _errorLog = errorLog;
    }
}