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

package net.sf.tapestry.engine;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;

import net.sf.tapestry.ApplicationRuntimeException;
import net.sf.tapestry.IComponent;
import net.sf.tapestry.IEngine;
import net.sf.tapestry.IEngineService;
import net.sf.tapestry.IForm;
import net.sf.tapestry.IMarkupWriter;
import net.sf.tapestry.IMonitor;
import net.sf.tapestry.IPage;
import net.sf.tapestry.IPageRecorder;
import net.sf.tapestry.IPageSource;
import net.sf.tapestry.IRequestCycle;
import net.sf.tapestry.PageLoaderException;
import net.sf.tapestry.PageRecorderCommitException;
import net.sf.tapestry.RenderRewoundException;
import net.sf.tapestry.RequestContext;
import net.sf.tapestry.RequestCycleException;
import net.sf.tapestry.StaleLinkException;
import net.sf.tapestry.Tapestry;
import net.sf.tapestry.event.ChangeObserver;
import net.sf.tapestry.event.ObservedChangeEvent;

/**
 *  Provides the logic for processing a single request cycle.  Provides access to
 *  the {@link IEngine engine} and the {@link RequestContext}.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 * 
 **/

public class RequestCycle implements IRequestCycle, ChangeObserver
{
    private static final Category CAT = Category.getInstance(RequestCycle.class);

    private IPage page;
    private IEngine engine;
    private IEngineService service;

    private RequestContext requestContext;

    private IMonitor monitor;

    private HttpServletResponse response;

    /**
     *  A mapping of pages loaded during the current request cycle.
     *  Key is the page name, value is the {@link IPage} instance.
     *
     **/

    private Map loadedPages;

    /**
     * A mapping of page recorders for the current request cycle.
     * Key is the page name, value is the {@link IPageRecorder} instance.
     *
     **/

    private Map loadedRecorders;

    private boolean rewinding = false;

    private Map attributes;

    private int actionId;
    private int targetActionId;
    private IComponent targetComponent;

    /** @since 2.0.3 **/
    
    private String[] serviceParameters;
    /**
     *  Standard constructor used to render a response page.
     *
     **/

    public RequestCycle(IEngine engine, RequestContext requestContext, IMonitor monitor)
    {
        this.engine = engine;
        this.requestContext = requestContext;
        this.monitor = monitor;
    }

    /**
     *  Called at the end of the request cycle (i.e., after all responses have been
     *  sent back to the client), to release all pages loaded during the request cycle.
     *
     **/

    public void cleanup()
    {
        if (loadedPages == null)
            return;

        IPageSource source = engine.getPageSource();
        Iterator i = loadedPages.values().iterator();

        while (i.hasNext())
        {
            IPage page = (IPage) i.next();

            source.releasePage(page);
        }

        loadedPages = null;
        loadedRecorders = null;

    }

    public IEngineService getService()
    {
        return service;
    }

    /**
     *  @since 1.0.1
     *
     **/

    public void setService(IEngineService value)
    {
        service = value;
    }

    public String encodeURL(String URL)
    {
        if (response == null)
            response = requestContext.getResponse();

        return response.encodeURL(URL);
    }

    public IEngine getEngine()
    {
        return engine;
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
     *  Gets the page from the engines's {@link IPageSource}.
     *
     **/

    public IPage getPage(String name)
    {
        IPage result = null;
        IPageRecorder recorder;
        IPageSource pageSource;

        if (name == null)
            throw new NullPointerException(Tapestry.getString("RequestCycle.invalid-null-name"));

        if (monitor != null)
            monitor.pageLoadBegin(name);

        if (loadedPages != null)
            result = (IPage) loadedPages.get(name);

        if (result == null)
        {
            pageSource = engine.getPageSource();

            try
            {
                result = pageSource.getPage(engine, name, monitor);
            }
            catch (PageLoaderException ex)
            {
                throw new ApplicationRuntimeException(
                    Tapestry.getString("RequestCycle.could-not-acquire-page", name),
                    ex);
            }

            result.setRequestCycle(this);

            // Get the recorder that will eventually observe and record
            // changes to persistent properties of the page.  If the page
            // has never emitted any page changes, then it will
            // not have a recorder.

            recorder = getPageRecorder(name);

            if (recorder != null)
            {
                // Have it rollback the page to the prior state.  Note that
                // the page has a null observer at this time.

                recorder.rollback(result);

                // Now, have the page use the recorder for any future
                // property changes.

                result.setChangeObserver(recorder);

                // And, if this recorder observed changes in a prior request cycle
                // (and was locked after committing in that cycle), it's time
                // to unlock.

                recorder.setLocked(false);
            }
            else
            {
                // No page recorder for the page.  We'll observe its
                // changes and create the page recorder dynamically
                // if it emits any.

                result.setChangeObserver(this);
            }

            if (loadedPages == null)
                loadedPages = new HashMap();

            loadedPages.put(name, result);
        }

        if (monitor != null)
            monitor.pageLoadEnd(name);

        return result;
    }

    /**
     *  Returns the page recorder for the named page.  This may come
     *  form the cycle's cache of page recorders or, if not yet encountered
     *  in this request cycle, the {@link IEngine#getPageRecorder(String)} is
     *  invoked to get the recorder, if it exists.
     **/

    protected IPageRecorder getPageRecorder(String name)
    {
        IPageRecorder result = null;

        if (loadedRecorders != null)
            result = (IPageRecorder) loadedRecorders.get(name);

        if (result != null)
            return result;

        result = engine.getPageRecorder(name);

        if (result == null)
            return null;

        if (loadedRecorders == null)
            loadedRecorders = new HashMap();

        loadedRecorders.put(name, result);

        return result;
    }

    /** 
     * 
     *  Gets the page recorder from the loadedRecorders cache, or from the engine
     *  (putting it into loadedRecorders).  If the recorder does not yet exist,
     *  it is created.
     * 
     *  @see IEngine#createPageRecorder(String, IRequestCycle)
     *  @since 2.0.3
     * 
     **/

    private IPageRecorder createPageRecorder(String name)
    {
        IPageRecorder result = getPageRecorder(name);

        if (result == null)
        {
            result = engine.createPageRecorder(name, this);

            if (loadedRecorders == null)
                loadedRecorders = new HashMap();

            loadedRecorders.put(name, result);
        }

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

    public boolean isRewound(IComponent component) throws StaleLinkException
    {
        // If not rewinding ...

        if (!rewinding)
            return false;

        if (actionId != targetActionId)
            return false;

        // OK, we're there, is the page is good order?

        if (component == targetComponent)
            return true;

        // Woops.  Mismatch.

        throw new StaleLinkException(
            component,
            Integer.toHexString(targetActionId),
            targetComponent.getExtendedId());
    }

    public void removeAttribute(String name)
    {
        if (CAT.isDebugEnabled())
            CAT.debug("Removing attribute " + name);

        if (attributes == null)
            return;

        attributes.remove(name);
    }

    /**
     *  Renders the page by invoking 
     * {@link IPage#renderPage(IMarkupWriter, IRequestCycle)}.  
     *  This clears all attributes.
     *
     **/

    public void renderPage(IMarkupWriter writer) throws RequestCycleException
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

        if (attributes != null)
            attributes.clear();

        try
        {
            page.renderPage(writer, this);

        }
        catch (RequestCycleException ex)
        {
            // RenderExceptions don't need to be wrapped.
            throw ex;
        }
        catch (ApplicationRuntimeException ex)
        {
            // Nothing much to add here.

            throw ex;
        }
        catch (Throwable ex)
        {
            // But wrap other exceptions in a RequestCycleException ... this
            // will ensure that some of the context is available.

            throw new RequestCycleException(ex.getMessage(), page, ex);
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
     *  Rewinds an individual form by invoking 
     *  {@link IForm#rewind(IMarkupWriter, IRequestCycle)}.
     *
     * <p>The process is expected to end with a {@link RenderRewoundException}.
     * If the entire page is renderred without this exception being thrown, it means
     * that the target action id was not valid, and a 
     * {@link RequestCycleException}
     * is thrown.
     *
     * <p>This clears all attributes.
     *
     *  @since 1.0.2
     **/

    public void rewindForm(IForm form, String targetActionId) throws RequestCycleException
    {
        IPage page = form.getPage();
        String pageName = null;

        if (monitor != null)
        {
            pageName = page.getName();
            monitor.pageRewindBegin(pageName);
        }

        rewinding = true;

        if (attributes != null)
            attributes.clear();

        // Fake things a little for getNextActionId() / isRewound()

        this.targetActionId = Integer.parseInt(targetActionId, 16);
        this.actionId = this.targetActionId - 1;

        this.targetComponent = form;

        try
        {
            form.rewind(NullWriter.getSharedInstance(), this);

            // Shouldn't get this far, because the form should
            // throw the RenderRewoundException.

            throw new StaleLinkException(
                Tapestry.getString("RequestCycle.form-rewind-failure", form.getExtendedId()),
                form);
        }
        catch (RenderRewoundException ex)
        {
            // This is acceptible and expected.
        }
        catch (RequestCycleException ex)
        {
            // RequestCycleExceptions don't need to be wrapped.
            throw ex;
        }
        catch (Throwable ex)
        {
            // But wrap other exceptions in a RequestCycleException ... this
            // will ensure that some of the context is available.

            throw new RequestCycleException(ex.getMessage(), page, ex);
        }
        finally
        {
            rewinding = false;
            this.actionId = 0;
            this.targetActionId = 0;
            this.targetComponent = null;
        }

        if (monitor != null)
            monitor.pageRewindEnd(pageName);

    }

    /**
     *  Rewinds the page by invoking 
     *  {@link IPage#renderPage(IMarkupWriter, IRequestCycle)}.
     *
     * <p>The process is expected to end with a {@link RenderRewoundException}.
     * If the entire page is renderred without this exception being thrown, it means
     * that the target action id was not valid, and a 
     * {@link RequestCycleException}
     * is thrown.
     *
     * <p>This clears all attributes.
     *
     **/

    public void rewindPage(String targetActionId, IComponent targetComponent)
        throws RequestCycleException
    {
        String pageName = null;

        if (monitor != null)
        {
            pageName = page.getName();
            monitor.pageRewindBegin(pageName);
        }

        rewinding = true;

        if (attributes != null)
            attributes.clear();

        actionId = -1;

        // Parse the action Id as hex since that's whats generated
        // by getNextActionId()
        this.targetActionId = Integer.parseInt(targetActionId, 16);
        this.targetComponent = targetComponent;

        try
        {
            page.renderPage(NullWriter.getSharedInstance(), this);

            // Shouldn't get this far, because the target component should
            // throw the RenderRewoundException.

            throw new StaleLinkException(page, targetActionId, targetComponent.getExtendedId());
        }
        catch (RenderRewoundException ex)
        {
            // This is acceptible and expected.
        }
        catch (RequestCycleException ex)
        {
            // RequestCycleExceptions don't need to be wrapped.
            throw ex;
        }
        catch (Throwable ex)
        {
            // But wrap other exceptions in a RequestCycleException ... this
            // will ensure that some of the context is available.

            throw new RequestCycleException(ex.getMessage(), page, ex);
        }
        finally
        {
            rewinding = false;
            actionId = 0;
            this.targetActionId = 0;
            this.targetComponent = null;
        }

        if (monitor != null)
            monitor.pageRewindEnd(pageName);

    }

    public void setAttribute(String name, Object value)
    {
        if (CAT.isDebugEnabled())
            CAT.debug("Set attribute " + name + " to " + value);

        if (attributes == null)
            attributes = new HashMap();

        attributes.put(name, value);
    }

    public void setPage(IPage value)
    {
        if (CAT.isDebugEnabled())
            CAT.debug("Set page to " + value);

        page = value;
    }

    public void setPage(String name)
    {
        if (CAT.isDebugEnabled())
            CAT.debug("Set page to " + name);

        page = getPage(name);
    }

    /**
     *  Invokes {@link IPageRecorder#commit()} on each page recorder loaded
     *  during the request cycle (even recorders marked for discard).
     *
     **/

    public void commitPageChanges() throws PageRecorderCommitException
    {
        if (CAT.isDebugEnabled())
            CAT.debug("Committing page changes");

        if (loadedRecorders == null || loadedRecorders.isEmpty())
            return;

        Iterator i = loadedRecorders.values().iterator();

        while (i.hasNext())
        {
            IPageRecorder recorder = (IPageRecorder) i.next();

            recorder.commit();
        }
    }

    /**
     *  For pages without a {@link IPageRecorder page recorder}, 
     *  we're the {@link ChangeObserver change observer}.
     *  If such a page actually emits a change, then
     *  we'll obtain a new page recorder from the
     *  {@link IEngine engine}, set the recorder
     *  as the page's change observer, and forward the event
     *  to the newly created recorder.  In addition, the
     *  new page recorder is remembered so that it will
     *  be committed by {@link #commitPageChanges()}.
     *
     **/

    public void observeChange(ObservedChangeEvent event)
    {
        IPage page = event.getComponent().getPage();
        String pageName = page.getName();

        if (CAT.isDebugEnabled())
            CAT.debug("Observed change in page " + pageName + "; creating page recorder.");

        IPageRecorder recorder = createPageRecorder(pageName);

        page.setChangeObserver(recorder);

        recorder.observeChange(event);
    }

    /**
     *  Finds the page and its page recorder, creating the page recorder if necessary.
     *  The page recorder is marked for discard regardless of its current state.
     * 
     *  <p>This may make the application stateful even if the page recorder does
     *  not yet exist.
     * 
     *  <p>The page recorder will be discarded at the end of the current request cycle.
     * 
     *  @since 2.0.2
     * 
     **/

    public void discardPage(String name)
    {
        if (CAT.isDebugEnabled())
            CAT.debug("Discarding page " + name);

        IPageRecorder recorder = engine.getPageRecorder(name);

        if (recorder == null)
        {

            page = getPage(name);

            recorder = createPageRecorder(name);

            page.setChangeObserver(recorder);
        }

        recorder.markForDiscard();
    }
    
    /** @since 2.0.3 **/
    
    public String[] getServiceParameters()
    {
        return serviceParameters;
    }
    
    /** @since 2.0.3 **/
    
    public void setServiceParameters(String[] serviceParameters)
    {
        this.serviceParameters = serviceParameters;
    }

}