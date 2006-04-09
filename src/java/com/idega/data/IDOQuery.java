package com.idega.data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.idega.data.query.SelectQuery;
import com.idega.util.IWTimestamp;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class IDOQuery implements Cloneable {

	StringBuffer _buffer;

	private static final String SELECT_ALL_FROM = "SELECT * FROM ";
	private static final String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM ";
	private static final String SELECT_COUNT = "SELECT COUNT(*) ";
	private static final String SELECT = "SELECT ";
	private static final String FROM = " FROM ";
	private static final String SUM = "SUM";
	private static final String COUNT = "COUNT";
	private static final String STAR = " * ";
	private static final String DISTINCT = " DISTINCT ";
	private static final String ORDER_BY = " ORDER BY ";
	private static final String GROUP_BY = " GROUP BY ";
	private static final String HAVING = " HAVING ";
	private static final String WHERE = " WHERE ";
	private static final String LIKE = " LIKE ";
	private static final String NOT_LIKE = " NOT LIKE ";
	private static final String EQUAL_SIGN = "=";
	private static final String NOT_EQUAL_SIGN = "!=";
	private static final String WHITE_SPACE = " ";
	private static final String QUOTATION_MARK = "'";
	private static final String DOUBLE_QUOTATION_MARK = "\"";

	private static final String LESS_THAN_SIGN = "<";
	private static final String GREATER_THAN_SIGN = ">";
	private static final String LESS_THAN_OR_EQUAL_SIGN = "<=";
	private static final String GREATER_THAN_OR_EQUAL_SIGN = ">=";
	//parenthesis
	private static final String PARENTHESIS_LEFT = "(";
	private static final String PARENTHESIS_RIGHT = ")";
	private static final String DELETE = "DELETE ";
	private static final String UPDATE = "UPDATE ";
	private static final String SET = "SET ";
	private static final String IN = " IN ";
	private static final String NOT_IN = " NOT IN ";
	private static final String COMMA = ",";
	private static final String AND = " AND ";
	private static final String OR = " OR ";
	private static final String IS_NULL = " IS NULL ";
	private static final String IS_NOT_NULL = " IS NOT NULL ";
	private static final String DESCENDING = " DESC ";
	private static final String TRUE = "Y";
	private static final String FALSE = "N";
	private static final String QUESTIONMARK = "?";

	private DatastoreInterface dataStore = null;
	private Vector objectValues = new Vector();

	public static IDOQuery getStaticInstance() {
		IDOQuery query = new IDOQuery();
		return query;
	}
	
	/**
	 * @see com.idega.data.GenericEntity.idoQuery()
	 */
	protected IDOQuery() {
		this._buffer = new StringBuffer();
	}
	

	protected IDOQuery(int length) {
		this._buffer = new StringBuffer(length);
	}

	protected IDOQuery(String str) {
		this._buffer = new StringBuffer(str);
	}

	/**
	 * Appends an SQL counterpart for a Boolean value (declared by Boolean.class in the BMPBean).
	 */
	public IDOQuery append(boolean b) {
		if (b) {
			return this.appendWithinSingleQuotes(TRUE);
		}
		else {
			return this.appendWithinSingleQuotes(FALSE);
		}
	}
	/**
	 * @see java.lang.StringBuffer#append(char)
	 */
	public IDOQuery append(char c) {
		this._buffer.append(c);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(char[],int,int)
	 */
	public IDOQuery append(char[] str, int offset, int len) {
		this._buffer.append(str, offset, len);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(char[])
	 */
	public IDOQuery append(char[] str) {
		this._buffer.append(str);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public IDOQuery append(double d) {
		this._buffer.append(d);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(float)
	 */
	public IDOQuery append(float f) {
		this._buffer.append(f);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(int)
	 */
	public IDOQuery append(int i) {
		this._buffer.append(i);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(long)
	 */
	public IDOQuery append(long l) {
		this._buffer.append(l);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(java.lang.Object)
	 */
	public IDOQuery append(Object obj) {
		this._buffer.append(obj);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#append(java.lang.String)
	 */
	public IDOQuery append(String str) {
		this._buffer.append(str);
		return this;
	}
	
	/**
	 * Appends quoted string like 'anObject'
	 * @param string
	 * @return 
	 */
	public IDOQuery appendQuoted(Object anObject) {
		this._buffer.append(QUOTATION_MARK);
		this._buffer.append(anObject);
		this._buffer.append(QUOTATION_MARK);
		return this;
	}
	
	public IDOQuery append(IDOEntityField field){
		return this.append(field.getSQLFieldName());
	}
	

	public IDOQuery append(Date date) {
		//IWTimestamp stamp = new IWTimestamp(date);
		//this.appendWithinSingleQuotes(stamp.toSQLString());
		this.append(getDatastore().format(date));
		return this;
	}

	public IDOQuery append(Timestamp timestamp) {
		//IWTimestamp stamp = new IWTimestamp(timestamp);
		//this.appendWithinSingleQuotes(stamp.toSQLString());
		this.append(getDatastore().format(timestamp));
		return this;
	}
	
	public IDOQuery append(IWTimestamp timestamp) {
		return this.append(timestamp.getTimestamp());
	}

	public IDOQuery append(IDOEntity entity) {
		Object pk = entity.getPrimaryKey();
		if (pk instanceof Integer) {
			return this.append(pk);
		}
		else {
			return this.appendWithinSingleQuotes(pk);
		}
	}

	/**
	 * @see java.lang.StringBuffer#capacity()
	 */
	public int capacity() {
		return this._buffer.capacity();
	}
	/**
	 * @see java.lang.StringBuffer#charAt(int)
	 */
	public char charAt(int index) {
		return this._buffer.charAt(index);
	}
	/**
	 * @see java.lang.StringBuffer#delete(int,int){
	 */
	public IDOQuery delete(int start, int end) {
		this._buffer.delete(start, end);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#deleteCharAt(int)
	 */
	public IDOQuery deleteCharAt(int index) {
		this._buffer.deleteCharAt(index);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#ensureCapacity(int)
	 */
	public void ensureCapacity(int minimumCapacity) {
		this._buffer.ensureCapacity(minimumCapacity);
	}

	public boolean equals(Object obj) {
		return this._buffer.equals(obj);
	}
	/**
	 * @see java.lang.StringBuffer#getChars(int,int,char[],int)
	 */
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		this._buffer.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	public int hashCode() {
		return this._buffer.hashCode();
	}

	/**
	 * @see java.lang.StringBuffer#insert(int,boolean)
	 */
	public IDOQuery insert(int offset, boolean b) {
		this._buffer.insert(offset, b);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,char)
	 */
	public IDOQuery insert(int offset, char c) {
		this._buffer.insert(offset, c);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,char[],int,int)
	 */
	public IDOQuery insert(int index, char[] str, int offset, int len) {
		this._buffer.insert(index, str, offset, len);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,char[])
	 */
	public IDOQuery insert(int offset, char[] str) {
		this._buffer.insert(offset, str);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,java.lang.String)
	 */
	public IDOQuery insert(int offset, double d) {
		this._buffer.insert(offset, d);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,float)
	 */
	public IDOQuery insert(int offset, float f) {
		this._buffer.insert(offset, f);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,int)
	 */
	public IDOQuery insert(int offset, int i) {
		this._buffer.insert(offset, i);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,long)
	 */
	public IDOQuery insert(int offset, long l) {
		this._buffer.insert(offset, l);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,java.lang.Object)
	 */
	public IDOQuery insert(int offset, Object obj) {
		this._buffer.insert(offset, obj);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#insert(int,java.lang.String)
	 */
	public IDOQuery insert(int offset, String str) {
		this._buffer.insert(offset, str);
		return this;
	}

	/**
	 * @see java.lang.StringBuffer#replace(int,int,String)
	 */
	public IDOQuery replace(int start, int end, String str) {
		this._buffer.replace(start, end, str);
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#reverse()
	 */
	public IDOQuery reverse() {
		this._buffer.reverse();
		return this;
	}
	/**
	 * @see java.lang.StringBuffer#setCharAt(int,char)
	 */
	public void setCharAt(int index, char ch) {
		this._buffer.setCharAt(index, ch);
	}
	/**
	 * @see java.lang.StringBuffer#setLength(int)
	 */
	public void setLength(int newLength) {
		this._buffer.setLength(newLength);
	}
	/**
	 * @see java.lang.StringBuffer#substring(int,int)
	 */
	public String substring(int start, int end) {
		return this._buffer.substring(start, end);
	}
	/**
	 * @see java.lang.StringBuffer#substring(int)
	 */
	public String substring(int start) {
		return this._buffer.substring(start);
	}
	/**
	 * @see java.lang.StringBuffer#toString()
	 */
	public String toString() {
		return this._buffer.toString();
	}

	public IDOQuery appendLeftParenthesis() {
		return this.append(PARENTHESIS_LEFT);
	}

	public IDOQuery appendRightParenthesis() {
		return this.append(PARENTHESIS_RIGHT);
	}

	public IDOQuery appendCommaDelimited(String[] str) {
		for (int i = 0; i < str.length; i++) {
			if (i != 0) {
				this.append(COMMA);
			}
			this.append(str[i]);
		}
		return this;
	}
	public IDOQuery appendCommaDelimited(Collection collection) {
		Iterator iter = collection.iterator();
		boolean first = true;
		while (iter.hasNext()) {
			Object item = iter.next();
			if (!first) {
				this.append(COMMA);
			}

			if (item instanceof IDOEntity) {
				this.append(((IDOEntity)item).getPrimaryKey());
			} else {
				this.append(item);
			}

			first = false;
		}
		return this;
	}

	public IDOQuery appendCommaDelimitedWithinSingleQuotes(String[] str) {
		for (int i = 0; i < str.length; i++) {
			if (i != 0) {
				this.append(COMMA);
			}
			this.appendWithinSingleQuotes(str[i]);
		}
		return this;
	}
	public IDOQuery appendCommaDelimitedWithinSingleQuotes(Collection collection) {
		Iterator iter = collection.iterator();
		boolean first = true;
		while (iter.hasNext()) {
			Object item = iter.next();
			if (!first) {
				this.append(COMMA);
			}

			if (item instanceof IDOEntity) {
				this.appendWithinSingleQuotes(((IDOEntity)item).getPrimaryKey());
			} else {
				this.appendWithinSingleQuotes(item);
			}

			first = false;
		}
		return this;
	}
	public IDOQuery appendCommaDelimitedWithinDoubleQuotes(String[] str) {
		for (int i = 0; i < str.length; i++) {
			if (i != 0) {
				this.append(COMMA);
			}
			this.appendWithinDoubleQuotes(str[i]);
		}
		return this;
	}
	public IDOQuery appendCommaDelimitedWithinDoubleQuotes(Collection collection) {
		Iterator iter = collection.iterator();
		boolean first = true;
		while (iter.hasNext()) {
			Object item = iter.next();
			if (!first) {
				this.append(COMMA);
			}

			if (item instanceof IDOEntity) {
				this.appendWithinDoubleQuotes(((IDOEntity)item).getPrimaryKey());
			} else {
				this.appendWithinSingleQuotes(item);
			}

			first = false;
		}
		return this;
	}

	public IDOQuery appendWithinDoubleQuotes(String str) {
		this.appendDoubleQuote();
		this.append(str);
		this.appendDoubleQuote();
		return this;
	}

	public IDOQuery appendWithinSingleQuotes(String str) {
		this.appendSingleQuote();
		this.append(str);
		this.appendSingleQuote();
		return this;
	}

	public IDOQuery appendWithinDoubleQuotes(Object obj) {
		this.appendDoubleQuote();
		this.append(obj);
		this.appendDoubleQuote();
		return this;
	}

	public IDOQuery appendWithinSingleQuotes(Object obj) {
		this.appendSingleQuote();
		this.append(obj);
		this.appendSingleQuote();
		return this;
	}

	public IDOQuery appendWithinParentheses(String str) {
		this.append(PARENTHESIS_LEFT);
		this.append(str);
		this.append(PARENTHESIS_RIGHT);
		return this;
	}
	public IDOQuery appendWithinParentheses(IDOQuery query) {
		return appendWithinParentheses((Object)query);
	}
	public IDOQuery appendWithinParentheses(Object obj) {
		this.append(PARENTHESIS_LEFT);
		this.append(obj);
		this.append(PARENTHESIS_RIGHT);
		return this;
	}

	public IDOQuery appendSelectAllFrom() {
		return this.append(SELECT_ALL_FROM);
	}
	public IDOQuery appendSelectAllFrom(IDOEntity entity) {
		//return this.appendSelectAllFrom(((IDOLegacyEntity)entity).getTableName());
		return this.appendSelectAllFrom(entity.getEntityDefinition().getSQLTableName());
	}
	public IDOQuery appendSelectAllFrom(String entityName) {
		this.append(SELECT_ALL_FROM);
		this.append(entityName);
		return this;
	}

	public IDOQuery appendSelectIDColumnFrom(IDOEntity entity) throws IDOCompositePrimaryKeyException {
		this.appendSelect();
		this.append(entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName());
		this.appendFrom();
		this.append(entity.getEntityDefinition().getSQLTableName());

		return this;

		//return this.appendSelectAllFrom(((IDOLegacyEntity)entity).getTableName());
		//return this.appendSelectAllFrom(entity.getEntityDefinition().getSQLTableName());
	}

	public IDOQuery appendSelectCountFrom() {
		return this.append(SELECT_COUNT_FROM);
	}
	
	public IDOQuery appendSelectCount() {
		return this.append(SELECT_COUNT);
	}
	
	public IDOQuery appendSelectCountFrom(IDOEntity entity) {
		return this.appendSelectCountFrom(entity.getEntityDefinition().getSQLTableName());
		//return this.appendSelectCountFrom(entity.getEntityDefinition().getSQLTableName());

	}
	public IDOQuery appendSelectCountFrom(String entityName) {
		this.append(SELECT_COUNT_FROM);
		this.append(entityName);
		return this;
	}

	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn) {
	    return appendSelectCountIDFrom(entityName, idColumn, null);
	}
	
	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn, String tableAlias) {
	    return appendSelectCountIDFrom(entityName, idColumn, tableAlias, false);
	}

	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn, boolean distinct) {
	    return appendSelectCountIDFrom(entityName, idColumn, null, distinct);
	}

	public IDOQuery appendSelectCountIDFrom(String entityName, String idColumn, String tableAlias, boolean distinct) {
		this.appendSelect().appendCount(idColumn, tableAlias, distinct).appendFrom().append(entityName);
		if (tableAlias != null) {
		    this.append(" ").append(tableAlias);
		}
		return this;
	}

	public IDOQuery appendSelect() {
		return this.append(SELECT);
	}

	public IDOQuery appendSum(String columnName) {
		return this.append(SUM).appendLeftParenthesis().append(columnName).appendRightParenthesis();
	}

	public IDOQuery appendSelectSumFrom(String columnName, String entityName) {
		return this.appendSelect().appendSum(columnName).appendFrom().append(entityName);
	}
	
	public IDOQuery appendSelectSumFrom(String columnName, IDOEntity entity) {
		return this.appendSelectSumFrom(columnName, entity.getEntityDefinition().getSQLTableName());
	}

	/**
	 * Create Query like "UPDATE tableName SET "
	 * @param entity
	 * @return
	 */
	public IDOQuery appendUpdateSet(IDOEntity entity) {
		String tableName = entity.getEntityDefinition().getSQLTableName();
		return append(UPDATE).append(tableName).append(WHITE_SPACE).append(SET);
	}
	
	public IDOQuery appendFrom() {
			return this.append(FROM);
	}
	
	public IDOQuery appendFrom(String tableName) {
		return this.append(FROM).append(" ").append(tableName);
}

	public IDOQuery appendFrom(String[] tableNames, String[] prmNames) {
		if(tableNames != null){
			this.append(FROM);
			for (int i = 0; i < tableNames.length; i++) {
				if(i>0){
					this.append(", ");
				}
				this.append(tableNames[i]);
				this.append(" ");
				this.append(prmNames[i]);
			}	
		} else {
			this.append(FROM);
		}
		return this;
	}
	
	public IDOQuery appendWhiteSpace() {
		return this.append(WHITE_SPACE);
	}
	
	public IDOQuery appendDelete() {
		return this.append(DELETE);
	}

	public IDOQuery appendStar() {
		return this.append(STAR);
	}
	
	public IDOQuery appendDistinct() {
		return this.append(DISTINCT);
	}

	public IDOQuery appendOrderBy() {
		return this.append(ORDER_BY);
	}

	public IDOQuery appendDescending() {
		return this.append(DESCENDING);
	}

	public IDOQuery appendOrderBy(String columnName) {
		this.append(ORDER_BY);
		this.append(columnName);
		return this;
	}
	
	public IDOQuery appendGroupBy(String columnName) {
		this.append(GROUP_BY);
		this.append(columnName);
		return this;
	}
	
	public IDOQuery appendHaving(){
		this.append(HAVING);
		return this;
	}
	
	public IDOQuery appendCount(String columnName){
	    return appendCount(columnName, null);
	}

	public IDOQuery appendCount(String columnName, String tableAlias){
	    return appendCount(columnName, tableAlias, false);
	}

	public IDOQuery appendCount(String columnName, boolean distinct){
	    return appendCount(columnName, null, distinct);
	}

	public IDOQuery appendCount(String columnName, String tableAlias, boolean distinct){
		this.append(COUNT);
		this.appendLeftParenthesis();
		if (distinct) {
		    this.append(DISTINCT);
		}
		if (tableAlias != null) {
		    this.append(tableAlias).append(".");
		}
		this.append(columnName);
		this.appendRightParenthesis();
		return this;
	}

	public IDOQuery appendOrderBy(String[] columnNames) {
		this.append(ORDER_BY);
		this.append(IDOUtil.getInstance().convertArrayToCommaseparatedString(columnNames));
		return this;
	}

	public IDOQuery appendOrderByDescending(String columnName) {
		this.appendOrderBy(columnName);
		this.appendDescending();
		return this;
	}

	public IDOQuery appendOrderByDescending(String[] columnNames) {
		this.appendOrderBy(columnNames);
		this.appendDescending();
		return this;
	}

	public IDOQuery appendWhere() {
		return this.append(WHERE);
	}

	public IDOQuery appendWhere(String str) {
		this.append(WHERE);
		this.append(str);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, String columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendWhereEquals(String columnName, boolean columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) with single quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEqualsWithSingleQuotes(String columnName, String columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, Object columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, IDOEntity entity) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, int columnValue) {
		return appendWhereEquals(columnName, Integer.toString(columnValue));
	}
	

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks
	 * @param columnName the name of the field
	 * @param columnValue the value
	 * @return IDOQuery this Object
	 */
	public IDOQuery appendWhereEquals(String columnName, Integer columnValue) {
		return appendWhereEquals(columnName, columnValue.toString());
	}

	public IDOQuery appendWhereEqualsQuoted(String columnName, String columnValue) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendWhereEquals(String columnName, Date date) {
		appendWhere(columnName);
		this.appendEqualSign();
		this.append(date);
		return this;
	}

	public IDOQuery appendEqualsQuoted(String columnName, String columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, String columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, boolean columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, int columnValue) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendEquals(String columnName, IDOEntity entity) {
		this.append(WHITE_SPACE);
		this.append(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	public IDOQuery appendAndEqualsQuoted(String columnName, String columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, Date date) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(date);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, int columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}
	
	public IDOQuery appendAndEquals(String columnName, Integer columnValue) {
		appendAndEquals(columnName,columnValue.intValue());
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, String columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, boolean columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}
	
	public IDOQuery appendAndEqualsTrue(String columnName) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(true);
		return this;
	}	
	
	/** Handles all values different from true ('Y') as false. 
	 */

	public IDOQuery appendAndNotEqualsTrue(String columnName) {
		appendAnd();
		append(columnName);
		this.appendNOTEqual();
		this.append(true);
		return this;
	}		

	public IDOQuery appendAndEquals(String columnName, Object columnValue) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendAndEquals(String columnName, IDOEntity entity) {
		appendAnd();
		append(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	public IDOQuery appendOrEqualsQuoted(String columnName, String columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.appendWithinSingleQuotes(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, Date date) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(date);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, int columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, boolean columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, String columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, Object columnValue) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(columnValue);
		return this;
	}

	public IDOQuery appendOrEquals(String columnName, IDOEntity entity) {
		appendOr();
		append(columnName);
		this.appendEqualSign();
		this.append(entity);
		return this;
	}

	public IDOQuery appendIn() {
		return this.append(IN);
	}
	public IDOQuery appendIn(String str) {
		this.append(IN);
		this.appendWithinParentheses(str);
		return this;
	}
	public IDOQuery appendIn(IDOQuery query) {
		this.append(IN);
		this.appendWithinParentheses(query);
		return this;
	}
	public IDOQuery appendIn(SelectQuery query) {
		this.append(IN);
		this.appendWithinParentheses(query.toString());
		return this;
	}

	public IDOQuery appendNotIn() {
		return this.append(NOT_IN);
	}
	/**
	 * Appends a not in clause within parantheses.
	 * @param str the String to be inside the not in clause.
	 * @return IDOQuery Returns this
	 */
	public IDOQuery appendNotIn(String str) {
		this.append(NOT_IN);
		this.appendWithinParentheses(str);
		return this;
	}
	public IDOQuery appendNotIn(IDOQuery query) {
		this.append(NOT_IN);
		this.appendWithinParentheses(query);
		return this;
	}

	public IDOQuery appendLike() {
		return this.append(LIKE);
	}

	public IDOQuery appendEqualSign() {
		return this.append(EQUAL_SIGN);
	}

	public IDOQuery appendLessThanSign() {
		return this.append(LESS_THAN_SIGN);
	}

	public IDOQuery appendGreaterThanSign() {
		return this.append(GREATER_THAN_SIGN);
	}

	public IDOQuery appendGreaterThanOrEqualsSign() {
		this.appendGreaterThanSign();
		return this.appendEqualSign();
	}

	public IDOQuery appendLessThanOrEqualsSign() {
		this.appendLessThanSign();
		return this.appendEqualSign();
	}

	public IDOQuery appendNOTEqual() {
		return this.append(NOT_EQUAL_SIGN);
	}
	
	
	public IDOQuery appendNOTLike() {
		return this.append(NOT_LIKE);
	}

	public IDOQuery appendSingleQuote() {
		return this.append(QUOTATION_MARK);
	}

	public IDOQuery appendDoubleQuote() {
		return this.append(DOUBLE_QUOTATION_MARK);
	}

	public IDOQuery appendAnd() {
		return this.append(AND);
	}

	public IDOQuery appendOr() {
		return this.append(OR);
	}

	public IDOQuery appendInArray(String[] array) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array));
	}

	public IDOQuery appendInArrayWithSingleQuotes(String[] array) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array, true));
	}
	
	public IDOQuery appendInCollection(Collection coll) {
		return this.appendIn().append(PARENTHESIS_LEFT).appendCommaDelimited(coll).append(PARENTHESIS_RIGHT);
		//return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertListToCommaseparatedString(coll));
	}
	
	public IDOQuery appendInCollectionWithSingleQuotes(Collection coll) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertListToCommaseparatedString(coll,true));
	}
	
	public IDOQuery appendNotInCollectionWithSingleQuotes(Collection coll) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertListToCommaseparatedString(coll,true));
	}
	
	public IDOQuery appendInForStringCollectionWithSingleQuotes(Collection coll) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfStringsToCommaseparatedString(coll));
	}
	
	public IDOQuery appendInForIntegerCollectionWithSingleQuotes(Collection coll) {
		return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfIntegersToCommaseparatedString(coll));
	}
	
	public IDOQuery appendNotInForStringCollectionWithSingleQuotes(Collection coll) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfStringsToCommaseparatedString(coll));
	}
	
	public IDOQuery appendNotInForIntegerCollectionWithSingleQuotes(Collection coll) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertCollectionOfIntegersToCommaseparatedString(coll));
	}


	public IDOQuery appendNotInArray(String[] array) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array));
	}

	public IDOQuery appendNotInArrayWithSingleQuotes(String[] array) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array, true));
	}

	public IDOQuery appendWhereIsNull(String columnName) {
		appendWhere(columnName);
		this.append(IS_NULL);
		return this;
	}

	public IDOQuery appendAndIsNull(String columnName) {
		this.appendAnd();
		this.append(columnName);
		this.append(IS_NULL);
		return this;
	}

	public IDOQuery appendOrIsNull(String columnName) {
		this.appendOr();
		this.append(columnName);
		this.append(IS_NULL);
		return this;
	}
	
	public IDOQuery appendIsNull() {
		this.append(IS_NULL);
		return this;
	}
	
	public IDOQuery appendAndIsNotNull(String columnName) {
		this.appendAnd();
		this.append(columnName);
		this.append(IS_NOT_NULL);
		return this;
	}

	public IDOQuery appendOrIsNotNull(String columnName) {
		this.appendOr();
		this.append(columnName);
		this.append(IS_NOT_NULL);
		return this;
	}
	
	public IDOQuery appendIsNotNull() {
		this.append(IS_NOT_NULL);
		return this;
	}
	
	
	/**
	 * Appends a condition where date column specified is between the provided dates
	 * exluding the provided dates (see appendWithinDates for included dates)
	 * @param dateColumnName
	 * @param fromDate
	 * @param toDate
	 * @return the query itself
	 */
	public IDOQuery appendBetweenDates(String dateColumnName,Date fromDate,Date toDate){
		this.append(dateColumnName).appendGreaterThanSign().append(fromDate);
		this.appendAnd().append(dateColumnName).appendLessThanSign().append(toDate);
		return this;
	}
	
	/**
	 * Appends a condition where timestamp column specified is within the provided timestamps
	 * including the provided timestamps (see appendWithinStamps for included dates)
	 * @param dateColumnName
	 * @param fromStamp
	 * @param toStamp
	 * @return
	 */
	public IDOQuery appendBetweenStamps(String dateColumnName,Timestamp fromStamp,Timestamp toStamp){
		this.append(dateColumnName).appendGreaterThanSign().append(fromStamp);
		this.appendAnd().append(dateColumnName).appendLessThanSign().append(toStamp);
		return this;
	}
	/**
	 * Appends a condition where date column specified is within the provided dates
	 * including the provided dates (see appendBetweenDates for excluded dates)
	 * @param dateColumnName
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public IDOQuery appendWithinDates(String dateColumnName,Date fromDate,Date toDate){
		this.append(dateColumnName).appendGreaterThanOrEqualsSign().append(fromDate);
		this.appendAnd().append(dateColumnName).appendLessThanOrEqualsSign().append(toDate);
		return this;
	}
	/**
	 * Appends a condition where timestamp column specified is within the provided timestamps
	 * including the provided timestamps (see appendBetweenDates for excluded timestamp)
	 * @param dateColumnName
	 * @param fromStamp
	 * @param toStamp
	 * @return
	 */
	public IDOQuery appendWithinStamps(String dateColumnName,Timestamp fromStamp,Timestamp toStamp){
		this.append(dateColumnName).appendGreaterThanOrEqualsSign().append(fromStamp);
		this.appendAnd().append(dateColumnName).appendLessThanOrEqualsSign().append(toStamp);
		return this;
	}
	public IDOQuery appendWhereEqualsTimestamp(String dateColumnName, Timestamp stamp) {
		appendWhere(dateColumnName);
		this.appendEqualSign();
		this.append(stamp);
		return this;
	}
	
	/**
	 * Append condition for periods columns overlapping provided start and end date
	 * where the following criterions on a entry period are used:
	 * 
	 * 1. Starts before start date and ends befor end date
	 *	2. Starts before start date and ends after end date
	 *	3. Starts after start date and ends before end date
	 *	4. Starts after start date and 2ends after end date
	 *
	 * @param validFromColumnName
	 * @param validToColumnName
	 * @param start
	 * @param end
	 * @return
	 */
	public IDOQuery appendOverlapPeriod(String validFromColumnName,String validToColumnName,Date start,Date end){
		String before = IDOQuery.LESS_THAN_OR_EQUAL_SIGN;
		String after = IDOQuery.GREATER_THAN_OR_EQUAL_SIGN;
		/*
		 * 1) starts before selected period, but end within
		 	2) start  before selected period, but end afterwards
		 	3) starts and end within selected period
		 	4) starts witin selected period, but end afterwards
		 		 
		 		 validFrom <= start && validTo <= end
		 		 or
		 		 validFrom <= start && validTo >= end
		 		 or
		 		 validFrom >= start && validTo <= end
		 		 or
		 		 validFrom >= start && validTo >= end
		 		 
		 		 // refined version by aron 24.02.04
		 		 validTo >= start && validTo <= end
		 		 or
		 		 validFrom >= start && validFrom <= end
		 		 or 
		 		 validFrom <= start && validTo >= end
		 */
	/*
		append("(");
			append("(");
				append(validFromColumnName).append(before).append(start);
				appendAnd();
				append(validToColumnName).append(before).append(end);
				append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(before).append(start);
				appendAnd();
				append(validToColumnName).append(after).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(after).append(start);
				appendAnd();
				append(validToColumnName).append(before).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(after).append(start);
				appendAnd();
				append(validToColumnName).append(after).append(end);
			append(")");
		append(")");	 
		*/
		append("(");
			append("(");
				append(validToColumnName).append(after).append(start);
				appendAnd();
				append(validToColumnName).append(before).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(after).append(start);
				appendAnd();
				append(validFromColumnName).append(before).append(end);
			append(")");
		appendOr();
			append("(");
				append(validFromColumnName).append(before).append(start);
				appendAnd();
				append(validToColumnName).append(after).append(end);
			append(")");
		append(")");	 
		return this;
	}
	
	public IDOQuery setToCount() {
		if (this._buffer != null) {
			String queryInUpperCase = this._buffer.toString().toUpperCase();
			int index = queryInUpperCase.indexOf(" FROM ");

			if (index > 0) {
				this._buffer.replace(0, index, IDOQuery.SELECT_COUNT);

			}

			queryInUpperCase = this._buffer.toString();
			int index2 = queryInUpperCase.indexOf(" ORDER BY ");
			
			if (index2 >0) {
				this._buffer = this._buffer.replace(index2,this._buffer.length(),"");
			}
			
		}
		return this;
	}
	
	public String setInPlaceHolder(Object value){
	    if(value!=null) {
				this.objectValues.add(value);
			}
	    return QUESTIONMARK;
	}
	
	public IDOQuery appendPlaceHolder(Object value){
	    this.append(QUESTIONMARK);
	    this.objectValues.add(value);
	    return this;
	}
	
	protected List getObjectValues(){
	    return this.objectValues;
	}
	
	
	
	protected void setDataStore(DatastoreInterface datastore){
		this.dataStore = datastore;
	}
	
	protected DatastoreInterface getDatastore(){
		if(this.dataStore==null) {
			this.dataStore = DatastoreInterface.getInstance();
		}
		return this.dataStore;
	}
	
	public Object clone() {
		IDOQuery clone = null;
		try {
			clone = (IDOQuery)super.clone();
			clone._buffer = new StringBuffer(this.toString());
			clone.dataStore = this.dataStore;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return clone;		
	}
}