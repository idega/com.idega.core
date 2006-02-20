package com.idega.core.user.presentation;

import java.sql.SQLException;

import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneType;
import com.idega.core.user.business.UserBusiness;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.TextInput;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserPhoneTab extends UserTab{

  private TextInput homePhoneField;
  private TextInput workPhoneField;
  private TextInput mobilePhoneField;
  private TextInput faxPhoneField;
  private DropdownMenu homePhoneMenu;
  private DropdownMenu workPhoneMenu;
  private DropdownMenu mobilePhoneMenu;
  private DropdownMenu faxPhoneMenu;
  private TextInput emailField;

  public static String homePhoneFieldName = "homePhone";
  public static String workPhoneFieldName = "workPhone";
  public static String mobilePhoneFieldName = "mobilePhone";
  public static String faxPhoneFieldName = "faxPhone";
  public static String homePhoneMenuName = "homeChoice";
  public static String workPhoneMenuName = "workChoice";
  public static String mobilePhoneMenuName = "mobileChoice";
  public static String faxPhoneMenuName = "faxChoice";
  public static String emailFieldName = "email";

  private Text homePhoneText;
  private Text workPhoneText;
  private Text mobilePhoneText;
  private Text faxPhoneText;
  private Text emailText;

  public UserPhoneTab() {
    super();
    this.setName("Phone/Mail");
  }

  public UserPhoneTab(int userId){
    this();
    this.setUserID(userId);
  }

  public void initializeFieldNames(){
  }

  public void initializeFieldValues(){
    fieldValues.put(UserPhoneTab.homePhoneFieldName,"");
    fieldValues.put(UserPhoneTab.workPhoneFieldName,"");
    fieldValues.put(UserPhoneTab.mobilePhoneFieldName,"");
    fieldValues.put(UserPhoneTab.faxPhoneFieldName,"");
    fieldValues.put(UserPhoneTab.homePhoneMenuName,"");
    fieldValues.put(UserPhoneTab.workPhoneMenuName,"");
    fieldValues.put(UserPhoneTab.mobilePhoneMenuName,"");
    fieldValues.put(UserPhoneTab.faxPhoneMenuName,"");
    fieldValues.put(UserPhoneTab.emailFieldName,"");

    this.updateFieldsDisplayStatus();
  }

  public void updateFieldsDisplayStatus(){
    homePhoneField.setContent((String)fieldValues.get(UserPhoneTab.homePhoneFieldName));
    workPhoneField.setContent((String)fieldValues.get(UserPhoneTab.workPhoneFieldName));
    mobilePhoneField.setContent((String)fieldValues.get(UserPhoneTab.mobilePhoneFieldName));
    faxPhoneField.setContent((String)fieldValues.get(UserPhoneTab.faxPhoneFieldName));

    if ( (String)fieldValues.get(UserPhoneTab.homePhoneMenuName) != null  && ((String)fieldValues.get(UserPhoneTab.homePhoneMenuName)).length() > 0)
      homePhoneMenu.setSelectedElement((String)fieldValues.get(UserPhoneTab.homePhoneMenuName));
    if ( (String)fieldValues.get(UserPhoneTab.workPhoneMenuName) != null  && ((String)fieldValues.get(UserPhoneTab.workPhoneMenuName)).length() > 0)
      workPhoneMenu.setSelectedElement((String)fieldValues.get(UserPhoneTab.workPhoneMenuName));
    if ( (String)fieldValues.get(UserPhoneTab.mobilePhoneMenuName) != null  && ((String)fieldValues.get(UserPhoneTab.mobilePhoneMenuName)).length() > 0)
      mobilePhoneMenu.setSelectedElement((String)fieldValues.get(UserPhoneTab.mobilePhoneMenuName));
    if ( (String)fieldValues.get(UserPhoneTab.faxPhoneMenuName) != null && ((String)fieldValues.get(UserPhoneTab.faxPhoneMenuName)).length() > 0 )
      faxPhoneMenu.setSelectedElement((String)fieldValues.get(UserPhoneTab.faxPhoneMenuName));

    emailField.setContent((String)fieldValues.get(UserPhoneTab.emailFieldName));
  }


  public void initializeFields() {
    PhoneType[] phoneTypes = null;
    try {
      phoneTypes = (PhoneType[]) com.idega.core.contact.data.PhoneTypeBMPBean.getStaticInstance(PhoneType.class).findAll();
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }

    homePhoneField = new TextInput(homePhoneFieldName);
    homePhoneField.setLength(24);

    workPhoneField = new TextInput(workPhoneFieldName);
    workPhoneField.setLength(24);

    mobilePhoneField = new TextInput(mobilePhoneFieldName);
    mobilePhoneField.setLength(24);

    faxPhoneField = new TextInput(faxPhoneFieldName);
    faxPhoneField.setLength(24);

    homePhoneMenu = new DropdownMenu(phoneTypes,homePhoneMenuName);
    workPhoneMenu = new DropdownMenu(phoneTypes,workPhoneMenuName);
    mobilePhoneMenu = new DropdownMenu(phoneTypes,mobilePhoneMenuName);
    faxPhoneMenu = new DropdownMenu(phoneTypes,faxPhoneMenuName);

    for ( int a = 0; a < phoneTypes.length; a++ ) {
      if ( a == 0 ) {
        homePhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
      else if ( a == 1 ) {
        workPhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
      else if ( a == 2 ) {
        mobilePhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
      else if ( a == 3 ) {
        faxPhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
    }

    emailField = new TextInput(emailFieldName);
    emailField.setLength(24);
  }

  public void initializeTexts(){
    homePhoneText = new Text("Phone"+" 1:");
    homePhoneText.setFontSize(fontSize);

    workPhoneText = new Text("Phone"+" 2:");
    workPhoneText.setFontSize(fontSize);

    mobilePhoneText = new Text("Phone"+" 3:");
    mobilePhoneText.setFontSize(fontSize);

    faxPhoneText = new Text("Phone"+" 4:");
    faxPhoneText.setFontSize(fontSize);

    emailText = new Text("E-mail"+":");
    emailText.setFontSize(fontSize);
  }


  public void lineUpFields(){
    this.resize(1,3);

    Table staffTable = new Table(3,4);
    staffTable.setWidth("100%");
    staffTable.setCellpadding(0);
    staffTable.setCellspacing(0);
    staffTable.setHeight(1,rowHeight);
    staffTable.setHeight(2,rowHeight);
    staffTable.setHeight(3,rowHeight);
    staffTable.setHeight(4,rowHeight);

    staffTable.add(homePhoneText,1,1);
    staffTable.add(homePhoneMenu,3,1);
    staffTable.add(homePhoneField,2,1);
    staffTable.add(workPhoneText,1,2);
    staffTable.add(workPhoneMenu,3,2);
    staffTable.add(workPhoneField,2,2);
    staffTable.add(mobilePhoneText,1,3);
    staffTable.add(mobilePhoneMenu,3,3);
    staffTable.add(mobilePhoneField,2,3);
    staffTable.add(faxPhoneText,1,4);
    staffTable.add(faxPhoneMenu,3,4);
    staffTable.add(faxPhoneField,2,4);
    this.add(staffTable,1,1);

    Table mailTable = new Table(2,1);
    mailTable.setWidth("100%");
    mailTable.setCellpadding(0);
    mailTable.setCellspacing(0);
    mailTable.setHeight(1,rowHeight);

    mailTable.add(emailText,1,1);
    mailTable.add(emailField,2,1);
    this.add(mailTable,1,3);
  }


  public boolean collect(IWContext iwc){
    if(iwc != null){

      String homePhone = iwc.getParameter(UserPhoneTab.homePhoneFieldName);
      String workPhone = iwc.getParameter(UserPhoneTab.workPhoneFieldName);
      String mobilePhone = iwc.getParameter(UserPhoneTab.mobilePhoneFieldName);
      String faxPhone = iwc.getParameter(UserPhoneTab.faxPhoneFieldName);
      String homePhoneType = iwc.getParameter(UserPhoneTab.homePhoneMenuName);
      String workPhoneType = iwc.getParameter(UserPhoneTab.workPhoneMenuName);
      String mobilePhoneType = iwc.getParameter(UserPhoneTab.mobilePhoneMenuName);
      String faxPhoneType = iwc.getParameter(UserPhoneTab.faxPhoneMenuName);
      String email = iwc.getParameter(UserPhoneTab.emailFieldName);

      if(homePhone != null){
        fieldValues.put(UserPhoneTab.homePhoneFieldName,homePhone);
      }
      if(workPhone != null){
        fieldValues.put(UserPhoneTab.workPhoneFieldName,workPhone);
      }
      if(mobilePhone != null){
        fieldValues.put(UserPhoneTab.mobilePhoneFieldName,mobilePhone);
      }
      if(faxPhone != null){
        fieldValues.put(UserPhoneTab.faxPhoneFieldName,faxPhone);
      }
      if(homePhoneType != null){
        fieldValues.put(UserPhoneTab.homePhoneMenuName,homePhoneType);
      }
      if(workPhoneType != null){
        fieldValues.put(UserPhoneTab.workPhoneMenuName,workPhoneType);
      }
      if(mobilePhoneType != null){
        fieldValues.put(UserPhoneTab.mobilePhoneMenuName,mobilePhoneType);
      }
      if(faxPhoneType != null){
        fieldValues.put(UserPhoneTab.faxPhoneMenuName,faxPhoneType);
      }
      if(email != null){
        fieldValues.put(UserPhoneTab.emailFieldName,email);
      }

      this.updateFieldsDisplayStatus();

      return true;
    }
    return false;
  }

  public boolean store(IWContext iwc){
    try{
      if(getUserId() > -1){
        String[] phoneString = { (String)fieldValues.get(UserPhoneTab.homePhoneFieldName),(String)fieldValues.get(UserPhoneTab.workPhoneFieldName),(String)fieldValues.get(UserPhoneTab.mobilePhoneFieldName),(String)fieldValues.get(UserPhoneTab.faxPhoneFieldName) };
        String[] phoneTypeString = { (String)fieldValues.get(UserPhoneTab.homePhoneMenuName),(String)fieldValues.get(UserPhoneTab.workPhoneMenuName),(String)fieldValues.get(UserPhoneTab.mobilePhoneMenuName),(String)fieldValues.get(UserPhoneTab.faxPhoneMenuName) };

        for ( int a = 0; a < phoneString.length; a++ ) {
          if ( phoneString[a] != null && phoneString[a].length() > 0 ) {
            business.updateUserPhone(getUserId(),Integer.parseInt(phoneTypeString[a]),phoneString[a]);
          }
        }
        if ( (String)fieldValues.get(UserPhoneTab.emailFieldName) != null && ((String)fieldValues.get(UserPhoneTab.emailFieldName)).length() > 0 )
          business.updateUserMail(getUserId(),(String)fieldValues.get(UserPhoneTab.emailFieldName));
      }
    }
    catch(Exception e){
      e.printStackTrace(System.err);
      throw new RuntimeException("update user exception");
    }
    return true;
  }


  public void initFieldContents(){

    try{
      Phone[] phones = UserBusiness.getUserPhones(getUserId());
      Email mail = UserBusiness.getUserMail(getUserId());

      for ( int a = 0; a < phones.length; a++ ) {
        if ( a == 0 ) {
          fieldValues.put(UserPhoneTab.homePhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          fieldValues.put(UserPhoneTab.homePhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
        else if ( a == 1 ) {
          fieldValues.put(UserPhoneTab.workPhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          fieldValues.put(UserPhoneTab.workPhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
        else if ( a == 2 ) {
          fieldValues.put(UserPhoneTab.mobilePhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          fieldValues.put(UserPhoneTab.mobilePhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
        else if ( a == 3 ) {
          fieldValues.put(UserPhoneTab.faxPhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          fieldValues.put(UserPhoneTab.faxPhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
      }
      if ( mail != null )
        fieldValues.put(UserPhoneTab.emailFieldName,(mail.getEmailAddress() != null) ? mail.getEmailAddress():"" );

      this.updateFieldsDisplayStatus();

    }
    catch(Exception e){
      System.err.println("UserPhoneTab error initFieldContents, userId : " + getUserId());
    }


  }


} // Class UserPhoneTab
