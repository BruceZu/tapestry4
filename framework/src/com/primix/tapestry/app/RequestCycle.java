package com.primix.tapestry.app;

import com.primix.foundation.*;
import com.primix.tapestry.spec.ApplicationSpecification;
import com.primix.tapestry.record.*;
import java.util.*;
import com.primix.tapestry.*;
import javax.servlet.http.*;
import com.primix.tapestry.components.*;

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
 *  Provides the logic for processing a single request cycle.  Provides access to
 *  the application and the {@link RequestContext}.
 *
 * @author Howard Ship
 * @version $Id$
 */
 
public class RequestCycle 
    implements IRequestCycle
{
	private IPage page;
	private IApplication application;

	private RequestContext requestContext;

	private IMonitor monitor;

	private HttpServletResponse response;

	/**
	*  Temporary string buffer used for assembling things.
	*
	*/

	private StringBuffer buffer;

	private static final int MAP_SIZE = 5;

	/**
	*  A mapping of pages loaded during the current request cycle.
	*  Key is the page name, value is the {@link IPage} instance.
	*
	*/

	private Map loadedPages;
	
	/**
	 * A mapping of page recorders for the current request cycle.
	 * Key is the page name, value is the {@link IPageRecorder} instance.
	 *
	 */
	 
	private Map loadedRecorders;

	private boolean rewinding = false;

	private Map attributes;

	private int actionId;
	private int targetActionId;
	private String targetIdPath;

	/**
	*  Standard constructor used to render a response page.
	*
	*/

	public RequestCycle(IApplication application, RequestContext requestContext,
		IMonitor monitor)
	{
		this.application = application;
		this.requestContext = requestContext;
		this.monitor = monitor;
	}

	/**
	*  Called at the end of the request cycle (i.e., after all responses have been
	*  sent back to the client), to release all pages loaded during the request cycle.
	*
	*/

	public void cleanup()
	{
		Iterator i;
		IPage page;
		IPageSource source;

		if (loadedPages == null)
			return;

		source = application.getPageSource();

		i = loadedPages.values().iterator();

		while (i.hasNext())
        {
			page = (IPage)i.next();

			source.releasePage(page);
		}

		loadedPages = null;
		loadedRecorders = null;
	}

	public String encodeURL(String URL)
	{
		if (response == null)
			response = requestContext.getResponse();

		return response.encodeURL(URL);
	}

	public IApplication getApplication()
	{
		return application;
	}

	public Object getAttribute(String name)
	{
		if (attributes == null)
			return null;

		return attributes.get(name);
	}

	public IMonitor getMonitor()
	{
		return monitor;
	}

	public String getNextActionId()
	{
		return Integer.toHexString(++actionId);
	}

	public IPage getPage()
	{
		return page;
	}

	/**
	*  Gets the page from the application's {@link IPageSource}.
	*
	*/

	public IPage getPage(String name)
	{
		IPage result = null;
		IPageRecorder recorder;
		IPageSource pageSource;
		ApplicationSpecification specification;

		if (monitor != null)
			monitor.pageLoadBegin(name);

		if (loadedPages != null)
			result = (IPage)loadedPages.get(name);

		if (result == null)
		{
			specification = application.getSpecification();

			pageSource = application.getPageSource();

			try
			{
				result = pageSource.getPage(application, name, monitor);
			}
			catch (PageLoaderException e)
			{
				throw new ApplicationRuntimeException("Failed to acquire page " + 
					name + ".", e);
			}

			recorder = getPageRecorder(name);

			// Ignore changes to properties of the page and its components
			// until after the page's state has been rolled back.  Here's
			// where things could, in theory, get tricky ... what if another
			// thread (for this same session) is using the shared recorder?
			// Also, this is not so necessary, since the changeObserver property
			// of the page is null, so it will not be generating any
			// change events.

			recorder.setActive(false);

			result.setChangeObserver(recorder);

			recorder.rollback(result);

			// From this point on, any changes must be tracked.

			recorder.setActive(true);

			if (loadedPages == null)
				loadedPages = new HashMap(MAP_SIZE);

			loadedPages.put(name, result);

		}

		if (monitor != null)
			monitor.pageLoadEnd(name);

		return result;
	}

	/**
	 *  Returns the page recorder for the named page.  This may come
	 *  form the cycle's cache of page recorders or, if not yet encountered
	 *  in this request cycle, the {@link IApplication#getPageRecorder(String)} is
	 *  invoked to get (or create) the page recorder.
	 *
	 */
	 
	protected IPageRecorder getPageRecorder(String name)
	{
		IPageRecorder result = null;
		
		if (loadedRecorders != null)
			result = (IPageRecorder)loadedRecorders.get(name);
		
		if (result != null)
			return result;
			
		result = application.getPageRecorder(name);
			
		if (loadedRecorders == null)
			loadedRecorders = new HashMap(MAP_SIZE);
			
		loadedRecorders.put(name, result);
		
		return result;
	}
		
	public RequestContext getRequestContext()
	{
		return requestContext;
	}

	public boolean isRewinding()
	{
		return rewinding;
	}

	public boolean isRewound(IComponent component)
	throws StaleLinkException
	{
		// If not rewinding ...

		if (!rewinding)
			return false;

		if (actionId != targetActionId)
			return false;
		
		// OK, we're there, is the page is good order?
		
		if (component.getIdPath().equals(targetIdPath))
			return true;
			
		// Woops.  Mismatch.
		
		throw new StaleLinkException(component, this, 
			Integer.toString(targetActionId), targetIdPath);
	}

	public void removeAttribute(String name)
	{
		if (attributes == null)
			return;

		attributes.remove(name);
	}

	/**
	*  Renders the page by invoking 
	* {@link IPage#renderPage(IResponseWriter, IRequestCycle)}.  
	*  This clears all attributes.
	*
	*/

	public void renderPage(IResponseWriter writer)
	throws RequestCycleException
	{
		String pageName = null;

		if (monitor != null)
		{
			pageName = page.getName();
			monitor.pageRenderBegin(pageName);
		}

		rewinding = false;
		actionId = -1;
		targetActionId = 0;

		// Forget any attributes from a previous render cycle.

		attributes = null;
	
		try
		{
			page.renderPage(writer, this);

		}
		catch (RequestCycleException e)
		{
			// RenderExceptions don't need to be wrapped.
			throw e;
		}
		catch (Throwable e)
		{
			// But wrap other exceptions in a RequestCycleException ... this
			// will ensure that some of the context is available.

			throw new RequestCycleException(e.getMessage(), page, this, e);
		}
		finally
		{
			actionId = 0;
			targetActionId = 0;
		}

		if (monitor != null)
		    monitor.pageRenderEnd(pageName);

	}

	/**
	*  Rewinds the page by invoking 
	*  {@link IPage#renderPage(IResponseWriter, IRequestCycle)}.
	*
	* <p>The process is expected to end with a {@link RenderRewoundException}.
	* If the entire page is renderred without this exception being thrown, it means
	* that the target action id was not valid, and a 
	* {@link RequestCycleException}
	* is thrown.
	*
	* <p>This clears all attributes.
	*
	*/

	public void rewindPage(String targetActionId, String targetIdPath)
	throws RequestCycleException
	{
		IResponseWriter writer;
		String pageName = null;

		if (monitor != null)
		{
			pageName = page.getName();
			monitor.pageRewindBegin(pageName);
		}

		rewinding = true;
		attributes = null;
		actionId = -1;

		this.targetActionId = Integer.parseInt(targetActionId);
		this.targetIdPath = targetIdPath;

		writer  = new HTMLResponseWriter(new NullOutputStream());

		try
		{
			page.renderPage(writer, this);

			// Shouldn't get this far, because the target component should
			// throw the RenderRewoundException.

			throw new StaleLinkException(page, this, 
				targetActionId, targetIdPath);
		}
		catch (RenderRewoundException e)
		{
			// This is acceptible and expected.
		}
		catch (RequestCycleException e)
		{
			// RequestCycleException don't need to be wrapped.
			throw e;
		}
		catch (Throwable e)
		{
			// But wrap other exceptions in a RequestCycleException ... this
			// will ensure that some of the context is available.

			throw new RequestCycleException(e.getMessage(), null, this, e);
		}
		finally
		{
			writer.close();

			rewinding = false;
			actionId = 0;
			this.targetActionId = 0;
			this.targetIdPath = null;
		}

		if (monitor != null)
			monitor.pageRewindEnd(pageName);

	}

	public void setAttribute(String name, Object value)
	{
		if (attributes == null)
			attributes = new HashMap(MAP_SIZE);

		attributes.put(name, value);
	}

	public void setPage(IPage value)
	{
		page = value;
	}

	public void setPage(String name)
	{
		page = getPage(name);
	}
	
  /**
	*  Invokes {@link IPageRecorder#commit()} on each page recorder loaded
	*  during the request cycle.
	*
	*/

	public void commitPageChanges()
	throws PageRecorderCommitException
	{
		Iterator i;
		IPageRecorder recorder;

		if (loadedRecorders == null)
			return;

		i = loadedRecorders.values().iterator();

		while (i.hasNext())
		{
			recorder = (IPageRecorder)i.next();

			recorder.commit();

			recorder.setActive(false);
		}
	}
}

