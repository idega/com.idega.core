package com.idega.user.util;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

/**
 * Title:         User class instance converter
 * Description:   Temporary Class to convert between the new and old user systems
 * Copyright:    Copyright (c) 2002-2003
 * Company:      Idega hf
 * @author <a href="mail:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class Converter {

  private Converter() {
  }

  protected static com.idega.core.user.data.UserHome getOldUserHome()throws java.rmi.RemoteException{
      return (com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHome(com.idega.core.user.data.User.class);
  }

  protected static com.idega.user.data.UserHome getNewUserHome()throws java.rmi.RemoteException{
    return (com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHome(com.idega.user.data.User.class);
  }

  public static com.idega.core.user.data.User convertToOldUser(com.idega.user.data.User user){
    try{
      Object id = user.getPrimaryKey();
      return getOldUserHome().findByPrimaryKey(id);
    }
    catch(RemoteException e){
    	//e.printStackTrace();
      throw new EJBException("RemoteException: "+e.getMessage());
    }
    catch(FinderException e){
    	//e.printStackTrace();
      throw new EJBException("FinderException: "+e.getMessage());
    }
  }

  public static com.idega.user.data.User convertToNewUser(com.idega.core.user.data.User user){
    try{
      Object id = user.getPrimaryKey();
      return getNewUserHome().findByPrimaryKey(id);
    }
    catch(RemoteException e){
    	//e.printStackTrace();
      throw new EJBException("RemoteException:"+e.getMessage());
    }
    catch(FinderException e){
    	//e.printStackTrace();
      throw new EJBException("FinderException: "+e.getMessage());
    } 
  }

}