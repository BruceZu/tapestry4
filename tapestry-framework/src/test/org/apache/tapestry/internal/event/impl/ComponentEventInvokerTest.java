// Copyright May 21, 2006 The Apache Software Foundation
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
package org.apache.tapestry.internal.event.impl;

import static org.easymock.EasyMock.checkOrder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.BaseComponentTestCase;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.BrowserEvent;
import org.apache.tapestry.event.EventTarget;
import org.apache.tapestry.form.FormSupport;
import org.apache.tapestry.form.FormSupportImpl;
import org.apache.tapestry.internal.event.ComponentEventProperty;
import org.apache.tapestry.listener.ListenerInvoker;
import org.apache.tapestry.listener.ListenerMap;
import org.apache.tapestry.spec.ComponentSpecification;
import org.apache.tapestry.spec.IComponentSpecification;
import org.testng.annotations.Test;


/**
 * Tests functionality of {@link ComponentEventInvoker}.
 * 
 * @author jkuhnert
 */
@Test
public class ComponentEventInvokerTest extends BaseComponentTestCase
{
    
    public void test_Event_Properties()
    {
        IComponentSpecification spec = new ComponentSpecification();
        spec.addEventListener("comp1", new String[] {"onClick"}, "testFoo", 
                null, false, false, false);
        
        assertTrue(spec.hasEvents("comp1"));
        
        ComponentEventProperty prop = spec.getComponentEvents("comp1");
        assertNotNull(prop);
        assertEquals(prop.getEventListeners("onClick").size(), 1);
        
        // ensure valid props always returned
        prop = spec.getComponentEvents("comp2");
        assertNotNull(prop);
        assertEquals(prop.getEvents().size(), 0);
        
        List listeners = prop.getEventListeners("nonExistant");
        assertNotNull(listeners);
        assertEquals(listeners.size(), 0);
    }
    
    public void test_Invoke_Component_Listener()
    {
        IRequestCycle cycle = newCycle();
        IComponent comp = newComponent();
        checkOrder(comp, false);
        IPage page = newMock(IPage.class);
        
        IComponentSpecification spec = new ComponentSpecification();
        
        ListenerInvoker listenerInvoker = newMock(ListenerInvoker.class);
        ListenerMap listenerMap = newMock(ListenerMap.class);
        
        IActionListener listener1 = newMock(IActionListener.class);
        
        Map comps = new HashMap();
        comps.put("testId", comp);
        
        Map tprops = new HashMap();
        tprops.put("id", "testId");
        BrowserEvent event = new BrowserEvent("onSelect", new EventTarget(tprops));
        
        ComponentEventInvoker invoker = new ComponentEventInvoker();
        invoker.setInvoker(listenerInvoker);
        
        spec.addEventListener("testId", new String[] { "onSelect" }, 
                "fooListener", null, false, false, false);
        invoker.addEventListener("testId", spec);
        
        expect(comp.getId()).andReturn("testId").anyTimes();
        
        expect(comp.getSpecification()).andReturn(spec).anyTimes();
        
        expect(comp.getPage()).andReturn(page);
        
        expect(page.getComponents()).andReturn(comps);
        
        expect(comp.getListeners()).andReturn(listenerMap);
        
        expect(listenerMap.getListener("fooListener")).andReturn(listener1);
        
        listenerInvoker.invokeListener(listener1, comp, cycle);
        
        replay();
        
        invoker.invokeListeners(comp, cycle, event);
        
        verify();
    }
    
    public void test_Invoke_Element_Listener()
    {
        IRequestCycle cycle = newCycle();
        IComponent comp = newComponent();
        checkOrder(comp, false);
        
        IPage page = newMock(IPage.class);
        IComponentSpecification spec = new ComponentSpecification();
        
        ListenerInvoker listenerInvoker = newMock(ListenerInvoker.class);
        ListenerMap listenerMap = newMock(ListenerMap.class);
        IActionListener listener = newMock(IActionListener.class);
        
        Map comps = new HashMap();
        comps.put("testId", comp);
        
        Map tprops = new HashMap();
        tprops.put("id", "testId");
        BrowserEvent event = new BrowserEvent("onSelect", new EventTarget(tprops));
        
        ComponentEventInvoker invoker = new ComponentEventInvoker();
        invoker.setInvoker(listenerInvoker);
        
        spec.addElementEventListener("testId", new String[] { "onSelect" }, 
                "fooListener", null, false, true, true);
        invoker.addEventListener("testId", spec);
        
        expect(comp.getId()).andReturn("testId").anyTimes();
        
        expect(comp.getSpecification()).andReturn(spec).anyTimes();
        
        expect(comp.getPage()).andReturn(page);
        
        expect(page.getComponents()).andReturn(comps);
        
        expect(comp.getListeners()).andReturn(listenerMap);
        
        expect(listenerMap.getListener("fooListener")).andReturn(listener);
        
        listenerInvoker.invokeListener(listener, comp, cycle);
        
        replay();
        
        invoker.invokeListeners(comp, cycle, event);
        
        verify();
    }
    
    public void test_Invoke_Form_Listener()
    {
        IRequestCycle cycle = newCycle();
        IForm form = newForm();
        checkOrder(form, false);
        FormSupport formSupport = newMock(FormSupport.class);
        IComponentSpecification spec = new ComponentSpecification();
        
        ListenerInvoker listenerInvoker = newMock(ListenerInvoker.class);
        ListenerMap listenerMap = newMock(ListenerMap.class);
        IActionListener listener = newMock(IActionListener.class);
        IPage page = newMock(IPage.class);
        checkOrder(page, false);
        
        Map comps = new HashMap();
        comps.put("form1", form);
        
        Map tprops = new HashMap();
        tprops.put("id", "form1");
        BrowserEvent event = new BrowserEvent("onSelect", new EventTarget(tprops));
        
        ComponentEventInvoker invoker = new ComponentEventInvoker();
        invoker.setInvoker(listenerInvoker);
        
        spec.addEventListener("form1", new String[] { "onSelect" }, "fooListener",
                "form1", false, false, false);
        invoker.addFormEventListener("form1", spec);
        
        expect(formSupport.getForm()).andReturn(form);
        
        expect(form.getId()).andReturn("form1").anyTimes();
        
        expect(form.getPage()).andReturn(page);
        
        expect(page.getComponents()).andReturn(comps);
        
        expect(form.getSpecification()).andReturn(spec);
        
        expect(form.getListeners()).andReturn(listenerMap);
        
        expect(listenerMap.getListener("fooListener")).andReturn(listener);
        
        form.addDeferredRunnable(isA(Runnable.class));
        
        cycle.setAttribute(FormSupportImpl.FIELD_FOCUS_ATTRIBUTE, Boolean.TRUE);
        
        replay();
        
        invoker.invokeFormListeners(formSupport, cycle, event);
        
        verify();
    }
    
    public void test_Invoke_Form_Listener_Enabled_Focus()
    {
        IRequestCycle cycle = newCycle();
        IForm form = newForm();
        checkOrder(form, false);
        FormSupport formSupport = newMock(FormSupport.class);
        IComponentSpecification spec = new ComponentSpecification();
        
        ListenerInvoker listenerInvoker = newMock(ListenerInvoker.class);
        ListenerMap listenerMap = newMock(ListenerMap.class);
        IActionListener listener = newMock(IActionListener.class);
        IPage page = newMock(IPage.class);
        checkOrder(page, false);
        
        Map comps = new HashMap();
        comps.put("form1", form);
        
        Map tprops = new HashMap();
        tprops.put("id", "form1");
        BrowserEvent event = new BrowserEvent("onSelect", new EventTarget(tprops));
        
        ComponentEventInvoker invoker = new ComponentEventInvoker();
        invoker.setInvoker(listenerInvoker);
        
        spec.addEventListener("form1", new String[] { "onSelect" }, "fooListener",
                "form1", false, false, true);
        invoker.addFormEventListener("form1", spec);
        
        expect(formSupport.getForm()).andReturn(form);
        
        expect(form.getId()).andReturn("form1").anyTimes();
        
        expect(form.getPage()).andReturn(page);
        
        expect(page.getComponents()).andReturn(comps);
        
        expect(form.getSpecification()).andReturn(spec);
        
        expect(form.getListeners()).andReturn(listenerMap);
        
        expect(listenerMap.getListener("fooListener")).andReturn(listener);
        
        form.addDeferredRunnable(isA(Runnable.class));
        
        replay();
        
        invoker.invokeFormListeners(formSupport, cycle, event);
        
        verify();
    }
}