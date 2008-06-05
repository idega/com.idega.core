package com.idega.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBMetaData;
import javax.ejb.FinderException;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001-2002
 *  Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class IDOFactory implements IDOHome,java.io.Serializable{
	
	protected String dataSource = GenericEntity.DEFAULT_DATASOURCE;
	
  protected IDOFactory(){
  }

  public String getDatasource() {
	  return ((GenericEntity)this.idoCheckOutPooledEntity()).getDatasource();
  }
  
  public void setDatasource(String dataSource) {
	  setDatasource(dataSource, true);
  }
  
  public void setDatasource(String dataSource, boolean reloadEntity) {
	  if (dataSource != null) {
		 this.dataSource = dataSource;
		 GenericEntity ent = ((GenericEntity) this.idoCheckOutPooledEntity());
		 ent.setDatasource(dataSource, reloadEntity);
		 this.idoCheckInPooledEntity(ent);
	  }
  }
  
  public IDOEntity idoCreate(Class entityInterfaceClass)throws javax.ejb.CreateException{
    //Class beanClass = IDOLookup.getBeanClassFor(entityInterfaceClass);
    try{
      IDOEntityBean entity = null;
      try{
        entity = (IDOEntityBean)IDOContainer.getInstance().createEntity(entityInterfaceClass);
      }
      catch(Error e){
        System.err.println("Error creating bean for : "+this.getClass().getName());
        e.printStackTrace();
      }
      entity.setEJBLocalHome(this);
	  entity.setDatasource(this.dataSource);
      return (IDOEntity)entity;
      //return (IDOEntity)beanClass.newInstance();
    }
    catch(Exception e){
      e.printStackTrace();
      throw new javax.ejb.CreateException(e.getMessage());
    }
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,int id)throws javax.ejb.FinderException{
    return this.idoFindByPrimaryKey(entityInterfaceClass,new Integer(id));
    /*try{
      IDOEntity theReturn = idoCreate(entityInterfaceClass);
      ((IDOLegacyEntity)theReturn).findByPrimaryKey(id);
      return theReturn;
    }
    catch(Exception e){
      throw new javax.ejb.FinderException(e.getMessage());
    }*/
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,Integer id)throws javax.ejb.FinderException{
    //return idoFindByPrimaryKey(entityInterfaceClass,id.intValue());
        return idoFindByPrimaryKey(entityInterfaceClass,(Object)id);
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,Object pk)throws javax.ejb.FinderException{
      IDOEntity theReturn = IDOContainer.getInstance().findByPrimaryKey(entityInterfaceClass,pk,this, this.dataSource);
      return theReturn;

    /*if(pk instanceof Integer){
      return idoFindByPrimaryKey(entityInterfaceClass,(Integer)pk);
    }
    else{
      throw new IDOFinderException("[idoFactory] : Primarykey other than type Integer not supported");
    }*/
  }
  
  


  public IDOEntity createIDO() throws CreateException{
    return idoCreate(getEntityInterfaceClass());
  }

  /**
   * @deprecated
   */
  public IDOEntity idoCreate() throws CreateException{
    return createIDO();
  }

  public IDOEntity idoFindByPrimaryKey(int primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  public IDOEntity idoFindByPrimaryKey(Integer primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  /**
   * @deprecated
   */
  public IDOEntity idoFindByPrimaryKey(Object primaryKey) throws FinderException{
    return findByPrimaryKeyIDO(primaryKey);
  }

  public IDOEntity findByPrimaryKeyIDO(Object primaryKey) throws FinderException{
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
    return idoFindByPrimaryKey(getEntityInterfaceClass(),realPK);
  }

  public IDOEntity findByPrimaryKeyIDO(int primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }
  
  public java.util.Collection findByPrimaryKeyCollection(java.util.Collection p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GenericEntity)entity).ejbFindByPrimaryKeyCollection(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}


  /**
   * @todo: implement
   */
  public EJBMetaData getEJBMetaData(){
      /**@todo: Implement this javax.ejb.EJBHome method*/
    throw new java.lang.UnsupportedOperationException("Method getEJBMetaData() not yet implemented.");
  }

  /**
   * @todo: implement
   */
  public HomeHandle getHomeHandle(){
      /**@todo: Implement this javax.ejb.EJBHome method*/
    throw new java.lang.UnsupportedOperationException("Method getHomeHandle() not yet implemented.");
  }

  /**
   * @todo: implement
   */
  public void remove(Handle handle){}

  public void remove(Object primaryKey){
    try{
      IDOEntity entity = idoFindByPrimaryKey(primaryKey);
      entity.remove();
    }
    catch(Exception e){
      throw new javax.ejb.EJBException(e.getMessage());
    }
  }

  protected abstract Class getEntityInterfaceClass();

  protected Class getEntityBeanClass(){
    return IDOLookup.getBeanClassFor(getEntityInterfaceClass());
  }


  /**
   *
   * @param setOfPrimaryKeys
   * @return Set of IDOEntity objects for this Factory
   * @throws FinderException
   */
  protected Set getEntitySetForPrimaryKeys(Set setOfPrimaryKeys)throws FinderException{
    Set theReturn = new java.util.HashSet();
    Iterator iter = setOfPrimaryKeys.iterator();
    while (iter.hasNext()) {
      Object pk = iter.next();
      IDOEntity entityObject = this.idoFindByPrimaryKey(pk);
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
  public Collection getEntityCollectionForPrimaryKeys(Collection collectionOfPrimaryKeys)throws FinderException{
  	if(collectionOfPrimaryKeys instanceof IDOPrimaryKeyList) {
  		return getIDOEntityListForPrimaryKeys(collectionOfPrimaryKeys);
  	} else {
	  	Collection theReturn = new Vector();
	    if (collectionOfPrimaryKeys != null){
		    Iterator iter = collectionOfPrimaryKeys.iterator();
		    while (iter.hasNext()) {
		      Object pk = iter.next();
		      if(pk instanceof IDOEntity){
		      	theReturn.add(pk);
		      } else {
			      IDOEntity entityObject = this.idoFindByPrimaryKey(pk);
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
  private Collection getIDOEntityListForPrimaryKeys(Collection collectionOfPrimaryKeys)throws FinderException{
    Collection theReturn = new IDOEntityList(collectionOfPrimaryKeys);
//    Iterator iter = collectionOfPrimaryKeys.iterator();
//    while (iter.hasNext()) {
//      Object pk = iter.next();
//      IDOEntity entityObject = this.idoFindByPrimaryKey(pk);
//      theReturn.add(entityObject);
//    }
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
  
	public Object decode(String pkString){
		IDOEntity theReturn = this.idoCheckOutPooledEntity();
		return theReturn.decode(pkString);	
	}
	
	public Collection decode(String[] pkString){
		IDOEntity theReturn = this.idoCheckOutPooledEntity();
		return theReturn.decode(pkString);	
	}

}
