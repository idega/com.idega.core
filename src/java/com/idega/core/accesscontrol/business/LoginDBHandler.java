package com.idega.core.accesscontrol.business;

import com.idega.util.idegaTimestamp;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.data.EntityFinder;
import com.idega.core.user.data.User;
import java.util.List;
import java.sql.SQLException;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class LoginDBHandler {


  public LoginDBHandler() {

  }

  protected static int createLogin( boolean update, int userID, String userLogin, String password) throws Exception {

    List noLogin = EntityFinder.findAllByColumn(LoginTable.getStaticInstance(), LoginTable.getUserIDColumnName(), userID);

    LoginTable loginTable;
    if(update){
      if(noLogin == null){
        throw new Exception("User_id : " + userID + " , cannot update login : cannot find login");
      }
      loginTable = (LoginTable)noLogin.get(0);
      if(loginTable.getUserId() != userID || loginTable == null){
        throw new Exception("Login update failed.");
      }
    }else{
      if(noLogin != null){
        throw new Exception("User_id : " + userID + " , cannot create new login : user has one already");
      }
      loginTable = new LoginTable();
    }


    if(userID > 0 && !update){
      loginTable.setUserId(userID);
    }else if(!update){
      throw new Exception("invalid user_id");
    }

    if(update){
      if(userLogin != null && !"".equals(userLogin)){
        loginTable.setUserPassword(password);
      }
    }else{
      if( userLogin != null && !"".equals(userLogin)){
        noLogin = EntityFinder.findAllByColumn(LoginTable.getStaticInstance(), LoginTable.getStaticInstance().getUserLoginColumnName(), userLogin);
        if(noLogin != null ){
          throw new Exception("login not valid : in use");
        }
        loginTable.setUserLogin(userLogin);
      }else{
        throw new Exception("Login not valid: null or emptyString");
      }
    }


    if( password != null && !"".equals(password)){
      loginTable.setUserPassword(password);
    }else if (!update){
      throw new Exception("Password not valid");
    }


    try {
      if(update){
        loginTable.update();
      }else{
        loginTable.insert();
      }
    }
    catch (SQLException ex) {
      if(update){
        throw new Exception("Login update failed");
      }else{
        throw new Exception("Login creation failed");
      }
    }


    if(loginTable.getID() < 1 && !update){
      throw new Exception("Login creation failed, login_id : " + loginTable.getID());
    }

    return loginTable.getID();
 }


  protected static int createLoginInfo(boolean update, int loginTableID ,Boolean accountEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {
    List noLoginInfo = EntityFinder.findAllByColumn(LoginInfo.getStaticInstance(), LoginInfo.getLoginTableIdColumnName(), loginTableID);

    LoginInfo logInfo;
    if(update){
      if(noLoginInfo == null){
        throw new Exception("login_id : " + loginTableID + " , cannot update loginInfo : cannot find loginInfo");
      }
      logInfo = (LoginInfo)noLoginInfo.get(0);
      if(logInfo.getID() != loginTableID || logInfo == null){
        throw new Exception("LoginInfo update failed. ");
      }
    }else{
      if(noLoginInfo != null){
        throw new Exception("login_id : " + loginTableID + " , cannot create new loginInfo : user has one already");
      }
      logInfo = new LoginInfo();
    }

    logInfo.setID(loginTableID);

    if( accountEnabled != null){
      logInfo.setAccountEnabled(accountEnabled);
    }

    if( modified != null){
      logInfo.setModified(modified);
    }else{
      logInfo.setModified(idegaTimestamp.RightNow());
    }

    logInfo.setDaysOfVality(daysOfVality);

    if(passwNeverExpires != null){
      logInfo.setPasswNeverExpires(passwNeverExpires);
    }

    if(userAllowedToChangePassw != null){
      logInfo.setAllowedToChange(userAllowedToChangePassw);
    }

    if(changeNextTime != null){
      logInfo.setChangeNextTime(changeNextTime);
    }

    if(!logInfo.getAllowedToChange() && logInfo.getChangeNextTime()){
      throw new Exception("inconsistency: userAllowedToChangePassw = false and changeNextTime = true");
    }


    try {
      if(update){
        logInfo.update();
      }else{
        logInfo.insert();
      }
    }
    catch (SQLException ex) {
      if(update){
        throw new Exception("LoginInfo update failed. ");
      }else{
        throw new Exception("LoginInfo creation failed. ");
      }
    }

    return logInfo.getID();

  }


  public static void createLogin( int userID, String userLogin, String password, Boolean accountEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {

      int loginTableID = createLogin( false, userID, userLogin, password);
      try {
        createLoginInfo(false, loginTableID ,accountEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime);
      }
      catch (Exception e) {
        if ("LoginInfo creation failed. ".equals(e.getMessage())){
          try {
            new LoginTable(loginTableID).delete();
            throw new Exception(e.getMessage()+"LoginTable entry was removed");
          }
          catch(SQLException sql){
            sql.printStackTrace();
            throw new Exception(e.getMessage()+"Transaction faild: LoginTable entry failed to remove");
          }
        }else{
          throw e;
        }
      }

  }


  public static void updateLogin( int userID, String userLogin, String password, Boolean accountEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {
    int loginTableID = createLogin( true, userID, userLogin, password);
    createLoginInfo(true, loginTableID ,accountEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime);
  }

  public static void updateLoginInfo(int loginTableID ,Boolean accoutEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {
    createLoginInfo(true, loginTableID ,accoutEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime);
  }


  public static void createLogin( int userID, String userLogin, String password) throws Exception {
    createLogin(userID, userLogin, password,null,null,-1,null,null,null);
  }


  public static void updateLogin( int userID, String userLogin, String password) throws Exception {
    createLogin( true, userID, userLogin, password);
  }

}