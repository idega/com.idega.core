package com.idega.user.data;


public class GroupHomeImpl extends com.idega.data.IDOFactory implements GroupHome{
	
 protected Class getEntityInterfaceClass(){
  return Group.class;
 }


//public Group createGroup()throws javax.ejb.CreateException{
//	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
//	Object pk = ((GroupBMPBean)entity).ejbCreateGroup();
//	((GroupBMPBean)entity).ejbPostCreateGroup();
//	this.idoCheckInPooledEntity(entity);
//	try{
//		return this.findByPrimaryKey(pk);
//	}
//	catch(javax.ejb.FinderException fe){
//		throw new com.idega.data.IDOCreateException(fe);
//	}
//	catch(Exception e){
//		throw new com.idega.data.IDOCreateException(e);
//	}
//}

 public Group create() throws javax.ejb.CreateException{
  return (Group) super.createIDO();
 }


public java.util.Collection findGroups(java.lang.String[] p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupBMPBean)entity).ejbFindGroups(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

//public Group findGroupByPrimaryKey(java.lang.Object p0)throws javax.ejb.FinderException{
//	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
//	Object pk = ((GroupBMPBean)entity).ejbFindGroupByPrimaryKey(p0);
//	this.idoCheckInPooledEntity(entity);
//	return this.findByPrimaryKey(pk);
//}

public Group findSystemUsersGroup()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((GroupBMPBean)entity).ejbFindSystemUsersGroup();
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public java.util.Collection findAllGroups(java.lang.String[] p0,boolean p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupBMPBean)entity).ejbFindAllGroups(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsContained(com.idega.user.data.Group p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupBMPBean)entity).ejbFindGroupsContained(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findTopNodeGroupsContained(com.idega.builder.data.IBDomain p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupBMPBean)entity).ejbFindTopNodeGroupsContained(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsByType(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupBMPBean)entity).ejbFindGroupsByType(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsByMetaData(String key, String value) throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupBMPBean)entity).ejbFindGroupsByMetaData(key,value);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Group) super.findByPrimaryKeyIDO(pk);
 }


public java.lang.String getGroupType(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupBMPBean)entity).ejbHomeGetGroupType();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int getNumberOfGroupsContained(com.idega.user.data.Group p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException, com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	return ((GroupBMPBean)entity).ejbHomeGetNumberOfGroupsContained(p0,p1,p2);
}

public int getNumberOfTopNodeGroupsContained(com.idega.builder.data.IBDomain p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException, com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	return ((GroupBMPBean)entity).ejbHomeGetNumberOfTopNodeGroupsContained(p0,p1,p2);
}


}