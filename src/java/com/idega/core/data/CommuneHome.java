package com.idega.core.data;


public interface CommuneHome extends com.idega.data.IDOHome
{
 public Commune create() throws javax.ejb.CreateException;
 public Commune findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllCommunes()throws javax.ejb.FinderException;
 public Commune findByCommuneNameAndProvince(java.lang.String p0,java.lang.Object p1)throws javax.ejb.FinderException;

}