package com.idega.user.data;


public class GroupEventHomeImpl extends com.idega.data.IDOFactory implements GroupEventHome
{
 protected Class getEntityInterfaceClass(){
  return GroupEvent.class;
 }

 public GroupEvent create() throws javax.ejb.CreateException{
  return (GroupEvent) super.idoCreate();
 }

 public GroupEvent findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (GroupEvent) super.idoFindByPrimaryKey(id);
 }

 public GroupEvent findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupEvent) super.idoFindByPrimaryKey(pk);
 }


}