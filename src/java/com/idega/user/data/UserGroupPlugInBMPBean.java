package com.idega.user.data;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.component.data.ICObject;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDORelationshipException;

/**
 * Title:        idegaWeb User Subsystem
 * Description:  idegaWeb User Subsystem is the base system for Users and Group management
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class UserGroupPlugInBMPBean extends GenericEntity implements UserGroupPlugIn{

	private static final long serialVersionUID = -7794040323399145228L;

  public UserGroupPlugInBMPBean() {
  }

  @Override
public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
	this.addAttribute("plug_in_name","Plug-In name",String.class);
    this.addAttribute("plug_in_desc","Plug-In desc",String.class);
    this.addAttribute("plug_in_type","Plug-In Type",String.class);
    this.addManyToOneRelationship("business_ic_object",ICObject.class);
    this.addManyToOneRelationship("presentation_ic_object",ICObject.class);
    this.addManyToManyRelationShip(GroupType.class);

    getEntityDefinition().setBeanCachingActiveByDefault(true);
  }

  @Override
public String getEntityName() {
    return "ic_user_plugin";
  }

  @Override
public ICObject getBusinessICObject(){
  	return (ICObject) getColumnValue("business_ic_object");
  }

  @Override
public ICObject getPresentationICObject(){
  	return (ICObject) getColumnValue("presentation_ic_object");
  }

  public Collection ejbFindAllPlugIns()throws FinderException{
    return super.idoFindAllIDsBySQL();
  }

  public Collection ejbFindRegisteredPlugInsForUser(User user)throws FinderException{
  	//get all grouptypes the user is associated with and find all plugins connected to them
    return ejbFindAllPlugIns();
  }

  public Collection ejbFindRegisteredPlugInsForGroup(Group group)throws FinderException{
  	//get all grouptypes the group is associated with and find all plugins connected to them

    return ejbFindAllPlugIns();
  }

  public Collection ejbFindRegisteredPlugInsForGroupType(GroupType groupType)throws FinderException, IDORelationshipException{
    return this.idoGetReverseRelatedEntities(groupType);
  }

  public Collection ejbFindRegisteredPlugInsForGroupType(String groupType)throws RemoteException, FinderException, IDORelationshipException{
    GroupTypeHome gHome = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
    return this.ejbFindRegisteredPlugInsForGroupType(gHome.findGroupTypeByGroupTypeString(groupType) );
  }

  @Override
public void setDescription(String description){
    setColumn("plug_in_desc",description);
  }


  @Override
public String getDescription(){
    return super.getStringColumnValue("plug_in_desc");
  }

  @Override
public String getDefaultDisplayName(){
    return getDescription();
  }

  @Override
public String getLocalizedDisplayName(Locale locale){
    return getDescription();
  }

}