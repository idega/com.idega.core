package com.idega.user.data;


public class GroupRelationTypeHomeImpl extends com.idega.data.IDOFactory implements GroupRelationTypeHome
{
 protected Class getEntityInterfaceClass(){
  return GroupRelationType.class;
 }


 public GroupRelationType create() throws javax.ejb.CreateException{
  return (GroupRelationType) super.createIDO();
 }


public java.util.Collection findAll()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationTypeBMPBean)entity).ejbFindAll();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public GroupRelationType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupRelationType) super.findByPrimaryKeyIDO(pk);
 }



}