package com.idega.user.data;


public class GroupTypeHomeImpl extends com.idega.data.IDOFactory implements GroupTypeHome
{
 protected Class getEntityInterfaceClass(){
  return GroupType.class;
 }


 public GroupType create() throws javax.ejb.CreateException{
  return (GroupType) super.createIDO();
 }


 public GroupType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


 public GroupType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupType) super.findByPrimaryKeyIDO(pk);
 }


 public GroupType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (GroupType) super.findByPrimaryKeyIDO(id);
 }


 public GroupType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}