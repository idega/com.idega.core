package com.idega.core.accesscontrol.business;



import com.idega.util.idegaTimestamp;

import com.idega.core.accesscontrol.data.LoginTable;

import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.LoginInfoHome;

import com.idega.core.accesscontrol.data.LoginRecord;
import com.idega.core.accesscontrol.data.LoginRecordHome;
import java.util.Iterator;
import javax.ejb.FinderException;
import java.rmi.RemoteException;
import javax.ejb.RemoveException;
import com.idega.data.IDORemoveException;


import com.idega.data.EntityFinder;

import com.idega.core.user.data.User;

import java.util.List;

import java.sql.SQLException;

import com.idega.util.Encrypter;





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



    List noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(), userID);





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

      loginTable = ((com.idega.core.accesscontrol.data.LoginTableHome)com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).createLegacy();

    }





    if(userID > 0 && !update){

      loginTable.setUserId(userID);

    }else if(!update){

      throw new Exception("invalid user_id");

    }





    String encryptedPassword = null;



    if( password != null && !"".equals(password)){

      encryptedPassword = Encrypter.encryptOneWay(password);

      loginTable.setUserPassword(encryptedPassword);

    }else if (!update){

      throw new Exception("Password not valid");

    }



    if(update){

      if(userLogin != null && !"".equals(userLogin)){

        if (!loginTable.getUserLogin().equals(userLogin)) {

          noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), userLogin);

          if (noLogin != null && (noLogin.size() > 0 && ((LoginTable)noLogin.get(0)).getUserId() != userID)) {

            LoginTable tempLoginTable = (LoginTable) noLogin.get(0);

            if (tempLoginTable.getUserId() != userID)

            throw new Exception("login not valid : in use");

          }

        }

        if(encryptedPassword != null){

          loginTable.setUserPassword(encryptedPassword);

          loginTable.setUserLogin(userLogin);

        }else{

          throw new Exception("Password not valid");

        }

      }

    }else{

      if( userLogin != null && !"".equals(userLogin)){

        noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), userLogin);

        if(noLogin != null ){

          throw new Exception("login not valid : in use");

        }

        loginTable.setUserLogin(userLogin);

      }else{

        throw new Exception("Login not valid: null or emptyString");

      }

    }



    loginTable.setLastChanged(idegaTimestamp.getTimestampRightNow());



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









  protected static int createLoginInfo(boolean update, int loginTableID ,Boolean accountEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {

    List noLoginInfo = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginInfoBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginInfoBMPBean.getLoginTableIdColumnName(), loginTableID);



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

      logInfo = ((com.idega.core.accesscontrol.data.LoginInfoHome)com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).createLegacy();

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



    if(daysOfVality > -1){

      logInfo.setDaysOfVality(daysOfVality);

    }



    if(passwordExpires != null){

      logInfo.setPasswordExpires(passwordExpires);

    }



    if(userAllowedToChangePassw != null){

      logInfo.setAllowedToChange(userAllowedToChangePassw);

    }



    if(changeNextTime != null){

      logInfo.setChangeNextTime(changeNextTime);

    }



    if(encryptionType != null){

      logInfo.setEncriptionType(encryptionType);

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





  public static void createLogin( int userID, String userLogin, String password, Boolean accountEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {



      int loginTableID = createLogin( false, userID, userLogin, password);

      try {

        createLoginInfo(false, loginTableID ,accountEnabled, modified, daysOfVality, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);

      }

      catch (Exception e) {

        if ("LoginInfo creation failed. ".equals(e.getMessage())){

          try {

            ((com.idega.core.accesscontrol.data.LoginTableHome)com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).findByPrimaryKeyLegacy(loginTableID).delete();

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



  /**

   * @deprecated

   */

  public static void updateLogin( int userID, String userLogin, String password, Boolean accountEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {

    updateLogin( userID, userLogin, password, accountEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, null);

  }



  public static void updateLogin( int userID, String userLogin, String password, Boolean accountEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {

    int loginTableID = createLogin( true, userID, userLogin, password);

    createLoginInfo(true, loginTableID ,accountEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, encryptionType);

  }



  /**

   * @deprecated

   */

  public static void updateLoginInfo(int loginTableID ,Boolean accoutEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {

    updateLoginInfo( loginTableID , accoutEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, null);

  }



  public static void updateLoginInfo(int loginTableID ,Boolean accoutEnabled, idegaTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {

    createLoginInfo(true, loginTableID ,accoutEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, encryptionType);

  }





  public static void createLogin( int userID, String userLogin, String password) throws Exception {

    createLogin(userID, userLogin, password,null,null,-1,null,null,null,null);

  }





  public static void updateLogin( int userID, String userLogin, String password) throws Exception {

    createLogin( true, userID, userLogin, password);

  }



  public static void changePassword(int userID, String password ) throws Exception {



    LoginTable loginTable;



    List noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(), userID);

    loginTable = (LoginTable)noLogin.get(0);



    if(loginTable != null){

      loginTable.setUserPassword(Encrypter.encryptOneWay(password));

      loginTable.update();

    } else {

      throw new Exception("Cannot update. Login does not exist");

    }



  }



  /**

   * @deprecated use getUserLogin

   */

  public static LoginTable findUserLogin(int iUserId){

    return getUserLogin(iUserId);

  }



  public static LoginTable getUserLogin(int userId){

    LoginTable LT = null;

    try {

      LoginTable l = com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance();

      List list = EntityFinder.findAllByColumn(l,com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(),userId);

      if(list != null){

        LT = (LoginTable) list.get(0);

      }

    }

    catch (SQLException ex) {

      ex.printStackTrace();

      LT = null;

    }

    return LT;

  }



  public static LoginInfo getLoginInfo(int loginTableId){
    LoginInfo li = null;
    try {
      li = ((com.idega.core.accesscontrol.data.LoginInfoHome)com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).findByPrimaryKeyLegacy(loginTableId);
    } catch (SQLException ex) {
      try {
        li = ((com.idega.core.accesscontrol.data.LoginInfoHome)com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).createLegacy();
        li.setID(loginTableId);
        li.insert();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return li;

  }



  public static void deleteUserLogin(int userId)throws SQLException {
    deleteLogin(findUserLogin(userId));
  }



  public static boolean isLoginInUse(String login){

    try {

      return (0 < com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance().getNumberOfRecords(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(),login));

    }

    catch (Exception ex) {

      ex.printStackTrace();

      return true;

    }



  }



  public static void deleteLogin(LoginTable login){
    if(login != null){
      try {
        LoginInfo li = ((com.idega.core.accesscontrol.data.LoginInfoHome)com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).findByPrimaryKeyLegacy(login.getID());
        try {
          int id = ((Integer)li.getPrimaryKey()).intValue();
          java.util.Collection recs = ((com.idega.core.accesscontrol.data.LoginRecordHome)com.idega.data.IDOLookup.getHomeLegacy(LoginRecord.class)).findAllLoginRecords(id);
          Iterator iter = recs.iterator();
          com.idega.core.accesscontrol.data.LoginRecordHome lr = ((com.idega.core.accesscontrol.data.LoginRecordHome)com.idega.data.IDOLookup.getHomeLegacy(LoginRecord.class));
          while (iter.hasNext()) {
            lr.remove(iter.next());
          }
        } catch (RemoteException ex) {
          ex.printStackTrace();
        } catch (RemoveException exc) {
          exc.printStackTrace();
        } catch (FinderException e){
          // assume login record does not exist for this login
        }

        li.remove();

      } catch (RemoteException e) {
        e.printStackTrace();
      } catch (SQLException sql){
        sql.printStackTrace();
      } catch (IDORemoveException ex){
        ex.printStackTrace();
      } catch (RemoveException ex){
        ex.printStackTrace();
      }


      try {
        login.remove();
      } catch (RemoteException e) {
        e.printStackTrace();
      } catch (RemoveException ex){
        ex.printStackTrace();
      }


    }
  }



  // Add-On by Aron 18.01.2001 login/logout tracking

  /**

   *  Records a login record, returns true if succeeds

   */

  public static int recordLogin(int iLoginId,String IPAddress){
    try {
      LoginRecord inRec =((com.idega.core.accesscontrol.data.LoginRecordHome)com.idega.data.IDOLookup.getHomeLegacy(LoginRecord.class)).createLegacy();
      inRec.setIPAdress(IPAddress);
      inRec.setLoginId(iLoginId);
      inRec.setLogInStamp(idegaTimestamp.getTimestampRightNow());
      inRec.store();
      Integer id = (Integer)inRec.getPrimaryKey();
      return id.intValue();
    }
    catch (RemoteException ex) {
      return (-1);
    }
 }



  /**

   *  Records a logout record, returns true if succeeds

   */

  public static boolean recordLogout(int iLoginRecordId){
    try {
      LoginRecord lr = ((LoginRecordHome)com.idega.data.IDOLookup.getHomeLegacy(LoginRecord.class)).findByPrimaryKey(iLoginRecordId);
      lr.setLogOutStamp(idegaTimestamp.getTimestampRightNow());
      lr.store();
//      StringBuffer sql = new StringBuffer("update ");
//      sql.append(com.idega.core.accesscontrol.data.LoginRecordBMPBean.getEntityTableName());
//      sql.append(" set out_stamp = '");
//      sql.append(idegaTimestamp.getTimestampRightNow().toString());
//      sql.append("'");
//      sql.append(" where ");
//      sql.append(((com.idega.core.accesscontrol.data.LoginRecordHome)com.idega.data.IDOLookup.getHomeLegacy(LoginRecord.class)).createLegacy().getIDColumnName());
//      sql.append( " = ");
//      sql.append(iLoginRecordId);
//      //System.err.println(sql.toString());
//
//      com.idega.data.SimpleQuerier.execute(sql.toString());
      return true;
    } catch (FinderException ex) {
      ex.printStackTrace();
    } catch (RemoteException e){
      e.printStackTrace();
    }
    return false;
  }



  public static boolean verifyPassword(String user, String password) throws Exception{

    boolean returner = false;

    java.util.List c = EntityFinder.getInstance().findAllByColumn(LoginTable.class,com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(),user);

    if(c!=null && c.size() > 0){

      LoginTable lt = (LoginTable) c.get(0);

      return Encrypter.verifyOneWayEncrypted(lt.getUserPassword(),password);

    }

    return false;

  }



}
