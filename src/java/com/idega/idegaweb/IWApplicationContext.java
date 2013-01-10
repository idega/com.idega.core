/*
 * $Id: IWApplicationContext.java,v 1.13 2005/11/25 14:23:48 tryggvil Exp $
 * Created in 2002 by Tryggvi Larusson
 *
 * Copyright (C) 2002-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import com.idega.core.builder.data.ICDomain;

/**
 * <p>
 * This Context object lives on in "application" (or servletcontext) scope
 * and gives access to some idegaWeb specific application bound instances such
 * as IWMainApplication.
 * </p>
 *
 * Last modified: $Date: 2005/11/25 14:23:48 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.13 $
 */
public interface IWApplicationContext extends java.io.Serializable{

  public IWMainApplication getIWMainApplication();
  public IWMainApplicationSettings getApplicationSettings();
  public IWSystemProperties getSystemProperties();
  public <V extends Object> void setApplicationAttribute(String attributeName, V attributeValue);
  public <V extends Object> V getApplicationAttribute(String attributeName);
  public <V extends Object> V getApplicationAttribute(String attributeName, V defaultObjectToReturnIfValueIsNull);
  public void removeApplicationAttribute(String attributeName);
  /**
   * Gets the Domain which this idegaWeb Application is running under
   */
  public ICDomain getDomain();
  public ICDomain getDomainByServerName(String serverName);

}
