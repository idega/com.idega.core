package com.idega.core.file.data;


public interface ICFileHome extends com.idega.data.IDOHome
{
 public ICFile create() throws javax.ejb.CreateException;
 public ICFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllDescendingOrdered()throws javax.ejb.FinderException;
 public ICFile findByFileName(java.lang.String p0)throws javax.ejb.FinderException;
 public ICFile findEntityOfSpecificVersion(com.idega.core.version.data.ICVersion p0)throws javax.ejb.FinderException;
 public ICFile findRootFolder()throws javax.ejb.FinderException;

}