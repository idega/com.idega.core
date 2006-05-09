package com.idega.core.component.business;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.CreateException;

import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.data.EntityFinder;
import com.idega.data.GenericEntity;
import com.idega.data.IDOContainer;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOHome;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * Title:        IW Core
 * Description:  Use this class to get and manipulate ICObject and ICObjectInstance data objects rather than constructing them with "new"
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000-2002 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</>,<a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ICObjectBusiness implements Singleton {


  private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new ICObjectBusiness();}};

  private  Map icoInstanceMap;
  private  Map icObjectMap;

  protected ICObjectBusiness(){
  	// empty
  }

  private  Map getIcoInstanceMap(){
    if(this.icoInstanceMap==null){
      this.icoInstanceMap = new HashMap();
    }
    return this.icoInstanceMap;
  }

  private  Map getIcObjectMap(){
    if(this.icObjectMap==null){
      this.icObjectMap = new HashMap();
    }
    return this.icObjectMap;
  }

  /**
   * Returns an instance of this business object
   */
  public static ICObjectBusiness getInstance(){
  	return (ICObjectBusiness) SingletonRepository.getRepository().getInstance(ICObjectBusiness.class, instantiator);
  }
  
 /**
   * Returns the Class associated with the ICObjectInstance
   */
  public Class getICObjectClassForInstance(int ICObjectInstanceId) {
    ICObjectInstance instance = this.getICObjectInstance(ICObjectInstanceId);
    ICObject obj = instance.getObject();
    if(obj != null){
      try {
        return obj.getObjectClass();
      }
      catch (ClassNotFoundException ex) {
        ex.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }


  /**
   * Returns the Class associated with the ICObject
   */
  public Class getICObjectClass(int ICObjectId) {
    ICObject obj = this.getICObject(ICObjectId);
    if(obj != null){
      try {
        return obj.getObjectClass();
      }
      catch (ClassNotFoundException ex) {
        ex.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }

  public PresentationObject getNewObjectInstance(Class icObjectClass){
      PresentationObject inst = null;
      try{
        inst = (PresentationObject)icObjectClass.newInstance();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }

  /**
   * Constructs a new PresentationObject with the class associated with the ICObjectInstance , icObjectClassName must be in the form of a int
   */
  public PresentationObject getNewObjectInstance(String icObjectClassName){
      PresentationObject inst = null;
      try{
        inst = getNewObjectInstance(RefactorClassRegistry.forName(icObjectClassName));
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }


  /**
   * Constructs a new PresentationObject with the class associated with the ICObjectInstance
   */
  public  PresentationObject getNewObjectInstance(int icObjectInstanceID){
      PresentationObject inst = null;
      if (icObjectInstanceID > -1) {
	      try{
  	      ICObjectInstance ico = this.getICObjectInstance(icObjectInstanceID);
    	    inst = ico.getNewInstance();
      	  inst.setICObjectInstance(ico);
	      }
  	    catch(Exception e){
    	    e.printStackTrace();
      	}
      }
      return inst;
  }



  /**
   * Returns the IWBundle that the ICObjectInstance is registered to, icObjectInstanceID must be of the form of an int
   */
  public  IWBundle getBundleForInstance(String icObjectInstanceID,IWMainApplication iwma){
    return getBundleForInstance(Integer.parseInt(icObjectInstanceID),iwma);
  }

  /**
   * Returns the IWBundle that the ICObjectInstance is registered to
   */
  public  IWBundle getBundleForInstance(int icObjectInstanceID,IWMainApplication iwma){
    try{
      if(icObjectInstanceID==-1){
        return iwma.getBundle(PresentationObject.CORE_IW_BUNDLE_IDENTIFIER);
      }
      else{
        ICObjectInstance instance = getICObjectInstance(icObjectInstanceID);
        return instance.getObject().getBundle(iwma);
      }
    }
    catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the Class that the ICObjectInstance is associated with, icObjectInstanceID must be of the form of an int
   */
  public Class getClassForInstance(String icObjectInstanceID)throws ClassNotFoundException{
    return getClassForInstance(Integer.parseInt(icObjectInstanceID));
  }

  /**
   * Returns the Class that the ICObjectInstance is associated with
   */
  public Class getClassForInstance(int icObjectInstanceID)throws ClassNotFoundException{
    if (icObjectInstanceID == -1) {
			return(com.idega.presentation.Page.class);
		}
		else {
			return getICObjectInstance(icObjectInstanceID).getObject().getObjectClass();
		}
  }

  /**
   * Returns ICObjectInstance that has the specific icObjectInstanceID
   */
  public ICObjectInstance getICObjectInstance(String icObjectInstanceID) {
    return getICObjectInstance(Integer.parseInt(icObjectInstanceID));
  }

  /**
   * Returns ICObject that has the specific icObjectID
   */
  public ICObject getICObject(int icObjectID){
    try{
      Integer key = new Integer(icObjectID);
      ICObject theReturn = (ICObject)getIcObjectMap().get(key);
      if(theReturn == null){
        theReturn =  ((com.idega.core.component.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).findByPrimaryKey(new Integer(icObjectID));
        getIcObjectMap().put(key,theReturn);
      }
      return theReturn;
    }
    catch(Exception e){
      throw new RuntimeException("Error getting ICObject for id="+icObjectID+" - message: "+e.getMessage());
    }
  }

  /**
   * Returns ICObjectInstance that has the specific icObjectInstanceID
   */
  public  ICObjectInstance getICObjectInstance(int icObjectInstanceID){
    try{
      Integer key = new Integer(icObjectInstanceID);
      ICObjectInstance theReturn = (ICObjectInstance)getIcoInstanceMap().get(key);
      if(theReturn == null){
        theReturn =  ((com.idega.core.component.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjectInstanceID);
        getIcoInstanceMap().put(key,theReturn);
      }
      return theReturn;
    }
    catch(Exception e){
      throw new RuntimeException("Error getting ICObjectInstance for id="+icObjectInstanceID+" - message: "+e.getMessage());
    }
  }


  /**
   * Creates a new empty ICObjectInstance
   * Catches any possible Exceptions and throws a RuntimeException if anything occurres
   */
  public  ICObjectInstance createICObjectInstance() throws IDOCreateException{
    try{
      return ((com.idega.core.component.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).createLegacy();
    }
    catch(RuntimeException re){
      throw new IDOCreateException(re);
    }
  }


  /**
   * Creates a new empty ICObject
   * Catches any possible Exceptions and throws a RuntimeException if anything occurres
   */
  public ICObject createICObject()throws IDOCreateException{
    try{
      return ((com.idega.core.component.data.ICObjectHome)com.idega.data.IDOLookup.getHome(ICObject.class)).create();
    }
    catch(RuntimeException re){
      throw new IDOCreateException(re);
    }
	catch (IDOLookupException e) {
		throw new IDOCreateException(e);
	}
	catch (CreateException e) {
		throw new IDOCreateException(e);
	}
  }



  /**
   * Creates a new empty ICObjectInstance
   * Catches any possible Exceptions and throws a RuntimeException if anything occurres
   */
  public  ICObjectInstance createICObjectInstanceLegacy(){
    try{
      return createICObjectInstance();
    }
    catch(IDOCreateException idoe){
      throw new RuntimeException(idoe.getMessage());
    }
  }


  /**
   * Creates a new empty ICObject
   * Catches any possible Exceptions and throws a RuntimeException if anything occurres
   */
  public ICObject createICObjectLegacy(){
    try{
      return createICObject();
    }
    catch(IDOCreateException idoe){
      throw new RuntimeException(idoe.getMessage());
    }
  }


  /**
   * Returns the related object's id relative to the objectinstance we have
   * Catches the error if there is any and returns the number -2
   * @todo cache somehow
   */
  public int getRelatedEntityId(ICObjectInstance icObjectInstance, Class entityToGetIdFromClass){
    try {
      
      IDOEntity entityToGetIdFrom = GenericEntity.getStaticInstanceIDO(entityToGetIdFromClass);
      String columnName = entityToGetIdFrom.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
      String SQLQuery = EntityFinder.getInstance().getFindRelatedSQLQuery(icObjectInstance,entityToGetIdFromClass);
      int theReturn = SimpleQuerier.executeIntQuery(SQLQuery,columnName);
      return theReturn;
      /*  
      List L = EntityFinder.getInstance().findRelated(icObjectInstance,entityToGetIdFromClass);
      if(!L.isEmpty()){
        return ((IDOLegacyEntity) L.get(0)).getID();
      }
      else
        return -1;
       */
    } catch (Exception e) {
        return -2;
    }
  }

  /**
   * Returns the related object's id relative to the objectinstance we have
   * Catches the error if there is any and returns null if there was an error
   * @todo cache somehow
   */
  public IDOLegacyEntity getRelatedEntity(ICObjectInstance icObjectInstance, Class entityToGetIdFromClass) throws IDOFinderException{
      /*List L = EntityFinder.getInstance().findRelated(icObjectInstance,entityToGetIdFrom);
      if(!L.isEmpty()){
        return (IDOLegacyEntity) L.get(0);
      }
      else{
        throw new IDOFinderException("Nothing found for ICObjectInstance with id="+icObjectInstance.getID()+" and "+entityToGetIdFrom.getName());
      }*/
      int id = getRelatedEntityId(icObjectInstance,entityToGetIdFromClass);
      IDOHome entityToGetIdFromHome;
	    try {
	        entityToGetIdFromHome = IDOLookup.getHome(entityToGetIdFromClass);
	        Integer pk = new Integer(id);
	        return (IDOLegacyEntity) IDOContainer.getInstance().findByPrimaryKey(entityToGetIdFromClass,pk,entityToGetIdFromHome);
	       
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return null;
      }






} // Class