package com.idega.core.builder.data;

import javax.ejb.*;

public interface ICDomain extends com.idega.data.IDOLegacyEntity
{
 public int getStartTemplateID();
 public void setName(java.lang.String p0);
 public int getStartPageID();
 public java.util.Collection getTopLevelGroupsUnderDomain()throws com.idega.data.IDORelationshipException, java.rmi.RemoteException, FinderException;
 /**
  * Get the base full http URL to this server. Note that this does not include the context path.<br/>
  * Returns something like: 'http://myserver.com:8080/'
  */ 
 public java.lang.String getURL();
 /**
  * Get the base full http URL to this server without the last slash. Note that this does not include the context path.<br/>
  * Returns something like: 'http://myserver.com:8080'
  */  
 public java.lang.String getURLWithoutLastSlash();
 
 public void setURL(String url);
 /**
  * Gets the name of the domain.<br/>
  * This is set by default 'Default Domain'.
  */  
 public java.lang.String getName();
 public void setStartTemplate(com.idega.core.builder.data.ICPage p0);
 public java.lang.String getDomainName();
 public void setDomainName(String domainName);
 public void setIBPage(com.idega.core.builder.data.ICPage p0);
 public com.idega.core.builder.data.ICPage getStartPage();
 public com.idega.core.builder.data.ICPage getStartTemplate();
 public void setServerName(String serverName);
 /**
  * Get the server name (Domain Name) example: 'www.idega.com' that is set on the server.
  */ 
 public String getServerName();
 /**
  * Get the server port that the http listener is listening on.
  */ 
 public int getServerPort();
 public void setServerPort(int serverPort);
 /**
  * Get the server context path that the server is running under.
  * Returns something like: '/mywebapp'
  */
 public String getServerContextPath();
 public void setServerContextPath(String contextPath);
 /**
  * Get the server port that the http listener is listening on.
  */ 
 public String getServerProtocol();
 public void setServerProtocol(String protocol); 
 /**
  * Get the UUID that uniquely identifies this domain
  */
 public String getUniqueId();
}
