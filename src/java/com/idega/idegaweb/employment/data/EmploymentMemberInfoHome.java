package com.idega.idegaweb.employment.data;


public interface EmploymentMemberInfoHome extends com.idega.data.IDOHome
{
 public EmploymentMemberInfo create() throws javax.ejb.CreateException;
 public EmploymentMemberInfo createLegacy();
 public EmploymentMemberInfo findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public EmploymentMemberInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public EmploymentMemberInfo findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}