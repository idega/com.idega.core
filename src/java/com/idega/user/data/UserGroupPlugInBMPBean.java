package com.idega.user.data;

import com.idega.data.*;
import com.idega.core.data.*;

import java.util.Collection;
import java.util.Locale;
import javax.ejb.*;

/**
 * Title:        idegaWeb User Subsystem
 * Description:  idegaWeb User Subsystem is the base system for Users and Group management
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class UserGroupPlugInBMPBean extends GenericEntity implements UserGroupPlugIn{

  public UserGroupPlugInBMPBean() {
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute("plug_in_desc","Plug-In Name",String.class);
    this.addAttribute("plug_in_type","Plug-In Type",String.class);
    this.addManyToOneRelationship("business_ic_object",ICObject.class);
    this.addManyToOneRelationship("presentation_ic_object",ICObject.class);
    this.addManyToManyRelationShip(Group.class);
  }

  public String getEntityName() {
    return "ic_user_plugin";
  }

  public Collection ejbFindAllPlugIns()throws FinderException{
    return super.idoFindAllIDsBySQL();
  }

  public Collection ejbFindRegisteredPlugInsForUser(User user)throws FinderException{
    return ejbFindAllPlugIns();
  }

  public Collection ejbFindRegisteredPlugInsForGroup(Group group)throws FinderException{
    return ejbFindAllPlugIns();
  }

  public void setDescription(String description){
    setColumn("plug_in_desc",description);
  }


  public String getDescription(){
    return super.getStringColumnValue("plug_in_desc");
  }

  public String getDefaultDisplayName(){
    return getDescription();
  }

  public String getLocalizedDisplayName(Locale locale){
    return getDescription();
  }

}