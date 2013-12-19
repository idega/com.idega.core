package com.idega.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.repository.data.ImplementorRepository;
import com.idega.util.ListUtil;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001-2002
 *  Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class IDOFactory implements IDOHome,java.io.Serializable{

	private static final long serialVersionUID = 463763865403762367L;

	protected String dataSource = GenericEntity.DEFAULT_DATASOURCE;

  protected IDOFactory(){
  }

  @Override
  public String getDatasource() {
	  return ((GenericEntity)this.idoCheckOutPooledEntity()).getDatasource();
  }

  @Override
  public void setDatasource(String dataSource) {
	  setDatasource(dataSource, true);
  }

  @Override
  public void setDatasource(String dataSource, boolean reloadEntity) {
	  if (dataSource != null) {
		 this.dataSource = dataSource;
		 GenericEntity ent = ((GenericEntity) this.idoCheckOutPooledEntity());
		 ent.setDatasource(dataSource, reloadEntity);
		 idoCheckInPooledEntity(ent);
	  }
  }

  public <T extends IDOEntity> T idoCreate(Class<T> entityInterfaceClass) throws CreateException {
    try{
      T entity = null;
      try{
        entity = IDOContainer.getInstance().createEntity(entityInterfaceClass);
      }
      catch(Error e){
        System.err.println("Error creating bean for : "+this.getClass().getName());
        e.printStackTrace();
      }
      ((IDOEntityBean) entity).setEJBLocalHome(this);
	  entity.setDatasource(this.dataSource);
      return entity;
      //return (IDOEntity)beanClass.newInstance();
    }
    catch(Exception e){
      e.printStackTrace();
      throw new javax.ejb.CreateException(e.getMessage());
    }
  }

  public <T extends IDOEntity> T idoFindByPrimaryKey(Class<T> entityInterfaceClass,int id)throws javax.ejb.FinderException{
    return this.idoFindByPrimaryKey(entityInterfaceClass,new Integer(id));
  }

  public <T extends IDOEntity> T idoFindByPrimaryKey(Class<T> entityInterfaceClass,Integer id)throws javax.ejb.FinderException{
    return idoFindByPrimaryKey(entityInterfaceClass,(Object)id);
  }

  public <T extends IDOEntity> T idoFindByPrimaryKey(Class<T> entityInterfaceClass,Object pk)throws javax.ejb.FinderException{
      T theReturn = IDOContainer.getInstance().findByPrimaryKey(entityInterfaceClass,pk,this, this.dataSource);
      return theReturn;
  }

  @Override
  public <T extends IDOEntity> T createIDO() throws CreateException {
	  Class<T> interfaceClass = getEntityInterfaceClass();
	  return idoCreate(interfaceClass);
  }

  public <T extends IDOEntity> T createEntity() throws CreateException {
	  Class<T> interfaceClass = getEntityInterfaceClass();
	  return idoCreate(interfaceClass);
  }

  /**
   * @deprecated
   */
  @Deprecated
  public <T extends IDOEntity> T idoCreate() throws CreateException{
    return createIDO();
  }

  public <T extends IDOEntity> T idoFindByPrimaryKey(int primaryKey) throws FinderException {
	  Class<T> interfaceClass = getEntityInterfaceClass();
	  return idoFindByPrimaryKey(interfaceClass, primaryKey);
  }

  public <T extends IDOEntity> T idoFindByPrimaryKey(Integer primaryKey) throws FinderException {
	  Class<T> interfaceClass = getEntityInterfaceClass();
	  return idoFindByPrimaryKey(interfaceClass, primaryKey);
  }

  /**
   * @deprecated
   */
  @Deprecated
public <T extends IDOEntity> T idoFindByPrimaryKey(Object primaryKey) throws FinderException{
    return findByPrimaryKeyIDO(primaryKey);
  }

  @Override
  public <T extends IDOEntity> T findByPrimaryKeyIDO(Object primaryKey) throws FinderException{
    Object realPK = primaryKey;
    if(primaryKey instanceof IDOEntity){
    	try{
    		throw new FinderException("Argument of type: "+primaryKey.getClass()+" should not be passed as a parameter to findByPrimaryKey(). This currently works but will be removed in future APIs. Please remove this usage !!!");
    	}
    	catch(FinderException fe){
    		fe.printStackTrace(System.err);
    	}
    	realPK = ((IDOEntity)primaryKey).getPrimaryKey();
    }
    Class<T> interfaceClass = getEntityInterfaceClass();
    return idoFindByPrimaryKey(interfaceClass, realPK);
  }

  public <T extends IDOEntity> T findSubTypeByPrimaryKeyIDO(Object primaryKey) throws FinderException {
	  if (primaryKey == null) {
		  return null;
	  }
	  Class<T> interfaceClass = getEntityInterfaceClass();
	  if (interfaceClass == null) {
		  return null;
	  }

	  //	Checking original entity
	  try {
		  return findByPrimaryKeyIDO(primaryKey);
	  } catch (Exception e) {}

	  try {
		  List<Class<T>> subTypes = ImplementorRepository.getInstance().getValidImplementorClasses(interfaceClass, getClass());
		  if (ListUtil.isEmpty(subTypes)) {
			  return null;
		  }

		  for (Class<T> subType: subTypes) {
			  IDOHome subTypeHome = IDOLookup.getHome(subType);
			  try {
				  return subTypeHome.findByPrimaryKeyIDO(primaryKey);
			  } catch (FinderException e) {}
		  }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }

	  return null;
  }

  public IDOEntity findByPrimaryKeyIDO(int primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  @Override
public <T extends IDOEntity> Collection<T> findByPrimaryKeyCollection(Collection<?> p0) throws FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Collection<?> ids = ((GenericEntity)entity).ejbFindByPrimaryKeyCollection(p0);
	idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

  @Override
public void remove(Object primaryKey){
    try{
      IDOEntity entity = findByPrimaryKeyIDO(primaryKey);
      entity.remove();
    }
    catch(Exception e){
      throw new javax.ejb.EJBException(e.getMessage());
    }
  }

  protected abstract <T extends IDOEntity> Class<T> getEntityInterfaceClass();

  protected <T extends IDOEntity> Class<T> getEntityBeanClass() {
	  Class<T> interfaceClass = getEntityInterfaceClass();
	  return IDOLookup.getBeanClassFor(interfaceClass);
  }

  /**
   *
   * @param setOfPrimaryKeys
   * @return Set of IDOEntity objects for this Factory
   * @throws FinderException
   */
  protected <T extends IDOEntity> Set<T> getEntitySetForPrimaryKeys(Set<?> setOfPrimaryKeys)throws FinderException{
    Set<T> theReturn = new HashSet<T>();
    for (Iterator<?> iter = setOfPrimaryKeys.iterator(); iter.hasNext();) {
      Object pk = iter.next();
      T entityObject = this.idoFindByPrimaryKey(pk);
      theReturn.add(entityObject);
    }
    return theReturn;
  }

  /**
   *
   * @param collectionOfPrimaryKeys
   * @return Collection of IDOEntity objects for this Factory
   * @throws FinderException
   */
  @Override
public <T extends IDOEntity> Collection<T> getEntityCollectionForPrimaryKeys(Collection<?> collectionOfPrimaryKeys) throws FinderException {
  	if (collectionOfPrimaryKeys instanceof IDOPrimaryKeyList) {
  		return getIDOEntityListForPrimaryKeys(collectionOfPrimaryKeys);
  	} else {
	  	Collection<T> theReturn = new ArrayList<T>();
	    if (collectionOfPrimaryKeys != null){
		    for (Object pk: collectionOfPrimaryKeys) {
		      if(pk instanceof IDOEntity){
		      	theReturn.add((T) pk);
		      } else {
			      T entityObject = this.findByPrimaryKeyIDO(pk);
			      theReturn.add(entityObject);
		      }
		    }
	    }
	    return theReturn;
  	}
  }

    /**
   *
   * @param collectionOfPrimaryKeys
   * @return Collection of IDOEntity objects for this Factory
   * @throws FinderException
   */
  private <T extends IDOEntity> Collection<T> getIDOEntityListForPrimaryKeys(Collection<?> collectionOfPrimaryKeys) throws FinderException{
    Collection<T> theReturn = new IDOEntityList<T>(collectionOfPrimaryKeys);
    return theReturn;
  }

  protected IDOEntity idoCheckOutPooledEntity(){
    /**
     * @todo: Change implementation
     */
	 GenericEntity ent = (GenericEntity) com.idega.data.GenericEntity.getStaticInstanceIDO(this.getEntityInterfaceClass(),this.dataSource);
	 ent.setDatasource(this.dataSource, false);
	 return ent;
  }


  protected void idoCheckInPooledEntity(IDOEntity entity){
    /**
     * @todo: implement
     */
  }

	@Override
	public Object decode(String pkString){
		IDOEntity theReturn = this.idoCheckOutPooledEntity();
		return theReturn.decode(pkString);
	}

	@Override
	public Collection<?> decode(String[] pkString){
		IDOEntity theReturn = this.idoCheckOutPooledEntity();
		return theReturn.decode(pkString);
	}

}
