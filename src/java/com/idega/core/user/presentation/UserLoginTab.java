package com.idega.core.user.presentation;

import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.text.Text;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;


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

  public static String userLoginFieldParameterName = "login";

  public static String passwordFieldParameterName = "password";
  public static String confirmPasswordFieldParameterName = "confirmPassword";

  public static String mustChangePasswordFieldParameterName = "mustChange";
  public static String cannotChangePasswordFieldParameterName = "cannotChange";
  public static String passwordNeverExpiresFieldParameterName = "neverExpires";
  public static String disableAccountFieldParameterName = "disableAccount";


  public UserLoginTab() {
    super();
    super.setName("Login");
  }
  public void initFieldContents() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }
  public void updateFieldsDisplayStatus() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }
  public void initializeFields() {
    userLoginField = new TextInput(userLoginFieldParameterName);
    userLoginField.setLength(14);
    userLoginField.setDisabled(true);
    userLoginField.setContent("userlogin");

    passwordField = new PasswordInput(passwordFieldParameterName);
    passwordField.setLength(14);
    confirmPasswordField = new PasswordInput(confirmPasswordFieldParameterName);
    confirmPasswordField.setLength(14);

    mustChangePasswordField = new CheckBox(mustChangePasswordFieldParameterName);
    cannotChangePasswordField = new CheckBox(cannotChangePasswordFieldParameterName);
    passwordNeverExpiresField = new CheckBox(passwordNeverExpiresFieldParameterName);
    disableAccountField = new CheckBox(disableAccountFieldParameterName);

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
    return true;
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

    frameTable.add(Text.getBreak(),2,1);
    frameTable.add(loginTable,2,1);
    frameTable.add(Text.getBreak(),2,1);
    frameTable.add(AccountPropertyTable,2,1);
    this.add(frameTable);

  }
  public boolean collect(IWContext iwc) {
    return true;
  }
  public void initializeFieldNames() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }
  public void initializeFieldValues() {
    fieldValues.put(this.userLoginFieldParameterName,"");
    fieldValues.put(this.passwordFieldParameterName,"");
    fieldValues.put(this.confirmPasswordFieldParameterName,"");
    fieldValues.put(this.mustChangePasswordFieldParameterName,"");
    fieldValues.put(this.cannotChangePasswordFieldParameterName,"");
    fieldValues.put(this.passwordNeverExpiresFieldParameterName,"");
    fieldValues.put(this.disableAccountFieldParameterName,"");

    this.updateFieldsDisplayStatus();
  }
}