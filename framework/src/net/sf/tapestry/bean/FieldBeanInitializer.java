package net.sf.tapestry.bean;

import java.lang.reflect.Field;

import net.sf.tapestry.ApplicationRuntimeException;
import net.sf.tapestry.IBeanProvider;
import net.sf.tapestry.IResourceResolver;
import net.sf.tapestry.Tapestry;

/**
 *  Initializes a bean with the value of a public static field.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 1.0.8
 *
 **/

public class FieldBeanInitializer extends AbstractBeanInitializer
{
    protected String _fieldName;
    protected Object _fieldValue;
    private boolean _fieldResolved = false;

    public FieldBeanInitializer(String propertyName, String fieldName)
    {
        super(propertyName);

        _fieldName = fieldName;
    }

    public void setBeanProperty(IBeanProvider provider, Object bean)
    {
        IResourceResolver resolver = provider.getResourceResolver();
        
        if (!_fieldResolved)
            resolveField(resolver);

        setBeanProperty(resolver, bean, _fieldValue);
    }

    private synchronized void resolveField(IResourceResolver resolver)
    {
        if (_fieldResolved)
            return;

        // This is all copied out of of FieldBinding!!

        int dotx = _fieldName.lastIndexOf('.');

        if (dotx < 0)
            throw new ApplicationRuntimeException(Tapestry.getString("invalid-field-name", _fieldName));

        String className = _fieldName.substring(0, dotx);
        String simpleFieldName = _fieldName.substring(dotx + 1);

        // Simple class names are assumed to be in the java.lang package.

        if (className.indexOf('.') < 0)
            className = "java.lang." + className;

        Class targetClass = null;

        try
        {
            targetClass = resolver.findClass(className);
        }
        catch (Throwable t)
        {
            throw new ApplicationRuntimeException(Tapestry.getString("unable-to-resolve-class", className), t);
        }

        Field field = null;

        try
        {
            field = targetClass.getField(simpleFieldName);
        }
        catch (NoSuchFieldException ex)
        {
            throw new ApplicationRuntimeException(Tapestry.getString("field-not-defined", _fieldName), ex);
        }

        // Get the value of the field.  null means look for it as a static
        // variable.

        try
        {
            _fieldValue = field.get(null);
        }
        catch (IllegalAccessException ex)
        {
            throw new ApplicationRuntimeException(Tapestry.getString("illegal-field-access", _fieldName), ex);
        }
        catch (NullPointerException ex)
        {
            throw new ApplicationRuntimeException(Tapestry.getString("field-is-instance", _fieldName), ex);
        }

        _fieldResolved = true;
    }

}