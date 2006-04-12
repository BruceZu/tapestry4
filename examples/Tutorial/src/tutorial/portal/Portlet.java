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

package tutorial.portal;

import net.sf.tapestry.BaseComponent;
import net.sf.tapestry.IActionListener;
import net.sf.tapestry.IAsset;
import net.sf.tapestry.IBinding;
import net.sf.tapestry.IComponent;
import net.sf.tapestry.IMarkupWriter;
import net.sf.tapestry.IRequestCycle;
import net.sf.tapestry.RequestCycleException;
import net.sf.tapestry.components.Block;

/**
 *  A Portlet component knows how to render the frame around a portlet block,
 *  as well as manage the controls (close and minimize/maximize).
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *
 **/

public class Portlet extends BaseComponent
{
    private IBinding modelBinding;
    private PortletModel model;

    public IBinding getModelBinding()
    {
        return modelBinding;
    }

    public void setModelBinding(IBinding value)
    {
        modelBinding = value;
    }

    public Object getModel()
    {
        return model;
    }

	// Simplify for new scheme
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) throws RequestCycleException
    {
        try
        {
            model = (PortletModel) modelBinding.getObject("model", PortletModel.class);

            super.renderComponent(writer, cycle);
        }
        finally
        {
            model = null;
        }
    }

    public IAsset getChangeStateImage()
    {
        return getAsset(model.isExpanded() ? "minimize" : "maximize");
    }

    public IAsset getChangeStateFocus()
    {
        return getAsset(model.isExpanded() ? "minimizeFocus" : "maximizeFocus");
    }

    public String getChangeStateLabel()
    {
        return model.isExpanded() ? "[Minimize]" : "[Maximize]";
    }

    public Block getBodyBlock()
    {
        if (model.isExpanded())
            return model.getBodyBlock(getPage().getRequestCycle());

        // If minimized, return null to prevent any display.

        return null;
    }

    private void changeState()
    {
        model.toggleExpanded();
    }

    public IActionListener getChangeStateListener()
    {
        return new IActionListener()
        {
            public void actionTriggered(IComponent component, IRequestCycle cycle) throws RequestCycleException
            {
                changeState();
            }
        };
    }
}