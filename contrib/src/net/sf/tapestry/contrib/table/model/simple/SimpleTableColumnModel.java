package net.sf.tapestry.contrib.table.model.simple;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.tapestry.contrib.table.model.ITableColumn;
import net.sf.tapestry.contrib.table.model.ITableColumnModel;
import net.sf.tapestry.contrib.table.model.common.ArrayIterator;

/**
 * A minimal implementation of the ITableColumnModel interface
 * 
 * @version $Id$
 * @author mindbridge
 */
public class SimpleTableColumnModel implements ITableColumnModel, Serializable
{

    private ITableColumn[] m_arrColumns;
    private Map m_mapColumns;

    public SimpleTableColumnModel(ITableColumn[] arrColumns)
    {
        m_arrColumns = arrColumns;

        m_mapColumns = new HashMap();
        for (int i = 0; i < m_arrColumns.length; i++)
            m_mapColumns.put(m_arrColumns[i].getColumnName(), m_arrColumns[i]);
    }

    public SimpleTableColumnModel(List arrColumns)
    {
        this((ITableColumn[]) arrColumns.toArray(new ITableColumn[arrColumns.size()]));
    }

    public int getColumnCount()
    {
        return m_arrColumns.length;
    }

    public ITableColumn getColumn(int nColumn)
    {
        if (nColumn < 0 || nColumn >= m_arrColumns.length)
        {
            // error message
            return null;
        }
        return m_arrColumns[nColumn];
    }

    public ITableColumn getColumn(String strColumn)
    {
        return (ITableColumn) m_mapColumns.get(strColumn);
    }

    public Iterator getColumns()
    {
        return new ArrayIterator(m_arrColumns);
    }

}