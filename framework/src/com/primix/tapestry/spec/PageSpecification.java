package com.primix.tapestry.spec;

import com.primix.tapestry.*;
import com.primix.foundation.*;

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
 *  Specification for a page, which defines the component and buffer size.
 *
 * <p>Future enhancements to Tapestry may allow for some or all of the following:
 *  <ul>
 *  <li>Define specific exception page for any page
 *  <li>Location of properties file for page (to help with localization)
 *  </ul>
 *
 *  @author Howard Ship
 *  @version $Id$
 */


public class PageSpecification extends BasePropertyHolder
{
	private int bufferSize = -1;
	private String specificationPath;

	public PageSpecification()
	{
	}

	public PageSpecification(String specificationPath)
	{
		this.specificationPath = specificationPath;
	}
	
	public PageSpecification(String specificationPath, int bufferSize)
	{
		this.specificationPath = specificationPath;
		this.bufferSize = bufferSize;
	}

	/**
	*  Returns the buffer size for the page (in bytes).  A value less than zero means no specific
	*  value was set for the page.
	*
	*/

	public int getBufferSize()
	{
		return bufferSize;
	}

	public String getSpecificationPath()
	{
		return specificationPath;
	}

	/**
	*  Sets the size of the buffer used when rendering the page, in bytes.
	*/

	public void setBufferSize(int value)
	{
		bufferSize = value;
	}

	public void setSpecificationPath(String value)
	{
		specificationPath = value;
	}
}
