package com.idega.user.business;

import com.idega.business.IBOServiceBean;
import java.util.Locale;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.user.data.*;
import com.idega.data.IDOLookup;
import javax.ejb.*;
import java.rmi.RemoteException;


/**
 * Title:        idegaWeb User Subsystem
 * Description:  idegaWeb User Subsystem
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class GroupBusinessBean extends IBOServiceBean implements GroupBusiness{

  private GroupHome groupHome;

  public GroupBusinessBean() {
  }


  public GroupHome getGroupHome(){
    if(groupHome==null){
      try{
        groupHome = (GroupHome)IDOLookup.getHome(Group.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return groupHome;
  }


  public Group createGroup(String name,String description,String type)throws CreateException,RemoteException{
    Group newGroup;
    newGroup =  getGroupHome().create();
    newGroup.setName(name);
    newGroup.setDescription(description);
    newGroup.setGroupType(type);
    newGroup.store();
    return newGroup;
  }

  public String getGroupType(Class groupClass)throws RemoteException{
    return ((GroupHome)IDOLookup.getHome(groupClass)).getGroupType();
  }

}