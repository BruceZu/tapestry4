// Copyright 2005 The Apache Software Foundation
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

import java.util.List;

import org.apache.tapestry.services.ApplicationGlobals;
import org.apache.tapestry.services.LinkFactory;
import org.apache.tapestry.services.ResponseRenderer;

/**
 * Base class for settting up services and configurations inside
 * {@link org.apache.tapestry.services.ApplicationGlobals tapestry.globals.ApplicationGlobals}.
 * 
 * @author Howard M. Lewis Ship
 * @since 3.1
 */
public class AbstractSetupApplicationGlobals
{

    private ApplicationGlobals _globals;

    private List _factoryServices;

    private ResponseRenderer _responseRenderer;

    private LinkFactory _linkFactory;

    protected void initialize()
    {
        _globals.storeFactoryServices(_factoryServices);
        _globals.storeResponseRenderer(_responseRenderer);
        _globals.storeLinkFactory(_linkFactory);
    }

    public void setGlobals(ApplicationGlobals globals)
    {
        _globals = globals;
    }

    public void setFactoryServices(List factoryServices)
    {
        _factoryServices = factoryServices;
    }

    public void setResponseRenderer(ResponseRenderer responseRenderer)
    {
        _responseRenderer = responseRenderer;
    }

    public void setLinkFactory(LinkFactory linkFactory)
    {
        _linkFactory = linkFactory;
    }
}