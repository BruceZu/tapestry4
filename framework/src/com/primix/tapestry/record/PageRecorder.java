package com.primix.tapestry.record;

import com.primix.foundation.prop.PropertyHelper;
import com.primix.tapestry.*;
import com.primix.tapestry.event.*;
import java.util.*;
import java.io.Serializable;

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
 *  Tracks changes to components on a page, allowing changes to be persisted across
 *  request cycles, and restoring the state of a page and component when needed.
 *
 *  <p>This is an abstract implementation; specific implementations can choose where
 * and how to persist the data.
 *
 * <p>TBD:  Explicit state (set during page render) that <i>prevents</i> property changes
 * by throwing a runtime exception when they occur.
 *
 * @author Howard Ship
 * @version $Id$
 */


public abstract class PageRecorder
    implements IPageRecorder, Serializable
{
	protected boolean active = false;
	protected boolean dirty = false;

	private int version = 0;
	private transient String versionString;

	/**
	*  Invoked to persist all changes that have been accumulated.  If the recorder
	*  saves change incrementally, this should ensure that all changes have been persisted.
	*
	*  <p>Subclasses should check the dirty flag.  If the recorder is dirty, changes
	* should be recorded, the dirty flag cleared and should invoke <code>incrementState()</code>.
	*
	*/

	public abstract void commit()
	throws PageRecorderCommitException;

	/**
	*  Returns a <code>Collection</code> of {@link IPageChange} objects
	*  identifying changes to the page and its components.
	*
	*/

	public abstract Collection getChanges();

	public String getVersion()
	{
		if (versionString == null)
			versionString = Integer.toHexString(version);

		return versionString;
	}

	/**
	*  Increments the version number.  This should be invoked from <code>commit()</code>
	*  if the recorder is dirty.  It also clears the dirty flag.
	*
	*/

	protected void incrementVersion()
	{
		version++;

		versionString = null;

		dirty = false;
	}


	/**
	*  Indicates whether the recorder is active and recording
	*  changes.  An inactive recorder ignores changes until it is
	*  activated.
	*
	*  <p>The default is false.
	*
	*/

	public boolean isActive()
	{
		return active;
	}

	/**
	*  Returns true if the page has observed a change.
	*  The dirty flag is cleared by
	*  {@link #commit()}.
	*
	*/

	public boolean isDirty()
	{
		return dirty;
	}

	/**
	*  Observes the change.  The object of the event is expected to
	*  be an {@link IComponent}.  Ignores the change if not active,
	*  otherwise, sets invokes {@link #recordChange(String, String,
	*  Serializable)}.
	*
	*  <p>If the property name in the event is null, then the recorder
	*  is marked dirty (but 
	*  {@link #recordChange(String, String,
	*  Serializable)} is not invoked.
	*
	*  <p>If the recorder is not active (typically, when a page is
	*  being rewound), then the event is simply ignored.
	*
	*/

	public void observeChange(ObservedChangeEvent event)
	{
		IComponent component;
		String propertyName;

		if (!active)
			return;

		component = event.getComponent();
		propertyName = event.getPropertyName();
		
		if (propertyName == null)
		{
			dirty = true;
			return;
		}

		recordChange(component.getIdPath(), propertyName, event.getNewValue());
	}

	/**
	*  Records a change to a particular component.  Subclasses may
	*  cache these in memory, or record them externally at this time.
	*
	*  <p>This method is responsible for setting the dirty flag if
	*  the described change is real.
	*
	*  @param componentPath the name of the component relative to the
	*  page which contains it.  May be null if the change was to a
	*  property of the page itself.
	*
	*  @param propertyName the name of the property which changed.
	*
	*  @param newValue the new value for the property, which may also
	*  be null.
	*
	*  @see IComponent#getIdPath()
	*
	*/

	protected abstract void recordChange(String componentPath, String propertyName, 
		Serializable newValue);

	/**
	*  Rolls back the page to the currently persisted state.
	*
	*/

	public void rollback(IPage page)
	{
		Iterator i;
		PageChange change;
		IComponent component;
		PropertyHelper helper;

		i = getChanges().iterator();

		while (i.hasNext())
		{
			change = (PageChange)i.next();

			component = page.getNestedComponent(change.componentPath);

			helper = PropertyHelper.forClass(component.getClass());

			helper.set(component, change.propertyName, change.newValue);
		}
	}

	public void setActive(boolean value)
	{
		active = value;
	}
}
