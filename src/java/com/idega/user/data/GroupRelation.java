package com.idega.user.data;

import javax.ejb.*;

public interface GroupRelation extends com.idega.data.IDOEntity
{
 public com.idega.user.data.Group getGroup();
 public java.sql.Timestamp getInitiationDate();
 public int getPassiveBy();
 public com.idega.user.data.Group getRelatedGroup();
 public java.lang.Integer getRelatedGroupPK();
 public com.idega.user.data.GroupRelationType getRelationship();
 public java.lang.String getRelationshipType();
 public java.lang.String getStatus();
 public java.sql.Timestamp getTerminationDate();
 public void initializeAttributes();
 public boolean isActive();
 public boolean isPassive();
 public void setActive();
 public void setGroup(com.idega.user.data.Group p0);
 public void setGroup(int p0);
 public void setInitiationDate(java.sql.Timestamp p0);
 public void setPassive();
 public void setPassiveBy(int p0);
 public void setRelatedGroup(com.idega.user.data.Group p0);
 public void setRelatedGroup(int p0);
 public void setRelatedUser(com.idega.user.data.User p0);
 public void setRelationship(com.idega.user.data.GroupRelationType p0);
 public void setRelationshipType(java.lang.String p0);
 public void setStatus(java.lang.String p0);
 public void setTerminationDate(java.sql.Timestamp p0);
 public void removeBy(com.idega.user.data.User p0) throws RemoveException;
}
