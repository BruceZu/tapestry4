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

package org.apache.tapestry.form;

import java.util.Collections;
import java.util.Iterator;

import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.coerce.ValueConverter;
import org.apache.tapestry.form.translator.Translator;
import org.apache.tapestry.form.validator.Validator;
import org.apache.tapestry.junit.TapestryTestCase;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidatorException;
import org.easymock.MockControl;

/**
 * Test case for {@link ValidatableFieldSupportImpl}.
 * 
 * @author Paul Ferraro
 * @since 4.0
 */
public class TestValidatableFieldSupportImpl extends TapestryTestCase
{
    private ValidatableFieldSupportImpl _support = new ValidatableFieldSupportImpl();
    
    private MockControl _componentControl = MockControl.createControl(ValidatableField.class);
    private ValidatableField _component = (ValidatableField) _componentControl.getMock();
    
    private MockControl _writerControl = MockControl.createControl(IMarkupWriter.class);
    private IMarkupWriter _writer = (IMarkupWriter) _writerControl.getMock();

    private MockControl _cycleControl = MockControl.createControl(IRequestCycle.class);
    private IRequestCycle _cycle = (IRequestCycle) _cycleControl.getMock();

    private MockControl _formControl = MockControl.createControl(IForm.class);
    private IForm _form = (IForm) _formControl.getMock();
    
    private MockControl _delegateControl = MockControl.createControl(IValidationDelegate.class);
    private IValidationDelegate _delegate = (IValidationDelegate) _delegateControl.getMock();
    
    private MockControl _translatorControl = MockControl.createControl(Translator.class);
    private Translator _translator = (Translator) _translatorControl.getMock();
    
    private MockControl _validatorControl = MockControl.createControl(Validator.class);
    private Validator _validator = (Validator) _validatorControl.getMock();
    
    private MockControl _valueConverterControl = MockControl.createControl(ValueConverter.class);
    private ValueConverter _valueConverter = (ValueConverter) _valueConverterControl.getMock();
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        _support.setValueConverter(_valueConverter);
    }

    /**
     * @see org.apache.hivemind.test.HiveMindTestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        _componentControl.reset();
        _writerControl.reset();
        _cycleControl.reset();
        _formControl.reset();
        _delegateControl.reset();
        _translatorControl.reset();
        _validatorControl.reset();
        _valueConverterControl.reset();
        
        super.tearDown();
    }

    private void replay()
    {
        _componentControl.replay();
        _writerControl.replay();
        _cycleControl.replay();
        _formControl.replay();
        _delegateControl.replay();
        _translatorControl.replay();
        _validatorControl.replay();
        _valueConverterControl.replay();
    }

    private void verify()
    {
        _componentControl.verify();
        _writerControl.verify();
        _cycleControl.verify();
        _formControl.verify();
        _delegateControl.verify();
        _translatorControl.verify();
        _validatorControl.verify();
        _valueConverterControl.verify();
    }
    
    public void testNullRender()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.isInError();
        _delegateControl.setReturnValue(false);
        
        _component.readValue();
        _componentControl.setReturnValue(null);
        
        _component.render(_writer, _cycle, "");
        _componentControl.setVoidCallable();
        
        replay();
        
        _support.render(_component, _writer, _cycle);
        
        verify();
    }
    
    public void testNotNullRender()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.isInError();
        _delegateControl.setReturnValue(false);
        
        Object object = new Object();
        
        _component.readValue();
        _componentControl.setReturnValue(object);
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);

        String value = "some value";
        
        _translator.format(_component, object);
        _translatorControl.setReturnValue(value);
        
        _component.render(_writer, _cycle, value);
        
        replay();
        
        _support.render(_component, _writer, _cycle);
        
        verify();
    }
    
    public void testNotNullInErrorRender()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.isInError();
        _delegateControl.setReturnValue(true);
        
        String value = "recorded value";
        
        _delegate.getFieldInputValue();
        _delegateControl.setReturnValue(value);
        
        _component.render(_writer, _cycle, value);
        
        replay();
        
        _support.render(_component, _writer, _cycle);
        
        verify();
    }
    
    public void testClientValidationDisabledRenderContributions()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.isClientValidationEnabled();
        _formControl.setReturnValue(false);
        
        replay();
        
        _support.renderContributions(_component, _writer, _cycle);
        
        verify();
    }
    
    public void testClientValidationEnabledNoValidatorsRenderContributions()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.isClientValidationEnabled();
        _formControl.setReturnValue(true);
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);
        
        _translator.renderContribution(_writer, _cycle, _component);
        _translatorControl.setVoidCallable();
        
        _component.getValidators();
        _componentControl.setReturnValue(null);
        
        _valueConverter.coerceValue(null, Iterator.class);
        _valueConverterControl.setReturnValue(Collections.EMPTY_LIST.iterator());
        
        replay();
        
        _support.renderContributions(_component, _writer, _cycle);
        
        verify();
    }
    
    public void testClientValidationEnabledValidatorRenderContributions()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.isClientValidationEnabled();
        _formControl.setReturnValue(true);
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);
        
        _translator.renderContribution(_writer, _cycle, _component);
        _translatorControl.setVoidCallable();
        
        _component.getValidators();
        _componentControl.setReturnValue(_validator);
        
        _valueConverter.coerceValue(_validator, Iterator.class);
        _valueConverterControl.setReturnValue(Collections.singletonList(_validator).iterator());
        
        _validator.renderContribution(_writer, _cycle, _component);
        _validatorControl.setVoidCallable();
        
        replay();
        
        _support.renderContributions(_component, _writer, _cycle);
        
        verify();
    }
    
    public void testNullBind()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.recordFieldInputValue("some value");
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);
        
        try
        {
            _translator.parse(_component, "some value");
            _translatorControl.setReturnValue(null);
            
            _component.writeValue(null);
            _componentControl.setVoidCallable();
            
            replay();
            
            _support.bind(_component, _writer, _cycle, "some value");
        }
        catch (ValidatorException e)
        {
            unreachable();
        }
        finally
        {
            verify();
        }
    }
    
    public void testNotNullNoValidatorsBind()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.recordFieldInputValue("some value");
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);
        
        try
        {
            Object object = new Object();
            
            _translator.parse(_component, "some value");
            _translatorControl.setReturnValue(object);
            
            _component.writeValue(object);
            _componentControl.setVoidCallable();
            
            _component.getValidators();
            _componentControl.setReturnValue(null);
            
            _valueConverter.coerceValue(null, Iterator.class);
            _valueConverterControl.setReturnValue(Collections.EMPTY_LIST.iterator());
            
            replay();
            
            _support.bind(_component, _writer, _cycle, "some value");
        }
        catch (ValidatorException e)
        {
            unreachable();
        }
        finally
        {
            verify();
        }
    }
    
    public void testNotNullTranslateFailBind()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.recordFieldInputValue("some value");
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);
        
        try
        {
            _translator.parse(_component, "some value");
            _translatorControl.setThrowable(new ValidatorException(""));
            
            replay();
            
            _support.bind(_component, _writer, _cycle, "some value");
            
            unreachable();
        }
        catch (ValidatorException e)
        {
            verify();
        }
    }
    
    public void testNotNullBind()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.recordFieldInputValue("some value");
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);
        
        try
        {
            Object object = new Object();
            
            _translator.parse(_component, "some value");
            _translatorControl.setReturnValue(object);
            
            _component.writeValue(object);
            _componentControl.setVoidCallable();
            
            _component.getValidators();
            _componentControl.setReturnValue(_validator);
            
            _valueConverter.coerceValue(_validator, Iterator.class);
            _valueConverterControl.setReturnValue(Collections.singletonList(_validator).iterator());
            
            _validator.validate(_component, object);
            _validatorControl.setVoidCallable();
            
            replay();
            
            _support.bind(_component, _writer, _cycle, "some value");
        }
        catch (ValidatorException e)
        {
            unreachable();
        }
        finally
        {
            verify();
        }
    }
    
    public void testNotNullValidateFailBind()
    {
        _component.getForm();
        _componentControl.setReturnValue(_form);
        
        _form.getDelegate();
        _formControl.setReturnValue(_delegate);
        
        _delegate.recordFieldInputValue("some value");
        
        _component.getTranslator();
        _componentControl.setReturnValue(_translator);
        
        try
        {
            Object object = new Object();
            
            _translator.parse(_component, "some value");
            _translatorControl.setReturnValue(object);
            
            _component.writeValue(object);
            _componentControl.setVoidCallable();
            
            _component.getValidators();
            _componentControl.setReturnValue(_validator);
            
            _valueConverter.coerceValue(_validator, Iterator.class);
            _valueConverterControl.setReturnValue(Collections.singletonList(_validator).iterator());
            
            _validator.validate(_component, object);
            _validatorControl.setThrowable(new ValidatorException(""));
            
            replay();
            
            _support.bind(_component, _writer, _cycle, "some value");
            
            unreachable();
        }
        catch (ValidatorException e)
        {
            verify();
        }
    }
}