package com.idega.util.caching;

import java.util.*;

/**
 * Title:        idegaWeb Utility classes
 * Description:  This class is to be used as a java.util.Map as a convenient way of caching objects.
                  The maximum number of cached objects can be set and increased an decreased (default number is 100)
                  The Map removes the least used objects in favour of newly inserted ones and keeps the most accessed objects.
 * Copyright:    Copyright (c) 2000-2002
 * Company:      idega
 * @author        <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5
 */

public class CacheMap extends WeakHashMap {

  private int maxNumberOfObjectsInMap=100;
  private Map accesses;

  public CacheMap(){
  }

  public void setMaxNumberOfObjects(int size){
    this.maxNumberOfObjectsInMap=size;
  }

  public int getMaxNumberOfObjects(){
    return maxNumberOfObjectsInMap;
  }

  private Map getAcesses(){
    if(accesses==null){
      accesses=new HashMap();
    }
    return accesses;
  }

  protected Object getLeastAccessedKey(){
    Iterator keyIter = getAcesses().keySet().iterator();
    Object theReturn = null;
    int lowestIntVal = -1;
    while (keyIter.hasNext()) {
      Object key = keyIter.next();
      Integer integ = (Integer)this.getAcesses().get(key);
      int intval = integ.intValue();
      if(intval<lowestIntVal || lowestIntVal==-1){
        lowestIntVal = intval;
        theReturn = key;
      }
    }
    return theReturn;
  }

  protected synchronized void reduce(int byCount){
    for (int i = 0; i < byCount; i++) {
      removeLeastUsedObjectFromMemory();
    }
  }

  public synchronized Object put(Object key,Object value){
    int difference = super.size()-this.getMaxNumberOfObjects();
    if(difference>=0){
      reduce(difference+1);
    }
    incrementAccessesForKey(key);
    return super.put(key,value);
  }

  public Object get(Object key){
    Object value = super.get(key);
    if(value==null){
      this.removeAccessesForKey(key);
      return value;
    }
    else{
      incrementAccessesForKey(key);
      return value;
    }
  }

  public synchronized Object remove(Object key){
    removeAccessesForKey(key);
    return super.remove(key);
  }

  protected synchronized void removeLeastUsedObjectFromMemory(){
      Object key = getLeastAccessedKey();
      onReducementFromMemory(key);
  }

  public synchronized void removeLeastUsedObjectFromMap(){
      Object key = getLeastAccessedKey();
      onReducementFromMap(key);
  }

  protected synchronized void onReducementFromMap(Object key){
    remove(key);
  }

  protected synchronized void onReducementFromMemory(Object key){
    remove(key);
  }

  protected void incrementAccessesForKey(Object key){
    Integer prevValue = (Integer)this.getAcesses().get(key);
    int prevIntValue;
    if(prevValue==null){
      prevIntValue = 0;
    }
    else{
      prevIntValue=prevValue.intValue();
    }
    Integer newValue = new Integer(prevIntValue++);
    getAcesses().put(key,newValue);
  }

  protected void removeAccessesForKey(Object key){
    this.getAcesses().remove(key);
  }

}