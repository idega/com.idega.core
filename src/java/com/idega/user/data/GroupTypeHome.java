package com.idega.user.data;

import javax.ejb.FinderException;

import com.idega.data.IDOException;


public interface GroupTypeHome extends com.idega.data.IDOHome
{
 public GroupType create() throws javax.ejb.CreateException;
 public GroupType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllGroupTypes()throws javax.ejb.FinderException;
 public GroupType findGroupTypeByGroupTypeString(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findVisibleGroupTypes()throws javax.ejb.FinderException;
 public java.lang.String getAliasGroupTypeString();
 public java.lang.String getGeneralGroupTypeString();
 public int getNumberOfVisibleGroupTypes()throws javax.ejb.FinderException,com.idega.data.IDOException;
 public java.lang.String getPermissionGroupTypeString();
 public java.lang.String getVisibleGroupTypesSQLString();
 public int getNumberOfGroupTypes()throws FinderException,IDOException;

}