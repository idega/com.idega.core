package com.idega.core.data;

import java.lang.String;
import java.lang.Integer;
import java.sql.SQLException;
import com.idega.data.GenericEntity;
import com.idega.idegaweb.IWMainApplication;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class CacheableEntity extends GenericEntity {


  public CacheableEntity() {
    super();
    IWMainApplication.getIWCacheManager().cacheTable(this);
  }

  public CacheableEntity(int id) throws SQLException{
    super(id);
    IWMainApplication.getIWCacheManager().cacheTable(this);
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
  //  IWMainApplication.getIWCacheManager().cacheTable(this);
  }

  /**
  *deletes this entity as a record in the datastore and cache
  */
  public void delete()throws SQLException{
    super.delete();

  }

  /**
  *updates this entity as a record in the datastore and cache
  */
  public void update()throws SQLException{
    super.update();
  }

  /**
  *Stores this entities table in memory. The default is to use
  *the idColumnName value as a key when fetching individual entities
  *from memory. you could override this method in a subclass and store
  *the table differently in the IWCacheManager.
  *default is cacheTable(GenericEntity entity)
  *also available are cacheTable(GenericEntity entity, String columnNameForKey)
  *and cacheTable(GenericEntity entity, String columnNameForKey,String columnNameForSecondKey)
  */
  public void cacheEntity(){
    IWMainApplication.getIWCacheManager().cacheTable(this);
    //IWMainApplication.getIWCacheManager().cacheTable(this,key1);
    //IWMainApplication.getIWCacheManager().cacheTable(this,key1,key2);

  }


}

