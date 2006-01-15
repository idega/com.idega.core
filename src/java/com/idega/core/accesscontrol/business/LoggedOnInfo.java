/*
 * $Id: LoggedOnInfo.java,v 1.16 2006/01/15 17:29:35 laddi Exp $
 * 
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import com.idega.core.accesscontrol.data.LoginRecord;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * <p>
 * An instance of this class is stored in HttpSession for each logged in user in idegaWeb.<br/>
 * This class implements HttpSessionBindingListener so that the login information is cleaned 
 * up when the users session times out.
 * </p>
 *
 * Last modified: $Date: 2006/01/15 17:29:35 $ by $Author: laddi $
 *
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,
 * 		   <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.16 $
 */
public class LoggedOnInfo implements HttpSessionBindingListener  {

  private User _user = null;
//  private HttpSession _session = null; 
  private IWTimestamp _timeOfLogon = null;
  private LoginTable _loginTable;
  private String _login = null;
  private LoginRecord _loginRecord;
  private String _encryptionType = null;
  private String _loginType = null;
  private Set _userRoles = null;
  private Map _loggedOnInfoAttribute = new HashMap();

  public LoggedOnInfo() {

  }
  //setters
  public void setUser(User user){
    _user = user;
  }

/*
  public void setSession(HttpSession session){
    _session = session;
  }
*/

  public void setTimeOfLogon(IWTimestamp timeOfLogon){
    _timeOfLogon = timeOfLogon;
  }

  public void setLogin(String login){
    _login = login;
  }

  public void setLoginRecord(LoginRecord loginRecord){
    _loginRecord = loginRecord;
  }
  
  public void setEncryptionType(String encryptionType){
  	_encryptionType = encryptionType;
  }

  //getters
  public User getUser(){
    return _user;
  }

/*
  public HttpSession getSession(){
    return _session;
  }
*/


  public IWTimestamp getTimeOfLogon(){
    return _timeOfLogon;
  }

  public String getLogin(){
    return _login;
  }

  public LoginRecord getLoginRecord(){
    return _loginRecord;
  }

  public String getEncryptionType(){
  	return _encryptionType;
  }


  //

  public boolean equals(Object obj){
    if(obj instanceof LoggedOnInfo ){
      return this.equals((LoggedOnInfo)obj);
    }
    
    return false;
    /*else if(obj instanceof HttpSession){
      return this.equals((HttpSession)obj);
    }
    else {
      return super.equals(obj);
    }*/
  }


  public boolean equals(LoggedOnInfo obj){
    return this.getUser().equals(obj.getUser());
  }
/*
  public boolean equals(HttpSession obj){
    return this.getSession().equals((HttpSession)obj);
  }
  
  
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent event) {
		//do nothing
		
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		//log out!
		String name = "Unknown";
		if(_user != null){
			name = _user.getName();
		}
		HttpSession session = event.getSession();
		LoginBusinessBean loginBean = LoginBusinessBean.getLoginBusinessBean(session);
		boolean success = loginBean.logOutUserOnSessionTimeout(session,this);
		System.out.println("LoggedOnInfo: Session has expired logging off user: "+name+". Success = "+ success);
		
	}
  
  	

	/**
	 * @return
	 */
	public String getLoginType() {
		return _loginType;
	}
	
	/**
	 * @param loginType
	 */
	public void setLoginType(String loginType) {
		_loginType = loginType;
	}

	/**
	 * @return
	 */
	public LoginTable getLoginTable() {
		return _loginTable;
	}

	/**
	 * @param id
	 */
	public void setLoginTable(LoginTable login) {
		_loginTable = login;
	}

	/**
	 * @return Returns the user role String's.
	 */
	public Set getUserRoles() {
		return _userRoles;
	}
	/**
	 * @param roles Collections of the role String's that the user has.
	 */
	public void setUserRoles(Set roles) {
		_userRoles = roles;
	}
	
	public void setAttribute(Object key, Object value){
		_loggedOnInfoAttribute.put(key,value);
	}
	
	public Object getAttribute(Object key){
		return _loggedOnInfoAttribute.get(key);
	}
}
