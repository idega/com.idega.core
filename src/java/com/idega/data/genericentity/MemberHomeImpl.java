package com.idega.data.genericentity;


public class MemberHomeImpl extends com.idega.data.IDOFactory implements MemberHome
{
 protected Class getEntityInterfaceClass(){
  return Member.class;
 }

 public Member create() throws javax.ejb.CreateException{
  return (Member) super.idoCreate();
 }

 public Member createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Member findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Member) super.idoFindByPrimaryKey(id);
 }

 public Member findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Member) super.idoFindByPrimaryKey(pk);
 }

 public Member findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}