package com.idega.core.location.data;

import com.idega.core.location.data.Commune;


public interface CommuneHome extends com.idega.data.IDOHome
{
 public Commune create() throws javax.ejb.CreateException;
 public Commune findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllCommunes()throws javax.ejb.FinderException;
 public Commune findByCommuneCode(java.lang.String p0)throws javax.ejb.FinderException;
 public Commune findByCommuneName(java.lang.String p0)throws javax.ejb.FinderException;
 public Commune findByCommuneNameAndProvince(java.lang.String p0,java.lang.Object p1)throws javax.ejb.FinderException;
 public Commune findDefaultCommune()throws javax.ejb.FinderException;

}