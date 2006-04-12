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

package net.sf.tapestry.vlib;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Timestamp;

import javax.ejb.FinderException;

import net.sf.tapestry.ApplicationRuntimeException;
import net.sf.tapestry.vlib.ejb.Person;

/**
 *  The visit object for the {@link VirtualLibraryEngine}.
 *
 *  Primarily, this is used to access the home interfaces and EJB instances, and
 *  to identify who the logged in user is.
 *
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *
 **/

public class Visit implements Serializable
{
    /**
     *  Used to identify the logged in user.
     *
     **/

    private transient Person _user;
    private Integer _userPK;

    /**
     *  Set when the user first logs in.  This is the time of their previous
     *  login (logging in returns the old value then updates the value
     *  for subsequent logins).
     *
     **/

    private Timestamp _lastAccess;

    private VirtualLibraryEngine _engine;

    public Visit(VirtualLibraryEngine engine)
    {
        _engine = engine;
    }

    public VirtualLibraryEngine getEngine()
    {
        return _engine;
    }

    /**
     *  Returns the time the user last accessed the database, which may
     *  be null if the user hasn't logged in yet.
     *
     **/

    public Timestamp getLastAccess()
    {
        return _lastAccess;
    }

    /**
     *  Gets the logged-in user, or null if the user is not logged in.
     *
     **/

    public Person getUser()
    {
        if (_user != null)
            return _user;

        if (_userPK == null)
            return null;

        for (int i = 0; i < 2; i++)
        {
            try
            {
                _user = _engine.getOperations().getPerson(_userPK);

                break;
            }
            catch (FinderException e)
            {
                throw new ApplicationRuntimeException("Could not locate user.", e);
            }
            catch (RemoteException ex)
            {
                _engine.rmiFailure("Unable to access logged-in user.", ex, i > 0);
            }
        }

        return _user;
    }

    /**
     *  Returns the primary key of the logged in user, or null if the
     *  user is not logged in.
     *
     **/

    public Integer getUserPK()
    {
        return _userPK;
    }

    /**
     *  Changes the logged in user. This is only invoked from the 
     *  {@link net.sf.tapestry.vlib.pages.Login} page.
     *
     **/

    public void setUser(Person value)
    {
        _lastAccess = null;
        _user = value;
        _userPK = null;

        if (_user == null)
            return;

        _userPK = _user.getPrimaryKey();

        _lastAccess = _user.getLastAccess();
    }

    /**
     *  Returns true if the user is logged in.
     *
     **/

    public boolean isUserLoggedIn()
    {
        return _userPK != null;
    }

    /**
     *  Returns true if the user has not been identified (has not
     *  logged in).
     *
     **/

    public boolean isUserLoggedOut()
    {
        return _userPK == null;
    }

    public boolean isLoggedInUser(Integer primaryKey)
    {
        if (_userPK == null)
            return false;

        return _userPK.equals(primaryKey);
    }

    /**
     *  Invoked by pages after they perform an operation that changes the backend
     *  database in such a way that cached data is no longer valid.  Currently,
     *  this should be invoked after changing the user's profile, or adding
     *  a new {@link net.sf.tapestry.vlib.ejb.IPublisher} entity.
     *
     **/

    public void clearCache()
    {
        _user = null;

        _engine.clearCache();
    }

}