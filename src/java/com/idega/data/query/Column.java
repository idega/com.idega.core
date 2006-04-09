package com.idega.data.query;

import com.idega.data.query.output.Outputable;
import com.idega.data.query.output.Output;
import com.idega.data.query.output.ToStringer;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class Column implements Outputable, Cloneable {

	private boolean distinct = false;
	private boolean count = false;
	private String countName;
	private String name;
	private Table table;
	private String prefix;
	private String postfix;

	public Column(Table table, String name) {
		this.table = table;
		this.name = name;
	}

	public Column(String name) {
		this.name = name;
	}

	public Table getTable() {
		return this.table;
	}

	public String getName() {
		return this.name;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getPostfix() {
		return this.postfix;
	}

	public void setAsDistinct() {
		this.distinct = true;
	}
	
	public void setAsCount() {
		this.count = true;
	}
	public void setAsCount(String countName) {
		setAsCount();
		this.countName = countName;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public String toString() {
		return ToStringer.toString(this);
	}

	public void write(Output out) {
		if (getPrefix() != null) {
			out.print(getPrefix());
		}
		if (this.count) {
			out.println("COUNT(");
		}
		if (this.distinct) {
			out.println("DISTINCT ");
		}
		if (getTable() != null) {
			out.print(getTable().getAlias()).print('.');
		}
		out.print(getName());
		if (getPostfix() != null) {
			out.print(getPostfix());
		}
		if (this.count) {
			out.println(")");
			if (this.countName != null) {
				out.println(" AS "+this.countName);
			}
		}
	}
	
    public Object clone() {
		Column obj = null;
		try {
			obj = (Column)super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

}