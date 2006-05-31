package com.idega.data;


import java.util.Collection;
import java.util.Map;
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
  /**
   * <p>
   * Holds a Map over cached entity objects for this BeanCache.
   * Keys are primary key object for the entities and value a (IDOEntity) Entity
   * </p>
   * @return
   */
  protected Map getCacheMap(){
    if(this.cacheMap==null){
      //cacheMap=new HashMap();
    	int maxCachedBeans = 200;
    	IDOEntityDefinition entityDef;
		try {
			entityDef = IDOLookup.getEntityDefinitionForClass(this.entityInterfaceClass);
	    	if(entityDef!=null){
	    		maxCachedBeans=entityDef.getMaxCachedBeans();
	    	}
	    	//if(this.entityInterfaceClass.equals(ICObject.class)){
	    	//	maxCachedBeans = 10000;
	    	//}
	    	this.cacheMap = new CacheMap(maxCachedBeans);
		}
		catch (IDOLookupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

  /**
   * <p>
   * Returns a Collectino of all cached entity objects in the 
   * bean cache for this BeanCache.
   * </p>
   * @return
   */
  protected Collection getCachedEntities(){
	  return getCacheMap().values();
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
