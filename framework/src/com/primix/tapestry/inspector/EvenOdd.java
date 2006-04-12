package com.primix.tapestry.inspector;

import com.primix.tapestry.*;
import com.primix.tapestry.spec.*;
import com.primix.tapestry.components.*;
import java.util.*;

/*
 * Tapestry Web Application Framework
 * Copyright (c) 2000 by Howard Ship and Primix Solutions
 *
 * Primix Solutions
 * One Arsenal Marketplace
 * Watertown, MA 02472
 * http://www.primix.com
 * mailto:hship@primix.com
 * 
 * This library is free software.
 * 
 * You may redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation.
 *
 * Version 2.1 of the license should be included with this distribution in
 * the file LICENSE, as well as License.html. If the license is not
 * included with this distribution, you may find a copy at the FSF web
 * site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 * Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 */


/**
 *  Used to create &lt;tr;&gt; tags with alternating values
 *  of "even" and "odd" for the HTML class attribute.  The sequence always
 *  starts with "even".
 *
 *  <p>No formal parameters, but allows informal parameters.  Allows a body
 *  (in fact, doesn't make sense without one).
 *
 *  @version $Id$
 *  @author Howard Ship
 *
 */
 
public class EvenOdd extends AbstractComponent
implements ILifecycle
{
	private boolean even;
	
	private String[] reservedNames = 
	{ "class" 
	};

	public EvenOdd(IPage page, IComponent container, String id,
		ComponentSpecification specification)
	{
		super(page, container, id, specification);
	}

	private String getNextClass()
	{
		String result;
		
		result = even ? "even" : "odd";
		
		even = !even;
		
		return result;
	}
	
	public void prepareForRender(IRequestCycle cycle)
	{
		even = true;
	}
	
	public void render(IResponseWriter writer,
                   IRequestCycle cycle)
    throws RequestCycleException
    {
		writer.begin("tr");
		writer.attribute("class", getNextClass());
		
		generateAttributes(cycle, writer, reservedNames);
		
		renderWrapped(writer, cycle);
		
		writer.end();
    }
}