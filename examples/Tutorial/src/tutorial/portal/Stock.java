package tutorial.portal;

import java.io.Serializable;

import net.sf.tapestry.util.prop.IPublicBean;

/**
 *  Stores information about a single Stock.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 * 
 **/

public class Stock implements Serializable, IPublicBean
{
    public String tickerId;
    public double price;
    public double change;

    public Stock()
    {
    }

    public Stock(String tickerId, double price, double change)
    {
        this.tickerId = tickerId;
        this.price = price;
        this.change = change;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("Stock[");
        buffer.append(tickerId);
        buffer.append(' ');
        buffer.append(price);
        buffer.append(' ');
        buffer.append(change);
        buffer.append(']');

        return buffer.toString();
    }
}