package com.idega.data;

import javax.ejb.*;

public interface CacheableEntity extends com.idega.data.IDOLegacyEntity
{
 public void cacheEntity();
 public void cacheEntityByID();
 public void delete()throws java.sql.SQLException;
 public java.lang.String getCacheKey();
}
