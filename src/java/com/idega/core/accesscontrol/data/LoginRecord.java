package com.idega.core.accesscontrol.data;

import javax.ejb.*;

public interface LoginRecord extends com.idega.data.IDOEntity
{
 public void setLoginId(int p0);
 public int getLoginId();
 public void setLogOutStamp(java.sql.Timestamp p0);
 public void setIPAdress(java.lang.String p0);
 public java.sql.Timestamp getLogInStamp();
 public java.lang.String getIPAdress();
 public java.sql.Timestamp getLogOutStamp();
 public void setLogInStamp(java.sql.Timestamp p0);
}
