package com.idega.data;


import java.util.Collection;
import java.util.Map;

import com.idega.core.component.data.ICObject;
import com.idega.util.caching.CacheMap;

/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOBeanCache {

  Class entityInterfaceClass;
  Map cacheMap;
  Map findQueryCacheMap;
  Map homeQueryCacheMap;

  public IDOBeanCache(Class entityInterfaceClass){
    this.entityInterfaceClass=entityInterfaceClass;
  }

  private Map getFindQueryCacheMap(){
    if(this.findQueryCacheMap==null){
      //findQueryCacheMap=new HashMap();
    	this.findQueryCacheMap = new CacheMap(200);
    }
    return this.findQueryCacheMap;
  }

  private Map getHomeQueryCacheMap(){
    if(this.homeQueryCacheMap==null){
      //homeQueryCacheMap=new HashMap();
    	this.homeQueryCacheMap = new CacheMap(200);
    }
    return this.homeQueryCacheMap;
  }

  private Map getCacheMap(){
    if(this.cacheMap==null){
      //cacheMap=new HashMap();
    	int maxCachedBeans = 200;
    	IDOEntityDefinition entityDef = (IDOEntityDefinition) IDOContainer.getInstance().getEntityDefinitions().get(this.entityInterfaceClass);
    	if(entityDef!=null){
    		maxCachedBeans=entityDef.getMaxCachedBeans();
    	}
    	if(this.entityInterfaceClass.equals(ICObject.class)){
    	}
    	this.cacheMap = new CacheMap(maxCachedBeans);
    }
    return this.cacheMap;
  }

  IDOEntity getCachedEntity(Object pk){
    return (IDOEntity)getCacheMap().get(pk);
  }

  void putCachedEntity(Object pk,IDOEntity entity){
    getCacheMap().put(pk,entity);
  }

  void removeCachedEntity(Object pk){
    getCacheMap().remove(pk);
  }

  void putCachedFindQuery(String querySQL,Collection pkColl){
      getFindQueryCacheMap().put(querySQL,pkColl);
  }

  Collection getCachedFindQuery(String querySQL){
      return (Collection)getFindQueryCacheMap().get(querySQL);
  }

  boolean isFindQueryCached(String queryString){
    return(getFindQueryCacheMap().get(queryString)!=null);
  }

  void putCachedHomeQuery(String querySQL,Object objectToCache){
      getHomeQueryCacheMap().put(querySQL,objectToCache);
  }

  Object getCachedHomeQuery(String querySQL){
      return getHomeQueryCacheMap().get(querySQL);
  }

  boolean isHomeQueryCached(String queryString){
    return(getHomeQueryCacheMap().get(queryString)!=null);
  }

  synchronized void flushAllHomeQueryCache(){
    this.homeQueryCacheMap=null;
  }

  synchronized void flushAllFindQueryCache(){
    this.findQueryCacheMap=null;
  }

  synchronized void flushAllBeanCache(){
    this.cacheMap=null;
  }


  synchronized void flushAllQueryCache(){
    flushAllFindQueryCache();
    flushAllHomeQueryCache();
  }

}
