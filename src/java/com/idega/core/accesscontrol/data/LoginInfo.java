package com.idega.core.accesscontrol.data;


public interface LoginInfo extends com.idega.data.IDOEntity,com.idega.data.MetaDataCapable
{
 public boolean getAccessClosed();
 public boolean getAccountEnabled();
 public boolean getAllowedToChange();
 public boolean getChangeNextTime();
 public int getDaysOfVality();
 public java.lang.String getEncryprionType();
 public int getFailedAttemptCount();
 public int getLoginTableId();
 public com.idega.util.IWTimestamp getModified();
 public boolean getPasswNeverExpires();
 public boolean getPasswordExpires();
 public boolean isLoginExpired();
 public boolean isLoginValid();
 public void setAccessClosed(boolean p0);
 public void setAccountEnabled(java.lang.Boolean p0);
 public void setAccountEnabled(boolean p0);
 public void setAllowedToChange(java.lang.Boolean p0);
 public void setAllowedToChange(boolean p0);
 public void setChangeNextTime(java.lang.Boolean p0);
 public void setChangeNextTime(boolean p0);
 public void setDaysOfVality(int p0);
 public void setEncriptionType(java.lang.String p0);
 public void setFailedAttemptCount(int p0);
 public void setLoginTableId(int p0);
 public void setModified(com.idega.util.IWTimestamp p0);
 public void setPasswNeverExpires(boolean p0);
 public void setPasswNeverExpires(java.lang.Boolean p0);
 public void setPasswordExpires(boolean p0);
 public void setPasswordExpires(java.lang.Boolean p0);
 public void setID(int id);
}
