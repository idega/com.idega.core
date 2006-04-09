package com.idega.util.caching;

import com.idega.data.IDOEntity;
import com.idega.data.IDOLegacyEntity;

/**
 * Title: Cache
 * Description: A Cache object for cache written to disk. It has various attributes such as a data object, real and virtual paths, time of caching, timeout etc.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class Cache {

  private String realPathToFile;
  private String virtualPathToFile;
  private IDOEntity cachedEntity;


  public Cache() {
  }

  public Cache(String realPathToFile, String virtualPathToFile, IDOLegacyEntity cachedEntity) {
    this(realPathToFile,virtualPathToFile);
    this.cachedEntity = cachedEntity;
  }

  public Cache(String realPathToFile, String virtualPathToFile) {
    this.realPathToFile = realPathToFile;
    this.virtualPathToFile = virtualPathToFile;
  }

  public void setEntity(IDOEntity entity){
    this.cachedEntity = entity;
  }

  public IDOEntity getEntity(){
    return this.cachedEntity;
  }

  public void setVirtualPathToFile(String virtualPathToFile){
    this.virtualPathToFile = virtualPathToFile;
  }

  public String getVirtualPathToFile(){
    return this.virtualPathToFile;
  }

  public void setRealPathToFile(String realPathToFile){
    this.realPathToFile = realPathToFile;
  }

  public String getRealPathToFile(){
    return this.realPathToFile;
  }

}
