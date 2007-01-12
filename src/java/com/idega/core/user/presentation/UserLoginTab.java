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
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
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
    this._displayLoginInfoSettings = false;
  }

  public void displayLoginInfoSettings(){
    this._displayLoginInfoSettings = true;
  }

  public UserLoginTab() {
    super();
    super.setName("Login");
  }

  public void init(){
    this.errorMessageTable = new Table();
    this.errorText = new Text();
    this.errorText.setFontColor("red");
  }

  public void initFieldContents() {
    try {
      LoginTable lTable = LoginDBHandler.getUserLogin(this.getUserId());
      LoginInfo lInfo = LoginDBHandler.getLoginInfo(lTable.getID());

      if(lTable != null){
        this.fieldValues.put(_PARAM_USER_LOGIN,lTable.getUserLogin());
      }
      if(lInfo != null){
        this.fieldValues.put(_PARAM_MUST_CHANGE_PASSWORD,new Boolean(lInfo.getChangeNextTime()));
        this.fieldValues.put(_PARAM_CANNOT_CHANGE_PASSWORD,new Boolean(!lInfo.getAllowedToChange()));
        this.fieldValues.put(_PARAM_PASSWORD_NEVER_EXPIRES,new Boolean(lInfo.getPasswordExpires()));
        this.fieldValues.put(_PARAM_DISABLE_ACCOUNT,new Boolean(!lInfo.getAccountEnabled()));
      }
      this.updateFieldsDisplayStatus();
    }
    catch (Exception ex) {
      System.err.println("UserLoginTab: error in initFieldContents() for user: "+this.getUserId());
    }
  }


  public void updateFieldsDisplayStatus() {
    this.userLoginField.setContent((String)this.fieldValues.get(_PARAM_USER_LOGIN));

    this.passwordField.setContent((String)this.fieldValues.get(_PARAM_PASSWORD));
    this.confirmPasswordField.setContent((String)this.fieldValues.get(_PARAM_PASSWORD));


    this.mustChangePasswordField.setChecked(((Boolean)this.fieldValues.get(_PARAM_MUST_CHANGE_PASSWORD)).booleanValue());
    this.cannotChangePasswordField.setChecked(((Boolean)this.fieldValues.get(_PARAM_CANNOT_CHANGE_PASSWORD)).booleanValue());
    this.passwordNeverExpiresField.setChecked(((Boolean)this.fieldValues.get(_PARAM_PASSWORD_NEVER_EXPIRES)).booleanValue());
    this.disableAccountField.setChecked(((Boolean)this.fieldValues.get(_PARAM_DISABLE_ACCOUNT)).booleanValue());
  }

  public void initializeFields() {
    this.userLoginField = new TextInput(_PARAM_USER_LOGIN);
    this.userLoginField.setLength(14);
    //userLoginField.setDisabled(true);
    //userLoginField.setContent("userlogin");

    this.passwordField = new PasswordInput(_PARAM_PASSWORD);
    this.passwordField.setLength(14);
    this.confirmPasswordField = new PasswordInput(_PARAM_CONFIRM_PASSWORD);
    this.confirmPasswordField.setLength(14);

    this.mustChangePasswordField = new CheckBox(_PARAM_MUST_CHANGE_PASSWORD);
    this.cannotChangePasswordField = new CheckBox(_PARAM_CANNOT_CHANGE_PASSWORD);
    this.passwordNeverExpiresField = new CheckBox(_PARAM_PASSWORD_NEVER_EXPIRES);
    this.disableAccountField = new CheckBox(_PARAM_DISABLE_ACCOUNT);

  }
  public void initializeTexts() {
    this.userLoginText = new Text("User login");
    this.passwordText = new Text("New password");
    this.confirmPasswordText = new Text("Confirm password");

    this.mustChangePasswordText = new Text("User must change password at next login");
    this.cannotChangePasswordText = new Text("User cannot change password");
    this.passwordNeverExpiresText = new Text("Password never expires");
    this.disableAccountText = new Text("Account is disabled");
  }

  public boolean store(IWContext iwc) {
    boolean updateLoginTable = true;
    String login = (String)this.fieldValues.get(_PARAM_USER_LOGIN);

    String passw = ((String)this.fieldValues.get(_PARAM_PASSWORD));
    String confirmedpassw = ((String)this.fieldValues.get(_PARAM_PASSWORD));


    Boolean mustChangePassw = ((Boolean)this.fieldValues.get(_PARAM_MUST_CHANGE_PASSWORD));//.booleanValue();
    Boolean canChangePassw = ((Boolean)this.fieldValues.get(_PARAM_CANNOT_CHANGE_PASSWORD)).booleanValue()? Boolean.FALSE:Boolean.TRUE;
    Boolean passwExpires = ((Boolean)this.fieldValues.get(_PARAM_PASSWORD_NEVER_EXPIRES));//.booleanValue();
    Boolean accountEnabled = ((Boolean)this.fieldValues.get(_PARAM_DISABLE_ACCOUNT)).booleanValue()? Boolean.FALSE:Boolean.TRUE;

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
              this.fieldValues.put(UserLoginTab._PARAM_USER_LOGIN,login);
            }
          }else{
            if(inUse){
              this.addErrorMessage("login in use");
            } else {
              this.fieldValues.put(UserLoginTab._PARAM_USER_LOGIN,login);
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
      this.errorMessageTable.empty();
      try {
        LoginTable loginTable = LoginDBHandler.getUserLogin(this.getUserId());
        if(loginTable != null){
          if(updateLoginTable){
            LoginDBHandler.updateLogin(this.getUserId(),login,passw);
          }
          if(this._displayLoginInfoSettings){
            LoginDBHandler.updateLoginInfo(loginTable.getID(),accountEnabled,IWTimestamp.RightNow(),5000,passwExpires,canChangePassw,mustChangePassw,null);
          }
        } else if(updateLoginTable){
          if(this._displayLoginInfoSettings){
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
    loginTable.setHeight(1,this.rowHeight);
    loginTable.setHeight(2,this.rowHeight);
    loginTable.setHeight(3,this.rowHeight);
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
    AccountPropertyTable.setHeight(1,this.rowHeight);
    AccountPropertyTable.setHeight(2,this.rowHeight);
    AccountPropertyTable.setHeight(3,this.rowHeight);
    AccountPropertyTable.setHeight(4,this.rowHeight);

    AccountPropertyTable.add(this.mustChangePasswordField,1,1);
    AccountPropertyTable.add(this.mustChangePasswordText,2,1);
    AccountPropertyTable.add(this.cannotChangePasswordField,1,2);
    AccountPropertyTable.add(this.cannotChangePasswordText,2,2);
    AccountPropertyTable.add(this.passwordNeverExpiresField,1,3);
    AccountPropertyTable.add(this.passwordNeverExpiresText,2,3);
    AccountPropertyTable.add(this.disableAccountField,1,4);
    AccountPropertyTable.add(this.disableAccountText,2,4);
    // AccountPropertyTable end

    this.errorMessageTable.setHeight(1);
    this.errorMessageTable.setCellpadding(0);
    this.errorMessageTable.setCellspacing(0);


    frameTable.add(Text.getBreak(),2,1);
    frameTable.add(loginTable,2,1);
    frameTable.add(Text.getBreak(),2,1);
    if(this._displayLoginInfoSettings){
      frameTable.add(AccountPropertyTable,2,1);
    }
    frameTable.add(this.errorMessageTable,2,1);

    this.add(frameTable);

  }
  public boolean collect(IWContext iwc) {
    if(iwc != null){

      String login = iwc.getParameter(UserLoginTab._PARAM_USER_LOGIN);
      String passw = iwc.getParameter(UserLoginTab._PARAM_PASSWORD);
      String confirmedpassw = iwc.getParameter(UserLoginTab._PARAM_CONFIRM_PASSWORD);

      String mustChangePassw = iwc.getParameter(UserLoginTab._PARAM_MUST_CHANGE_PASSWORD);
      String cannotChangePassw = iwc.getParameter(UserLoginTab._PARAM_CANNOT_CHANGE_PASSWORD);
      String passwExpires = iwc.getParameter(UserLoginTab._PARAM_PASSWORD_NEVER_EXPIRES);
      String accountDisabled = iwc.getParameter(UserLoginTab._PARAM_DISABLE_ACCOUNT);

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
              this.fieldValues.put(UserLoginTab._PARAM_USER_LOGIN,login);
            }
          }else{
            if(inUse){
              this.addErrorMessage("login in use");
            } else {
              this.fieldValues.put(UserLoginTab._PARAM_USER_LOGIN,login);
            }
          }
        } else {
          this.addErrorMessage("login not valid");
        }

        if(passw != null && confirmedpassw != null && passw.equals(confirmedpassw)){
          this.fieldValues.put(UserLoginTab._PARAM_PASSWORD,passw);
          this.fieldValues.put(UserLoginTab._PARAM_CONFIRM_PASSWORD,confirmedpassw);
        } else {
          this.addErrorMessage("password and confirmed password not valid or not the same");
          this.fieldValues.put(UserLoginTab._PARAM_PASSWORD,"");
          this.fieldValues.put(UserLoginTab._PARAM_CONFIRM_PASSWORD,"");
        }
      }else{
        this.fieldValues.put(UserLoginTab._PARAM_PASSWORD,"");
        this.fieldValues.put(UserLoginTab._PARAM_CONFIRM_PASSWORD,"");
      }


      if(this._displayLoginInfoSettings){
        if(cannotChangePassw != null && mustChangePassw != null){
          this.addErrorMessage("'User must change password at next login' and 'User cannot change password' cannot both be checked");
          this.fieldValues.put(UserLoginTab._PARAM_MUST_CHANGE_PASSWORD,Boolean.TRUE);
          this.fieldValues.put(UserLoginTab._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.FALSE);
        } else {
          if(mustChangePassw != null){
            this.fieldValues.put(UserLoginTab._PARAM_MUST_CHANGE_PASSWORD,Boolean.TRUE);
          } else {
            this.fieldValues.put(UserLoginTab._PARAM_MUST_CHANGE_PASSWORD,Boolean.FALSE);
          }

          if(cannotChangePassw != null){
            this.fieldValues.put(UserLoginTab._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.TRUE);
          }else {
            this.fieldValues.put(UserLoginTab._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.FALSE);
          }
        }

        if(passwExpires != null){
          this.fieldValues.put(UserLoginTab._PARAM_PASSWORD_NEVER_EXPIRES,Boolean.TRUE);
        }else {
          this.fieldValues.put(UserLoginTab._PARAM_PASSWORD_NEVER_EXPIRES,Boolean.FALSE);
        }

        if(accountDisabled != null){
          this.fieldValues.put(UserLoginTab._PARAM_DISABLE_ACCOUNT,Boolean.TRUE);
        }else {
          this.fieldValues.put(UserLoginTab._PARAM_DISABLE_ACCOUNT,Boolean.FALSE);
        }
      }

      this.updateFieldsDisplayStatus();

      if(someErrors()){
        this.fieldValues.put(UserLoginTab._PARAM_PASSWORD,"");
        this.fieldValues.put(UserLoginTab._PARAM_CONFIRM_PASSWORD,"");
        presentErrorMessage(this.clearErrorMessages());
        return false;
      }else{
        this.errorMessageTable.empty();
        return true;
      }
    }
    this.addErrorMessage("IWContext is null");
    if(someErrors()){
      this.fieldValues.put(UserLoginTab._PARAM_PASSWORD,"");
      this.fieldValues.put(UserLoginTab._PARAM_CONFIRM_PASSWORD,"");
      presentErrorMessage(this.clearErrorMessages());
      return false;
    }else{
      this.errorMessageTable.empty();
      return true;
    }
  }

  public void presentErrorMessage(String[] messages){
    this.errorMessageTable.empty();
    if(messages != null){
      for (int i = 0; i < messages.length; i++) {
        Text message = (Text)this.errorText.clone();
        message.setText("* "+messages[i]+Text.BREAK);

        this.errorMessageTable.add(message);
      }

    }
  }


  public void initializeFieldNames() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }
  public void initializeFieldValues() {
    this.fieldValues.put(UserLoginTab._PARAM_USER_LOGIN,"");
    this.fieldValues.put(UserLoginTab._PARAM_PASSWORD,"");
    this.fieldValues.put(UserLoginTab._PARAM_CONFIRM_PASSWORD,"");
    this.fieldValues.put(UserLoginTab._PARAM_MUST_CHANGE_PASSWORD,Boolean.FALSE);
    this.fieldValues.put(UserLoginTab._PARAM_CANNOT_CHANGE_PASSWORD,Boolean.FALSE);
    this.fieldValues.put(UserLoginTab._PARAM_PASSWORD_NEVER_EXPIRES,Boolean.FALSE);
    this.fieldValues.put(UserLoginTab._PARAM_DISABLE_ACCOUNT,Boolean.FALSE);

    this.updateFieldsDisplayStatus();
  }
}
