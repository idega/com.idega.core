package com.idega.data;

import java.sql.SQLException;

import com.idega.idegaweb.IWMainApplication;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class CacheableEntityBMPBean extends com.idega.data.GenericEntity implements com.idega.data.CacheableEntity {


  public CacheableEntityBMPBean() {
    super();
  }

  public CacheableEntityBMPBean(int id) throws SQLException{
    super(id);
  }

  /** implemented in subclasses*/
  public void initializeAttributes() {
  }
  /** implemented in subclasses*/
  public String getEntityName() {
    return null;
  }

  /**
  *Inserts this entity as a record into the datastore and cache
  */
  public void insert()throws SQLException{
    super.insert();
    cacheEntity();/**@todo this should not happen all the time*/
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().insertIntoCachedTable(this);
  }

  /**
  *deletes this entity as a record in the datastore and cache
  */
  public void delete()throws SQLException{
    cacheEntity();
    super.delete();
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().deleteFromCachedTable(this);
  }

  /**
  *updates this entity as a record in the datastore and cache
  */
  public void update()throws SQLException{
    cacheEntity();
    super.update();
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().updateFromCachedTable(this);
  }

  /**
  *Stores this entities table in memory. The default is to use
  *the idColumnName value as a key when fetching individual entities
  *from memory. you could override this method in a subclass and store
  *the table differently in the IWCacheManager.
  *default is cacheTable(IDOLegacyEntity entity)
  *also available are cacheTable(IDOLegacyEntity entity, String columnNameForKey)
  *and cacheTable(IDOLegacyEntity entity, String columnNameForKey,String columnNameForSecondKey)
  */
  public void cacheEntity(){
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().cacheTable(this,getCacheKey());
    //IWMainApplication.getIWCacheManager().cacheTable(this,key1);
    //IWMainApplication.getIWCacheManager().cacheTable(this,key1,key2);
  }

  public void cacheEntityByID(){
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().cacheTable(this,getIDColumnName());
  }

  public String getCacheKey(){
    return getIDColumnName();
  }



}

