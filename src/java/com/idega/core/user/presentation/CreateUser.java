package com.idega.core.user.presentation;

import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.interfaceobject.TextInput;
import com.idega.jmodule.object.interfaceobject.CheckBox;
import com.idega.jmodule.object.interfaceobject.SubmitButton;
import com.idega.jmodule.object.interfaceobject.Form;
import com.idega.jmodule.object.interfaceobject.PasswordInput;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.textObject.Text;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.user.data.User;
import com.idega.util.idegaTimestamp;
import com.idega.transaction.IdegaTransactionManager;
import javax.transaction.TransactionManager;

import java.sql.SQLException;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class CreateUser extends Window {

  private Text firstNameText;
  private Text middleNameText;
  private Text lastNameText;
  private Text userLoginText;
  private Text passwordText;
  private Text confirmPasswordText;

  private Text generateLoginText;
  private Text generatePasswordText;
  private Text mustChangePasswordText;
  private Text cannotChangePasswordText;
  private Text passwordNeverExpiresText;
  private Text disableAccountText;
  private Text goToPropertiesText;

  private TextInput firstNameField;
  private TextInput middleNameField;
  private TextInput lastNameField;
  private TextInput userLoginField;
  private PasswordInput passwordField;
  private PasswordInput confirmPasswordField;

  private CheckBox generateLoginField;
  private CheckBox generatePasswordField;
  private CheckBox mustChangePasswordField;
  private CheckBox cannotChangePasswordField;
  private CheckBox passwordNeverExpiresField;
  private CheckBox disableAccountField;
  private CheckBox goToPropertiesField;

  private SubmitButton okButton;
  private SubmitButton cancelButton;

  private Form myForm;

  public static String okButtonParameterValue = "ok";
  public static String cancelButtonParameterValue = "cancel";
  public static String submitButtonParameterName = "submit";

  public static String firstNameFieldParameterName = "firstName";
  public static String middleNameFieldParameterName = "middleName";
  public static String lastNameFieldParameterName = "lastName";
  public static String userLoginFieldParameterName = "login";
  public static String passwordFieldParameterName = "password";
  public static String confirmPasswordFieldParameterName = "confirmPassword";

  public static String generateLoginFieldParameterName = "generateLogin";
  public static String generatePasswordFieldParameterName = "generatePassword";
  public static String mustChangePasswordFieldParameterName = "mustChange";
  public static String cannotChangePasswordFieldParameterName = "cannotChange";
  public static String passwordNeverExpiresFieldParameterName = "neverExpires";
  public static String disableAccountFieldParameterName = "disableAccount";
  public static String goToPropertiesFieldParameterName = "gotoProperties";

  private UserBusiness business;

  private String rowHeight = "37";

  public CreateUser() {
    super();
    this.setName("idegaWeb Builder - Stofna félaga");
    this.setHeight(440);
    this.setWidth(390);
    this.setBackgroundColor("#d4d0c8");
    this.setScrollbar(false);
    myForm = new Form();
    this.add(myForm);
    business = new UserBusiness();
    initializeTexts();
    initializeFields();
    lineUpElements();
  }

  protected void initializeTexts(){

    firstNameText = new Text("First name");
    middleNameText = new Text("Middle name");
    lastNameText = new Text("Last name");
    userLoginText = new Text("User login");
    passwordText = new Text("Password");
    confirmPasswordText = new Text("Confirm password");

    generateLoginText = new Text("generate");
    generatePasswordText = new Text("generate");
    mustChangePasswordText = new Text("User must change password at next login");
    cannotChangePasswordText = new Text("User cannot change password");
    passwordNeverExpiresText = new Text("Password never expires");
    disableAccountText = new Text("Account is disabled");
    goToPropertiesText = new Text("go to properties");
  }

  protected void initializeFields(){
    firstNameField = new TextInput(firstNameFieldParameterName);
    firstNameField.setLength(12);
    middleNameField = new TextInput(middleNameFieldParameterName);
    middleNameField.setLength(12);
    lastNameField = new TextInput(lastNameFieldParameterName);
    lastNameField.setLength(12);
    userLoginField = new TextInput(userLoginFieldParameterName);
    userLoginField.setLength(12);
    passwordField = new PasswordInput(passwordFieldParameterName);
    passwordField.setLength(12);
    confirmPasswordField = new PasswordInput(confirmPasswordFieldParameterName);
    confirmPasswordField.setLength(12);

    generateLoginField = new CheckBox(generateLoginFieldParameterName);
    generatePasswordField = new CheckBox(generatePasswordFieldParameterName);
    mustChangePasswordField = new CheckBox(mustChangePasswordFieldParameterName);
    cannotChangePasswordField = new CheckBox(cannotChangePasswordFieldParameterName);
    passwordNeverExpiresField = new CheckBox(passwordNeverExpiresFieldParameterName);
    disableAccountField = new CheckBox(disableAccountFieldParameterName);
    goToPropertiesField = new CheckBox(goToPropertiesFieldParameterName);

    okButton = new SubmitButton("     OK     ",submitButtonParameterName,okButtonParameterValue);
    cancelButton = new SubmitButton(" Cancel ",submitButtonParameterName,cancelButtonParameterValue);

  }


  public void lineUpElements(){

    Table frameTable = new Table(1,5);
    frameTable.setAlignment("center");
    frameTable.setVerticalAlignment("middle");
    frameTable.setCellpadding(0);
    frameTable.setCellspacing(0);

    // nameTable begin
    Table nameTable = new Table(4,2);
    nameTable.setCellpadding(0);
    nameTable.setCellspacing(0);
    nameTable.setHeight(1,rowHeight);
    nameTable.setHeight(2,rowHeight);

    nameTable.add(firstNameText,1,1);
    nameTable.add(firstNameField,2,1);
    nameTable.add(middleNameText,3,1);
    nameTable.add(middleNameField,4,1);
    nameTable.add(lastNameText,1,2);
    nameTable.add(lastNameField,2,2);
    // nameTable end

    // loginTable begin
    Table loginTable = new Table(4,3);
    loginTable.setCellpadding(0);
    loginTable.setCellspacing(0);
    loginTable.setHeight(1,rowHeight);
    loginTable.setHeight(2,rowHeight);
    loginTable.setHeight(3,rowHeight);

    loginTable.add(this.userLoginText,1,1);
    loginTable.add(this.userLoginField,2,1);
    loginTable.add(this.generateLoginField,3,1);
    loginTable.add(this.generateLoginText,4,1);
    loginTable.add(this.passwordText,1,2);
    loginTable.add(this.passwordField,2,2);
    loginTable.add(this.generatePasswordField,3,2);
    loginTable.add(this.generatePasswordText,4,2);
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

    // propertyTable begin
    Table propertyTable = new Table(2,1);
    propertyTable.setCellpadding(0);
    propertyTable.setCellspacing(0);
    propertyTable.setHeight(1,rowHeight);

    propertyTable.add(this.goToPropertiesText,1,1);
    propertyTable.add(this.goToPropertiesField,2,1);
    // propertyTable end


    // buttonTable begin
    Table buttonTable = new Table(3,1);
    buttonTable.setCellpadding(0);
    buttonTable.setCellspacing(0);
    buttonTable.setHeight(1,rowHeight);
    buttonTable.setWidth(2,"5");

    buttonTable.add(okButton,1,1);
    buttonTable.add(cancelButton,3,1);
    // buttonTable end


    frameTable.add(nameTable,1,1);
    frameTable.add(loginTable,1,2);
    frameTable.add(AccountPropertyTable,1,3);
    frameTable.add(propertyTable,1,4);
    frameTable.setAlignment(1,4,"right");
    frameTable.add(buttonTable,1,5);
    frameTable.setAlignment(1,5,"right");

    myForm.add(frameTable);

  }



  public void commitCreation(ModuleInfo modinfo) throws Exception{

    User newUser;

    String login = modinfo.getParameter(this.userLoginFieldParameterName);
    String passw = modinfo.getParameter(this.passwordFieldParameterName);
    String cfPassw = modinfo.getParameter(this.confirmPasswordFieldParameterName);
    String password = null;

    String mustChage = modinfo.getParameter(this.mustChangePasswordFieldParameterName);
    String cannotchangePassw = modinfo.getParameter(this.cannotChangePasswordFieldParameterName);
    String passwNeverExpires = modinfo.getParameter(this.passwordNeverExpiresFieldParameterName);
    String disabledAccount = modinfo.getParameter(this.disableAccountFieldParameterName);

    Boolean bMustChage;
    Boolean bAllowedToChangePassw;
    Boolean bPasswNeverExpires;
    Boolean bEnabledAccount;

    if(mustChage != null && !"".equals(mustChage)){
      bMustChage = Boolean.TRUE;
    }else{
      bMustChage = Boolean.FALSE;
    }

    if(cannotchangePassw != null && !"".equals(cannotchangePassw)){
      bAllowedToChangePassw = Boolean.FALSE;
    }else{
      bAllowedToChangePassw = Boolean.TRUE;
    }

    if(passwNeverExpires != null && !"".equals(passwNeverExpires)){
      bPasswNeverExpires = Boolean.TRUE;
    }else{
      bPasswNeverExpires = Boolean.FALSE;
    }

    if(disabledAccount != null && !"".equals(disabledAccount)){
      bEnabledAccount = Boolean.FALSE;
    }else{
      bEnabledAccount = Boolean.TRUE;
    }


    if(passw != null && cfPassw != null && passw.equals(cfPassw)){
      password = passw;
    }else if(passw != null && cfPassw != null && !passw.equals(cfPassw)){
      throw new Exception("password and confirmed password not the same");
    }

    TransactionManager transaction = IdegaTransactionManager.getInstance();
    try{
      transaction.begin();
      newUser = business.insertUser( modinfo.getParameter(firstNameFieldParameterName),
                                   modinfo.getParameter(middleNameFieldParameterName),
                                   modinfo.getParameter(lastNameFieldParameterName),
                                   null,null,null,null);


      LoginDBHandler.createLogin(newUser.getID(),login,password,bEnabledAccount,idegaTimestamp.RightNow(),
                                 5000,bPasswNeverExpires,bAllowedToChangePassw,bMustChage);

      transaction.commit();
    }catch(Exception e){
      transaction.rollback();
      throw new Exception(e.getMessage()+" : User entry was removed");
    }

  }


  public void main(ModuleInfo modinfo) throws Exception {
    String submit = modinfo.getParameter("submit");
    if(submit != null){
      if(submit.equals("ok")){
        this.commitCreation(modinfo);
        this.close();
        this.setParentToReload();
      }else if(submit.equals("cancel")){
        this.close();
      }
    }
  }


}