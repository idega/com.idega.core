package com.idega.user.data;


public class GroupRelationTypeHomeImpl extends com.idega.data.IDOFactory implements GroupRelationTypeHome
{
 protected Class getEntityInterfaceClass(){
  return GroupRelationType.class;
 }

 public GroupRelationType create() throws javax.ejb.CreateException{
  return (GroupRelationType) super.idoCreate();
 }

 public GroupRelationType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (GroupRelationType) super.idoFindByPrimaryKey(id);
 }

 public GroupRelationType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupRelationType) super.idoFindByPrimaryKey(pk);
 }


}