package com.idega.user.data;

public class UserGroupRepresentativeHomeImpl extends com.idega.user.data.GroupHomeImpl implements UserGroupRepresentativeHome
//public class UserGroupRepresentativeHomeImpl extends com.idega.data.IDOFactory implements UserGroupRepresentativeHome
{
 protected Class getEntityInterfaceClass(){
  return UserGroupRepresentative.class;
 }


// public UserGroupRepresentative create() throws javax.ejb.CreateException{
//  return (UserGroupRepresentative) super.createIDO();
// }
//
//
// public UserGroupRepresentative findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
//  return (UserGroupRepresentative) super.findByPrimaryKeyIDO(pk);
// }


public java.lang.String getGroupType(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((UserGroupRepresentativeBMPBean)entity).ejbHomeGetGroupType();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}