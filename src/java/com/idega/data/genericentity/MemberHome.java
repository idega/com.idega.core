package com.idega.data.genericentity;


public interface MemberHome extends com.idega.data.IDOHome
{
 public Member create() throws javax.ejb.CreateException;
 public Member createLegacy();
 public Member findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Member findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Member findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}