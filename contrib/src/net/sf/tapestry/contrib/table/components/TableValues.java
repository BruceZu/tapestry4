package net.sf.tapestry.contrib.table.components;

import java.util.Iterator;

import net.sf.tapestry.IBinding;
import net.sf.tapestry.IRender;
import net.sf.tapestry.RequestCycleException;
import net.sf.tapestry.contrib.table.model.ITableColumn;
import net.sf.tapestry.contrib.table.model.ITableColumnModel;

/**
 * A low level Table component that generates the columns in the current row in the table.
 * This component must be wrapped by {@link net.sf.tapestry.contrib.table.components.TableRows}.
 * 
 * <p>
 * The component iterates over the columns in the table and 
 * automatically renders the column values for the current table row. 
 * The columns are wrapped in 'td' tags by default. <br>
 * The column values are rendered using the renderer returned by the 
 * getValueRenderer() method in {@link net.sf.tapestry.contrib.table.model.ITableColumn}.
 * 
 * <p>
 * <table border=1 align="center">
 * <tr>
 *    <th>Parameter</th>
 *    <th>Type</th>
 *    <th>Direction </th>
 *    <th>Required</th>
 *    <th>Default</th>
 *    <th>Description</th>
 * </tr>
 *
 * <tr>
 *  <td>element</td>
 *  <td>String</td>
 *  <td>in</td>
 *  <td>no</td>
 *  <td>td</td>
 *  <td align="left">The tag to use to wrap the column values in.</td> 
 * </tr>
 *
 * </table> 
 * 
 * @author mindbridge
 * @version $Id$
 *
 */
public class TableValues extends AbstractTableRowComponent
{
    // Bindings (custom)
    private IBinding m_objElementBinding = null;

	// Transient
	private ITableColumn m_objTableColumn;

	public Iterator getTableColumnIterator() throws RequestCycleException
	{
		ITableColumnModel objColumnModel =
			getTableModelSource().getTableModel().getColumnModel();
		return objColumnModel.getColumns();
	}

	/**
	 * Returns the tableColumn.
	 * @return ITableColumn
	 */
	public ITableColumn getTableColumn()
	{
		return m_objTableColumn;
	}

	/**
	 * Sets the tableColumn.
	 * @param tableColumn The tableColumn to set
	 */
	public void setTableColumn(ITableColumn tableColumn)
	{
		m_objTableColumn = tableColumn;
	}

	public IRender getTableValueRenderer() throws RequestCycleException
	{
		Object objRow = getTableRowSource().getTableRow();
		return getTableColumn().getValueRenderer(
			getPage().getRequestCycle(),
			getTableModelSource(),
			objRow);
	}

    /**
     * Returns the elementBinding.
     * @return IBinding
     */
    public IBinding getElementBinding()
    {
        return m_objElementBinding;
    }

    /**
     * Sets the elementBinding.
     * @param elementBinding The elementBinding to set
     */
    public void setElementBinding(IBinding elementBinding)
    {
        m_objElementBinding = elementBinding;
    }

    /**
     * Returns the element.
     * @return String
     */
    public String getElement()
    {
        IBinding objElementBinding = getElementBinding();
        if (objElementBinding == null || objElementBinding.getObject() == null)
            return "td";
        return objElementBinding.getString();
    }
}