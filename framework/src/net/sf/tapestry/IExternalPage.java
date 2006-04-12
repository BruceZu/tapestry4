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

package net.sf.tapestry;

import net.sf.tapestry.IPage;
import net.sf.tapestry.IRequestCycle;

/**
 *  Defines a page which may be referenced externally via a URL using the 
 *  {@link net.sf.tapestry.engine.ExternalService}. External pages may be bookmarked 
 *  via their URL for latter display. See the 
 *  {@link net.sf.tapestry.link.ExternalLink} for details on how to invoke
 *  <tt>IExternalPage</tt>s.
 * 
 *  @see net.sf.tapestry.engine.ExternalService
 *
 *  @author Howard Lewis Ship
 *  @author Malcolm Edgar
 *  @version $Id$
 *  @since 2.2
 **/

public interface IExternalPage extends IPage
{
    /**
     *  Initialize the external page with the given array of parameters and
     *  request cycle.
     *  <p>
     *  This method is invoked after {@link IPage#validate(IRequestCycle)}.
     *
     *  @param parameters the array of page parameters
     *  @param the current request cycle
     * 
     **/
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle)
    throws RequestCycleException;
}