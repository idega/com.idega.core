package com.idega.core.accesscontrol.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import com.idega.core.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class LoggedOnInfo implements HttpSessionBindingListener  {

  private User _user = null;
//  private HttpSession _session = null; 
  private IWTimestamp _timeOfLogon = null;
  private int _loginTableId = -1;
  private String _login = null;
  private int _loginRecordId = -1;
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

  public void setLoginRecordId(int loginRecordId){
    _loginRecordId = loginRecordId;
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

  public int getLoginRecordId(){
    return _loginRecordId;
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
		
		boolean success = LoginBusinessBean.logOutUserOnSessionTimeout(event.getSession(),this);
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
	public int getLoginTableId() {
		return _loginTableId;
	}

	/**
	 * @param id
	 */
	public void setLoginTableId(int id) {
		_loginTableId = id;
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
