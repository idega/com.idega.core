package com.idega.data;

import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.business.IBOLookup;


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
 */

//TODO remove throwing of IDOLookupException where not needed and change IDOLookupException to inherit from Exception and not RemoteException
public class IDOLookup extends IBOLookup{

  private static IDOLookup idoInstance;

  private static IDOLookup getIDOLookupInstance(){
    if(idoInstance==null){
      idoInstance = new IDOLookup();
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
  protected String getBeanSuffix(){
    return BMP_BEAN_SUFFIX;
  }

  private IDOLookup() {
  }



  /**
   * Gets an instance of the implementation of the Home interface for the data bean.
   * <br>The object retured can then needs to be casted to the specific home interface for the bean.
   * @param entityInterfaceClass i the interface of the data bean.
   */
  public static IDOHome getHome(Class entityInterfaceClass)throws IDOLookupException{
		IDOHome home = null;
		
    try {
			home = (IDOHome)getIDOLookupInstance().getEJBHomeInstance(entityInterfaceClass);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IDOLookupException(e);
		}
		
		return home;
    
  
  
  }




  public static IDOHome getHomeLegacy(Class entityInterfaceClass) {
  	try{
      return getHome(entityInterfaceClass);
  	}
  	catch(IDOLookupException e){
  		System.err.println(e.getMessage());
  	}
		return null;
  }

  /**
   * Gets the Class object for the interface of a data bean.
   * @param entityBeanOrInterfaceClass can be either the BMP bean class or the interface class itself.
   */
  public static Class getInterfaceClassFor(Class entityBeanOrInterfaceClass){
    return getIDOLookupInstance().getInterfaceClassForNonStatic(entityBeanOrInterfaceClass);
  }

    /**
   * Gets the Class object for the (BMP) bean class of a data bean.
   * @param entityInterfaceClass i theinterface of the data bean.
   */
  public static Class getBeanClassFor(Class entityInterfaceClass){
    return getIDOLookupInstance().getBeanClassForNonStatic(entityInterfaceClass);
  }




  public static IDOEntity create(Class entityInterfaceClass)throws IDOLookupException, CreateException{
    return getHome(entityInterfaceClass).createIDO();
  }

  public static IDOEntity findByPrimaryKey(Class entityInterfaceClass,int id)throws IDOLookupException, FinderException{
			return getHome(entityInterfaceClass).findByPrimaryKeyIDO(new Integer(id));
  }

  public static IDOEntity findByPrimaryKey(Class entityInterfaceClass,Integer id)throws IDOLookupException,FinderException{
			return getHome(entityInterfaceClass).findByPrimaryKeyIDO(id);
  }


   public static IDOLegacyEntity createOld(Class entityInterfaceOrBeanClass){
      //return createNew(entityInterfaceClass);
      if(entityInterfaceOrBeanClass.isInterface()){
        return createLegacy(getBeanClassFor(entityInterfaceOrBeanClass));
      }
      else{
        try{
          return (IDOLegacyEntity)entityInterfaceOrBeanClass.newInstance();
        }
        catch(Exception e){
          e.printStackTrace();
          throw new RuntimeException(e.getMessage());
        }
      }
   }

  public static IDOLegacyEntity createLegacy(Class entityInterfaceClass){
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).createIDO();
    }
    catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }


  public static IDOLegacyEntity findByPrimaryKeyLegacyOld(Class entityInterfaceOrBeanClass,int id)throws java.sql.SQLException{
    //return findByPrimaryKeyNew(entityInterfaceOrBeanClass,id);
    try{
      IDOLegacyEntity entity = createLegacy(entityInterfaceOrBeanClass);
      entity.findByPrimaryKey(id);
      return entity;
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,int id)throws java.sql.SQLException{
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).findByPrimaryKeyIDO(new Integer(id));
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,int id,String dataSourceName)throws java.sql.SQLException{
    return findByPrimaryKeyLegacy(entityInterfaceClass,new Integer(id),dataSourceName);
  }

  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,Integer id,String dataSourceName)throws java.sql.SQLException{
    try{
      IDOHome home = getHome(entityInterfaceClass);
      IDOLegacyEntity entity = (IDOLegacyEntity)IDOContainer.getInstance().findByPrimaryKey(entityInterfaceClass,id,null,home,dataSourceName);
      /*IDOLegacyEntity entity = (IDOLegacyEntity)getHome(entityInterfaceClass).createIDO();
      entity.setDatasource(datasource);
      ((IDOEntityBean)entity).ejbFindByPrimaryKey(id);
      ((IDOEntityBean)entity).ejbLoad();*/
      return entity;
      //return (IDOLegacyEntity)getHome(entityInterfaceClass).idoFindByPrimaryKey(new Integer(id));
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }


  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,Integer id)throws java.sql.SQLException{
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).findByPrimaryKeyIDO(id);
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }


  public static IDOEntityDefinition getEntityDefinitionForClass(Class entityInterfaceClass) throws IDOLookupException{
    return GenericEntity.getStaticInstanceIDO(entityInterfaceClass).getEntityDefinition();
  }

  /**
   * Sould only be used for LegacyEntities
   */
  public static IDOEntity instanciateEntity(Class entityBeanOrInterfaceClass){
    try{
      Class beanClass = entityBeanOrInterfaceClass;
      if(beanClass.isInterface()){
        beanClass = getBeanClassFor(entityBeanOrInterfaceClass);
      }
      IDOEntity instance = (IDOEntity)beanClass.newInstance();
      try{
      ((IDOEntityBean)instance).setEJBLocalHome(getHome(entityBeanOrInterfaceClass));
      }
      catch(Exception e){
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
  
  protected IBOLookup getIBOLookup(){
  	return IBOLookup.getInstance();
  }
  /**
   * Overrided from IBOLookup to hold the same map between IDOLookup and IBOLookup
   */
  public Map getBeanClassesMap(){
  	return getIBOLookup().getBeanClassesMap();
  }
  /**
   * Overrided from IBOLookup to hold the same map between IDOLookup and IBOLookup
   */
  public Map getInterfaceClassesMap(){
  	return getIBOLookup().getInterfaceClassesMap();
  }
  
  /**
   * Overrided from IBOLookup to hold the same map between IDOLookup and IBOLookup
   */
  public Map getHomesMap(){
  	return getIBOLookup().getHomesMap();
  }

}
