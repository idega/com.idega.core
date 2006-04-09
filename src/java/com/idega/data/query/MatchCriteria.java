package com.idega.data.query;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import com.idega.data.DatastoreInterface;
import com.idega.data.IDOEntity;
import com.idega.data.query.output.Output;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class MatchCriteria extends Criteria implements PlaceHolder {

	public static final String EQUALS = "=";
	public static final String GREATER = ">";
	public static final String LESS = "<";
	public static final String GREATEREQUAL = ">=";
	public static final String LESSEQUAL = "<=";
	public static final String LIKE = "LIKE";
	public static final String NOTLIKE = "NOT LIKE";
	public static final String NOTEQUALS = "<>";

	public static final String IS = "IS";
	public static final String ISNOT = "IS NOT";
	public static final String NULL = "NULL";

	private Column column;
	private String value;
	private String matchType;
	private DatastoreInterface dataStore;
	private Object placeHolderValue;

	/**
	 * Adds a null value to the given <code>Column</code> (...AND columnName IS
	 * NULL...)
	 * 
	 * @param column
	 */
	public MatchCriteria(Column column) {
		this.column = column;
		this.value = NULL;
		this.matchType = IS;
	}

	/**
	 * Adds a null value to the given <code>Column</code> (...AND columnName IS
	 * NULL...)
	 * 
	 * @param column
	 */
	public MatchCriteria(Column column, boolean notNull) {
		this.column = column;
		this.value = NULL;
		if (notNull) {
			this.matchType = ISNOT;
		}
		else {
			this.matchType = IS;
		}
	}

	public MatchCriteria(Column column, String matchType, String value) {
		this.column = column;
		this.matchType = matchType;
		if (value == null) {
			this.value = value;
			if (matchType.equals(MatchCriteria.EQUALS)) {
				this.matchType = IS;
			}
		} else if(NULL==value){
			this.value = value;
		} else {
			this.value = quote(value);
			//if(!matchType.equalsIgnoreCase(LIKE))
			    this.placeHolderValue = value;
		}
		
	}

	public MatchCriteria(Column column, String matchType, String value, boolean addQuotes) {
		this.column = column;
		if (addQuotes) {
			this.value = quote(value);
		}
		else {
			this.value = value;
		}
		this.matchType = matchType;
		if(!matchType.equalsIgnoreCase(LIKE)) {
			this.placeHolderValue = value;
		}
	}

	public MatchCriteria(Column column, String matchType, float value) {
		this.column = column;
		this.value = "" + value;
		this.matchType = matchType;
		if(!matchType.equalsIgnoreCase(LIKE)) {
			this.placeHolderValue = new Float(value);
		}
	}

	public MatchCriteria(Column column, String matchType, int value) {
		this.column = column;
		this.value = "" + value;
		this.matchType = matchType;
		if(!matchType.equalsIgnoreCase(LIKE)) {
			this.placeHolderValue = new Integer(value);
		}
	}

	public MatchCriteria(Column column, String matchType, boolean value) {
		this.column = column;
		if (value) {
			this.value = quote("Y");
			if(!matchType.equalsIgnoreCase(LIKE)) {
				this.placeHolderValue = Boolean.TRUE;
			}
		}
		else {
			this.value = quote("N");
			if(!matchType.equalsIgnoreCase(LIKE)) {
				this.placeHolderValue = Boolean.FALSE;
			}
		}
		this.matchType = matchType;
		
	}

	public MatchCriteria(Column column, String matchType, Object value) {
		this.column = column;
		this.value = value.toString();
		this.matchType = matchType;
		if(!matchType.equalsIgnoreCase(LIKE)) {
			this.placeHolderValue = value;
		}
	}

	public MatchCriteria(Column column, String matchType, Date value) {
		this.column = column;
		this.value = getDatastore().format(value);
		this.matchType = matchType;
		if(!matchType.equalsIgnoreCase(LIKE)) {
			this.placeHolderValue = value;
		}
		
	}

	public MatchCriteria(Column column, String matchType, Timestamp value) {
		this.column = column;
		this.value = getDatastore().format(value);
		this.matchType = matchType;
		if(!matchType.equalsIgnoreCase(LIKE)) {
			this.placeHolderValue = value;
		}
	}

	public MatchCriteria(Column column, String matchType, IDOEntity value) {
		this.column = column;
		Object pk = value.getPrimaryKey();
		if (pk instanceof Number) {
			this.value = pk.toString();
		}
		else {
			this.value = quote(pk.toString());
		}
		this.matchType = matchType;
	}

	public MatchCriteria(Table table, String columnname) {
		this(table.getColumn(columnname));
	}

	public MatchCriteria(Table table, String columnname, String matchType, boolean value) {
		this(table.getColumn(columnname), matchType, value);
	}

	public MatchCriteria(Table table, String columnname, String matchType, int value) {
		this(table.getColumn(columnname), matchType, value);
	}

	public MatchCriteria(Table table, String columnname, String matchType, float value) {
		this(table.getColumn(columnname), matchType, value);
	}

	public MatchCriteria(Table table, String columnname, String matchType, String value, boolean addQuotes) {
		this(table.getColumn(columnname), matchType, value, addQuotes);
	}

	public MatchCriteria(Table table, String columnname, String matchType, String value) {
		this(table.getColumn(columnname), matchType, value);
	}
	
	public MatchCriteria(Table table, String columnname, String matchType, Object value) {
		this(table.getColumn(columnname), matchType, value);
	}

	public MatchCriteria(Table table, String columnname, String matchType, Date value) {
		this(table.getColumn(columnname), matchType, value);
	}

	public MatchCriteria(Table table, String columnname, String matchType, Timestamp value) {
		this(table.getColumn(columnname), matchType, value);
	}

	public MatchCriteria(Table table, String columnname, String matchType, IDOEntity value) {
		this(table.getColumn(columnname), matchType, value);
	}

	public Column getColumn() {
		return this.column;
	}

	public void write(Output out) {
	    if(out.isFlagged() && getPlaceValue()!=null) {
				out.print(this.column).print(' ').print(this.matchType).print(' ').print("?");
			}
			else {
				out.print(this.column).print(' ').print(this.matchType).print(' ').print(this.value);
			}
	}

	protected DatastoreInterface getDatastore() {
		if (this.dataStore == null) {
			this.dataStore = DatastoreInterface.getInstance();
		}
		return this.dataStore;
	}
	
    public Set getTables(){
		Set s = new HashSet();
		s.add(this.column.getTable());
		return s; 
    }

    /* (non-Javadoc)
     * @see com.idega.data.query.PlaceHolder#getPlaceValue()
     */
    public Object getPlaceValue() {
        return this.placeHolderValue;
    }
    
    public List getValues(){
        	Vector v = new Vector(1);
        	if(this.placeHolderValue!=null) {
						v.add(this.placeHolderValue);
					}
        	return v;
    }
    
    public Object clone(){
		MatchCriteria obj = (MatchCriteria)super.clone();
		if(this.column!=null){
			obj.column = (Column) this.column.clone();
		}
		
		return obj;
	}
	public String getMatchType() {
		return this.matchType;
	}
	public void setMatchType(String matchType) {
		this.matchType = matchType;
		if (this.value == null) {
			if (matchType.equals(MatchCriteria.EQUALS)) {
				this.matchType = IS;
			}
		}
	}
}