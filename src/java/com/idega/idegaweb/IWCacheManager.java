package com.idega.idegaweb;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import java.util.*;
import java.io.PrintWriter;

public class IWCacheManager {

  private static final String IW_CACHEMANAGER_KEY = "iw_cachemanager";

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

      if(currentTime > invalidation){
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

  public synchronized Object getObject(String key){
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
      timesMap = new Hashtable();
    }
    return timesMap;
  }

  private Map getObjectsMap(){
    if(objectsMap==null){
      objectsMap = new Hashtable();
    }
    return objectsMap;
  }

  private Map getIntervalsMap(){
    if(intervalsMap==null){
      intervalsMap = new Hashtable();
    }
    return intervalsMap;
  }


}