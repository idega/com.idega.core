package com.idega.data.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import com.idega.data.IDOEntity;
import com.idega.data.query.output.Output;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class InCriteria extends Criteria implements PlaceHolder {

	private Column column;
	private String value;
	private SelectQuery subSelect;
	private boolean notInCr = false;

	public InCriteria(Column column, Collection values) {
		this.column = column;
		StringBuffer v = new StringBuffer();
		Iterator i = values.iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			Object curr = i.next();
			hasNext = i.hasNext();
			if (curr instanceof Number) {
				v.append(curr);
			}
			else if (curr instanceof IDOEntity) {
				Object primaryKey = ((IDOEntity) curr).getPrimaryKey();
				if (primaryKey instanceof Number) {
					v.append(primaryKey.toString());
				}
				else {
					v.append(quote(primaryKey.toString()));
				}
			}
			else {
				v.append(quote(curr.toString()));
			}
			if (hasNext) {
				v.append(',');
			}
		}
		this.value = v.toString();
	}

	public InCriteria(Column column, Object[] values) {
		this.column = column;
		StringBuffer v = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			if (value instanceof Number) {
				v.append(value);
			}
			else if (value instanceof IDOEntity) {
				Object primaryKey = ((IDOEntity) value).getPrimaryKey();
				if (primaryKey instanceof Number) {
					v.append(primaryKey.toString());
				}
				else {
					v.append(quote(primaryKey.toString()));
				}
			}
			else {
				v.append(quote(value.toString()));
			}

			if (i < values.length - 1) {
				v.append(',');
			}
		}
		this.value = v.toString();
	}

	public InCriteria(Column column, String[] values) {
		this.column = column;
		StringBuffer v = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			v.append(quote(values[i]));
			if (i < values.length - 1) {
				v.append(',');
			}
		}
		this.value = v.toString();
	}

	public InCriteria(Column column, int[] values) {
		this.column = column;
		StringBuffer v = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			v.append(values[i]);
			if (i < values.length - 1) {
				v.append(',');
			}
		}
		this.value = v.toString();
	}

	public InCriteria(Column column, float[] values) {
		this.column = column;
		StringBuffer v = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			v.append(values[i]);
			if (i < values.length - 1) {
				v.append(',');
			}
		}
		this.value = v.toString();
	}

	public InCriteria(Column column, SelectQuery subSelect) {
		this.column = column;
		this.subSelect = subSelect;
	}

	public InCriteria(Column column, String subSelect) {
		this.column = column;
		this.value = subSelect;
	}

	public InCriteria(Table table, String columnname, Collection values) {
		this(table.getColumn(columnname), values);
	}

	public InCriteria(Table table, String columnname, float[] values) {
		this(table.getColumn(columnname), values);
	}

	public InCriteria(Table table, String columnname, int[] values) {
		this(table.getColumn(columnname), values);
	}

	public InCriteria(Table table, String columnname, SelectQuery subSelect) {
		this(table.getColumn(columnname), subSelect);
	}

	public InCriteria(Table table, String columnname, String subSelect) {
		this(table.getColumn(columnname), subSelect);
	}

	public InCriteria(Table table, String columnname, Object[] values) {
		this(table.getColumn(columnname), values);
	}

	public InCriteria(Table table, String columnname, String[] values) {
		this(table.getColumn(columnname), values);
	}

	// /////////
	public InCriteria(Column column, Collection values, boolean notIn) {
		this(column, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Column column, String[] values, boolean notIn) {
		this(column, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Column column, int[] values, boolean notIn) {
		this(column, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Column column, float[] values, boolean notIn) {
		this(column, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Column column, SelectQuery subSelect, boolean notIn) {
		this(column, subSelect);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Column column, String subSelect, boolean notIn) {
		this(column, subSelect);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Table table, String columnname, Collection values, boolean notIn) {
		this(table, columnname, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Table table, String columnname, float[] values, boolean notIn) {
		this(table, columnname, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Table table, String columnname, int[] values, boolean notIn) {
		this(table, columnname, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Table table, String columnname, SelectQuery subSelect, boolean notIn) {
		this(table, columnname, subSelect);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Table table, String columnname, String subSelect, boolean notIn) {
		this(table, columnname, subSelect);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Table table, String columnname, Object[] values, boolean notIn) {
		this(table, columnname, values);
		setAsNotInCriteria(notIn);
	}

	public InCriteria(Table table, String columnname, String[] values, boolean notIn) {
		this(table, columnname, values);
		setAsNotInCriteria(notIn);
	}

	// ////////
	public void setAsNotInCriteria(boolean value) {
		this.notInCr = value;
	}

	public void setAsNotInCriteria() {
		setAsNotInCriteria(true);
	}

	public Column getColumn() {
		return this.column;
	}

	public void write(Output out) {
		out.print(this.column);
		if (this.notInCr) {
			out.println(" NOT IN (");
		}
		else {
			out.println(" IN (");
		}
		out.indent();
		if (this.subSelect != null) {
			this.subSelect.write(out);
		}
		else {
			out.print(this.value);
		}
		out.unindent();
		out.println();
		out.print(")");
	}

	public Set getTables() {
		Set s = new HashSet();
		s.add(this.column.getTable());
		return s;
	}

	public List getValues() {
		Vector l = new Vector();
		if (this.subSelect != null) {
			l.addAll(this.subSelect.getValues());
		}
		return l;
	}
	
    public Object clone(){
		InCriteria obj = (InCriteria)super.clone();
		if(this.column!=null){
			obj.column = (Column) this.column.clone();
		}
		
		if(this.subSelect!=null){
			obj.subSelect = (SelectQuery) this.subSelect.clone();
		}
		return obj;
	}

}
