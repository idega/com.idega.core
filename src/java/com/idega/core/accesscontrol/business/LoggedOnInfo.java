/*
 * $Id: LoggedOnInfo.java,v 1.19 2006/05/18 16:18:33 thomas Exp $
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
import com.idega.core.accesscontrol.jaas.IWCredential;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * <p>
 * An instance of this class is stored in HttpSession for each logged in user in idegaWeb.<br/>
 * This class implements HttpSessionBindingListener so that the login information is cleaned 
 * up when the users session times out.
 * </p>
 *
 * Last modified: $Date: 2006/05/18 16:18:33 $ by $Author: thomas $
 *
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,
 * 		   <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.19 $
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
  private Map credentials = new HashMap(0);

  

public LoggedOnInfo() {
	// empty
  }
  //setters
  public void setUser(User user){
    this._user = user;
  }

/*
  public void setSession(HttpSession session){
    _session = session;
  }
*/

  public void setTimeOfLogon(IWTimestamp timeOfLogon){
    this._timeOfLogon = timeOfLogon;
  }

  public void setLogin(String login){
    this._login = login;
  }

  public void setLoginRecord(LoginRecord loginRecord){
    this._loginRecord = loginRecord;
  }
  
  public void setEncryptionType(String encryptionType){
  	this._encryptionType = encryptionType;
  }

  //getters
  public User getUser(){
    return this._user;
  }

/*
  public HttpSession getSession(){
    return _session;
  }
*/


  public IWTimestamp getTimeOfLogon(){
    return this._timeOfLogon;
  }

  public String getLogin(){
    return this._login;
  }

  public LoginRecord getLoginRecord(){
    return this._loginRecord;
  }

  public String getEncryptionType(){
  	return this._encryptionType;
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
		if(this._user != null){
			name = this._user.getName();
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
		return this._loginType;
	}
	
	/**
	 * @param loginType
	 */
	public void setLoginType(String loginType) {
		this._loginType = loginType;
	}

	/**
	 * @return
	 */
	public LoginTable getLoginTable() {
		return this._loginTable;
	}

	/**
	 * @param id
	 */
	public void setLoginTable(LoginTable login) {
		this._loginTable = login;
	}

	/**
	 * @return Returns the user role String's.
	 */
	public Set getUserRoles() {
		return this._userRoles;
	}
	/**
	 * @param roles Collections of the role String's that the user has.
	 */
	public void setUserRoles(Set roles) {
		this._userRoles = roles;
	}
	
	public void setAttribute(Object key, Object value){
		this._loggedOnInfoAttribute.put(key,value);
	}
	
	public Object getAttribute(Object key){
		return this._loggedOnInfoAttribute.get(key);
	}
	
	public IWCredential putCredential(String originator, IWCredential credential) {
		return (IWCredential) credentials.put(originator, credential);
	}
	
	/**
	 * @return Returns the credentials.
	 */
	public Map getCredentials() {
		return credentials;
	}

}
