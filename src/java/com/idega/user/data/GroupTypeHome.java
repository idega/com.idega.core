package com.idega.user.data;


public interface GroupTypeHome extends com.idega.data.IDOHome
{
 public GroupType create() throws javax.ejb.CreateException;
 public GroupType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllGroupTypes()throws javax.ejb.FinderException;
 public java.util.Collection findVisibleGroupTypes()throws javax.ejb.FinderException;
 public int getNumberOfVisibleGroupTypes()throws javax.ejb.FinderException,com.idega.data.IDOException;

}