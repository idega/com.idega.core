package com.idega.data;
import java.rmi.RemoteException;
import java.lang.StringBuffer;

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
  private static final String IN = " IN( ";
  private static final String NOT_IN = " NOT IN(";
  private static final String VALUES = " VALUES(";
  private static final String COMMA = ",";






  public IDOQuery appendSelectAllFrom(){
    return this.append(SELECT_ALL_FROM);
  }
  public IDOQuery appendSelectAllFrom(IDOEntity entity) throws RemoteException {
    return this.appendSelectAllFrom(entity.getEntityDefinition().getSQLTableName());
  }
  public IDOQuery appendSelectAllFrom(String entityName){
    this.append(SELECT_ALL_FROM);
    this.append(entityName);
    return this;
  }


  public IDOQuery appendSelectCountFrom(){
    return this.append(SELECT_COUNT_FROM);
  }
  public IDOQuery appendSelectCountFrom(IDOEntity entity) throws RemoteException {
    return this.appendSelectCountFrom(entity.getEntityDefinition().getSQLTableName());
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

  public IDOQuery appendLike(){
    return this.append(LIKE);
  }

  public IDOQuery appendEqualSign(){
    return this.append(EQUAL_SIGN);
  }

  public IDOQuery appendSingleQuote(){
    return this.append(QUOTATION_MARK);
  }

  public IDOQuery appendDoubleQuote(){
    return this.append(DOUBLE_QUOTATION_MARK);
  }





}