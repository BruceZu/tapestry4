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

package net.sf.tapestry.record;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.tapestry.PageRecorderCommitException;

/**
 *  Simple implementation of {@link net.sf.tapestry.IPageRecorder}
 *  that stores page changes in-memory using
 *  collections.
 *
 *  <p>The recorder must be made session persistant, either by being stored
 *  directly in the session, or being referenced from a session-persistant
 *  object.  {@link net.sf.tapestry.engine.SimpleEngine} simply stores a {@link Map} of
 *  these page recorders.
 *
 *  @author Howard Ship
 *  @version $Id$
 * 
 **/

public class SimplePageRecorder extends PageRecorder implements Externalizable
{
    /**
     *  @since 2.0.4
     * 
     **/

    private static final long serialVersionUID = -2088018593972661079L;

    private final static int MAP_SIZE = 7;

    /**
     *  Dictionary of changes, keyed on an instance of ChangeKey
     *  (which enapsulates component path and property name).  The
     *  value is the new value for the object.
     *
     **/

    private Map changes;

    /**
     *  Simply clears the dirty flag, because there is no external place
     *  to store changed page properties.  Sets the locked flag to prevent
     *  subsequent changes from occuring now.
     *
     **/

    public void commit() throws PageRecorderCommitException
    {
        dirty = false;
        locked = true;
    }

    /**
     *  Returns true if the recorder has any changes recorded.
     *
     **/

    public boolean getHasChanges()
    {
        if (changes == null)
            return false;

        return (changes.size() > 0);
    }

    public Collection getChanges()
    {
        Collection result;
        Iterator i;
        Map.Entry entry;
        PageChange change;
        int count;
        ChangeKey key;
        Object value;

        if (changes == null)
            return Collections.EMPTY_LIST;

        count = changes.size();
        result = new ArrayList(count);

        i = changes.entrySet().iterator();
        while (i.hasNext())
        {
            entry = (Map.Entry) i.next();

            key = (ChangeKey) entry.getKey();

            value = entry.getValue();

            change = new PageChange(key.componentPath, key.propertyName, value);

            result.add(change);
        }

        return result;
    }

    protected void recordChange(
        String componentPath,
        String propertyName,
        Object newValue)
    {
        ChangeKey key;
        Object oldValue;

        key = new ChangeKey(componentPath, propertyName);

        if (changes == null)
            changes = new HashMap(MAP_SIZE);

        // Check the prior value.  If this is not an actual change,
        // then don't bother recording it, or marking this page recorder
        // dirty.

        oldValue = changes.get(key);
        if (newValue == oldValue)
            return;

        try
        {
            if (oldValue != null && oldValue.equals(newValue))
                return;
        }
        catch (Exception ex)
        {
            // Ignore.
        }

        dirty = true;

        changes.put(key, newValue);
    }

    /**
     *  Reads the state stored by {@link #writeExternal(ObjectOutput)}.
     *
     **/

    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        String componentPath;
        int count = in.readInt();

        if (count == 0)
            return;

        changes = new HashMap(MAP_SIZE);

        for (int i = 0; i < count; i++)
        {
            // Read a boolean ... if true, a UTF string follows.

            if (in.readBoolean())
                componentPath = (String) in.readObject();
            else
                componentPath = null;

            String propertyName = in.readUTF();
            Object rawValue = in.readObject();
            Object value = restoreValue(rawValue);

            changes.put(new ChangeKey(componentPath, propertyName), value);
        }

    }

    /**
     *  Writes the changes.  Writes the number of changes as an int, which
     *  may be zero.
     *
     *  <p>For each change, writes:
     *  <ul>
     *  <li>componentPath ({@link String}, may be null)
     *  <li>propertyName ({@link String})
     *  <li>value (may be null)
     *  </ul>
     *
     **/

    public void writeExternal(ObjectOutput out) throws IOException
    {
        if (changes == null)
        {
            out.writeInt(0);
            return;
        }

        out.writeInt(changes.size());
        Iterator i = changes.entrySet().iterator();

        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry) i.next();
            ChangeKey key = (ChangeKey) entry.getKey();

            // To avoid writeObject(), we write a boolean
            // indicating whether there's a non-null UTF string
            // following.

            out.writeBoolean(key.componentPath != null);

            if (key.componentPath != null)
                out.writeObject(key.componentPath);

            out.writeUTF(key.propertyName);
            out.writeObject(persistValue(entry.getValue()));
        }
    }
}