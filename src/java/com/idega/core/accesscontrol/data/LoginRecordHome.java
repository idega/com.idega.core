package com.idega.core.accesscontrol.data;


public interface LoginRecordHome extends com.idega.data.IDOHome
{
 public LoginRecord create() throws javax.ejb.CreateException;
 public LoginRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllLoginRecords(int p0)throws javax.ejb.FinderException;
 public LoginRecord findByLoginID(int p0)throws javax.ejb.FinderException;

}