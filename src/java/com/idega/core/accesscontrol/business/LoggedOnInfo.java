package com.idega.core.accesscontrol.business;

import javax.servlet.http.HttpSession;
import com.idega.core.user.data.User;
import com.idega.util.idegaTimestamp;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class LoggedOnInfo {

  private User _user = null;
  private HttpSession _session = null;
  private idegaTimestamp _timeOfLogon = null;
  private String _login = null;

  public LoggedOnInfo() {

  }
  //setters
  public void setUser(User user){
    _user = user;
  }

  public void setSession(HttpSession session){
    _session = session;
  }

  public void setTimeOfLogon(idegaTimestamp timeOfLogon){
    _timeOfLogon = timeOfLogon;
  }

  public void setLogin(String login){
    _login = login;
  }

  //getters
  public User getUser(){
    return _user;
  }

  public HttpSession getSession(){
    return _session;
  }

  public idegaTimestamp getTimeOfLogon(){
    return _timeOfLogon;
  }

  public String getLogin(){
    return _login;
  }


  //

  public boolean equals(Object obj){
    if(obj instanceof LoggedOnInfo ){
      return this.equals((LoggedOnInfo)obj);
    }else if(obj instanceof HttpSession){
      return this.equals((HttpSession)obj);
    }else {
      return super.equals(obj);
    }
  }

  public boolean equals(LoggedOnInfo obj){
    return this.getSession().equals((HttpSession)((LoggedOnInfo)obj).getSession());
  }

  public boolean equals(HttpSession obj){
    return this.getSession().equals((HttpSession)obj);
  }
}