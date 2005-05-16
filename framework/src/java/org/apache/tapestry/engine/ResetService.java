// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.tapestry.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.services.LinkFactory;
import org.apache.tapestry.services.ResetEventCoordinator;
import org.apache.tapestry.services.ResponseRenderer;
import org.apache.tapestry.services.ServiceConstants;

/**
 * ServiceLink used to discard all cached data (templates, specifications, et cetera). This is
 * primarily used during development. It could be a weakness of a Tapestry application, making it
 * susceptible to denial of service attacks, which is why it is disabled by default. The link
 * generated by the ResetService redisplays the current page after discarding all data.
 * 
 * @author Howard Lewis Ship
 * @since 1.0.9
 */

public class ResetService implements IEngineService
{
    /** @since 4.0 */

    private ResponseRenderer _responseRenderer;

    /** @since 4.0 */

    private ResetEventCoordinator _resetEventCoordinator;

    /** @since 4.0 */
    private boolean _enabled;

    /** @since 4.0 */

    private LinkFactory _linkFactory;

    public ILink getLink(IRequestCycle cycle, Object parameter)
    {
        if (parameter != null)
            throw new IllegalArgumentException(EngineMessages.serviceNoParameter(this));

        Map parameters = new HashMap();

        parameters.put(ServiceConstants.SERVICE, Tapestry.RESET_SERVICE);
        parameters.put(ServiceConstants.PAGE, cycle.getPage().getPageName());

        return _linkFactory.constructLink(cycle, parameters, true);
    }

    public String getName()
    {
        return Tapestry.RESET_SERVICE;
    }

    public void service(IRequestCycle cycle) throws IOException
    {
        String pageName = cycle.getParameter(ServiceConstants.PAGE);

        if (_enabled)
            _resetEventCoordinator.fireResetEvent();

        cycle.activate(pageName);

        // Render the same page (that contained the reset link).

        _responseRenderer.renderResponse(cycle);
    }

    /** @since 4.0 */
    public void setResponseRenderer(ResponseRenderer responseRenderer)
    {
        _responseRenderer = responseRenderer;
    }

    /** @since 4.0 */

    public void setResetEventCoordinator(ResetEventCoordinator resetEventCoordinator)
    {
        _resetEventCoordinator = resetEventCoordinator;
    }

    /** @since 4.0 */

    public void setEnabled(boolean enabled)
    {
        _enabled = enabled;
    }

    /** @since 4.0 */
    public void setLinkFactory(LinkFactory linkFactory)
    {
        _linkFactory = linkFactory;
    }
}