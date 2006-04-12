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

package net.sf.tapestry.components;

import net.sf.tapestry.IComponent;

/**
 *  A component that renders an HTML &lt;a&gt; element.  It exposes some
 *  properties to the components it wraps.  This is basically to facilitate
 *  the {@link net.sf.tapestry.html.Rollover} component.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 * 
 **/

public interface IServiceLink extends IComponent
{
    /**
     *  The name of an {@link net.sf.tapestry.IRequestCycle} attribute in which the
     *  current service link is stored.  ServiceLink links do not nest.
     *
     **/

    public static final String ATTRIBUTE_NAME = "net.sf.tapestry.active.IServiceLink";

    /**
     *  Returns whether this service link component is enabled or disabled.
     *
     *  @since 0.2.9
     *
     **/

    public boolean isDisabled();

    /**
     *  Adds a new event handler.  When the event occurs, the JavaScript function
     *  specified is executed.  Multiple functions can be specified, in which case
     *  all of them are executed.
     *
     *  <p>This was created for use by
     *  {@link net.sf.tapestry.html.Rollover} to set mouse over and mouse out handlers on
     *  the {@link IServiceLink} that wraps it, but can be used for
     *  many other things as well.
     *
     *  @since 0.2.9
     **/

    public void addEventHandler(ServiceLinkEventType eventType, String functionName);
}