package net.sf.tapestry;

import java.util.Collection;

import net.sf.tapestry.event.ChangeObserver;
import net.sf.tapestry.event.PageDetachListener;

/**
 *  Defines an object that can observe changes to properties of
 *  a page and its components, store the state of the page between request cycles,
 *  and restore a page's state on a subsequent request cycle.
 *
 *  <p>Concrete implementations of this can store the changes in memory,
 *  as client-side cookies, in a flat file, or in a database.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 * 
 **/

public interface IPageRecorder extends ChangeObserver
{
    /**
     *  Invoked after the recorder is instantiated to initialize
     *  it for the current request cycle.
     * 
     *  @param pageName the fully qualified page name
     *  @param cycle the current request cycle
     * 
     *  @since 2.4
     * 
     **/
    
    public void initialize(String pageName, IRequestCycle cycle);
    
    
    /**
     *  Invoked at the end of a request cycle in which the
     *  page recorder is discarded (either implicitly, because
     *  the page recorder has no changes, or explicitly
     *  because of {@link IEngine#forgetPage(String)} or
     *  {@link #markForDiscard()}.
     * 
     *  @since 2.4
     * 
     **/
    
    public void discard();
    
	/**
	 *  Persists all changes that have been accumulated.  If the recorder
	 *  saves change incrementally, this should ensure that all changes have been persisted.
	 *
	 *  <p>After commiting, a page recorder automatically locks itself.
     * 
	 **/

	public void commit() throws PageRecorderCommitException;

	/**
	 *  Returns a {@link Collection} of {@link IPageChange} objects that represent
	 *  the persistant state of the page.
	 *
	 **/

	public Collection getChanges();

	/**
	 *  Returns true if the recorder has any changes for the page.
	 *
	 **/

	public boolean getHasChanges();

	/**
	 *  Returns true if the recorder has observed any changes that have not
	 *  been committed to external storage.
	 *
	 **/

	public boolean isDirty();

	/**
	 *  Returns true if the recorder is in a locked state, following
	 *  a {@link #commit()}.
	 *
	 **/

	public boolean isLocked();

	/**
	 *  Rolls back the page to the currently persisted state.
	 *
	 *  <p>A page recorder can only rollback changes to properties
	 *  which have changed at some point.  This can cause some minor
	 *  problems, addressed by  {@link PageDetachListener#pageDetached(PageEvent)}.
     * 
	 **/

	public void rollback(IPage page);

	/**
	 *  Invoked to lock or unlock the recorder.  Recoders are locked
	 *  after they are commited, and stay locked until
	 *  explicitly unlocked in a subsequent request cycle.
	 *
	 **/

	public void setLocked(boolean value);
    
    /**
     *  Invoked to mark the recorder for discarding at the end of the request cycle.
     * 
     *  @since 2.0.2
     * 
     **/
    
    public void markForDiscard();
    
    /**
     *  Returns true if the recorder has been marked for discard.
     * 
     **/
    
    public boolean isMarkedForDiscard();
}