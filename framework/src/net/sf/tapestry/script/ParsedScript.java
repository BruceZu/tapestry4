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

package net.sf.tapestry.script;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.tapestry.IScript;
import net.sf.tapestry.ScriptException;
import net.sf.tapestry.ScriptSession;

/**
 *  A top level container for a number of {@link IScriptToken script tokens}.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 0.2.9
 * 
 **/

public class ParsedScript implements IScript
{
    private String scriptPath;

    public ParsedScript(String scriptPath)
    {
        this.scriptPath = scriptPath;
    }

    public String getScriptPath()
    {
        return scriptPath;
    }

    private List tokens = new ArrayList();

    public void addToken(IScriptToken token)
    {
        tokens.add(token);
    }

    public ScriptSession execute(Map symbols) throws ScriptException
    {
        ScriptSession result = new ScriptSession(scriptPath, symbols);
        Iterator i = tokens.iterator();

        while (i.hasNext())
        {
            IScriptToken token = (IScriptToken) i.next();

            token.write(null, result);
        }

        return result;
    }
}