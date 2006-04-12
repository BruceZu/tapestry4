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

import net.sf.tapestry.*;
import net.sf.tapestry.IMarkupWriter;

/**
 *  A {@link IMarkupWriter} that does absolutely <em>nothing</em>; this
 *  is used during the rewind phase of the request cycle when output
 *  is discarded anyway.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 0.2.9
 *
 **/

public class NullWriter implements IMarkupWriter
{
    private static IMarkupWriter shared;

    public static IMarkupWriter getSharedInstance()
    {
        if (shared == null)
            shared = new NullWriter();

        return shared;
    }

    public void printRaw(char[] buffer, int offset, int length)
    {
    }

    public void printRaw(String value)
    {
    }

    public void println()
    {
    }

    public void print(char[] data, int offset, int length)
    {
    }

    public void print(char value)
    {
    }

    public void print(int value)
    {
    }

    public void print(String value)
    {
    }

    /**
     *  Returns <code>this</code>: since a NullWriter doesn't actually
     *  do anything, one is as good as another!.
     *
     **/

    public IMarkupWriter getNestedWriter()
    {
        return this;
    }

    public String getContentType()
    {
        return null;
    }

    public void flush()
    {
    }

    public void end()
    {
    }

    public void end(String name)
    {
    }

    public void comment(String value)
    {
    }

    public void closeTag()
    {
    }

    public void close()
    {
    }

    /**
     *  Always returns false.
     *
     **/

    public boolean checkError()
    {
        return false;
    }

    public void beginEmpty(String name)
    {
    }

    public void begin(String name)
    {
    }

    public void attribute(String name)
    {
    }

    public void attribute(String name, int value)
    {
    }

    public void attribute(String name, String value)
    {
    }
}