package com.idega.core.accesscontrol.business;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PermissionMap {

  private Map firstKey;

  public PermissionMap() {
    this.firstKey = new Hashtable();
/*
    firstKey.clear();
    firstKey.containsKey(object);
    firstKey.containsValue(object);
    firstKey.entrySet();
    firstKey.equals();
    firstKey.get();
    firstKey.hashCode();
    firstKey.isEmpty();
    firstKey.keySet();
    firstKey.putAll();
    firstKey.put();
    firstKey.remove();
    firstKey.size();
    firstKey.values();
*/
  }

  public void put(Object key1, Object key2, Object key3, Object value){
    Map secondKey = (Map)this.firstKey.get(key1);
    if(secondKey == null){
      secondKey = new Hashtable();
      this.firstKey.put(key1,secondKey);
    }
    Map thirdKey = (Map)secondKey.get(key2);
    if(thirdKey == null){
      thirdKey = new Hashtable();
      secondKey.put(key2,thirdKey);
    }
    thirdKey.put(key3,value);
  }

  public void put(Object key1, Object key2, Map value){
    Map secondKey = (Map)this.firstKey.get(key1);
    if(secondKey == null){
      secondKey = new Hashtable();
      this.firstKey.put(key1,secondKey);
    }
    secondKey.put(key2,value);
  }


  public void clear(){
    this.firstKey.clear();
  }

  public List get(Object key1, Object key2, List key3){
    Map thirdKey = this.get(key1, key2);
    if(thirdKey != null){
      List toReturn = new Vector();
      if(key3 != null){
        Iterator iter = key3.iterator();
        while (iter.hasNext()) {
          Object value = thirdKey.get(iter.next());
          if(value != null){
            toReturn.add(value);
          }
        }
      }
      return toReturn;
    } else{
      return null;
    }
  }

  public Map get(Object key1, Object key2){
    Map secondKey = (Map)this.firstKey.get(key1);
    if(secondKey != null){
      return (Map)secondKey.get(key2);
    }else{
      return null;
    }
  }


}
