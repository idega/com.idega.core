/*
 * $Id: IWUserContext.java,v 1.10 2005/12/07 11:51:51 tryggvil Exp $
 * Created in 2002 by Tryggvi Larusson
 *
 * Copyright (C) 2002-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.security.Principal;
import java.util.Locale;

//import com.idega.core.accesscontrol.business.AccessController;
//import com.idega.core.component.data.ICObject;
import com.idega.user.data.User;
//import com.idega.presentation.PresentationObject;
import com.idega.user.business.UserProperties;

/**
 * <p>
 * This Context object lives on in "session" scope
 * and gives access to some idegaWeb specific session 
 * bound instances such as the idegaWeb User object.
 * </p>
 * 
 * Last modified: $Date: 2005/12/07 11:51:51 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.10 $
 */
public interface IWUserContext extends java.io.Serializable{

  public Object getSessionAttribute(String attributeName);
  public void setSessionAttribute(String attributeName,Object attribute);
  public String getSessionId();
  public void removeSessionAttribute(String attributeName);
  public Locale getCurrentLocale();
  public void setCurrentLocale(Locale locale);
  /**
   * @deprecated Replaced with getCurrentUser()
   **/
  public com.idega.core.user.data.User getUser();
  //public AccessController getAccessController();
  public IWApplicationContext getApplicationContext();
  public UserProperties getUserProperties();
  /**
   * Gets the current user associated with this context
   * <br>This method is meant to replace getUser()
   * @return The current user if there is one associated with the current context. If there is none the method returns null.
   **/
  public User getCurrentUser();
  
  /**
   * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
   * @return Returns the user principal for the current user or null if he is not logged in
   */
  public Principal getUserPrincipal();
  
  /**
   * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
   * @return Returns the loginname of the current user or null if he is not logged in
   */
  public String getRemoteUser();

  /**
   * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
   * @return Returns true if user is in role, else false;
   */
  public boolean isUserInRole(String role);


  //temp
  /*public boolean hasPermission(String permissionKey, PresentationObject obj);
  public boolean hasViewPermission(PresentationObject obj);
  public boolean hasEditPermission(PresentationObject obj);
  public boolean hasPermission(List groupIds, String permissionKey, PresentationObject obj);
  public boolean hasFilePermission(String permissionKey, int id);
  public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId);
  public boolean hasViewPermission(List groupIds, PresentationObject obj);
  public boolean hasEditPermission(List groupIds, PresentationObject obj);*/
  public boolean isSuperAdmin();
  public boolean isLoggedOn();


}
