package org.apache.tapestry.bean;

import org.apache.hivemind.Defense;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IBeanProvider;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.services.BindingSource;

/**
 * An {@link org.apache.tapestry.bean.IBeanInitializer}&nbsp; implementation that uses an
 * {@link org.apache.tapestry.IBinding}&nbsp; to obtain the value which will be assigned to the
 * bean property.
 * 
 * @author Howard M. Lewis Ship
 * @since 3.1
 */
public class BindingBeanInitializer extends AbstractBeanInitializer
{
    private BindingSource _bindingSource;

    private String _bindingReference;

    /** @since 3.1 */
    private IBinding _binding;

    /** @since 3.1 */
    public BindingBeanInitializer(BindingSource source)
    {
        Defense.notNull(source, "source");

        _bindingSource = source;
    }

    /**
     * @since 3.1
     */
    public void setBindingReference(String bindingReference)
    {
        _bindingReference = bindingReference;
    }

    /** @since 3.1 */
    public String getBindingReference()
    {
        return _bindingReference;
    }

    public void setBeanProperty(IBeanProvider provider, Object bean)
    {
        if (_binding == null)
        {
            IComponent component = provider.getComponent();

            String description = BeanMessages.propertyInitializerName(_propertyName);

            _binding = _bindingSource.createBinding(
                    component,
                    description,
                    _bindingReference,
                    getLocation());
        }

        Class propertyType = PropertyUtils.getPropertyType(bean, _propertyName);

        Object bindingValue = _binding.getObject(propertyType);

        setBeanProperty(bean, bindingValue);
    }
}
