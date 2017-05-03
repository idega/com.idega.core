package com.idega.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalObject;
import javax.ejb.FinderException;

import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

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

	private Logger logger = null;

	protected Logger getLog() {
		if (this.logger == null) {
			this.logger = Logger.getLogger(getClass().getName());
		}

		return this.logger;
	}

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
        getLog().log(Level.WARNING, "Error creating bean for: " + this.getClass().getName(), e);
      }
      ((IDOEntityBean) entity).setEJBLocalHome(this);
	  entity.setDatasource(this.dataSource);
      return entity;
      //return (IDOEntity)beanClass.newInstance();
    }
    catch(Exception e){
    	 getLog().log(Level.WARNING, "Error creating bean for: " + entityInterfaceClass.getName(), e);
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
   * @deprecated use {@link IDOFactory#createIDO()}
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
   * @deprecated use {@link IDOFactory#findByPrimaryKeyIDO(Object)}
   */
  @Deprecated
public <T extends IDOEntity> T idoFindByPrimaryKey(Object primaryKey) throws FinderException{
    return findByPrimaryKeyIDO(primaryKey);
  }

  @Override
  public <T extends IDOEntity> T findByPrimaryKeyIDO(Object primaryKey) throws FinderException {
	  return findByPrimaryKeyIDO(primaryKey, null);
  }
  public <T extends IDOEntity> T findByPrimaryKeyIDO(Object primaryKey, Class<? extends IDOEntity> interfaceClass) throws FinderException {
    Object realPK = primaryKey;
    if (primaryKey instanceof IDOEntity) {
    	try{
    		throw new FinderException("Argument of type: "+primaryKey.getClass()+" should not be passed as a parameter to findByPrimaryKey(). This currently works but will be removed in future APIs. Please remove this usage !!!");
    	} catch (FinderException fe) {
    		getLog().log(Level.WARNING, "Error finding entity type of " + getEntityInterfaceClass() +  " by primary key" + primaryKey, fe);
    	}
    	realPK = ((IDOEntity) primaryKey).getPrimaryKey();
    }
    interfaceClass = interfaceClass == null ? getEntityInterfaceClass() : interfaceClass;
    return (T) idoFindByPrimaryKey(interfaceClass, realPK);
  }


	/**
	 *
	 * <p>Makes a search for entity with primary key in all hierarchy.</p>
	 * @param primaryKeys is {@link Collection} of
	 * {@link EJBLocalObject#getPrimaryKey()}, not <code>null</code>;
	 * @return entities, extending this entity by given primary key or
	 * entities of this type by primary key;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public <T extends IDOEntity> List<T> findSubTypesByPrimaryKeysIDO(Collection<Object> primaryKeys) {
		if (ListUtil.isEmpty(primaryKeys)) {
			return Collections.emptyList();
		}

		ArrayList<T> entities = new ArrayList<T>(primaryKeys.size());
		for (Object primaryKey : primaryKeys) {
			T entity = findSubTypeByPrimaryKeyIDO(primaryKey);
			if (entity != null) {
				entities.add(entity);
			}
		}

		return entities;
	}

	/**
	 *
	 * @param primaryKey is {@link IDOEntity#getPrimaryKey()} to search by,
	 * not <code>null</code>;
	 * @return entity itself or a sub-type which has given id or
	 * <code>null</code> failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public <T extends IDOEntity> T findSubTypeByPrimaryKeyIDO(Object primaryKey) {
		if (primaryKey == null || StringUtil.isEmpty(primaryKey.toString())) {
			return null;
		}

		Class<T> interfaceClass = getEntityInterfaceClass();
		if (interfaceClass == null) {
			return null;
		}

		/* Getting sub-types */
		Collection<Class<? extends T>> subTypes = CoreUtil.getSubTypesOf(
				interfaceClass, true);
		if (ListUtil.isEmpty(subTypes)) {
			/* Checking original entity */
			try {
				return findByPrimaryKeyIDO(primaryKey);
			} catch (FinderException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).info(
						"Instances of " + interfaceClass.getSimpleName()
								+ " not found");
			}

			return null;
		}

		/* Getting homes */
		Set<? extends IDOHome> homes = getHomesForSubtypes(subTypes);
		if (ListUtil.isEmpty(homes)) {
			return null;
		}

		/* Searching for instance by primary keys in sub-types */
		for (IDOHome home : homes) {
			try {
				T entity = home.findByPrimaryKeyIDO(primaryKey);
				if (entity != null) {
					java.util.logging.Logger.getLogger(getClass().getName()).info(
							"Found subtype " + entity.getClass().getSimpleName()
									+ " by primary key: '" + primaryKey + "'");
					return entity;
				}
			} catch (FinderException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).info(
						"No instances in " + home.getClass().getSimpleName()
								+ " by id: '" + primaryKey
								+ "' proceeding to next one! ");
			}
		}

		return null;
	}

	/**
	 *
	 * @return {@link Set} of different {@link IDOHome}s or
	 * {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected <T extends IDOEntity> Set<? extends IDOHome> getHomesForSubtypes() {
		Collection<Class<? extends IDOEntity>> subTypes = CoreUtil.getSubTypesOf(
				getEntityInterfaceClass(), true);
		if (!ListUtil.isEmpty(subTypes)) {
			return getHomesForSubtypes(subTypes);
		}

		return Collections.emptySet();
	}

	/**
	 *
	 * @param subTypes is {@link IDOEntity}s to get {@link IDOHome}s for,
	 * not <code>null</code>;
	 * @return {@link Set} of different {@link IDOHome}s or
	 * {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected <T extends IDOEntity> Set<? extends IDOHome> getHomesForSubtypes(
			Collection<Class<? extends T>> subTypes) {
		if (ListUtil.isEmpty(subTypes)) {
			return Collections.emptySet();
		}

		Set<IDOHome> homes = new HashSet<IDOHome>();
		for (Class<? extends T> subType : subTypes) {
			IDOHome home = null;
			try {
				home = IDOLookup.getHome(subType);
			} catch (IDOLookupException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING,
						"Failed to get home for " + subType.getSimpleName() +
						" cause of: ", e);
			}

			if (home != null) {
				homes.add(home);
			}
		}

		return homes;
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
	  return getEntityCollectionForPrimaryKeys(collectionOfPrimaryKeys, null);
  }

  public <T extends IDOEntity> Collection<T> getEntityCollectionForPrimaryKeys(Collection<?> collectionOfPrimaryKeys, Class<? extends IDOEntity> theClass) throws FinderException {
  	if (collectionOfPrimaryKeys instanceof IDOPrimaryKeyList) {
  		return getIDOEntityListForPrimaryKeys(collectionOfPrimaryKeys);
  	} else {
	  	Collection<T> theReturn = new ArrayList<T>();
	    if (collectionOfPrimaryKeys != null){
		    for (Object pk: collectionOfPrimaryKeys) {
		      if (pk instanceof IDOEntity) {
		      	theReturn.add((T) pk);
		      } else {
			      T entityObject = this.findByPrimaryKeyIDO(pk, theClass);
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

  protected IDOEntity idoCheckOutPooledEntity() {
	  return idoCheckOutPooledEntity(this.getEntityInterfaceClass());
  }

  protected <T extends IDOEntity> IDOEntity idoCheckOutPooledEntity(Class<T> interfaceClass) {
	 GenericEntity ent = (GenericEntity) com.idega.data.GenericEntity.getStaticInstanceIDO(interfaceClass, this.dataSource);
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
