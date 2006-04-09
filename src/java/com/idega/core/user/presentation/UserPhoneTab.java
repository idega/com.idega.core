package com.idega.core.user.presentation;

import java.sql.SQLException;

import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneType;
import com.idega.core.user.business.UserBusiness;
import com.idega.data.GenericEntity;
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
    this.fieldValues.put(UserPhoneTab.homePhoneFieldName,"");
    this.fieldValues.put(UserPhoneTab.workPhoneFieldName,"");
    this.fieldValues.put(UserPhoneTab.mobilePhoneFieldName,"");
    this.fieldValues.put(UserPhoneTab.faxPhoneFieldName,"");
    this.fieldValues.put(UserPhoneTab.homePhoneMenuName,"");
    this.fieldValues.put(UserPhoneTab.workPhoneMenuName,"");
    this.fieldValues.put(UserPhoneTab.mobilePhoneMenuName,"");
    this.fieldValues.put(UserPhoneTab.faxPhoneMenuName,"");
    this.fieldValues.put(UserPhoneTab.emailFieldName,"");

    this.updateFieldsDisplayStatus();
  }

  public void updateFieldsDisplayStatus(){
    this.homePhoneField.setContent((String)this.fieldValues.get(UserPhoneTab.homePhoneFieldName));
    this.workPhoneField.setContent((String)this.fieldValues.get(UserPhoneTab.workPhoneFieldName));
    this.mobilePhoneField.setContent((String)this.fieldValues.get(UserPhoneTab.mobilePhoneFieldName));
    this.faxPhoneField.setContent((String)this.fieldValues.get(UserPhoneTab.faxPhoneFieldName));

    if ( (String)this.fieldValues.get(UserPhoneTab.homePhoneMenuName) != null  && ((String)this.fieldValues.get(UserPhoneTab.homePhoneMenuName)).length() > 0) {
			this.homePhoneMenu.setSelectedElement((String)this.fieldValues.get(UserPhoneTab.homePhoneMenuName));
		}
    if ( (String)this.fieldValues.get(UserPhoneTab.workPhoneMenuName) != null  && ((String)this.fieldValues.get(UserPhoneTab.workPhoneMenuName)).length() > 0) {
			this.workPhoneMenu.setSelectedElement((String)this.fieldValues.get(UserPhoneTab.workPhoneMenuName));
		}
    if ( (String)this.fieldValues.get(UserPhoneTab.mobilePhoneMenuName) != null  && ((String)this.fieldValues.get(UserPhoneTab.mobilePhoneMenuName)).length() > 0) {
			this.mobilePhoneMenu.setSelectedElement((String)this.fieldValues.get(UserPhoneTab.mobilePhoneMenuName));
		}
    if ( (String)this.fieldValues.get(UserPhoneTab.faxPhoneMenuName) != null && ((String)this.fieldValues.get(UserPhoneTab.faxPhoneMenuName)).length() > 0 ) {
			this.faxPhoneMenu.setSelectedElement((String)this.fieldValues.get(UserPhoneTab.faxPhoneMenuName));
		}

    this.emailField.setContent((String)this.fieldValues.get(UserPhoneTab.emailFieldName));
  }


  public void initializeFields() {
    PhoneType[] phoneTypes = null;
    try {
      phoneTypes = (PhoneType[]) GenericEntity.getStaticInstance(PhoneType.class).findAll();
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }

    this.homePhoneField = new TextInput(homePhoneFieldName);
    this.homePhoneField.setLength(24);

    this.workPhoneField = new TextInput(workPhoneFieldName);
    this.workPhoneField.setLength(24);

    this.mobilePhoneField = new TextInput(mobilePhoneFieldName);
    this.mobilePhoneField.setLength(24);

    this.faxPhoneField = new TextInput(faxPhoneFieldName);
    this.faxPhoneField.setLength(24);

    this.homePhoneMenu = new DropdownMenu(phoneTypes,homePhoneMenuName);
    this.workPhoneMenu = new DropdownMenu(phoneTypes,workPhoneMenuName);
    this.mobilePhoneMenu = new DropdownMenu(phoneTypes,mobilePhoneMenuName);
    this.faxPhoneMenu = new DropdownMenu(phoneTypes,faxPhoneMenuName);

    for ( int a = 0; a < phoneTypes.length; a++ ) {
      if ( a == 0 ) {
        this.homePhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
      else if ( a == 1 ) {
        this.workPhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
      else if ( a == 2 ) {
        this.mobilePhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
      else if ( a == 3 ) {
        this.faxPhoneMenu.setSelectedElement(Integer.toString(phoneTypes[a].getID()));
      }
    }

    this.emailField = new TextInput(emailFieldName);
    this.emailField.setLength(24);
  }

  public void initializeTexts(){
    this.homePhoneText = new Text("Phone"+" 1:");
    this.homePhoneText.setFontSize(this.fontSize);

    this.workPhoneText = new Text("Phone"+" 2:");
    this.workPhoneText.setFontSize(this.fontSize);

    this.mobilePhoneText = new Text("Phone"+" 3:");
    this.mobilePhoneText.setFontSize(this.fontSize);

    this.faxPhoneText = new Text("Phone"+" 4:");
    this.faxPhoneText.setFontSize(this.fontSize);

    this.emailText = new Text("E-mail"+":");
    this.emailText.setFontSize(this.fontSize);
  }


  public void lineUpFields(){
    this.resize(1,3);

    Table staffTable = new Table(3,4);
    staffTable.setWidth("100%");
    staffTable.setCellpadding(0);
    staffTable.setCellspacing(0);
    staffTable.setHeight(1,this.rowHeight);
    staffTable.setHeight(2,this.rowHeight);
    staffTable.setHeight(3,this.rowHeight);
    staffTable.setHeight(4,this.rowHeight);

    staffTable.add(this.homePhoneText,1,1);
    staffTable.add(this.homePhoneMenu,3,1);
    staffTable.add(this.homePhoneField,2,1);
    staffTable.add(this.workPhoneText,1,2);
    staffTable.add(this.workPhoneMenu,3,2);
    staffTable.add(this.workPhoneField,2,2);
    staffTable.add(this.mobilePhoneText,1,3);
    staffTable.add(this.mobilePhoneMenu,3,3);
    staffTable.add(this.mobilePhoneField,2,3);
    staffTable.add(this.faxPhoneText,1,4);
    staffTable.add(this.faxPhoneMenu,3,4);
    staffTable.add(this.faxPhoneField,2,4);
    this.add(staffTable,1,1);

    Table mailTable = new Table(2,1);
    mailTable.setWidth("100%");
    mailTable.setCellpadding(0);
    mailTable.setCellspacing(0);
    mailTable.setHeight(1,this.rowHeight);

    mailTable.add(this.emailText,1,1);
    mailTable.add(this.emailField,2,1);
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
        this.fieldValues.put(UserPhoneTab.homePhoneFieldName,homePhone);
      }
      if(workPhone != null){
        this.fieldValues.put(UserPhoneTab.workPhoneFieldName,workPhone);
      }
      if(mobilePhone != null){
        this.fieldValues.put(UserPhoneTab.mobilePhoneFieldName,mobilePhone);
      }
      if(faxPhone != null){
        this.fieldValues.put(UserPhoneTab.faxPhoneFieldName,faxPhone);
      }
      if(homePhoneType != null){
        this.fieldValues.put(UserPhoneTab.homePhoneMenuName,homePhoneType);
      }
      if(workPhoneType != null){
        this.fieldValues.put(UserPhoneTab.workPhoneMenuName,workPhoneType);
      }
      if(mobilePhoneType != null){
        this.fieldValues.put(UserPhoneTab.mobilePhoneMenuName,mobilePhoneType);
      }
      if(faxPhoneType != null){
        this.fieldValues.put(UserPhoneTab.faxPhoneMenuName,faxPhoneType);
      }
      if(email != null){
        this.fieldValues.put(UserPhoneTab.emailFieldName,email);
      }

      this.updateFieldsDisplayStatus();

      return true;
    }
    return false;
  }

  public boolean store(IWContext iwc){
    try{
      if(getUserId() > -1){
        String[] phoneString = { (String)this.fieldValues.get(UserPhoneTab.homePhoneFieldName),(String)this.fieldValues.get(UserPhoneTab.workPhoneFieldName),(String)this.fieldValues.get(UserPhoneTab.mobilePhoneFieldName),(String)this.fieldValues.get(UserPhoneTab.faxPhoneFieldName) };
        String[] phoneTypeString = { (String)this.fieldValues.get(UserPhoneTab.homePhoneMenuName),(String)this.fieldValues.get(UserPhoneTab.workPhoneMenuName),(String)this.fieldValues.get(UserPhoneTab.mobilePhoneMenuName),(String)this.fieldValues.get(UserPhoneTab.faxPhoneMenuName) };

        for ( int a = 0; a < phoneString.length; a++ ) {
          if ( phoneString[a] != null && phoneString[a].length() > 0 ) {
            this.business.updateUserPhone(getUserId(),Integer.parseInt(phoneTypeString[a]),phoneString[a]);
          }
        }
        if ( (String)this.fieldValues.get(UserPhoneTab.emailFieldName) != null && ((String)this.fieldValues.get(UserPhoneTab.emailFieldName)).length() > 0 ) {
					this.business.updateUserMail(getUserId(),(String)this.fieldValues.get(UserPhoneTab.emailFieldName));
				}
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
          this.fieldValues.put(UserPhoneTab.homePhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          this.fieldValues.put(UserPhoneTab.homePhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
        else if ( a == 1 ) {
          this.fieldValues.put(UserPhoneTab.workPhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          this.fieldValues.put(UserPhoneTab.workPhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
        else if ( a == 2 ) {
          this.fieldValues.put(UserPhoneTab.mobilePhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          this.fieldValues.put(UserPhoneTab.mobilePhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
        else if ( a == 3 ) {
          this.fieldValues.put(UserPhoneTab.faxPhoneMenuName,(phones[a].getPhoneTypeId() != -1) ? Integer.toString(phones[a].getPhoneTypeId()):"" );
          this.fieldValues.put(UserPhoneTab.faxPhoneFieldName,(phones[a].getNumber() != null) ? phones[a].getNumber():"" );
        }
      }
      if ( mail != null ) {
				this.fieldValues.put(UserPhoneTab.emailFieldName,(mail.getEmailAddress() != null) ? mail.getEmailAddress():"" );
			}

      this.updateFieldsDisplayStatus();

    }
    catch(Exception e){
      System.err.println("UserPhoneTab error initFieldContents, userId : " + getUserId());
    }


  }


} // Class UserPhoneTab
