package com.idega.core.accesscontrol.business;

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
  private String _login = null;
  private int _loginRecordId = -1;

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
    return this.getUser().equals(((LoggedOnInfo)obj).getUser());
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
		String name = _user.getName();
		boolean success = LoginBusinessBean.logOutUserOnSessionTimeout(event.getSession(),this);
		System.out.println("LoggedOnInfo: Session has expired logging off user: "+name+". Success = "+ success);
		
	}
  
  
}
