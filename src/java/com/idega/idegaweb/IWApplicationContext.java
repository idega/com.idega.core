package com.idega.idegaweb;

import com.idega.core.builder.data.ICDomain;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IWApplicationContext extends java.io.Serializable{

  public IWMainApplication getIWMainApplication();
  public IWMainApplicationSettings getApplicationSettings();
  public IWSystemProperties getSystemProperties();
  public void setApplicationAttribute(String attributeName,Object attributeValue);
  public Object getApplicationAttribute(String attributeName);
  public Object getApplicationAttribute(String attributeName,Object defaultObjectToReturnIfValueIsNull);
  public void removeApplicationAttribute(String attributeName);
  /**
   * Gets the Domain which this idegaWeb Application is running under
   */
  public ICDomain getDomain();
  public ICDomain getDomainByServerName(String serverName);

}
