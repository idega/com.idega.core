package com.idega.user.data;


public class GroupRelationHomeImpl extends com.idega.data.IDOFactory implements GroupRelationHome
{
 protected Class getEntityInterfaceClass(){
  return GroupRelation.class;
 }


 public GroupRelation create() throws javax.ejb.CreateException{
  return (GroupRelation) super.createIDO();
 }


 public GroupRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupRelation) super.findByPrimaryKeyIDO(pk);
 }



}