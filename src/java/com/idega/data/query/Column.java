package com.idega.data.query;

import com.idega.data.query.output.Outputable;
import com.idega.data.query.output.Output;
import com.idega.data.query.output.ToStringer;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 */
public class Column implements Outputable {

    private boolean distinct = false;
		private String name;
    private Table table;

    public Column(Table table, String name) {
        this.table = table;
        this.name = name;
    }

    public Column(String name) {
    	this.name = name;
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }
    
    public void setAsDistinct() {
    	distinct = true;
    }

    public String toString() {
        return ToStringer.toString(this);
    }

    public void write(Output out) {
    	if (distinct) {
    		out.println("DISTINCT ");
    	}
    	if (getTable() != null) {
    		out.print(getTable().getAlias()).print('.');
    	}
      out.print(getName());
    }

}
