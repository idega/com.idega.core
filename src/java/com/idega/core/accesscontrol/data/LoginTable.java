package com.idega.core.accesscontrol.data;

import javax.ejb.*;

public interface LoginTable extends com.idega.data.IDOLegacyEntity,com.idega.util.EncryptionType
{
 public java.sql.Timestamp getLastChanged();
 public int getUserId();
 public java.lang.String getUserLogin();
 public java.lang.String getUserPassword();
 public void setDefaultValues();
 public void setLastChanged(java.sql.Timestamp p0);
 public void setUserId(java.lang.Integer p0);
 public void setUserId(int p0);
 public void setUserLogin(java.lang.String p0);
 public void setUserPassword(java.lang.String p0);
}
