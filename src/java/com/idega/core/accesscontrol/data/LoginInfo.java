package com.idega.core.accesscontrol.data;

import com.idega.data.*;
import com.idega.util.idegaTimestamp;
import java.sql.SQLException;
import java.sql.Date;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class LoginInfo extends GenericEntity {

  public static String className = "com.idega.core.accesscontrol.data.LoginInfo";

  public LoginInfo() {
    super();
  }


  public LoginInfo(int id) throws SQLException {
    super(id);
  }


  public void initializeAttributes() {
    addAttribute(getIDColumnName(),"Notandi",true,true,"java.lang.Integer","one-to-one",LoginTable.getStaticInstance().className);
    addAttribute(getAccountEnabledColumnName(),"Aðgangur virkur",true,true,"java.lang.Boolean");
    addAttribute(getModifiedColumnName(),"Síðast breytt",true,true,"java.sql.Date");
    addAttribute(getDaysOfValityColumnName(),"Dagar í gildi",true,true,"java.sql.Integer");
    addAttribute(getPasswNeverExpiresColumnName(),"Lykilorð rennur aldrei út",true,true,"java.lang.Boolean");
    addAttribute(getAllowedToChangeColumnName(),"Notandi má breyta",true,true,"java.lang.Boolean");
    addAttribute(getChangeNextTimeColumnName(),"Bryta næst",true,true,"java.lang.Boolean");
  }

  public void setDefaultValues(){
    this.setAccountEnabled(Boolean.TRUE);
    this.setAllowedToChange(Boolean.TRUE);
    this.setChangeNextTime(Boolean.FALSE);
    this.setDaysOfVality(10000);
    this.setModified(idegaTimestamp.RightNow());
    this.setPasswNeverExpires(Boolean.TRUE);
  }

  public String getEntityName() {
    return "ic_login_info";
  }

  public String getIDColumnName(){
    return getLoginTableIdColumnName();
  }

  public static LoginInfo getStaticInstance(){
    return (LoginInfo)LoginInfo.getStaticInstance(className);
  }


  /*  ColumNames begin   */

  public static String getLoginTableIdColumnName(){
    return LoginTable.getStaticInstance().getIDColumnName();
  }

  public static String getAccountEnabledColumnName(){
    return "account_enabled";
  }

  public static String getModifiedColumnName(){
    return "modified";
  }

  public static String getDaysOfValityColumnName(){
    return "days_of_vality";
  }

  public static String getPasswNeverExpiresColumnName(){
    return "passwd_expires";
  }

  public static String getAllowedToChangeColumnName(){
    return "allowed_to_change";
  }

  public static String getChangeNextTimeColumnName(){
    return "change_next_time";
  }

  /*  ColumNames end   */


  /*  Getters begin   */
  public int getLoginTableId(){
    return this.getIntColumnValue(getLoginTableIdColumnName());
  }

  public boolean getAccountEnabled(){
    return this.getBooleanColumnValue(getAccountEnabledColumnName());
  }

  public idegaTimestamp getModified(){
    return new idegaTimestamp((Date)this.getColumnValue(getModifiedColumnName()));
  }

  public int getDaysOfVality(){
    return this.getIntColumnValue(getDaysOfValityColumnName());
  }

  public boolean getPasswNeverExpires(){
    return !this.getBooleanColumnValue(getPasswNeverExpiresColumnName());
  }

  public boolean getAllowedToChange(){
    return this.getBooleanColumnValue(getAllowedToChangeColumnName());
  }

  public boolean getChangeNextTime(){
    return this.getBooleanColumnValue(getChangeNextTimeColumnName());
  }
  /*  Getters end   */



  /*  Setters begin   */
  public void setLoginTableId(int id){
    this.setColumn(this.getLoginTableIdColumnName(),id);
  }

  public void setAccountEnabled(boolean value){
    this.setColumn(getAccountEnabledColumnName(),value);
  }

  public void setModified(idegaTimestamp date){
    this.setColumn(getModifiedColumnName(),date.getSQLDate());
  }

  public void setDaysOfVality(int days){
    this.setColumn(getDaysOfValityColumnName(),days);
  }

  public void setPasswNeverExpires(boolean value){
    this.setColumn(getPasswNeverExpiresColumnName(),!value);
  }

  public void setAllowedToChange(boolean value){
    this.setColumn(getAllowedToChangeColumnName(),value);
  }

  public void setChangeNextTime(boolean value){
    this.setColumn(getChangeNextTimeColumnName(),value);
  }



  public void setAccountEnabled(Boolean value){
    this.setColumn(getAccountEnabledColumnName(),value);
  }

  public void setPasswNeverExpires(Boolean value){
    if(value != null){
      this.setColumn(getPasswNeverExpiresColumnName(),!value.booleanValue());
    }else{
      this.setColumn(getPasswNeverExpiresColumnName(),value);
    }
  }

  public void setAllowedToChange(Boolean value){
    this.setColumn(getAllowedToChangeColumnName(),value);
  }

  public void setChangeNextTime(Boolean value){
    this.setColumn(getChangeNextTimeColumnName(),value);
  }



  /*  Setters end   */





}// Class end