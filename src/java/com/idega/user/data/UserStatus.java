package com.idega.user.data;


public interface UserStatus extends com.idega.data.IDOEntity
{
 public com.idega.user.data.Group getGroup();
 public int getGroupId();
 public com.idega.user.data.Status getStatus();
 public int getStatusId();
 public com.idega.user.data.User getUser();
 public int getUserId();
 public void initializeAttributes();
 public void setGroup(com.idega.user.data.Group p0);
 public void setGroupId(int p0);
 public void setStatus(com.idega.user.data.Status p0);
 public void setStatusId(java.lang.String p0);
 public void setUser(com.idega.user.data.User p0);
 public void setUserId(int p0);
}
