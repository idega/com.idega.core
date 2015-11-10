/*
 * $Id: IWCacheManager.java,v 1.38 2007/05/10 22:34:28 thomas Exp $
 * Created in 2001 by Tryggvi Larusson
 *
 * Copyright (C) 2001-2005 Idega software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.core.file.data.ICFile;
import com.idega.data.CacheableEntity;
import com.idega.data.GenericEntity;
import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.IDOLookup;
import com.idega.repository.RepositoryService;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.caching.Cache;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.TextSoap;

/**
 * <p>
 * Class used to manage caches in idegaWeb. This manages the cache system
 * that can be used by com.idega.presentation.Block and suclasses to store
 * fragments of their rendering output in memory.
 * </p>
 * Copyright: Copyright (c) 2001-2005 idega software<br/>
 * Last modified: $Date: 2007/05/10 22:34:28 $ by $Author: thomas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @deprecated Replaced with IWCacheManager2
 * @version $Revision: 1.38 $
 */
@Deprecated
public class IWCacheManager implements Singleton {

  private static final String IW_CACHEMANAGER_KEY = "iw_cachemanager";
  public static final String IW_ROOT_CACHE_DIRECTORY = "iw_cache";
  private Logger log = Logger.getLogger(IWCacheManager.class.getName());

  private Map<String, Object> objectsMap;
  private Map<String, Long> timesMap;
  private Map<String, Long> intervalsMap;
  private Map<Object, Object> entityMaps;
  private Map<Class<?>, List<String>> entityMapsKeys;
  private Map<String, List<String>> _keysMap;

  private IWCacheManagerEventClient iwCacheManagerEventClient = null;

  private static final long CACHE_NEVER_EXPIRES = -1;

  private IWCacheManager() {
	  iwCacheManagerEventClient = new IWCacheManagerEventClient(this);
  }

  public static IWCacheManager getInstance(IWMainApplication iwma){
    IWCacheManager iwcm = (IWCacheManager)iwma.getAttribute(IW_CACHEMANAGER_KEY);
    if(iwcm==null){
      iwcm = new IWCacheManager();
      iwma.setAttribute(IW_CACHEMANAGER_KEY,iwcm);
    }
    return iwcm;
  }

  /*public static IWCacheManager getInstance(){
    if(instance==null){
      instance = new IWCacheManager();
    }
    return instance;
  }*/

  public boolean isCacheValid(String key){
    if(isNull(key)){
    //System.out.println(" CacheKey is null ");
      return false;
    }
    else{
      long currentTime = System.currentTimeMillis();
      long invalidation = getTimeOfInvalidation(key);
       //System.out.println("Current     : "+currentTime);
       //System.out.println("Invalidation: "+invalidation);

      if ( invalidation == CACHE_NEVER_EXPIRES ) {
        //System.out.println(" cache never expires ");
      		return true;
      }
      else if (currentTime > invalidation) {
        //System.out.println(" currentTime > invalidation ");
        return false;
      }
      else{
        //System.out.println(" currentTime <= invalidation ");
        return true;
      }
    }
  }

  private boolean isNull(String key){
    return !getObjectsMap().containsKey(key);
  }

  public void invalidateCache(String key){
	iwCacheManagerEventClient.invalidateCache(key);
    removeCache(key, null);
  }

  public void invalidateCacheWithPartialKey(String key, String partialKey) {
	iwCacheManagerEventClient.invalidateCacheWithPartialKey(key, partialKey);
  	removeCache(key, partialKey);

  }

  private Map<String, List<String>> getKeysMap(){
    if(this._keysMap==null){
      this._keysMap=new ConcurrentHashMap<String, List<String>>();
    }
    return this._keysMap;
  }

  public void registerDerivedKey(String key,String derivedKey){
    List<String> derived = getKeysMap().get(key);
    if(derived==null){
      derived=new ArrayList<String>();
      getKeysMap().put(key,derived);
    }
    derived.add(derivedKey);
  }

  public void setObject(String key,String derivedKey, Object object,long cacheInterval){
    registerDerivedKey(key,derivedKey);
    setObject(derivedKey,object,cacheInterval);
  }


  private void setObject(String key, Object object,long cacheInterval){
    getObjectsMap().put(key,object);
    getTimesMap().put(key,getCurrentTime());
    if (cacheInterval < 1) {
  			cacheInterval = CACHE_NEVER_EXPIRES;
    }
    getIntervalsMap().put(key,new Long(cacheInterval));
  }

  public Object getObject(String key){
	Object theReturn = getObjectsMap().get(key);
    return theReturn;
  }

  private Long getCurrentTime(){
    return new Long(System.currentTimeMillis());
  }

  private long getTimeOfInvalidation(String key){
  		long interval = getCacheingInterval(key);
  		if (interval == CACHE_NEVER_EXPIRES) {
  			return interval;
  		}
  		else {
  			return getTimeOfCacheing(key)+interval;
  		}
  }

  private long getTimeOfCacheing(String key){
    Long time = getTimesMap().get(key);
    if(time!=null){
      return time.longValue();
    }
    else{
      return 0;
    }
  }

  private long getCacheingInterval(String key){
    Long time = getIntervalsMap().get(key);
    if(time!=null){
      return time.longValue();
    }
    else{
      return 0;
    }
  }

  private void removeCache(String key, String partialKey){
  		if (partialKey == null) {
	  		removeFromGlobalCache(key);
	  	}

    Map<String, List<String>> map = getKeysMap();
    List<String> derived = map.get(key);
    if(derived!=null){
      Iterator<String> iter = derived.iterator();
      while (iter.hasNext()) {
        String item = iter.next();
        if (partialKey == null) {
        		removeFromGlobalCache(item);
        }
        else {
        		if (item.indexOf(partialKey) != -1) {
        			removeFromGlobalCache(item);
        		}
        }
      }
    }
  }

  private void removeFromGlobalCache(String key){
    getObjectsMap().remove(key);
    getTimesMap().remove(key);
    getIntervalsMap().remove(key);
  }


  private Map<String, Long> getTimesMap(){
    if(this.timesMap==null){
      this.timesMap = new ConcurrentHashMap<String, Long>();
    }
    return this.timesMap;
  }

  private Map<String, Object> getObjectsMap(){
    if(this.objectsMap==null){
      this.objectsMap = new ConcurrentHashMap<String, Object>();
    }
    return this.objectsMap;
  }

  private Map<String, Long> getIntervalsMap(){
    if(this.intervalsMap==null){
      this.intervalsMap = new ConcurrentHashMap<String, Long>();
    }
    return this.intervalsMap;
  }

  public Cache getCachedBlobObject( String entityClassString, int id, IWMainApplication iwma){
	  return getCachedBlobObject(entityClassString, id, iwma, null);
  }

  public Cache getCachedBlobObject( String entityClassString, int id, IWMainApplication iwma, String datasource){
    //check if this blob has already been cached
    Cache cache = (Cache) getObject(entityClassString+id+datasource);
    if (cache == null || !isBlobCached(cache)) {//if null cache it for next time
     cache = cacheBlob(entityClassString,id,iwma, datasource);
    }
    return cache;
  }

  /**
   * Checks if the blob object is really cached on disk
 * @param cache
 * @return true if the cached file exists
 */
private boolean isBlobCached(Cache cache){
  	java.io.File f = new java.io.File(cache.getRealPathToFile());
    return f.exists() && f.canRead() && f.length() > 0;
  }

  private Cache cacheBlob(String entityClassString, int id , IWMainApplication iwma, String datasource){
    InputStream input = null;
    Cache cacheObject = null;
    String fileName = null;
    String realPath = null;
    try {
    	Class<? extends IDOEntity> entityClass = RefactorClassRegistry.forName(entityClassString);
    	IDOHome home = IDOLookup.getHome(entityClass);
    	if (datasource != null) {
    		entityClass = RefactorClassRegistry.forName(entityClassString);
    		home = IDOLookup.getHome(entityClass, datasource);
    	}
    	GenericEntity ent = (GenericEntity) home.findByPrimaryKeyIDO(new Integer(id));

    	if (ent instanceof ICFile) {
    		ICFile file = (ICFile) ent;
    		String uri = file.getFileUri();
    		if (!StringUtil.isEmpty(uri)) {
    			RepositoryService repository = ELUtil.getInstance().getBean(RepositoryService.BEAN_NAME);
    			input = repository.getInputStreamAsRoot(uri);
    		}
    	}

    	if (input == null) {
	    	try {
	    		input = ent.getInputStreamColumnValue(ent.getLobColumnName());
	    	} catch (Exception e) {
	    		log.log(Level.WARNING, "Error getting input stream for " + entityClassString + ", ID: " + id, e);
	    	}
    	}
    	if (input == null) {
    		log.warning("Unable to resolve input stream for " + ent + ". DB entity: " +	entityClassString + ", ID " + id + ", datasource " + datasource);
    	}

    	realPath = iwma.getApplicationRealPath() + FileUtil.getFileSeparator() + IW_ROOT_CACHE_DIRECTORY;
    	String appVPath = iwma.getApplicationContextURI();

    	String virtualPath;
    	if (appVPath.endsWith(CoreConstants.SLASH)) {
			virtualPath = appVPath + IW_ROOT_CACHE_DIRECTORY;
		} else {
			virtualPath = appVPath + CoreConstants.SLASH + IW_ROOT_CACHE_DIRECTORY;
		}

    	fileName = ent.getID() + CoreConstants.UNDER + ent.getName();
    	fileName = TextSoap.findAndCut(fileName,CoreConstants.SPACE);
    	fileName = StringHandler.stripNonRomanCharacters(fileName, new char[] {'_', '.', '0', '1','2','3','4','5','6','7','8','9'});

    	if (input == null) {
    		log.warning("Unable to write file " + fileName + " to " + realPath + " because input stream for " +
    				entityClassString + ", ID " + id + ", datasource " + datasource + " is not defined");
    	} else {
    		FileUtil.streamToFile(input, realPath, fileName);
    		cacheObject = new Cache();
    		cacheObject.setEntity(ent);
    		cacheObject.setRealPathToFile(realPath+FileUtil.getFileSeparator()+fileName);
    		cacheObject.setVirtualPathToFile(virtualPath+CoreConstants.SLASH+URLEncoder.encode(fileName));//used to url encode here
    		setObject(entityClassString+id+datasource,cacheObject,0);
    	}
    } catch (Exception e) {
    	log.log(Level.WARNING, "Some error occured while writing file " + fileName + " to " + realPath + ". DB entity: " + entityClassString + ", ID " + id + ", datasource " + datasource, e);
    } finally {
    	IOUtil.close(input);
    }

    return cacheObject;
  }

  public static void deleteCachedBlobs(IWMainApplication iwma){
    String realPath = iwma.getApplicationRealPath();
    FileUtil.deleteAllFilesInDirectory(realPath + IW_ROOT_CACHE_DIRECTORY);
  }

/** caches a whole table. Must be of type CacheableEntity**/
  public void cacheTable(CacheableEntity entity){
    cacheTable(entity,entity.getCacheKey());
  }

/** caches a single entity of type IDOEntity **/
  public void cacheEntity(IDOEntity entity, String cacheKey){
	  if (cacheKey == null) {
		  log.warning("Cache key is not provided. Value: " + entity);
		  return;
	  }
	  if (entity == null) {
		  log.warning("Cache value is not provided. Key: " + cacheKey);
		  return;
	  }

	  if( this.entityMaps == null ){
      this.entityMaps = new ConcurrentHashMap<Object, Object>();
    }
    this.entityMaps.put(cacheKey, entity);
  }

  public IDOLegacyEntity getCachedEntity(String cacheKey){
    if( this.entityMaps != null ){
      return (IDOLegacyEntity) this.entityMaps.get(cacheKey);
    }
		else {
			return null;
		}
  }

  public void removeCachedEntity(String cacheKey){
    iwCacheManagerEventClient.removeCachedEntity(cacheKey);
    if( this.entityMaps != null ){
      this.entityMaps.remove(cacheKey);
    }
  }
/** caches a whole table and specifies which column to use for a key. Must be of type CacheableEntity**/
  public void cacheTable(CacheableEntity entity, String columnNameForKey){
    cacheTable(entity,columnNameForKey,null);
  }

  public void removeTableFromCache(Class<?> entityClass){
    iwCacheManagerEventClient.removeTableFromCache(entityClass);
    if( this.entityMaps != null ){
      this.entityMaps.remove(entityClass);
      if( this.entityMapsKeys != null ) {
				this.entityMapsKeys.remove(entityClass);
			}
    }
  }

  public Map getCachedTableMap(Class<?> entityClass){
    if( this.entityMaps!=null ){
       return (Map) this.entityMaps.get(entityClass);
    }
		else {
			return null;
		}
  }

  public void cacheTable(CacheableEntity entity, String columnNameForKey ,String columnNameForSecondKey){

    if( this.entityMaps == null ){
      this.entityMaps = new ConcurrentHashMap<Object, Object>();
      this.entityMapsKeys = new ConcurrentHashMap<Class<?>, List<String>>();
    }


    if( this.entityMaps.get(getCorrectClassForEntity(entity)) == null ){
      Map<String, Object> entityMap = new ConcurrentHashMap<String, Object>();
      List<String> keys = new ArrayList<String>();

      IDOLegacyEntity[] e;
      try {
        e = entity.findAll();
        if( (e!= null) && (e.length>0) ){
          boolean hasTwoKeys = false;
          //store key names for update, insert and delete
          keys.add(columnNameForKey);
          if( columnNameForSecondKey != null ){
            keys.add(columnNameForSecondKey);
            hasTwoKeys = true;
          }
          this.entityMapsKeys.put(getCorrectClassForEntity(entity),keys);

          //traverse through the table and make the entitymap and put it in the master map
          for (int i = 0; i < e.length; i++) {
            if(hasTwoKeys){
              entityMap.put(StringHandler.concatAlphabetically(e[i].getStringColumnValue(columnNameForKey),e[i].getStringColumnValue(columnNameForSecondKey)),e[i]);
            }
            else{
               entityMap.put(e[i].getStringColumnValue(columnNameForKey),e[i]);
            }
          }
          this.entityMaps.put(getCorrectClassForEntity(entity),entityMap);
          //done!
        }
      }
      catch (Exception ex) {
        ex.printStackTrace(System.err);
      }
    }

  }

  public IDOLegacyEntity getFromCachedTable(Class<?> entityClass, String value ){
    IDOLegacyEntity entity = null;

    if( this.entityMaps != null ){
      Map entityMap = getEntityMap(entityClass);
      if( entityMap != null ){
       entity = (IDOLegacyEntity) entityMap.get(value);
      }
    }
		else {
			System.out.println("IWCacheManager entityMaps is null!");
		}

    return entity;
  }

  public IDOLegacyEntity getFromCachedTable(Class<?> entityClass, String value, String value2 ){
    return getFromCachedTable(entityClass, StringHandler.concatAlphabetically(value,value2));
  }

  public Map getEntityMap(Class<?> entityClass){
    Map entityMap = null;
    if( this.entityMaps != null ){
      Class<?> entityBeanClass = this.getCorrectClassForEntity(entityClass);
      entityMap = (Map) this.entityMaps.get(entityBeanClass);
    }//else System.out.println("IWCacheManager entityMaps is null!");
    return entityMap;
  }

  public List<String> getEntityKeyVector(Class<?> entityClass){
	  List<String> entityKeys = null;
    if( this.entityMapsKeys != null ){
      entityKeys = this.entityMapsKeys.get(entityClass);
    }
    return entityKeys;
  }


  public void updateFromCachedTable(IDOLegacyEntity entity){
	  List<String> keys = getEntityKeyVector(getCorrectClassForEntity(entity));
    if( keys!=null ){
      int length = keys.size();
      if(length==2){
        getEntityMap(getCorrectClassForEntity(entity)).put(StringHandler.concatAlphabetically(entity.getStringColumnValue(keys.get(0)),entity.getStringColumnValue(keys.get(1))),entity);
      }
      else{
         getEntityMap(getCorrectClassForEntity(entity)).put(entity.getStringColumnValue(keys.get(0)),entity);
      }
    }
  }

  public void deleteFromCachedTable(IDOLegacyEntity entity){
	  List<String> keys = getEntityKeyVector(getCorrectClassForEntity(entity));
    if( keys!=null ){
      int length = keys.size();
      if(length==2){
         getEntityMap(getCorrectClassForEntity(entity)).remove(StringHandler.concatAlphabetically(entity.getStringColumnValue(keys.get(0)),entity.getStringColumnValue(keys.get(1))));
      }
      else{
         getEntityMap(getCorrectClassForEntity(entity)).remove(entity.getStringColumnValue(keys.get(0)));
      }
    }
  }

  public void insertIntoCachedTable(IDOLegacyEntity entity){
    updateFromCachedTable(entity);
  }


  private Class<?> getCorrectClassForEntity(IDOLegacyEntity entityInstance){
    return getCorrectClassForEntity(entityInstance.getClass());
  }

  private Class<?> getCorrectClassForEntity(Class entityBeanOrInterfaceClass){
      return com.idega.data.IDOLookup.getInterfaceClassFor(entityBeanOrInterfaceClass);
  }

  /**
   * Clears all caching in for all objects
   */
  public void clearAllCaches(){
	iwCacheManagerEventClient.clearAllCaches();
  	this.log.info("Clearing all IWCacheManager cache");
  	this._keysMap=null;
  	this.entityMaps=null;
  	this.entityMapsKeys=null;
  	this.intervalsMap=null;
  	this.objectsMap=null;
  	this.timesMap=null;
  }

  /**
   * Unloads the instance from the application (typically called on shutdown)
   */
  public void unload(IWMainApplication iwma){
  	iwma.removeAttribute(IW_CACHEMANAGER_KEY);
  	clearAllCaches();
  }

  public Map<String, Object> getCacheMap(){
	  return getObjectsMap();
  }

}