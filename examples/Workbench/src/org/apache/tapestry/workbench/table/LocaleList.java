/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation", "Tapestry" 
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" 
 *    or "Tapestry", nor may "Apache" or "Tapestry" appear in their 
 *    name, without prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE TAPESTRY CONTRIBUTOR COMMUNITY
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.tapestry.workbench.table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.table.model.ITableColumnModel;
import org.apache.tapestry.contrib.table.model.ITableDataModel;
import org.apache.tapestry.contrib.table.model.ITableModel;
import org.apache.tapestry.contrib.table.model.ITableRendererSource;
import org.apache.tapestry.contrib.table.model.ITableSessionStateManager;
import org.apache.tapestry.contrib.table.model.ognl.ExpressionTableColumn;
import org.apache.tapestry.contrib.table.model.ognl.ExpressionTableColumnModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleListTableDataModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumnFormRendererSource;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableSessionStateManager;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableState;

/**
 * @author mindbridge
 *
 */
public abstract class LocaleList extends BaseComponent
{
    // immutable values
	private ITableSessionStateManager m_objTableSessionStateManager;

    /**
     * @see org.apache.tapestry.AbstractComponent#finishLoad()
     */
    protected void finishLoad()
    {
        super.finishLoad();
        initTableSessionStateManager();
    }
    
	/**
	 * Method initTableSessionStateManager.
	 * Creates the Table Session State Manager, and thus determines what part
	 * of the table model will be saved in the session.
     * See comments for details
	 */
	protected void initTableSessionStateManager()
	{
		// Use the simple data model using the array of standard Locales
		ITableDataModel objDataModel =
			new SimpleListTableDataModel(Locale.getAvailableLocales());

		// This is a simple to use column model that uses OGNL to access
		// the data to be displayed in each column. The first string is
		// the name of the column, and the second is the OGNL expression.
		// We also enable sorting for all columns by setting the second argument to true
		ITableColumnModel objColumnModel =
			new ExpressionTableColumnModel(
				new String[] {
                    "Locale", "toString()",
					"Language", "displayLanguage",
					"Country", "displayCountry",
					"Variant", "displayVariant",
                    "ISO Language", "ISO3Language",
                    "ISO Country", "ISO3Country", 
                    },
				true);


        // Modify the columns to render headers designed for use in a form. 
        // These headers ensure that the form is submitted when clicking on them
        // and sorting by the column so that current form selections are preserved.
        // Skip this if the table is not used in a form or you do not need 
        // that behaviour.  
        // This is usually used in conjunction with the TableFormPages and
        // the TableFormRows components.
        ITableRendererSource objRendererSource = new SimpleTableColumnFormRendererSource();
        for (Iterator it = objColumnModel.getColumns(); it.hasNext(); ) {
            ExpressionTableColumn objColumn = (ExpressionTableColumn) it.next();
            objColumn.setColumnRendererSource(objRendererSource);
        }


		// Here we make a choice as to how the table would operate: 
		//
		// We select a session state manager that stores only the table state 
		// in the session. This makes the session state very small, but it causes 
		// the table model to be recreated every time (note that the data and 
		// column models remain the same -- they are created only once here). 
		// The recreation of the table model means sorting of the locales 
		// according the the state will be invoked every time the page is displayed.
		//
		// Essentially in this case we sacrfice CPU load for memory, and since 
		// the amount of data (the number of locales) may be significant, 
		// this approach should behave much better when there are many users.
		m_objTableSessionStateManager =
			new SimpleTableSessionStateManager(objDataModel, objColumnModel);
	}

	/**
	 * Method getTableModel.
	 * @return ITableModel the initial Table Model to use
	 */
	public ITableModel getTableModel()
	{
		// Use the Session State Manager to create an initial table model 
		// with an initial state (no sorting, show first page)
		return m_objTableSessionStateManager.recreateTableModel(
			new SimpleTableState());
	}

	/**
	 * Method getTableSessionStateManager.
	 * @return ITableSessionStateManager the Table Session State Manager to use
	 */
	public ITableSessionStateManager getTableSessionStateManager()
	{
		return m_objTableSessionStateManager;
	}

    public boolean getCheckboxSelected() 
    {
        return getSelectedLocales().contains(getCurrentLocale());
    }
    
    public void setCheckboxSelected(boolean bSelected) 
    {
        Locale objLocale = getCurrentLocale();
        Set setSelectedLocales = getSelectedLocales();
        
        if (bSelected)
            setSelectedLocales.add(objLocale);
        else
            setSelectedLocales.remove(objLocale);
        
        // persist value
        setSelectedLocales(setSelectedLocales);
    }

    public void selectLocales(IRequestCycle objCycle)
    {
        Set setSelectedLocales = getSelectedLocales();
        Locale[] arrLocales = new Locale[setSelectedLocales.size()];
        setSelectedLocales.toArray(arrLocales);

        ILocaleSelectionListener objListener = 
            (ILocaleSelectionListener) getLocaleSelectionListenerBinding().getObject();
        objListener.localesSelected(arrLocales);

        // clear selection
        setSelectedLocales(new HashSet());
    }

    public abstract IBinding getLocaleSelectionListenerBinding();
    
    public abstract Locale getCurrentLocale();

    public abstract Set getSelectedLocales();

    public abstract void setSelectedLocales(Set set);
}
