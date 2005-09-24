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

package org.apache.tapestry;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Locatable;
import org.apache.hivemind.Location;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.tapestry.markup.AsciiMarkupFilter;
import org.apache.tapestry.markup.MarkupWriterImpl;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.spec.IParameterSpecification;
import org.apache.tapestry.test.Creator;
import org.easymock.MockControl;

/**
 * Base class for testing components, or testing classes that operate on components. Simplifies
 * creating much of the infrastructure around the components.
 * <p>
 * This class may eventually be part of the Tapestry distribution.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public abstract class BaseComponentTestCase extends HiveMindTestCase
{
    private Creator _creator;

    protected Creator getCreator()
    {
        if (_creator == null)
            _creator = new Creator();

        return _creator;
    }

    protected CharArrayWriter _charArrayWriter;

    protected IMarkupWriter newBufferWriter()
    {
        _charArrayWriter = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(_charArrayWriter);

        return new MarkupWriterImpl("text/html", pw, new AsciiMarkupFilter());
    }

    protected void assertBuffer(String expected)
    {
        String actual = _charArrayWriter.toString();

        assertEquals("Buffered markup writer content.", expected, actual);

        _charArrayWriter.reset();
    }

    protected Object newInstance(Class componentClass)
    {
        return newInstance(componentClass, null);
    }

    protected Object newInstance(Class componentClass, String propertyName, Object propertyValue)
    {
        return getCreator().newInstance(componentClass, new Object[]
        { propertyName, propertyValue });
    }

    protected Object newInstance(Class componentClass, Object[] properties)
    {
        return getCreator().newInstance(componentClass, properties);
    }

    protected IRequestCycle newCycle()
    {
        return (IRequestCycle) newMock(IRequestCycle.class);
    }

    protected IRequestCycle newCycle(boolean rewinding)
    {
        MockControl control = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) control.getMock();

        trainIsRewinding(cycle, rewinding);

        return cycle;
    }

    protected void trainIsRewinding(IRequestCycle cycle, boolean rewinding)
    {
        cycle.isRewinding();
        setReturnValue(cycle,rewinding);
    }

    protected IRequestCycle newCycleGetPage(String pageName, IPage page)
    {
        MockControl control = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) control.getMock();

        cycle.getPage(pageName);
        control.setReturnValue(page);

        return cycle;
    }

    protected IRequestCycle newCycleGetUniqueId(String id, String uniqueId)
    {
        MockControl control = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) control.getMock();

        cycle.getUniqueId(id);
        control.setReturnValue(uniqueId);

        return cycle;
    }

    protected IRequestCycle newCycleGetParameter(String name, String value)
    {
        MockControl control = newControl(IRequestCycle.class);
        IRequestCycle cycle = (IRequestCycle) control.getMock();

        cycle.getParameter(name);
        control.setReturnValue(value);

        return cycle;
    }

    protected IMarkupWriter newWriter()
    {
        return (IMarkupWriter) newMock(IMarkupWriter.class);
    }

    protected IBinding newBinding(Object value)
    {
        MockControl control = newControl(IBinding.class);
        IBinding binding = (IBinding) control.getMock();

        binding.getObject();
        control.setReturnValue(value);

        return binding;
    }

    protected IBinding newBinding(Location location)
    {
        MockControl control = newControl(IBinding.class);
        IBinding binding = (IBinding) control.getMock();

        binding.getLocation();
        control.setReturnValue(location);

        return binding;
    }

    protected IComponent newComponent(String extendedId, Location location)
    {
        MockControl control = newControl(IComponent.class);
        IComponent component = (IComponent) control.getMock();

        component.getExtendedId();
        control.setReturnValue(extendedId);

        component.getLocation();
        control.setReturnValue(location);

        return component;
    }

    protected IComponentSpecification newSpec(String parameterName, IParameterSpecification pspec)
    {
        MockControl control = newControl(IComponentSpecification.class);
        IComponentSpecification spec = (IComponentSpecification) control.getMock();

        spec.getParameter(parameterName);
        control.setReturnValue(pspec);

        return spec;
    }

    protected IRender newRender()
    {
        return (IRender) newMock(IRender.class);
    }

    protected IPage newPage()
    {
        return (IPage) newMock(IPage.class);
    }

    protected IPage newPage(String name)
    {
        return newPage(name, 1);
    }

    protected IPage newPage(String name, int count)
    {
        MockControl control = newControl(IPage.class);
        IPage page = (IPage) control.getMock();

        page.getPageName();
        control.setReturnValue(name, count);

        return page;
    }

    protected IForm newForm()
    {
        return (IForm) newMock(IForm.class);
    }

    protected IRender newBody()
    {
        return new IRender()
        {
            public void render(IMarkupWriter writer, IRequestCycle cycle)
            {
                writer.print("BODY");
            }
        };
    }

    protected PageRenderSupport newPageRenderSupport()
    {
        return (PageRenderSupport) newMock(PageRenderSupport.class);
    }

    protected void trainGetSupport(IRequestCycle cycle, PageRenderSupport support)
    {
        trainGetAttribute(cycle, TapestryUtils.PAGE_RENDER_SUPPORT_ATTRIBUTE, support);
    }

    protected void trainGetAttribute(IRequestCycle cycle, String attributeName, Object attribute)
    {
        MockControl control = getControl(cycle);

        cycle.getAttribute(attributeName);

        control.setReturnValue(attribute);
    }

    protected void trainGetUniqueId(IRequestCycle cycle, String id, String uniqueId)
    {
        MockControl control = getControl(cycle);

        cycle.getUniqueId(id);
        control.setReturnValue(uniqueId);
    }

    protected void trainGetIdPath(IComponent component, String idPath)
    {
        MockControl control = getControl(component);

        component.getIdPath();
        control.setReturnValue(idPath);
    }

    protected void trainGetParameter(IRequestCycle cycle, String name, String value)
    {
        MockControl control = getControl(cycle);

        cycle.getParameter(name);
        control.setReturnValue(value);
    }

    protected void trainGetPageName(IPage page, String pageName)
    {
        page.getPageName();

        setReturnValue(page,pageName);
    }

    protected void trainBuildURL(IAsset asset, IRequestCycle cycle, String URL)
    {
        asset.buildURL(cycle);

        setReturnValue(asset,URL);
    }

    protected IAsset newAsset()
    {
        return (IAsset) newMock(IAsset.class);
    }

    protected IEngine newEngine(ClassResolver resolver)
    {
        IEngine engine = (IEngine) newMock(IEngine.class);

        engine.getClassResolver();
        setReturnValue(engine,resolver);

        return engine;
    }

    protected void trainGetEngine(IPage page, IEngine engine)
    {
        page.getEngine();

        setReturnValue(page,engine);
    }

    protected IComponent newComponent()
    {
        return (IComponent) newMock(IComponent.class);
    }

    protected void trainGetPage(IComponent component, IPage page)
    {
        component.getPage();
        setReturnValue(component,page);
    }

    protected void trainGetExtendedId(IComponent component, String extendedId)
    {
        component.getExtendedId();
        setReturnValue(component,extendedId);
    }

    protected void trainGetLocation(Locatable locatable, Location location)
    {
        locatable.getLocation();
        setReturnValue(locatable,location);
    }

    protected IBinding newBinding()
    {
        return (IBinding) newMock(IBinding.class);
    }

    protected void trainGetComponent(IComponent container, String componentId, IComponent containee)
    {
        container.getComponent(componentId);
        setReturnValue(container,containee);
    }
}