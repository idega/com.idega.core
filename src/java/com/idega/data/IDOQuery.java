package com.idega.data;

import java.util.Collection;
import java.util.Iterator;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IDOQuery {

  StringBuffer _buffer;

  public IDOQuery() {
    _buffer = new StringBuffer();
  }

  public IDOQuery(int length) {
    _buffer = new StringBuffer(length);
  }

  public IDOQuery(String str) {
    _buffer = new StringBuffer(str);
  }


  /**
   * @see java.lang.StringBuffer#append(boolean)
   */
  public IDOQuery append(boolean b){
    _buffer.append(b);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(char)
   */
  public IDOQuery append(char c){
    _buffer.append(c);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(char[],int,int)
   */
  public IDOQuery append(char[] str, int offset, int len){
    _buffer.append(str,offset,len);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(char[])
   */
  public IDOQuery append(char[] str){
    _buffer.append(str);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(java.lang.String)
   */
  public IDOQuery append(double d){
    _buffer.append(d);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(float)
   */
  public IDOQuery append(float f){
    _buffer.append(f);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(int)
   */
  public IDOQuery append(int i){
    _buffer.append(i);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(long)
   */
  public IDOQuery append(long l){
    _buffer.append(l);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(java.lang.Object)
   */
  public IDOQuery append(Object obj){
    _buffer.append(obj);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#append(java.lang.String)
   */
  public IDOQuery append(String str){
    _buffer.append(str);
    return this;
  }

  /**
   * @see java.lang.StringBuffer#capacity()
   */
  public int capacity(){
    return _buffer.capacity();
  }
  /**
   * @see java.lang.StringBuffer#charAt(int)
   */
  public char charAt(int index){
    return _buffer.charAt(index);
  }
  /**
   * @see java.lang.StringBuffer#delete(int,int){
   */
  public IDOQuery delete(int start, int end){
    _buffer.delete(start, end);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#deleteCharAt(int)
   */
  public IDOQuery deleteCharAt(int index){
    _buffer.deleteCharAt(index);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#ensureCapacity(int)
   */
  public void ensureCapacity(int minimumCapacity){
    _buffer.ensureCapacity(minimumCapacity);
  }

  public boolean equals(Object obj){
    return _buffer.equals(obj);
  }
  /**
   * @see java.lang.StringBuffer#getChars(int,int,char[],int)
   */
  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin){
    _buffer.getChars(srcBegin, srcEnd, dst, dstBegin);
  }

  public int hashCode(){
    return _buffer.hashCode();
  }

  /**
   * @see java.lang.StringBuffer#insert(int,boolean)
   */
  public IDOQuery insert(int offset, boolean b){
    _buffer.insert(offset, b);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,char)
   */
  public IDOQuery insert(int offset, char c){
    _buffer.insert(offset, c);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,char[],int,int)
   */
  public IDOQuery insert(int index, char[] str, int offset, int len){
    _buffer.insert(index, str,offset,len);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,char[])
   */
  public IDOQuery insert(int offset, char[] str){
    _buffer.insert(offset, str);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,java.lang.String)
   */
  public IDOQuery insert(int offset, double d){
    _buffer.insert(offset, d);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,float)
   */
  public IDOQuery insert(int offset, float f){
    _buffer.insert(offset, f);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,int)
   */
  public IDOQuery insert(int offset, int i){
    _buffer.insert(offset, i);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,long)
   */
  public IDOQuery insert(int offset, long l){
    _buffer.insert(offset, l);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,java.lang.Object)
   */
  public IDOQuery insert(int offset, Object obj){
    _buffer.insert(offset, obj);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#insert(int,java.lang.String)
   */
  public IDOQuery insert(int offset, String str){
    _buffer.insert(offset, str);
    return this;
  }



  /**
   * @see java.lang.StringBuffer#replace(int,int,String)
   */
  public IDOQuery replace(int start, int end, String str){
    _buffer.replace(start, end, str);
    return this;
  }
  /**
   * @see java.lang.StringBuffer#reverse()
   */
  public IDOQuery reverse(){
    _buffer.reverse();
    return this;
  }
  /**
   * @see java.lang.StringBuffer#setCharAt(int,char)
   */
  public void setCharAt(int index, char ch){
    _buffer.setCharAt(index, ch);
  }
  /**
   * @see java.lang.StringBuffer#setLength(int)
   */
  public void setLength(int newLength){
    _buffer.setLength(newLength);
  }
  /**
   * @see java.lang.StringBuffer#substring(int,int)
   */
  public String substring(int start, int end){
    return _buffer.substring(start, end);
  }
  /**
   * @see java.lang.StringBuffer#substring(int)
   */
  public String substring(int start){
    return _buffer.substring(start);
  }
  /**
   * @see java.lang.StringBuffer#toString()
   */
  public String toString(){
    return _buffer.toString();
  }




  private static final String SELECT_ALL_FROM = "SELECT * FROM ";
  private static final String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM ";
  private static final String SELECT = "SELECT ";
  private static final String FROM = " FROM ";
  private static final String STAR = " * ";
  private static final String ORDER_BY = " ORDER BY ";
  private static final String WHERE = " WHERE ";
  private static final String LIKE = " LIKE ";
  private static final String EQUAL_SIGN = "=";
  private static final String EXCLAMATION_MARK = "!";
  private static final String NOT_EQUAL_SIGN = "!=";
  private static final String WHITE_SPACE = " ";
  private static final String QUOTATION_MARK = "'";
  private static final String DOUBLE_QUOTATION_MARK = "\"";
  //parenthesis
  private static final String PARENTHESIS_LEFT = "(";
  private static final String PARENTHESIS_RIGHT = ")";
  private static final String DELETE_FROM = "DELETE FROM ";
  private static final String INSERT_INTO = "INSERT INTO ";
  private static final String DELETE = "DELETE ";
  private static final String INSERT = "INSERT ";
  private static final String UPDATE = "UPDATE ";
  private static final String IN = " IN ";
  private static final String NOT_IN = " NOT IN ";
  private static final String VALUES = " VALUES ";
  private static final String COMMA = ",";
  private static final String AND = " AND ";
  private static final String OR = " OR ";


  public IDOQuery appendLeftParenthesis(){
    return this.append(PARENTHESIS_LEFT);
  }

  public IDOQuery appendRightParenthesis(){
    return this.append(PARENTHESIS_RIGHT);
  }

  public IDOQuery appendCommaDelimited(String[] str){
    for (int i = 0; i < str.length; i++) {
      if(i != 0){
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
      if(!first){
        this.append(COMMA);
      }

      if(item instanceof IDOEntity ){
        this.append(((IDOEntity)item).getPrimaryKey());
      } else {
        this.append(item);
      }

      first = false;
    }
    return this;
  }

  public IDOQuery appendCommaDelimitedWithinSingleQuotes(String[] str){
    for (int i = 0; i < str.length; i++) {
      if(i != 0){
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
      if(!first){
        this.append(COMMA);
      }

      if(item instanceof IDOEntity ){
        this.appendWithinSingleQuotes(((IDOEntity)item).getPrimaryKey());
      } else {
        this.appendWithinSingleQuotes(item);
      }

      first = false;
    }
    return this;
  }
  public IDOQuery appendCommaDelimitedWithinDoubleQuotes(String[] str){
    for (int i = 0; i < str.length; i++) {
      if(i != 0){
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
      if(!first){
        this.append(COMMA);
      }

      if(item instanceof IDOEntity ){
        this.appendWithinDoubleQuotes(((IDOEntity)item).getPrimaryKey());
      } else {
        this.appendWithinSingleQuotes(item);
      }

      first = false;
    }
    return this;
  }

  public IDOQuery appendWithinDoubleQuotes(String str){
    this.appendDoubleQuote();
    this.append(str);
    this.appendDoubleQuote();
    return this;
  }

  public IDOQuery appendWithinSingleQuotes(String str){
    this.appendSingleQuote();
    this.append(str);
    this.appendSingleQuote();
    return this;
  }

  public IDOQuery appendWithinDoubleQuotes(Object obj){
    this.appendDoubleQuote();
    this.append(obj);
    this.appendDoubleQuote();
    return this;
  }

  public IDOQuery appendWithinSingleQuotes(Object obj){
    this.appendSingleQuote();
    this.append(obj);
    this.appendSingleQuote();
    return this;
  }

  public IDOQuery appendWithinParentheses(String str){
    this.append(PARENTHESIS_LEFT);
    this.append(str);
    this.append(PARENTHESIS_RIGHT);
    return this;
  }
  public IDOQuery appendWithinParentheses(IDOQuery query){
    return appendWithinParentheses((Object)query);
  }
  public IDOQuery appendWithinParentheses(Object obj){
    this.append(PARENTHESIS_LEFT);
    this.append(obj);
    this.append(PARENTHESIS_RIGHT);
    return this;
  }




  public IDOQuery appendSelectAllFrom(){
    return this.append(SELECT_ALL_FROM);
  }
  public IDOQuery appendSelectAllFrom(IDOEntity entity) {
  	return this.appendSelectAllFrom(((IDOLegacyEntity)entity).getTableName());
    //return this.appendSelectAllFrom(entity.getEntityDefinition().getSQLTableName());
  }
  public IDOQuery appendSelectAllFrom(String entityName){
    this.append(SELECT_ALL_FROM);
    this.append(entityName);
    return this;
  }


  public IDOQuery appendSelectCountFrom(){
    return this.append(SELECT_COUNT_FROM);
  }
  public IDOQuery appendSelectCountFrom(IDOEntity entity) {
    return this.appendSelectCountFrom(((IDOLegacyEntity)entity).getTableName());
    //return this.appendSelectCountFrom(entity.getEntityDefinition().getSQLTableName());
    
  }
  public IDOQuery appendSelectCountFrom(String entityName){
    this.append(SELECT_COUNT_FROM);
    this.append(entityName);
    return this;
  }


  public IDOQuery appendSelect(){
    return this.append(SELECT);
  }

  public IDOQuery appendFrom(){
    return this.append(FROM);
  }

  public IDOQuery appendDelete(){
    return this.append(DELETE);
  }

  public IDOQuery appendStar(){
    return this.append(STAR);
  }

  public IDOQuery appendOrderBy(){
    return this.append(ORDER_BY);
  }
  public IDOQuery appendOrderBy(String columnName){
    this.append(ORDER_BY);
    this.append(columnName);
    return this;
  }


  public IDOQuery appendWhere(){
    return this.append(WHERE);
  }

  public IDOQuery appendWhere(String str){
    this.append(WHERE);
    this.append(str);
    return this;
  }

	/**
	 * Appends a where (where columnName=columnValue) without quotemarks	 * @param columnName the name of the field
	 * @param columnValue the value	 * @return IDOQuery this Object	 */
  public IDOQuery appendWhereEquals(String columnName,String columnValue){
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
   public IDOQuery appendWhereEquals(String columnName,int columnValue){
		return appendWhereEquals(columnName,Integer.toString(columnValue));
  }

  
  public IDOQuery appendWhereEqualsQuoted(String columnName,String columnValue){
  	appendWhere(columnName);
  	this.appendEqualSign();
  	this.appendWithinSingleQuotes(columnValue);
  	return this;
  }
  
  public IDOQuery appendEqualsQuoted(String columnName,String columnValue){
  	this.append(WHITE_SPACE);
  	this.append(columnName);
  	this.appendEqualSign();
  	this.appendWithinSingleQuotes(columnValue);
  	return this;
  }



  public IDOQuery appendAndEqualsQuoted(String columnName,String columnValue){
  	appendAnd();
  	append(columnName);
  	this.appendEqualSign();
  	this.appendWithinSingleQuotes(columnValue);
  	return this;
  }

  public IDOQuery appendIn(){
    return this.append(IN);
  }
  public IDOQuery appendIn(String str){
    this.append(IN);
    this.appendWithinParentheses(str);
    return this;
  }
  public IDOQuery appendIn(IDOQuery query){
    this.append(IN);
    this.appendWithinParentheses(query);
    return this;
  }

  public IDOQuery appendNotIn(){
    return this.append(NOT_IN);
  }
  /**
   * Appends a not in clause within parantheses.   * @param str the String to be inside the not in clause.   * @return IDOQuery Returns this   */
  public IDOQuery appendNotIn(String str){
    this.append(NOT_IN);
    this.appendWithinParentheses(str);
    return this;
  }
  public IDOQuery appendNotIn(IDOQuery query){
    this.append(NOT_IN);
    this.appendWithinParentheses(query);
    return this;
  }


  public IDOQuery appendLike(){
    return this.append(LIKE);
  }

  public IDOQuery appendEqualSign(){
    return this.append(EQUAL_SIGN);
  }

  public IDOQuery appendNOTEqual(){
    return this.append(NOT_EQUAL_SIGN);
  }

  public IDOQuery appendSingleQuote(){
    return this.append(QUOTATION_MARK);
  }

  public IDOQuery appendDoubleQuote(){
    return this.append(DOUBLE_QUOTATION_MARK);
  }

  public IDOQuery appendAnd(){
    return this.append(AND);
  }

  public IDOQuery appendOr(){
    return this.append(OR);
  }
  
  public IDOQuery appendInArray(String[] array) {
  	return this.appendIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array));	
  }

	public IDOQuery appendNotInArray(String[] array) {
		return this.appendNotIn().appendWithinParentheses(IDOUtil.getInstance().convertArrayToCommaseparatedString(array));
	}


}