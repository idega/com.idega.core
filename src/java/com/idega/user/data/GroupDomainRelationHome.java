package com.idega.user.data;


public interface GroupDomainRelationHome extends com.idega.data.IDOHome
{
 public GroupDomainRelation create() throws javax.ejb.CreateException;
 public GroupDomainRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findDomainsRelationshipsContaining(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public java.util.Collection findDomainsRelationshipsContaining(com.idega.core.builder.data.ICDomain p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException;
 public java.util.Collection findDomainsRelationshipsContaining(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsUnder(com.idega.core.builder.data.ICDomain p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsUnder(com.idega.core.builder.data.ICDomain p0,com.idega.user.data.GroupDomainRelationType p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsUnder(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsUnderDomainByRelationshipType(int p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.lang.String getFindGroupsDomainRelationshipsContainingSQL(int p0,java.lang.String p1);
 public java.lang.String getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(int p0,java.lang.String p1);

}