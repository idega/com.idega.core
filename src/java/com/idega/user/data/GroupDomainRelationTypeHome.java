package com.idega.user.data;


public interface GroupDomainRelationTypeHome extends com.idega.data.IDOHome
{
 public GroupDomainRelationType create() throws javax.ejb.CreateException;
 public GroupDomainRelationType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public com.idega.user.data.GroupDomainRelationType getTopNodeRelationType()throws javax.ejb.FinderException;
 public java.lang.String getTopNodeRelationTypeString();

}