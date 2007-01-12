package com.idega.data.query;

import com.idega.data.query.output.Outputable;
import com.idega.data.query.output.Output;
import com.idega.data.query.output.ToStringer;

/**
 * ORDER BY clause. See SelectQuery.addOrder(Order).
 * 
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 */
public class Order implements Outputable {

    public static final boolean ASCENDING = true;
    public static final boolean DESCENDING = false;

    private Column column;
    private boolean ascending;

    /**
     * @param column    Column to order by.
     * @param ascending Order.ASCENDING or Order.DESCENDING
     */
    public Order(Column column, boolean ascending) {
        this.column = column;
        this.ascending = ascending;
    }

    public Column getColumn() {
        return this.column;
    }

    public String toString() {
        return ToStringer.toString(this);
    }

    public void write(Output out) {
        this.column.write(out);
        if (!this.ascending) {
            out.print(" DESC");
        }
    }

}
