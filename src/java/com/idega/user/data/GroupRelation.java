package com.idega.user.data;

import java.sql.Timestamp;


public interface GroupRelation extends com.idega.data.IDOEntity
{

 public static final String FIELD_INITIATION_DATE = "INITIATION_DATE";
 public static final String FIELD_GROUP = "IC_GROUP_ID";
 public static final String FIELD_RELATED_GROUP = "RELATED_IC_GROUP_ID";
 public static final String FIELD_STATUS = "GROUP_RELATION_STATUS";
 public final static String STATUS_ACTIVE="ST_ACTIVE";
 public final static String STATUS_PASSIVE="ST_PASSIVE";
 public final static String STATUS_PASSIVE_PENDING="PASS_PEND";
 public final static String STATUS_ACTIVE_PENDING="ACT_PEND";


	
 public com.idega.user.data.Group getGroup();
 public int getGroupID();
 public java.sql.Timestamp getInitiationDate();
 public Timestamp getInitiationModificationDate();
 public int getPassiveBy();
 public com.idega.user.data.Group getRelatedGroup();
 public java.lang.Integer getRelatedGroupPK();
 public com.idega.user.data.GroupRelationType getRelationship();
 public java.lang.String getRelationshipType();
 public java.lang.String getStatus();
 public java.sql.Timestamp getTerminationDate();
 public Timestamp getTerminationModificationDate();
 public void initializeAttributes();
 public boolean isActive();
 public boolean isActivePending();
 public boolean isPassive();
 public boolean isPassivePending();
 public boolean isPending();
 public void removeBy(com.idega.user.data.User p0)throws javax.ejb.RemoveException;
 public void removeBy(com.idega.user.data.User p0,java.sql.Timestamp p1)throws javax.ejb.RemoveException;
 public void setActive();
 public void setActivePending();
 public void setGroup(int p0);
 public void setGroup(com.idega.user.data.Group p0);
 public void setInitiationDate(java.sql.Timestamp p0);
 public void setPassive();
 public void setPassiveBy(int p0);
 public void setPassivePending();
 public void setRelatedGroup(int p0);
 public void setRelatedGroup(com.idega.user.data.Group p0);
 public void setRelatedGroupType(java.lang.String p0);
 public void setRelatedUser(com.idega.user.data.User p0);
 public void setRelationship(com.idega.user.data.GroupRelationType p0);
 public void setRelationshipType(java.lang.String p0);
 public void setStatus(java.lang.String p0);
 public void setTerminationDate(java.sql.Timestamp p0);
}
