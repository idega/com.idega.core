package com.idega.idegaweb;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;
import java.io.PrintWriter;
import com.idega.util.caching.Cache;
import com.idega.util.FileUtil;
import com.idega.data.GenericEntity;
import com.idega.data.CacheableEntity;
import com.idega.util.StringHandler;


public class IWCacheManager {

  private static final String IW_CACHEMANAGER_KEY = "iw_cachemanager";
  public static final String IW_ROOT_CACHE_DIRECTORY = "iw_cache";

  //private static IWCacheManager instance;
  private Map objectsMap;
  private Map timesMap;
  private Map intervalsMap;
  private Map entityMaps;
  private Map entityMapsKeys;

  private IWCacheManager() {
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

      if( (currentTime > invalidation) && (invalidation!=0) ){//invalidation==0 cache forever or until app.server shuts down
        //System.out.println(" currentTime > invalidation ");
        return false;
      }
      else{
        //System.out.println(" currentTime < invalidation ");
        return true;
      }
    }
  }

  private boolean isNull(String key){
    return !getObjectsMap().containsKey(key);
  }

  public void invalidateCache(String key){
    removeCache(key);
  }

  public synchronized void setObject(String key,Object object,long cacheInterval){
    getObjectsMap().put(key,object);
    getTimesMap().put(key,getCurrentTime());
    getIntervalsMap().put(key,new Long(cacheInterval));
  }

  public Object getObject(String key){
    return getObjectsMap().get(key);
  }

  private Long getCurrentTime(){
    return new Long(System.currentTimeMillis());
  }

  private long getTimeOfInvalidation(String key){
    return getTimeOfCacheing(key)+getCacheingInterval(key);
  }

  private long getTimeOfCacheing(String key){
    Long time = (Long)getTimesMap().get(key);
    if(time!=null){
      return time.longValue();
    }
    else{
      return 0;
    }
  }

  private long getCacheingInterval(String key){
    Long time = (Long)getIntervalsMap().get(key);
    if(time!=null){
      return time.longValue();
    }
    else{
      return 0;
    }
  }

  private synchronized void removeCache(String key){
    getObjectsMap().remove(key);
    getTimesMap().remove(key);
    getIntervalsMap().remove(key);
  }


  private Map getTimesMap(){
    if(timesMap==null){
      timesMap = new HashMap();
    }
    return timesMap;
  }

  private Map getObjectsMap(){
    if(objectsMap==null){
      objectsMap = new java.util.HashMap();
    }
    return objectsMap;
  }

  private Map getIntervalsMap(){
    if(intervalsMap==null){
      intervalsMap = new HashMap();
    }
    return intervalsMap;
  }
//added by Eirikur Hrafnsson, eiki@idega.is
  public Cache getCachedBlobObject( String entityClassString, int id, IWMainApplication iwma){
    //check if this blob has already been cached
    Cache cache = (Cache) getObject(entityClassString+id);
    if( cache == null ) {//if null cache it for next time
     cache = cacheBlob(entityClassString,id,iwma);
    }
    return cache;
  }

  private Cache cacheBlob(String entityClassString, int id , IWMainApplication iwma){
    InputStream input = null;
    GenericEntity entity;
    Cache cacheObject = null;
    try{
      entity = GenericEntity.getEntityInstance(Class.forName(entityClassString),id);
      input = entity.getInputStreamColumnValue(entity.getLobColumnName());
      String realPath = iwma.getApplicationRealPath()+FileUtil.getFileSeparator()+IW_ROOT_CACHE_DIRECTORY;
      String virtualPath = "/"+IW_ROOT_CACHE_DIRECTORY;
      String fileName = entity.getID()+"_"+entity.getName();

      if(input != null ){
        FileUtil.streamToFile(input,realPath,fileName);
        cacheObject = new Cache();
        cacheObject.setEntity(entity);
        cacheObject.setRealPathToFile(realPath+FileUtil.getFileSeparator()+fileName);
        cacheObject.setVirtualPathToFile(virtualPath+"/"+URLEncoder.encode(fileName));//used to url encode here
        setObject(entityClassString+id,cacheObject,0);
      }

    }
    catch( Exception e ){
     e.printStackTrace(System.err);
     System.err.println("IWCacheManager : error getting stream from blob");
    }
    finally{
      try{
       if (input != null ) input.close();
      }
      catch(IOException e){
        e.printStackTrace(System.err);
        System.err.println("IWCacheManager : error closing stream");
      }
    }

    return cacheObject;
  }

  public static void deleteCachedBlobs(IWMainApplication iwma){
    String realPath = iwma.getApplicationRealPath();
    FileUtil.deleteAllFilesInDirectory( realPath+IW_ROOT_CACHE_DIRECTORY );
  }

/** caches a whole table. Must be of type CacheableEntity**/
  public void cacheTable(CacheableEntity entity){
    cacheTable(entity,entity.getCacheKey());
  }

/** caches a single entity of type GenericEntity **/
  public void cacheEntity(GenericEntity entity, String cacheKey){
    if( entityMaps == null ){
      entityMaps = new HashMap();
    }
    entityMaps.put(cacheKey, entity);
  }

  public GenericEntity getCachedEntity(String cacheKey){
    if( entityMaps != null ){
      return (GenericEntity) entityMaps.get(cacheKey);
    }
    else return null;
  }

  public void removeCachedEntity(String cacheKey){
    if( entityMaps != null ){
      entityMaps.remove(cacheKey);
    }
  }
/** caches a whole table and specifies which column to use for a key. Must be of type CacheableEntity**/
  public void cacheTable(CacheableEntity entity, String columnNameForKey){
    cacheTable(entity,columnNameForKey,null);
  }

  public void removeTableFromCache(Class entityClass){
    if( entityMaps != null ){
      entityMaps.remove(entityClass);
      if( entityMapsKeys != null ) entityMapsKeys.remove(entityClass);
    }
  }

  public Map getCachedTableMap(Class entityClass){
    if( entityMaps!=null ){
       return (Map) entityMaps.get(entityClass);
    }
    else return null;
  }

  public void cacheTable(CacheableEntity entity, String columnNameForKey ,String columnNameForSecondKey){

    if( entityMaps == null ){
      entityMaps = new HashMap();
      entityMapsKeys = new HashMap();
    }


    if( entityMaps.get(entity.getClass()) == null ){
      Map entityMap = new HashMap();
      Vector keys = new Vector();

      GenericEntity[] e;
      try {
        e = entity.findAll();
        if( (e!= null) && (e.length>0) ){
          boolean hasTwoKeys = false;
          //store key names for update, insert and delete
          keys.addElement(columnNameForKey);
          if( columnNameForSecondKey != null ){
            keys.addElement(columnNameForSecondKey);
            hasTwoKeys = true;
          }
          entityMapsKeys.put(entity.getClass(),keys);

          //traverse through the table and make the entitymap and put it in the master map
          for (int i = 0; i < e.length; i++) {
            if(hasTwoKeys){
              entityMap.put(StringHandler.concatAlphabetically(e[i].getStringColumnValue(columnNameForKey),e[i].getStringColumnValue(columnNameForSecondKey)),e[i]);
            }
            else{
               entityMap.put(e[i].getStringColumnValue(columnNameForKey),e[i]);
            }
          }
          entityMaps.put(entity.getClass(),entityMap);
          //done!
        }
      }
      catch (Exception ex) {
        ex.printStackTrace(System.err);
      }
    }

  }

  public GenericEntity getFromCachedTable(Class entityClass, String value ){
    GenericEntity entity = null;

    if( entityMaps != null ){
      Map entityMap = (Map)entityMaps.get(entityClass);
      if( entityMap != null ){
       entity = (GenericEntity) entityMap.get(value);
      }
    }else System.out.println("IWCacheManager entityMaps is null!");

    return entity;
  }

  public GenericEntity getFromCachedTable(Class entityClass, String value, String value2 ){
    return getFromCachedTable(entityClass, StringHandler.concatAlphabetically(value,value2));
  }

  public Map getEntityMap(Class entityClass){
    Map entityMap = null;
    if( entityMaps != null ){
      entityMap = (Map) entityMaps.get(entityClass);
    }//else System.out.println("IWCacheManager entityMaps is null!");
    return entityMap;
  }

  public Vector getEntityKeyVector(Class entityClass){
    Vector entityKeys = null;
    if( entityMapsKeys != null ){
      entityKeys = (Vector) entityMapsKeys.get(entityClass);
    }
    return entityKeys;
  }


  public void updateFromCachedTable(GenericEntity entity){
    Vector keys = getEntityKeyVector(entity.getClass());
    if( keys!=null ){
      int length = keys.size();
      if(length==2){
        getEntityMap(entity.getClass()).put(StringHandler.concatAlphabetically(entity.getStringColumnValue((String) keys.elementAt(0)),entity.getStringColumnValue((String) keys.elementAt(1))),entity);
      }
      else{
         getEntityMap(entity.getClass()).put(entity.getStringColumnValue((String) keys.elementAt(0)),entity);
      }
    }
  }

  public void deleteFromCachedTable(GenericEntity entity){
    Vector keys = getEntityKeyVector(entity.getClass());
    if( keys!=null ){
      int length = keys.size();
      if(length==2){
         getEntityMap(entity.getClass()).remove(StringHandler.concatAlphabetically(entity.getStringColumnValue((String) keys.elementAt(0)),entity.getStringColumnValue((String) keys.elementAt(1))));
      }
      else{
         getEntityMap(entity.getClass()).remove(entity.getStringColumnValue((String) keys.elementAt(0)));
      }
    }
  }

  public void insertIntoCachedTable(GenericEntity entity){
    updateFromCachedTable(entity);
  }

}