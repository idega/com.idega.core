package com.idega.user.data;


public class GroupEventTypeHomeImpl extends com.idega.data.IDOFactory implements GroupEventTypeHome
{
 protected Class getEntityInterfaceClass(){
  return GroupEventType.class;
 }

 public GroupEventType create() throws javax.ejb.CreateException{
  return (GroupEventType) super.idoCreate();
 }

 public GroupEventType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (GroupEventType) super.idoFindByPrimaryKey(id);
 }

 public GroupEventType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupEventType) super.idoFindByPrimaryKey(pk);
 }


}