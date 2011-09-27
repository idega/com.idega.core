/*
 * $Id: IDOHome.java,v 1.15 2008/06/05 14:12:12 eiki Exp $
 * 
 * Copyright (C) 2001-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.data;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

/**
 * <p>
 * Base "Home" interface for IDO Entity beans.
 * </p>
 * Last modified: $Date: 2008/06/05 14:12:12 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.15 $
 */
public interface IDOHome extends EJBLocalHome {

  public IDOEntity createIDO() throws CreateException;

  public String getDatasource();
  public void setDatasource(String dataSource);
  public void setDatasource(String dataSource, boolean reloadEntity);
  
  public <T extends IDOEntity> T findByPrimaryKeyIDO(Object primaryKey) throws FinderException;
  public Collection findByPrimaryKeyCollection(Collection primaryKey) throws FinderException;
  public <T extends IDOEntity> Collection<T> getEntityCollectionForPrimaryKeys(Collection<?> collectionOfPrimaryKeys)throws FinderException;

  public Object decode(String pkString);
  public Collection decode(String[] primaryKeys);
}
