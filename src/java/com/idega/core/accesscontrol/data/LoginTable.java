package com.idega.core.accesscontrol.data;

import com.idega.core.user.data.User;


public interface LoginTable extends com.idega.data.IDOLegacyEntity,com.idega.util.EncryptionType
{
 public java.sql.Timestamp getLastChanged();
 public int getUserId();
 public User getUser();
 public java.lang.String getUserLogin();
 public java.lang.String getUserPassword();
 public void setDefaultValues();
 public void setLastChanged(java.sql.Timestamp p0);
 public void setUserId(java.lang.Integer p0);
 public void setUserId(int p0);
 public void setUserLogin(java.lang.String p0);
 public void setUserPassword(java.lang.String p0);
 public String getUserPasswordInClearText();
 public void setLoginType(String loginType);
 public String getLoginType();

  /**
  * Sets both the intented encrypted password and the original unencrypted password for temporary retrieval
  */
 public void setUserPassword(java.lang.String encryptedPassword,String unEncryptedPassword)throws java.rmi.RemoteException;
 /**
 * Gets the original password if the record is newly created, therefore it can be retrieved , if this is not a newly created record the exception PasswordNotKnown is thrown
 */
 public String getUnencryptedUserPassword()throws PasswordNotKnown,java.rmi.RemoteException;

}
