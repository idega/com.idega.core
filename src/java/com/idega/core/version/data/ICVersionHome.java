package com.idega.core.version.data;

import java.util.Collection;

public interface ICVersionHome extends com.idega.data.IDOHome
{
 public ICVersion create() throws javax.ejb.CreateException;
 public ICVersion findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Collection<ICVersion> findChildrens(com.idega.core.version.data.ICVersion p0)throws javax.ejb.FinderException;
 public int getChildrenCount(com.idega.core.version.data.ICVersion p0)throws com.idega.data.IDOException;

}