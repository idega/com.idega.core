package com.idega.idegaweb;

import javax.servlet.ServletContext;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IWApplicationContext {

  public IWMainApplication getApplication();
  public IWMainApplicationSettings getApplicationSettings();
  public void setApplicationAttribute(String attributeName,Object attributeValue);
  public Object getApplicationAttribute(String attributeName);
  public void removeApplicationAttribute(String attributeName);

}