package com.idega.core.accesscontrol.data;

import java.sql.Date;
import java.sql.SQLException;

import com.idega.util.EncryptionType;
import com.idega.util.IWTimestamp;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class LoginInfoBMPBean extends com.idega.data.GenericEntity implements com.idega.core.accesscontrol.data.LoginInfo {

  private static final String ENTITY_NAME = "IC_LOGIN_INFO";
  private static final String COLUMN_ACCOUNT_ENABLED = "ACCOUNT_ENABLED";
  private static final String COLUMN_MODIFIED = "MODIFIED";
  private static final String COLUMN_DAYS_OF_VALITY = "DAYS_OF_VALITY";
  private static final String COLUMN_PASSWD_EXPIRES = "PASSWD_EXPIRES";
  private static final String COLUMN_ALLOWED_TO_CHANGE = "ALLOWED_TO_CHANGE";
  private static final String COLUMN_CHANGE_NEXT_TIME = "CHANGE_NEXT_TIME";
  private static final String COLUMN_ENCRYPTION_TYPE = "ENCRYPTION_TYPE";
  public static String className = LoginInfo.class.getName();

  public LoginInfoBMPBean() {
    super();
  }


  public LoginInfoBMPBean(int id) throws SQLException {
    super(id);
  }


  public void initializeAttributes() {
    addAttribute(getIDColumnName(),"Notandi",true,true,Integer.class,"one-to-one",LoginTable.class);
    addAttribute(getAccountEnabledColumnName(),"Aðgangur virkur",true,true,Boolean.class);
    addAttribute(getModifiedColumnName(),"Síðast breytt",true,true,java.sql.Date.class);
    addAttribute(getDaysOfValityColumnName(),"Dagar í gildi",true,true,Integer.class);
    addAttribute(getPasswordExpiresColumnName(),"Lykilorð rennur út",true,true,Boolean.class);
    addAttribute(getAllowedToChangeColumnName(),"Notandi má breyta",true,true,Boolean.class);
    addAttribute(getChangeNextTimeColumnName(),"Breyta næst",true,true,Boolean.class);
    addAttribute(getEncryptionTypeColumnName(),"Kóðunaraðferð",true,true,String.class,30);
  }

  public void setDefaultValues(){
    this.setAccountEnabled(Boolean.TRUE);
    this.setAllowedToChange(Boolean.TRUE);
    this.setChangeNextTime(Boolean.FALSE);
    this.setDaysOfVality(10000);
    this.setModified(IWTimestamp.RightNow());
    this.setPasswordExpires(Boolean.FALSE);
    this.setEncriptionType(EncryptionType.MD5);
  }

  public String getEntityName() {
    return ENTITY_NAME;
  }

  public String getIDColumnName(){
    return getLoginTableIdColumnName();
  }

  public static LoginInfo getStaticInstance(){
    return (LoginInfo)com.idega.core.accesscontrol.data.LoginInfoBMPBean.getStaticInstance(className);
  }


  /*  ColumNames begin   */

  public static String getLoginTableIdColumnName(){
    return com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance().getIDColumnName();
  }

  public static String getAccountEnabledColumnName(){
    return COLUMN_ACCOUNT_ENABLED;
  }

  public static String getModifiedColumnName(){
    return COLUMN_MODIFIED;
  }

  public static String getDaysOfValityColumnName(){
    return COLUMN_DAYS_OF_VALITY;
  }

  public static String getPasswordExpiresColumnName(){
    return COLUMN_PASSWD_EXPIRES;
  }
  /**
   * @deprecated
   */
  public static String getPasswNeverExpiresColumnName(){
    return getPasswordExpiresColumnName();
  }

  public static String getAllowedToChangeColumnName(){
    return COLUMN_ALLOWED_TO_CHANGE;
  }

  public static String getChangeNextTimeColumnName(){
    return COLUMN_CHANGE_NEXT_TIME;
  }

  public static String getEncryptionTypeColumnName(){
    return COLUMN_ENCRYPTION_TYPE;
  }

  /*  ColumNames end   */


  /*  Getters begin   */
  public int getLoginTableId(){
    return this.getIntColumnValue(getLoginTableIdColumnName());
  }

  public boolean getAccountEnabled(){
    return this.getBooleanColumnValue(getAccountEnabledColumnName());
  }

  public IWTimestamp getModified(){
    return new IWTimestamp((Date)this.getColumnValue(getModifiedColumnName()));
  }

  public int getDaysOfVality(){
    return this.getIntColumnValue(getDaysOfValityColumnName());
  }


  public boolean getPasswordExpires(){
    return this.getBooleanColumnValue(getPasswordExpiresColumnName());
  }
  /**
   * @deprecated
   */
  public boolean getPasswNeverExpires(){
    return !getPasswordExpires();
  }

  public boolean getAllowedToChange(){
    return this.getBooleanColumnValue(getAllowedToChangeColumnName());
  }

  public boolean getChangeNextTime(){
    return this.getBooleanColumnValue(getChangeNextTimeColumnName());
  }

  public String getEncryprionType(){
    return this.getStringColumnValue(getEncryptionTypeColumnName());
  }
  /*  Getters end   */



  /*  Setters begin   */
  public void setLoginTableId(int id){
    this.setColumn(this.getLoginTableIdColumnName(),id);
  }

  public void setAccountEnabled(boolean value){
    this.setColumn(getAccountEnabledColumnName(),value);
  }

  public void setAccountEnabled(Boolean value){
    this.setColumn(getAccountEnabledColumnName(),value);
  }

  public void setModified(IWTimestamp date){
    this.setColumn(getModifiedColumnName(),date.getSQLDate());
  }

  public void setDaysOfVality(int days){
    this.setColumn(getDaysOfValityColumnName(),days);
  }

  public void setAllowedToChange(boolean value){
    this.setColumn(getAllowedToChangeColumnName(),value);
  }

  public void setAllowedToChange(Boolean value){
    this.setColumn(getAllowedToChangeColumnName(),value);
  }

  public void setChangeNextTime(boolean value){
    this.setColumn(getChangeNextTimeColumnName(),value);
  }

  public void setChangeNextTime(Boolean value){
    this.setColumn(getChangeNextTimeColumnName(),value);
  }


  public void setPasswordExpires(boolean value){
      this.setColumn(getPasswordExpiresColumnName(),value);
  }

  public void setPasswordExpires(Boolean value){
      this.setColumn(getPasswordExpiresColumnName(),value);
  }

  /**
   * @deprecated
   */
  public void setPasswNeverExpires(boolean value){
    this.setColumn(getPasswNeverExpiresColumnName(),!value);
  }

  /**
   * @deprecated
   */
  public void setPasswNeverExpires(Boolean value){
    if(value != null){
      this.setColumn(getPasswordExpiresColumnName(),!value.booleanValue());
    }else{
      this.setColumn(getPasswordExpiresColumnName(),value);
    }
  }

  public void setEncriptionType(String type){
      this.setColumn(getEncryptionTypeColumnName(),type);
  }
  
  public boolean isLoginExpired(){
  	if(getPasswordExpires()){
  		IWTimestamp modified =getModified();
  		modified.addDays(this.getDaysOfVality());
  		return modified.isEarlierThan(IWTimestamp.RightNow());
  	}
  	return false;
  }
  
  public boolean isLoginValid(){
  	return !isLoginExpired();
  }

  /*  Setters end   */





}// Class end
