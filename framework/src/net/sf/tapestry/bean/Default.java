//
// Tapestry Web Application Framework
// Copyright (c) 2000-2002 by Howard Lewis Ship
//
// Howard Lewis Ship
// http://sf.net/projects/tapestry
// mailto:hship@users.sf.net
//
// This library is free software.
//
// You may redistribute it and/or modify it under the terms of the GNU
// Lesser General Public License as published by the Free Software Foundation.
//
// Version 2.1 of the license should be included with this distribution in
// the file LICENSE, as well as License.html. If the license is not
// included with this distribution, you may find a copy at the FSF web
// site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
// Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied waranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//

package net.sf.tapestry.bean;

import net.sf.tapestry.IBinding;
import net.sf.tapestry.util.pool.IPoolable;

/**
 *  A helper bean to assist with providing defaults for unspecified
 *  parameters.    It is initalized
 *  with an {@link IBinding} and a default value.  It's value property
 *  is either the value of the binding, but if the binding is null,
 *  or the binding returns null, the default value is returned.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 1.0.5
 * 
 **/

public class Default implements IPoolable
{
    private IBinding binding;
    private Object defaultValue;

    public void resetForPool()
    {
        binding = null;
        defaultValue = null;
    }

    public void setBinding(IBinding value)
    {
        binding = value;
    }

    public IBinding getBinding()
    {
        return binding;
    }

    public void setDefaultValue(Object value)
    {
        defaultValue = value;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }

    /**
     *  Returns the value of the binding.  However, if the binding is null, or the binding
     *  returns null, then the defaultValue is returned instead.
     *
     **/

    public Object getValue()
    {
        if (binding == null)
            return defaultValue;

        Object value = binding.getObject();

        if (value == null)
            return defaultValue;

        return value;
    }
}