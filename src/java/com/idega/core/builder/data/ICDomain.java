package com.idega.core.builder.data;

import javax.ejb.*;

public interface ICDomain extends com.idega.data.IDOLegacyEntity
{
 public int getStartTemplateID();
 public void setName(java.lang.String p0);
 public int getStartPageID();
 public java.util.Collection getTopLevelGroupsUnderDomain()throws com.idega.data.IDORelationshipException, java.rmi.RemoteException, FinderException;
 public java.lang.String getURL();
 public java.lang.String getName();
 public void setStartTemplate(com.idega.core.builder.data.ICPage p0);
 public java.lang.String getDomainName();
 public void setIBPage(com.idega.core.builder.data.ICPage p0);
 public com.idega.core.builder.data.ICPage getStartPage();
 public com.idega.core.builder.data.ICPage getStartTemplate();
 public void setServerName(String serverName);
 public String getServerName();
}
