package com.idega.core.accesscontrol.data;

import com.idega.user.data.User;


public interface LoginRecord extends com.idega.data.IDOEntity
{
 public java.lang.String getIPAdress();
 public java.sql.Timestamp getLogInStamp();
 public java.sql.Timestamp getLogOutStamp();
 public int getLoginId();
 public void initializeAttributes();
 public void setIPAdress(java.lang.String p0);
 public void setLogInStamp(java.sql.Timestamp p0);
 public void setLogOutStamp(java.sql.Timestamp p0);
 public void setLoginId(int p0);
 
 public int getLoginAsUserID();
 public void setLoginAsUserID(int userId);
 public User getLoginAsUser();
 public void setLoginAsUser(User user);
 
 
}
