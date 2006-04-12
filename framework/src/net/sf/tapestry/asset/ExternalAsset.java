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

package net.sf.tapestry.asset;

import java.io.InputStream;
import java.net.URL;

import net.sf.tapestry.IAsset;
import net.sf.tapestry.IRequestCycle;
import net.sf.tapestry.ResourceUnavailableException;
import net.sf.tapestry.Tapestry;

/**
 *  A reference to an external URL.  {@link ExternalAsset}s are not
 *  localizable.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 * 
 **/

public class ExternalAsset implements IAsset
{
    private String URL;

    public ExternalAsset(String URL)
    {
        this.URL = URL;
    }

    /**
     *  Simply returns the URL of the external asset.
     *
     **/

    public String buildURL(IRequestCycle cycle)
    {
        return URL;
    }

    public InputStream getResourceAsStream(IRequestCycle cycle) throws ResourceUnavailableException
    {
        URL url;

        try
        {
            url = new URL(URL);

            return url.openStream();
        }
        catch (Exception ex)
        {
            // MalrformedURLException or IOException

            throw new ResourceUnavailableException(
                Tapestry.getString("ExternalAsset.resource-missing", URL),
                ex);
        }

    }

    public String toString()
    {
        return "ExternalAsset[" + URL + "]";
    }
}