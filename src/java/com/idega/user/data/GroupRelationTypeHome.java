package com.idega.user.data;


public interface GroupRelationTypeHome extends com.idega.data.IDOHome
{
 public GroupRelationType create() throws javax.ejb.CreateException;
 public GroupRelationType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;

}