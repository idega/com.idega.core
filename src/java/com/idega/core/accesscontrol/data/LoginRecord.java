package com.idega.core.accesscontrol.data;

import javax.ejb.*;

public interface LoginRecord extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getIPAdress();
 public java.sql.Timestamp getLogInStamp();
 public java.sql.Timestamp getLogOutStamp();
 public int getLoginId();
 public void setIPAdress(java.lang.String p0);
 public void setLogInStamp(java.sql.Timestamp p0);
 public void setLogOutStamp(java.sql.Timestamp p0);
 public void setLoginId(int p0);
}
