package com.idega.idegaweb;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
import java.io.InputStream;
import java.io.IOException;
import java.util.*;
import java.io.PrintWriter;
import com.idega.util.caching.Cache;
import com.idega.util.FileUtil;
import com.idega.data.GenericEntity;


public class IWCacheManager {

  private static final String IW_CACHEMANAGER_KEY = "iw_cachemanager";
  public static final String IW_ROOT_CACHE_DIRECTORY = "iw_cache";

  //private static IWCacheManager instance;
  private Map objectsMap;
  private Map timesMap;
  private Map intervalsMap;

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
        cacheObject.setVirtualPathToFile(virtualPath+"/"+java.net.URLEncoder.encode(fileName));
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

  // not done yet
  private static void deleteCachedBlob(String pathAndFileName){
     FileUtil.delete(pathAndFileName);
  }
}