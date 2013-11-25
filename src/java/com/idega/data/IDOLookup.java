package com.idega.data;

import java.sql.SQLException;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.business.IBOHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOService;
import com.idega.business.IBOServiceBean;
import com.idega.repository.data.Singleton;

/**
 * Title:        idegaclasses
 * Title:        idega Data Objects
 * Description:  IDOLookup is a class use to look up and get a reference to instances of IDO (BMP Entity EJB Beans) objects.<br><br>
 * <br>IDOLookup should be used mainly for looking up home instances for a data bean.
 * <br>Instances of IDO home classes are obtained by passing either a Class representing a bean interface or implementation. (For example User.class or UserBMPBean.class) to the getHome() method.
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 * @param <E>
 */

public class IDOLookup<E extends IDOEntity, EB extends IDOEntity> extends IBOLookup<IDOEntity, IDOEntity, IDOHome, E, EB> implements Singleton {

  private static IDOLookup<IDOEntity, IDOEntity> idoInstance;

  private static synchronized IDOLookup<IDOEntity, IDOEntity> getIDOLookupInstance(){
    if(idoInstance==null){
      idoInstance = new IDOLookup<IDOEntity, IDOEntity>();
    }
    return idoInstance;
  }

	/**
	 * Unload the previously loaded instance and all its resources
	 */
	public static void unload(){
		idoInstance=null;
	}

  private final String BMP_BEAN_SUFFIX = "BMPBean";

  @Override
  protected String getBeanSuffix(){
    return this.BMP_BEAN_SUFFIX;
  }

  private IDOLookup() {
  }

  /**
   * Gets an instance of the implementation of the Home interface for the data bean.
   * <br>The object returned can then needs to be casted to the specific home interface for the bean.
   * @param entityInterfaceClass i the interface of the data bean.
   */
  public static <E extends IDOEntity> IDOHome getHome(Class<E> entityInterfaceClass) throws IDOLookupException{
	 return IDOLookup.getHome(entityInterfaceClass, GenericEntity.DEFAULT_DATASOURCE);
  }

  /**
   * Gets an instance of the implementation of the Home interface for the data bean.
   * <br>The object retured can then needs to be casted to the specific home interface for the bean.
   * @param entityInterfaceClass i the interface of the data bean.
   */
  public static <E extends IDOEntity> IDOHome getHome(Class<E> entityInterfaceClass, String datasource) throws IDOLookupException{
		IDOHome home = null;

		try {
			Class<E> interf = entityInterfaceClass;
			if (!entityInterfaceClass.isInterface()) {
				interf = getInterfaceClassFor(entityInterfaceClass);
			}
			if (datasource != null && !datasource.equals(GenericEntity.DEFAULT_DATASOURCE)) {
				home = IDOLookup.getIDOLookupInstance().homesMapLookup((Class<IDOEntity>) interf, datasource);
				if (home == null) {
					home = IDOLookup.getIDOLookupInstance().getEJBHomeInstance((Class<IDOEntity>) interf, datasource);
					home.setDatasource(datasource, false);
				}
			} else {
				home = IDOLookup.getIDOLookupInstance().getEJBHomeInstance((Class<IDOEntity>) interf);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IDOLookupException(e);
		}

		return home;
  }

  public static <E extends IDOEntity> IDOHome getHomeLegacy(Class<E> entityInterfaceClass) {
  	try {
      return getHome(entityInterfaceClass);
  	} catch(IDOLookupException e){
  		System.err.println(e.getMessage());
  	}
	return null;
  }

  /**
   * Gets the Class object for the interface of a data bean.
   * @param entityBeanOrInterfaceClass can be either the BMP bean class or the interface class itself.
   */
  public static <E extends IDOEntity> Class<E> getInterfaceClassFor(Class<E> entityBeanOrInterfaceClass) {
    return (Class<E>) getIDOLookupInstance().getInterfaceClassForNonStatic((Class<IDOEntity>) entityBeanOrInterfaceClass);
  }

    /**
   * Gets the Class object for the (BMP) bean class of a data bean.
   * @param entityInterfaceClass i the interface of the data bean.
   */
  public static <E extends IDOEntity> Class<E> getBeanClassFor(Class<? extends IDOEntity> entityInterfaceClass) {
    Class<E> beanClass = (Class<E>) getIDOLookupInstance().getBeanClassForNonStatic((Class<IDOEntity>) entityInterfaceClass);
    return beanClass;
  }

  public static <E extends IDOEntity> E create(Class<E> entityInterfaceClass) throws IDOLookupException, CreateException {
    return getHome(entityInterfaceClass).createIDO();
  }

  public static <E extends IDOEntity> E findByPrimaryKey(Class<E> entityInterfaceClass, int id)throws IDOLookupException, FinderException {
	return getHome(entityInterfaceClass).findByPrimaryKeyIDO(id);
  }

  public static <E extends IDOEntity> E findByPrimaryKey(Class<E> entityInterfaceClass, Integer id)throws IDOLookupException,FinderException {
	return getHome(entityInterfaceClass).findByPrimaryKeyIDO(id);
  }

   public static <L extends IDOLegacyEntity> L createOld(Class<L> entityInterfaceOrBeanClass) {
      if (entityInterfaceOrBeanClass.isInterface()) {
    	  Class<L> beanClass = getBeanClassFor(entityInterfaceOrBeanClass);
    	  return createLegacy(beanClass);
      } else {
        try {
          return entityInterfaceOrBeanClass.newInstance();
        } catch(Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e.getMessage());
        }
      }
   }

  public static <L extends IDOLegacyEntity> L createLegacy(Class<L> entityInterfaceClass){
    try {
      return getHome(entityInterfaceClass).createIDO();
    } catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }

  public static <L extends IDOLegacyEntity> L findByPrimaryKeyLegacyOld(Class<L> entityInterfaceOrBeanClass,int id) throws SQLException {
    try{
      L entity = createLegacy(entityInterfaceOrBeanClass);
      entity.findByPrimaryKey(id);
      return entity;
    } catch(Exception e) {
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static <L extends IDOLegacyEntity> L findByPrimaryKeyLegacy(Class<L> entityInterfaceClass,int id)throws SQLException{
    try{
      return getHome(entityInterfaceClass).findByPrimaryKeyIDO(new Integer(id));
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static <L extends IDOLegacyEntity> L findByPrimaryKeyLegacy(Class<L> entityInterfaceClass,int id,String dataSourceName)throws SQLException{
    return findByPrimaryKeyLegacy(entityInterfaceClass,new Integer(id),dataSourceName);
  }

  public static <L extends IDOLegacyEntity> L findByPrimaryKeyLegacy(Class<L> entityInterfaceClass,Integer id,String dataSourceName)throws SQLException{
    try{
      IDOHome home = getHome(entityInterfaceClass);
      L entity = IDOContainer.getInstance().findByPrimaryKey(entityInterfaceClass,id,null,home,dataSourceName);
      return entity;
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static <L extends IDOLegacyEntity> L findByPrimaryKeyLegacy(Class<L> entityInterfaceClass,Integer id)throws java.sql.SQLException{
    try{
      return getHome(entityInterfaceClass).findByPrimaryKeyIDO(id);
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static <E extends IDOEntity> IDOEntityDefinition getEntityDefinitionForClass(Class<E> entityInterfaceClass) throws IDOLookupException{
    return GenericEntity.getStaticInstanceIDO(entityInterfaceClass).getEntityDefinition();
  }

  /**
   * Calls IDOLookup.instanciateEntity(Class entityBeanOrInterfaceClass, String datasource) with
   * the defailt datasource
   * @param entityBeanOrInterfaceClass
   * @return
   */
  public static <T extends IDOEntity> T instanciateEntity(Class<T> entityBeanOrInterfaceClass){
	return IDOLookup.instanciateEntity(entityBeanOrInterfaceClass, GenericEntity.DEFAULT_DATASOURCE);
  }

  /**
   * Sould only be used for LegacyEntities
   */
  public static <T extends IDOEntity> T instanciateEntity(Class<T> entityBeanOrInterfaceClass, String datasource) {
    try{
      Class<T> beanClass = entityBeanOrInterfaceClass;
      if (beanClass.isInterface()) {
        beanClass = getBeanClassFor(entityBeanOrInterfaceClass);
      }
      T instance = beanClass.newInstance();
      instance.setDatasource(datasource);
      try{
    	  ((IDOEntityBean) instance).setEJBLocalHome(getHome(entityBeanOrInterfaceClass, datasource));
      }  catch(Exception e){
      	//do nothing
      }
      return instance;
    }
    catch(Exception e){
	 System.err.println("Exception in IDOLookup#instanciateEntity(Class) for "+entityBeanOrInterfaceClass);
      e.printStackTrace();
      throw new EJBException(e.getMessage());
    }
  }

  protected IBOLookup<IBOService, IBOServiceBean, IBOHome, IBOService, IBOServiceBean> getIBOLookup(){
  	return IBOLookup.getInstance();
  }
  /**
   * Overrided from IBOLookup to hold the same map between IDOLookup and IBOLookup
   */
  @Override
public Map getBeanClassesMap(){
  	return getIBOLookup().getBeanClassesMap();
  }
  /**
   * Overrided from IBOLookup to hold the same map between IDOLookup and IBOLookup
   */
  @Override
public Map getInterfaceClassesMap(){
  	return getIBOLookup().getInterfaceClassesMap();
  }

  /**
   * Overrided from IBOLookup to hold the same map between IDOLookup and IBOLookup
   */
  @Override
public Map getHomesMap(){
  	return getIBOLookup().getHomesMap();
  }

}
