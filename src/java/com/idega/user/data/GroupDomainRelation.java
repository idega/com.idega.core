package com.idega.user.data;


public interface GroupDomainRelation extends com.idega.data.IDOEntity
{
 public com.idega.core.builder.data.ICDomain getDomain();
 public int getPassiveBy();
 public com.idega.user.data.Group getRelatedGroup();
 public java.lang.Integer getRelatedGroupPK();
 public com.idega.user.data.GroupDomainRelationType getRelationship();
 public void initializeAttributes();
 public void removeBy(com.idega.user.data.User p0)throws javax.ejb.RemoveException;
 public void setDomain(com.idega.core.builder.data.ICDomain p0);
 public void setDomain(int p0);
 public void setPassiveBy(int p0);
 public void setRelatedGroup(com.idega.user.data.Group p0);
 public void setRelatedGroup(int p0);
 public void setRelatedUser(com.idega.user.data.User p0);
 public void setRelationship(com.idega.user.data.GroupDomainRelationType p0);
}
