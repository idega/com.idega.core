package com.idega.data;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: 
 *    A representation of an entity. 
 *    Implemented by GenericEntity.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jul 15, 2003
 */
public interface EntityRepresentation {

  public Object getColumnValue(String columnName);
  
  public Object getPrimaryKey();
  
}
