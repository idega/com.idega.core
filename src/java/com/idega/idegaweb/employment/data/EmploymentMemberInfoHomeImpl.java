package com.idega.idegaweb.employment.data;


public class EmploymentMemberInfoHomeImpl extends com.idega.data.IDOFactory implements EmploymentMemberInfoHome
{
 protected Class getEntityInterfaceClass(){
  return EmploymentMemberInfo.class;
 }

 public EmploymentMemberInfo create() throws javax.ejb.CreateException{
  return (EmploymentMemberInfo) super.idoCreate();
 }

 public EmploymentMemberInfo createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public EmploymentMemberInfo findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (EmploymentMemberInfo) super.idoFindByPrimaryKey(id);
 }

 public EmploymentMemberInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (EmploymentMemberInfo) super.idoFindByPrimaryKey(pk);
 }

 public EmploymentMemberInfo findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}