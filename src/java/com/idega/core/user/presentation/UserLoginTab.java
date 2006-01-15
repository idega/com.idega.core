package com.idega.core.user.presentation;

import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.text.Text;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.util.IWTimestamp;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserLoginTab extends UserTab {

  private Text userLoginText;
  private TextInput userLoginField;

  private Table errorMessageTable;
  private Text errorText;

  private Text passwordText;
  private Text confirmPasswordText;
  private PasswordInput passwordField;
  private PasswordInput confirmPasswordField;

  private Text generatePasswordText;
  private Text mustChangePasswordText;
  private Text cannotChangePasswordText;
  private Text passwordNeverExpiresText;
  private Text disableAccountText;
  private CheckBox mustChangePasswordField;
  private CheckBox cannotChangePasswordField;
  private CheckBox passwordNeverExpiresField;
  private CheckBox disableAccountField;

  public static String _PARAM_USER_LOGIN = "login";

  public static String _PARAM_PASSWORD = "password";
  public static String _PARAM_CONFIRM_PASSWORD = "confirmPassword";

  public static String _PARAM_MUST_CHANGE_PASSWORD = "mustChange";
  public static String _PARAM_CANNOT_CHANGE_PASSWORD = "cannotChange";
  public static String _PARAM_PASSWORD_NEVER_EXPIRES = "neverExpires";
  public static String _PARAM_DISABLE_ACCOUNT = "disableAccount";

  private boolean _displayLoginInfoSettings = true;

  public void doNotDisplayLoginInfoSettings(){
    _displayLoginInfoSettings = false;
  }

  public void displayLoginInfoSettings(){
    _displayLoginInfoSettings = true;
  }

  public UserLoginTab() {
    super();
    super.setName("Login");
  }

  public void init(){
    errorMessageTable = new Table();
    errorText = new Text();
    errorText.setFontColor("red");
  }

  public void initFieldContents() {
    try {
      LoginTable lTable = LoginDBHandler.getUserLogin(this.getUserId());
      LoginInfo lInfo = LoginDBHandler.getLoginInfo(lTable);

      if(lTable != null){
        fieldValues.put(_PARAM_USER_LOGIN,lTable.getUserLogin());
      }
      if(lInfo != null){
        fieldValues.put(_PARAM_MUST_CHANGE_PASSWORD,new Boolean(lInfo.getChangeNextTime()));
        fieldValues.put(_PARAM_CANNOT_CHANGE_PASSWORD,new Boolean(!lInfo.getAllowedToChange()));
        fieldValues.put(_PARAM_PASSWORD_NEVER_EXPIRES,new Boolean(lInfo.getPasswordExpires()));
        fieldValues.put(_PARAM_DISABLE_ACCOUNT,new Boolean(!lInfo.getAccountEnabled()));
      }
      this.updateFieldsDisplayStatus();
    }
    catch (Exception ex) {
      System.err.println("UserLoginTab: error in initFieldContents() for user: "+this.getUserId());
    }
  }


  public void updateFieldsDisplayStatus() {
    userLoginField.setContent((String)fieldValues.get(_PARAM_USER_LOGIN));

    passwordField.setContent((String)fieldValues.get(_PARAM_PASSWORD));
    confirmPasswordField.setContent((String)fieldValues.get(_PARAM_PASSWORD));


    mustChangePasswordField.setChecked(((Boolean)fieldValues.get(_PARAM_MUST_CHANGE_PASSWORD)).booleanValue());
    cannotChangePasswordField.setChecked(((Boolean)fieldValues.get(_PARAM_CANNOT_CHANGE_PASSWORD)).booleanValue());
    passwordNeverExpiresField.setChecked(((Boolean)fieldValues.get(_PARAM_PASSWORD_NEVER_EXPIRES)).booleanValue());
    disableAccountField.setChecked(((Boolean)fieldValues.get(_PARAM_DISABLE_ACCOUNT)).booleanValue());
  }

  public void initializeFields() {
    userLoginField = new TextInput(_PARAM_USER_LOGIN);
    userLoginField.setLength(14);
    //userLoginField.setDisabled(true);
    //userLoginField.setContent("userlogin");

    passwordField = new PasswordInput(_PARAM_PASSWORD);
    passwordField.setLength(14);
    confirmPasswordField = new PasswordInput(_PARAM_CONFIRM_PASSWORD);
    confirmPasswordField.setLength(14);

    mustChangePasswordField = new CheckBox(_PARAM_MUST_CHANGE_PASSWORD);
    cannotChangePasswordField = new CheckBox(_PARAM_CANNOT_CHANGE_PASSWORD);
    passwordNeverExpiresField = new CheckBox(_PARAM_PASSWORD_NEVER_EXPIRES);
    disableAccountField = new CheckBox(_PARAM_DISABLE_ACCOUNT);

  }
  public void initializeTexts() {
    userLoginText = new Text("User login");
    passwordText = new Text("New password");
    confirmPasswordText = new Text("Confirm password");

    mustChangePasswordText = new Text("User must change password at next login");
    cannotChangePasswordText = new Text("User cannot change password");
    passwordNeverExpiresText = new Text("Password never expires");
    disableAccountText = new Text("Account is disabled");
  }

  public boolean store(IWContext iwc) {
    boolean updateLoginTable = true;
    String login = (String)fieldValues.get(_PARAM_USER_LOGIN);

    String passw = ((String)fieldValues.get(_PARAM_PASSWORD));
    String confirmedpassw = ((String)fieldValues.get(_PARAM_PASSWORD));


    Boolean mustChangePassw = ((Boolean)fieldValues.get(_PARAM_MUST_CHANGE_PASSWORD));//.booleanValue();
    Boolean canChangePassw = ((Boolean)fieldValues.get(_PARAM_CANNOT_CHANGE_PASSWORD)).booleanValue()? Boolean.FALSE:Boolean.TRUE;
    Boolean passwExpires = ((Boolean)fieldValues.get(_PARAM_PASSWORD_NEVER_EXPIRES));//.booleanValue();
    Boolean accountEnabled = ((Boolean)fieldValues.get(_PARAM_DISABLE_ACCOUNT)).booleanValue()? Boolean.FALSE:Boolean.TRUE;

    try {

      if(((passw != null && !passw.equals(""))&&((confirmedpassw != null && !confirmedpassw.equals("")))) ){
        if(login != null && !login.equals("")){
          LoginTable userLoginTable = LoginDBHandler.getUserLogin(this.getUserId());
          String oldLogin = null;
          if(userLoginTable != null){
            oldLogin = userLoginTable.getUserLogin();
          }
          boolean inUse = LoginDBHandler.isLoginInUse(login);
          if(oldLogin != null){
            if(inUse && !oldLogin.equals(login)){
              this.addErrorMessage("login in use");
            } else {
              fieldValues.put(this._PARAM_USER_LOGIN,login);
            }
          }else{
            if(inUse){
              this.addErrorMessage("login in use");
            } else {
              fieldValues.put(this._PARAM_USER_LOGIN,login);
            }
          }
        } else {
          this.addErrorMessage("login not valid");
        }
      }else{
        updateLoginTable = false;
      }

    }
    catch (Exception ex) {
      this.addErrorMessage(ex.getMessage());
    }

    if(someErrors()){
      presentErrorMessage(this.clearErrorMessages());
      return false;
    }else{
      errorMessageTable.empty();
      try {
        LoginTable loginTable = LoginDBHandler.getUserLogin(this.getUserId());
        if(loginTable != null){
          if(updateLoginTable){
            LoginDBHandler.updateLogin(this.getUserId(),login,passw);
          }
          if(_displayLoginInfoSettings){
            LoginDBHandler.updateLoginInfo(loginTable,accountEnabled,IWTimestamp.RightNow(),5000,passwExpires,canChangePassw,mustChangePassw,null);
          }
        } else if(updateLoginTable){
          if(_displayLoginInfoSettings){
            LoginDBHandler.createLogin(this.getUserId(),login,passw,accountEnabled,IWTimestamp.RightNow(),5000,passwExpires,canChangePassw,mustChangePassw,null);
          } else {
            LoginDBHandler.createLogin(this.getUserId(),login,passw);
          }
        }
        return true;
      }
      catch (Exception ex) {
        this.addErrorMessage(ex.getMessage());
        presentErrorMessage(this.clearErrorMessages());
        return false;
      }
    }
  }

  public void lineUpFields() {
    Table frameTable = new Table(2,1);
    frameTable.setCellpadding(0);
    frameTable.setCellspacing(0);

       // loginTable begin
    Table loginTable = new Table(2,3);
    loginTable.setCellpadding(0);
    loginTable.setCellspacing(0);
    loginTable.setHeight(1,rowHeight);
    loginTable.setHeight(2,rowHeight);
    loginTable.setHeight(3,rowHeight);
    loginTable.setWidth(1,"120");

    loginTable.add(this.userLoginText,1,1);
    loginTable.add(this.userLoginField,2,1);
    loginTable.add(this.passwordText,1,2);
    loginTable.add(this.passwordField,2,2);
    loginTable.add(this.confirmPasswordText,1,3);
    loginTable.add(this.confirmPasswordField,2,3);
    // loginTable end

    // AccountPropertyTable begin
    Table AccountPropertyTable = new Table(2,4);
    AccountPropertyTable.setCellpadding(0);
    AccountPropertyTable.setCellspacing(0);
    AccountPropertyTable.setHeight(1,rowHeight);
    AccountPropertyTable.setHeight(2,rowHeight);
    AccountPropertyTable.setHeight(3,rowHeight);
    AccountPropertyTable.setHeight(4,rowHeight);

    AccountPropertyTable.add(this.mustChangePasswordField,1,1);
    AccountPropertyTable.add(this.mustChangePasswordText,2,1);
    AccountPropertyTable.add(this.cannotChangePasswordField,1,2);
    AccountPropertyTable.add(this.cannotChangePasswordText,2,2);
    AccountPropertyTable.add(this.passwordNeverExpiresField,1,3);
    AccountPropertyTable.add(this.passwordNeverExpiresText,2,3);
    AccountPropertyTable.add(this.disableAccountField,1,4);
    AccountPropertyTable.add(this.disableAccountText,2,4);
    // AccountPropertyTable end

    errorMessageTable.setHeight(1);
    errorMessageTable.setCellpadding(0);
    errorMessageTable.setCellspacing(0);


    frameTable.add(Text.getBreak(),2,1);
    frameTable.add(loginTable,2,1);
    frameTable.add(Text.getBreak(),2,1);
    if(_displayLoginInfoSettings){
      frameTable.add(AccountPropertyTable,2,1);
    }
    frameTable.add(errorMessageTable,2,1);

    this.add(frameTable);

  }
  public boolean collect(IWContext iwc) {
    if(iwc != null){

      String login = iwc.getParameter(this._PARAM_USER_LOGIN);
      String passw = iwc.getParameter(this._PARAM_PASSWORD);
      String confirmedpassw = iwc.getParameter(this._PARAM_CONFIRM_PASSWORD);

      String mustChangePassw = iwc.getParameter(this._PARAM_MUST_CHANGE_PASSWORD);
      String cannotChangePassw = iwc.getParameter(this._PARAM_CANNOT_CHANGE_PASSWORD);
      String passwExpires = iwc.getParameter(this._PARAM_PASSWORD_NEVER_EXPIRES);
      String accountDisabled = iwc.getParameter(this._PARAM_DISABLE_ACCOUNT);

      if(((passw != null && !passw.equals("")) || ((confirmedpassw != null && !confirmedpassw.equals("")))) ){
        if(login != null && !login.equals("")){
          LoginTable userLoginTable = LoginDBHandler.getUserLogin(this.getUserId());
          String oldLogin = null;
          if(userLoginTable != null){
            oldLogin = userLoginTable.getUserLogin();
          }
          boolean inUse = LoginDBHandler.isLoginInUse(login);
          if(oldLogin != null){
            if(inUse && !oldLogin.equals(login)){
              this.addErrorMessage("login in use");
            } else {
              fieldValues.put(this._PARAM_USER_LOGIN,login);
            }
          }else{
            if(inUse){
              this.addErrorMessage("login in use");
            } else {
              fieldValues.put(this._PARAM_USER_LOGIN,login);
            }
          }
        } else {
          this.addErrorMessage("login not valid");
        }

        if(passw != null && confirmedpassw != null && passw.equals(confirmedpassw)){
          fieldValues.put(this._PARAM_PASSWORD,passw);
          fieldValues.put(this._PARAM_CONFIRM_PASSWORD,confirmedpassw);
        } else {
          this.addErrorMessage("password and confirmed password not valid or not the same");
          fieldValues.put(this._PARAM_PASSWORD,"");
          fieldValues.put(this._PARAM_CONFIRM_PASSWORD,"");
        }
      }else{
        fieldValues.put(this._PARAM_PASSWORD,"");
        fieldValues.put(this._PARAM_CONFIRM_PASSWORD,"");
      }


      if(_displayLoginInfoSettings){
        if(cannotChangePassw != null && mustChangePassw != null){
          this.addErrorMessage("'User must change password at next login' and 'User cannot change password' cannot both be checked");
          fieldValues.put(this._PARAM_MUST_CHANGE_PASSWORD,Boolean.TRUE);
          fieldValues.put(this._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.FALSE);
        } else {
          if(mustChangePassw != null){
            fieldValues.put(this._PARAM_MUST_CHANGE_PASSWORD,Boolean.TRUE);
          } else {
            fieldValues.put(this._PARAM_MUST_CHANGE_PASSWORD,Boolean.FALSE);
          }

          if(cannotChangePassw != null){
            fieldValues.put(this._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.TRUE);
          }else {
            fieldValues.put(this._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.FALSE);
          }
        }

        if(passwExpires != null){
          fieldValues.put(this._PARAM_PASSWORD_NEVER_EXPIRES,Boolean.TRUE);
        }else {
          fieldValues.put(this._PARAM_PASSWORD_NEVER_EXPIRES,Boolean.FALSE);
        }

        if(accountDisabled != null){
          fieldValues.put(this._PARAM_DISABLE_ACCOUNT,Boolean.TRUE);
        }else {
          fieldValues.put(this._PARAM_DISABLE_ACCOUNT,Boolean.FALSE);
        }
      }

      this.updateFieldsDisplayStatus();

      if(someErrors()){
        fieldValues.put(this._PARAM_PASSWORD,"");
        fieldValues.put(this._PARAM_CONFIRM_PASSWORD,"");
        presentErrorMessage(this.clearErrorMessages());
        return false;
      }else{
        errorMessageTable.empty();
        return true;
      }
    }
    this.addErrorMessage("IWContext is null");
    if(someErrors()){
      fieldValues.put(this._PARAM_PASSWORD,"");
      fieldValues.put(this._PARAM_CONFIRM_PASSWORD,"");
      presentErrorMessage(this.clearErrorMessages());
      return false;
    }else{
      errorMessageTable.empty();
      return true;
    }
  }

  public void presentErrorMessage(String[] messages){
    errorMessageTable.empty();
    if(messages != null){
      for (int i = 0; i < messages.length; i++) {
        Text message = (Text)errorText.clone();
        message.setText("* "+messages[i]+Text.BREAK);

        errorMessageTable.add(message);
      }

    }
  }


  public void initializeFieldNames() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }
  public void initializeFieldValues() {
    fieldValues.put(this._PARAM_USER_LOGIN,"");
    fieldValues.put(this._PARAM_PASSWORD,"");
    fieldValues.put(this._PARAM_CONFIRM_PASSWORD,"");
    fieldValues.put(this._PARAM_MUST_CHANGE_PASSWORD,Boolean.FALSE);
    fieldValues.put(this._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.FALSE);
    fieldValues.put(this._PARAM_PASSWORD_NEVER_EXPIRES,Boolean.FALSE);
    fieldValues.put(this._PARAM_DISABLE_ACCOUNT,Boolean.FALSE);

    this.updateFieldsDisplayStatus();
  }
}
