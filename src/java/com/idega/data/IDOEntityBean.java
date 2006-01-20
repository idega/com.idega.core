/* $Id: IDOEntityBean.java,v 1.12 2006/01/20 16:43:50 tryggvil Exp $ 
 * 
 * Copyright (C) 2002-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.data;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.FinderException;


/**
 * <p>
 * Base (implementation) interface for IDO Entity Beans.
 * This is implemented by the default base entity implementation GenericEntity.
 * </p>
 * Last modified: $Date: 2006/01/20 16:43:50 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.12 $
 */
public interface IDOEntityBean extends EntityBean{

  //public Object getId() throws RemoteException;
  //public String getName() throws RemoteException;

  /*public void addTo(IDOEntity entity) throws RemoteException;
  public void removeFrom(IDOEntity entity) throws RemoteException;
  */
  public Object ejbCreate() throws CreateException;
  //public Object ejbCreate(Object pk) throws CreateException;
  public Object ejbFindByPrimaryKey(Object pk) throws FinderException;
  
  public void setEJBLocalHome(javax.ejb.EJBLocalHome ejbHome);
  //public void setEJBHome(javax.ejb.EJBHome ejbHome);
  public Class getPrimaryKeyClass();
  
  public void setDatasource(String dataSource);

/**
 * @return Set
 */
  public java.util.Collection getAttributes();

}
