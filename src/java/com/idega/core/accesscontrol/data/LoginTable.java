package com.idega.core.accesscontrol.data;

import com.idega.user.data.Group;
import com.idega.user.data.User;


public interface LoginTable extends com.idega.data.IDOLegacyEntity,com.idega.util.EncryptionType
{
 public java.sql.Timestamp getLastChanged();
 public int getUserId();
 public User getUser();
 public java.lang.String getUserLogin();
 public java.lang.String getUserPassword();
 public void setDefaultValues();
 public void setLastChanged(java.sql.Timestamp p0);
 /**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setChangedByGroup
	 */
	public void setChangedByGroup(Group group);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setChangedByGroupId
	 */
	public void setChangedByGroupId(int changedByGroupId);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getChangedByGroupId
	 */
	public int getChangedByGroupId();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getChangedByGroup
	 */
	public Group getChangedByGroup();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setChangedByUser
	 */
	public void setChangedByUser(User changedByUser);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setChangedByUserId
	 */
	public void setChangedByUserId(int changedByUserId);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getChangedByUserId
	 */
	public int getChangedByUserId();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getChangedByUser
	 */
	public User getChangedByUser();
 
 public void setUserId(java.lang.Integer p0);
 public void setUserId(int p0);
 public void setUserLogin(java.lang.String p0);
 public void setUserPassword(java.lang.String p0);
 /**
  * just sets the password column value as this string without encoding.
  */
 public void setUserPasswordInClearText(String password);
 /**
  * just returns the password column value as is.
  */
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
