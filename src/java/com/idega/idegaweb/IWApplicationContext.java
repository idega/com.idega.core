package com.idega.idegaweb;

import com.idega.builder.data.IBDomain;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IWApplicationContext extends java.io.Serializable{

  public IWMainApplication getApplication();
  public IWMainApplicationSettings getApplicationSettings();
  public IWSystemProperties getSystemProperties();
  public void setApplicationAttribute(String attributeName,Object attributeValue);
  public Object getApplicationAttribute(String attributeName);
  public void removeApplicationAttribute(String attributeName);
  /**
   * Gets the Domain which this idegaWeb Application is running under
   */
  public IBDomain getDomain();

}
