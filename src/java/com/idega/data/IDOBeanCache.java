package com.idega.data;


import java.util.HashMap;
import java.util.Map;

/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */

public class IDOBeanCache {

  Class entityInterfaceClass;
  Map cacheMap;

  public IDOBeanCache(Class entityInterfaceClass){
    this.entityInterfaceClass=entityInterfaceClass;
  }

  protected Map getCacheMap(){
    if(cacheMap==null){
      cacheMap=new HashMap();
    }
    return cacheMap;
  }

  IDOEntity getCachedEntity(Object pk){
    return (IDOEntity)getCacheMap().get(pk);
  }

  void putCachedEntity(Object pk,IDOEntity entity){
    getCacheMap().put(pk,entity);
  }

}
