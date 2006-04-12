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

package net.sf.tapestry.link;

import net.sf.tapestry.IBinding;
import net.sf.tapestry.IEngineService;
import net.sf.tapestry.IRequestCycle;
import net.sf.tapestry.RequestCycleException;
import net.sf.tapestry.RequiredParameterException;

/**
 *  A component for creating a navigation link to another page, 
 *  using the page service.
 *
 *
 *  <table border=1>
 *  <tr> 
 *  <th>Parameter</th> 
 *  <th>Type</th> 
 *  <th>Direction</th> 
 *  <th>Required</th> 
 *  <th>Default</th> 
 *  <th>Description</th>
 *  </tr>
 *
 * <tr>
 *        <td>page</td>
 *        <td>java.lang.String</td>
 *        <td>R</td>
 *        <td>yes</td>
 *        <td>&nbsp;</td>
 *        <td>The name of a page to link to.</td>  </tr>
 *
 * <tr>
 *   <td>disabled</td> <td>boolean</td> <td>R</td> <td>No</td> <td>true</td>
 *   <td>Controls whether the link is produced.  If disabled, the portion of the template
 *  the link surrounds is still rendered, but not the link itself.
 *  </td></tr>
 *
 *
 * <tr>
 *        <td>scheme</td>
 *        <td>java.lang.String</td>
 *        <td>R</td>
 *        <td>no</td>
 *        <td>&nbsp;</td>
 *        <td>If specified, then a longer URL (including scheme, server and possibly port)
 * is generated using the specified scheme. Server is determined fromt he incoming request,
 * and port is deterimined from the port paramter or the incoming request.
 *  </td>
 * </tr>
 *
 * <tr>
 *        <td>port</td>
 *        <td>int</td>
 *        <td>R</td>
 *        <td>no</td>
 *        <td>&nbsp;</td>
 *        <td>If specified, then a longer URL (including scheme, server and port)
 *  is generated using the specified port.  The server is determined from the incoming
 *  request, the scheme from the scheme paramter or the incoming request.
 * </td>
 * </tr>
 *
 * <tr>
 *        <td>anchor</td>
 *        <td>java.lang.String</td>
 *        <td>R</td>
 *        <td>no</td>
 *        <td>&nbsp;</td>
 *        <td>The name of an anchor or element to link to.  The final URL will have '#'
 *   and the anchor appended to it.
 * </td> </tr>
 *
 * </table>
 *
 * <p>Informal parameters are allowed.
 *  The final URL will have '#'
 *   and the anchor appended to it.
 *
 * @author Howard Ship
 * @version $Id$
 *
 **/

public class Page extends GestureLink
{
	private String targetPage;

    /**
     *  Returns {@link IEngineService#PAGE_SERVICE}.
     *
     **/

    protected String getServiceName()
    {
        return IEngineService.PAGE_SERVICE;
    }

    /**
     *  Returns a single-element String array; the lone element is the
     *  name of the page, retrieved from the 'page' parameter.
     *
     **/

    protected String[] getContext(IRequestCycle cycle) throws RequestCycleException
    {
        return new String[] { targetPage };
    }

    public String getTargetPage()
    {
        return targetPage;
    }


    public void setTargetPage(String targetPage)
    {
        this.targetPage = targetPage;
    }

}