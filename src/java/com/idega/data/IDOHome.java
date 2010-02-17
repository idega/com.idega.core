/*
 * $Id: IDOHome.java,v 1.14.2.1 2008/06/04 20:34:26 gimmi Exp $
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
 * Last modified: $Date: 2008/06/04 20:34:26 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.14.2.1 $
 */
public interface IDOHome extends EJBLocalHome{//EJBHome {

  public IDOEntity createIDO() throws CreateException;
  //public IDOEntity createIDO() throws CreateException, RemoteException;

  public String getDatasource();
  public void setDatasource(String dataSource);
  public void setDatasource(String dataSource, boolean reloadEntity);
  
  /*public IDOEntity idoFindByPrimaryKey(int primaryKey) throws RemoteException, FinderException;*/
  public IDOEntity findByPrimaryKeyIDO(Object primaryKey) throws FinderException;
  public Collection findByPrimaryKeyCollection(Collection primaryKey) throws FinderException;
  //public IDOEntity findByPrimaryKeyIDO(Object primaryKey) throws RemoteException, FinderException;

  public Collection getEntityCollectionForPrimaryKeys(Collection collectionOfPrimaryKeys)throws FinderException;

  
  /*public List findAll() throws RemoteException, FinderException;*/
  public Object decode(String pkString);
  public Collection decode(String[] primaryKeys);
}
