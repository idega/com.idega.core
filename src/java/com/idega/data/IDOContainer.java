package com.idega.data;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;

import javax.ejb.*;

/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOContainer {

  private static IDOContainer instance;

  private Map emptyBeanInstances;
  private Map beanCacheMap;

  private IDOContainer() {
  }

  public static IDOContainer getInstance(){
    if(instance==null){
      instance = new IDOContainer();
    }
    return instance;
  }

  protected Map getBeanMap(){
    if(emptyBeanInstances==null){
      emptyBeanInstances = new HashMap();
    }
    return emptyBeanInstances;
  }

  protected Map getBeanCacheMap(){
    if(beanCacheMap==null){
      beanCacheMap = new HashMap();
    }
    return beanCacheMap;
  }


  protected IDOBeanCache getBeanCache(Class entityInterfaceClass){
    IDOBeanCache idobc = (IDOBeanCache)getBeanCacheMap().get(entityInterfaceClass);
    if(idobc==null){
      idobc = new IDOBeanCache(entityInterfaceClass);
    }
    return idobc;
  }

  protected List getFreeBeansList(Class entityInterfaceClass){
    List l = (List)getBeanMap().get(entityInterfaceClass);

    if(l==null){
      l = new Vector();
    }
    getBeanMap().put(entityInterfaceClass,l);
    return l;
  }

  protected IDOEntity getFreeBeanInstance(Class entityInterfaceClass)throws Exception{
    List l = getFreeBeansList(entityInterfaceClass);
    IDOEntity entity = null;
    if(!l.isEmpty()){
      entity= (IDOEntity)l.get(0);
    }
    if(entity==null){
      entity = this.instanciateBean(entityInterfaceClass);
    }
    return entity;
  }

  public IDOEntity createEntity(Class entityInterfaceClass)throws javax.ejb.CreateException{
    try{
      IDOEntity entity = getFreeBeanInstance(entityInterfaceClass);
      ((EntityBean)entity).ejbActivate();
      return entity;
    }
    catch(Exception e){
      e.printStackTrace();
      throw new CreateException(e.getMessage());
    }
  }

  protected IDOEntity instanciateBean(Class entityInterfaceClass)throws Exception{
    Class beanClass = IDOLookup.getBeanClassFor(entityInterfaceClass);
    IDOEntity entity = (IDOEntity)beanClass.newInstance();
    return entity;
  }



  public IDOEntity findByPrimaryKey(Class entityInterfaceClass,Object pk)throws javax.ejb.CreateException{
    try{
      IDOEntity entity=null;
      IDOBeanCache cache = null;
      if(beancachingActive(entityInterfaceClass)){
        cache = this.getBeanCache(entityInterfaceClass);
        entity = cache.getCachedEntity(pk);
      }
      if(entity==null){
        entity = this.createEntity(entityInterfaceClass);
        /**
         *@todo
         */
         ((IDOEntityBean)entity).ejbCreate(pk);
         ((IDOEntityBean)entity).ejbLoad();
      }

      if(beancachingActive(entityInterfaceClass)){
        cache.putCachedEntity(pk,entity);
      }
      return entity;
    }
    catch(Exception e){
      throw new CreateException(e.getMessage());
    }
  }

  protected boolean beancachingActive(Class entityInterfaceClass){
    return false;
  }

  IDOEntity getPooledInstance(Class entityInterfaceClass){
    return null;
  }


}
