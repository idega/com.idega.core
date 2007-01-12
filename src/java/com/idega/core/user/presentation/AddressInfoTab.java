package com.idega.core.user.presentation;

import java.util.Hashtable;
import java.util.StringTokenizer;

import com.idega.core.location.data.Address;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.TextInput;




/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class AddressInfoTab extends UserTab{

  private TextInput streetField;
  private TextInput cityField;
  private TextInput provinceField;
  private TextInput postalCodeField;
  private TextInput countryField;
  private TextInput poBoxField;

  private String streetFieldName = "UMstreet";
  private String cityFieldName = "UMcity";
  private String provinceFieldName = "UMprovince";
  private String postalCodeFieldName = "UMpostal";
  private String countryFieldName = "UMconty";
  private String poBoxFieldName = "UMpoBox";

  private Text streetText;
  private Text cityText;
  private Text provinceText;
  private Text postalCodeText;
  private Text countryText;
  private Text poBoxText;

  public AddressInfoTab() {
    super();
    this.setName("Address");
  }

  public void initializeFieldNames(){
    this.streetFieldName = "UMstreet";
    this.cityFieldName = "UMcity";
    this.provinceFieldName = "UMprovince";
    this.postalCodeFieldName = "UMpostal";
    this.countryFieldName = "UMconty";
    this.poBoxFieldName = "UMpoBox";
/*
    streetFieldName += this.getID();
    cityFieldName += this.getID();
    provinceFieldName += this.getID();
    postalCodeFieldName += this.getID();
    countryFieldName += this.getID();
    poBoxFieldName += this.getID();
*/
  }


  public void initializeFieldValues(){

    this.fieldValues = new Hashtable();
    this.fieldValues.put(this.streetFieldName,"");
    this.fieldValues.put(this.cityFieldName,"");
    this.fieldValues.put(this.provinceFieldName,"");
    this.fieldValues.put(this.postalCodeFieldName,"");
    this.fieldValues.put(this.countryFieldName,"");
    this.fieldValues.put(this.poBoxFieldName,"");

    this.updateFieldsDisplayStatus();
  }

  public void updateFieldsDisplayStatus(){
    this.streetField.setContent((String)this.fieldValues.get(this.streetFieldName));

    this.cityField.setContent((String)this.fieldValues.get(this.cityFieldName));

    this.provinceField.setContent((String)this.fieldValues.get(this.provinceFieldName));

    this.postalCodeField.setContent((String)this.fieldValues.get(this.postalCodeFieldName));

    this.countryField.setContent((String)this.fieldValues.get(this.countryFieldName));

    this.poBoxField.setContent((String)this.fieldValues.get(this.poBoxFieldName));

  }


  public void initializeFields(){
    this.streetField = new TextInput(this.streetFieldName);
    this.streetField.setLength(20);
    //streetField.setOnFocus();

    this.cityField = new TextInput(this.cityFieldName);
    this.cityField.setLength(20);

    this.provinceField = new TextInput(this.provinceFieldName);
    this.provinceField.setLength(20);

    this.postalCodeField = new TextInput(this.postalCodeFieldName);
    this.postalCodeField.setLength(4);
    this.postalCodeField.setDisabled(true);

    this.countryField = new TextInput(this.countryFieldName);
    this.countryField.setLength(20);
    this.countryField.setDisabled(true);

    this.poBoxField = new TextInput(this.poBoxFieldName);
    this.poBoxField.setLength(10);

  }

  public void initializeTexts(){
    this.streetText = new Text("Street");
    this.streetText.setFontSize(this.fontSize);

    this.cityText = new Text("City");
    this.cityText.setFontSize(this.fontSize);

    this.provinceText = new Text("Province");
    this.provinceText.setFontSize(this.fontSize);

    this.postalCodeText = new Text("Postal");
    this.postalCodeText.setFontSize(this.fontSize);

    this.countryText = new Text("Country");
    this.countryText.setFontSize(this.fontSize);

    this.poBoxText = new Text("P.O.Box");
    this.poBoxText.setFontSize(this.fontSize);

  }


  public void lineUpFields(){
    this.resize(1,1);

    Table addressTable = new Table(2,4);

//    FramePane fpane = new FramePane();

    addressTable.setWidth("100%");
    addressTable.setCellpadding(0);
    addressTable.setCellspacing(0);
    addressTable.setHeight(1,this.columnHeight);
    addressTable.setHeight(2,this.columnHeight);
    addressTable.setHeight(3,this.columnHeight);
    addressTable.setHeight(4,this.columnHeight);
    addressTable.setWidth(1,"70");

    addressTable.add(this.streetText,1,1);
    addressTable.add(this.streetField,2,1);
    addressTable.add(this.cityText,1,2);
    addressTable.add(this.cityField,2,2);
    addressTable.add(this.provinceText,1,3);
    addressTable.add(this.provinceField,2,3);
    addressTable.add(this.countryText,1,4);
    addressTable.add(this.countryField,2,4);

    this.add(addressTable);
//    fpane.add(addressTable);

    Table addressTable2 = new Table(4,1);

    addressTable2.setWidth("100%");
    addressTable2.setCellpadding(0);
    addressTable2.setCellspacing(0);
    addressTable2.setHeight(1,this.columnHeight);
    addressTable2.setWidth(1,"70");
    addressTable2.setWidth(2,"70");
    addressTable2.setWidth(3,"70");

    addressTable2.add(this.postalCodeText, 1, 1);
    addressTable2.add(this.postalCodeField, 2, 1);
    addressTable2.add(this.poBoxText, 3, 1);
    addressTable2.add(this.poBoxField, 4, 1);

    this.add(addressTable2);
//    fpane.add(addressTable2);
//    this.add(fpane);

  }


  public boolean collect(IWContext iwc){

    if(iwc != null){
      String street = iwc.getParameter(this.streetFieldName);
      String city = iwc.getParameter(this.cityFieldName);
      String province = iwc.getParameter(this.provinceFieldName);
      String postal = iwc.getParameter(this.postalCodeFieldName);
      String country = iwc.getParameter(this.countryFieldName);
      String poBox = iwc.getParameter(this.poBoxFieldName);

      if(street != null){
        this.fieldValues.put(this.streetFieldName,street);
      }
      if(city != null){
        this.fieldValues.put(this.cityFieldName,city);
      }
      if(province != null){
        this.fieldValues.put(this.provinceFieldName,province);
      }
      if(postal != null){
        this.fieldValues.put(this.postalCodeFieldName,postal);
      }
      if(country != null){
        this.fieldValues.put(this.countryFieldName,country);
      }
      if(poBox != null){
        this.fieldValues.put(this.poBoxFieldName,poBox);
      }

      this.updateFieldsDisplayStatus();

      return true;
    }
    return false;
  }

  public boolean store(IWContext iwc){

    try{
      StringTokenizer tok = new StringTokenizer((String)this.fieldValues.get(this.streetFieldName));

      //fieldValues.get(this.postalCodeFieldName);
      //fieldValues.get(this.countryFieldName);

      this.business.updateUserAddress1(this.getUserId(), (tok.hasMoreTokens())?tok.nextToken():"", (tok.hasMoreTokens())?tok.nextToken():"", (String)this.fieldValues.get(this.cityFieldName), null, (String)this.fieldValues.get(this.provinceFieldName), null, (String)this.fieldValues.get(this.poBoxFieldName));

      return true;
    }catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }

  public void initFieldContents(){
    try{
      Address addr = this.business.getUserAddress1(this.getUserId());

      boolean hasAddress = false;
      if(addr != null){
        hasAddress = true;
      }

      this.fieldValues.put(this.streetFieldName,(hasAddress) ? addr.getStreetName()+" "+addr.getStreetNumber():"" );
      this.fieldValues.put(this.cityFieldName,(hasAddress) ? addr.getCity():"" );
      this.fieldValues.put(this.provinceFieldName,(hasAddress) ? addr.getProvince():"" );
      this.fieldValues.put(this.postalCodeFieldName,(hasAddress) ? "":"" );
      this.fieldValues.put(this.countryFieldName,(hasAddress) ? "":"" );
      this.fieldValues.put(this.poBoxFieldName,(hasAddress) ? addr.getPOBox():"");
      this.updateFieldsDisplayStatus();

    }catch(Exception e){
      System.err.println("AddressInfoTab error initFieldContents, userId : " + getUserId());
    }
  }




} // Class AddressInfoTab
