package com.idega.core.user.presentation;

import java.util.Hashtable;
import java.util.StringTokenizer;

import com.idega.core.data.Address;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.TextInput;




/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
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
    streetFieldName = "UMstreet";
    cityFieldName = "UMcity";
    provinceFieldName = "UMprovince";
    postalCodeFieldName = "UMpostal";
    countryFieldName = "UMconty";
    poBoxFieldName = "UMpoBox";
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

    fieldValues = new Hashtable();
    fieldValues.put(this.streetFieldName,"");
    fieldValues.put(this.cityFieldName,"");
    fieldValues.put(this.provinceFieldName,"");
    fieldValues.put(this.postalCodeFieldName,"");
    fieldValues.put(this.countryFieldName,"");
    fieldValues.put(this.poBoxFieldName,"");

    this.updateFieldsDisplayStatus();
  }

  public void updateFieldsDisplayStatus(){
    streetField.setContent((String)fieldValues.get(this.streetFieldName));

    cityField.setContent((String)fieldValues.get(this.cityFieldName));

    provinceField.setContent((String)fieldValues.get(this.provinceFieldName));

    postalCodeField.setContent((String)fieldValues.get(this.postalCodeFieldName));

    countryField.setContent((String)fieldValues.get(this.countryFieldName));

    poBoxField.setContent((String)fieldValues.get(this.poBoxFieldName));

  }


  public void initializeFields(){
    streetField = new TextInput(streetFieldName);
    streetField.setLength(20);
    //streetField.setOnFocus();

    cityField = new TextInput(cityFieldName);
    cityField.setLength(20);

    provinceField = new TextInput(provinceFieldName);
    provinceField.setLength(20);

    postalCodeField = new TextInput(postalCodeFieldName);
    postalCodeField.setLength(4);
    postalCodeField.setDisabled(true);

    countryField = new TextInput(countryFieldName);
    countryField.setLength(20);
    countryField.setDisabled(true);

    poBoxField = new TextInput(poBoxFieldName);
    poBoxField.setLength(10);

  }

  public void initializeTexts(){
    streetText = new Text("Street");
    streetText.setFontSize(fontSize);

    cityText = new Text("City");
    cityText.setFontSize(fontSize);

    provinceText = new Text("Province");
    provinceText.setFontSize(fontSize);

    postalCodeText = new Text("Postal");
    postalCodeText.setFontSize(fontSize);

    countryText = new Text("Country");
    countryText.setFontSize(fontSize);

    poBoxText = new Text("P.O.Box");
    poBoxText.setFontSize(fontSize);

  }


  public void lineUpFields(){
    this.resize(1,1);

    Table addressTable = new Table(2,4);

//    FramePane fpane = new FramePane();

    addressTable.setWidth("100%");
    addressTable.setCellpadding(0);
    addressTable.setCellspacing(0);
    addressTable.setHeight(1,columnHeight);
    addressTable.setHeight(2,columnHeight);
    addressTable.setHeight(3,columnHeight);
    addressTable.setHeight(4,columnHeight);
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
    addressTable2.setHeight(1,columnHeight);
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
        fieldValues.put(this.streetFieldName,street);
      }
      if(city != null){
        fieldValues.put(this.cityFieldName,city);
      }
      if(province != null){
        fieldValues.put(this.provinceFieldName,province);
      }
      if(postal != null){
        fieldValues.put(this.postalCodeFieldName,postal);
      }
      if(country != null){
        fieldValues.put(this.countryFieldName,country);
      }
      if(poBox != null){
        fieldValues.put(this.poBoxFieldName,poBox);
      }

      this.updateFieldsDisplayStatus();

      return true;
    }
    return false;
  }

  public boolean store(IWContext iwc){

    try{
      StringTokenizer tok = new StringTokenizer((String)fieldValues.get(this.streetFieldName));

      //fieldValues.get(this.postalCodeFieldName);
      //fieldValues.get(this.countryFieldName);

      business.updateUserAddress1(this.getUserId(), (tok.hasMoreTokens())?tok.nextToken():"", (tok.hasMoreTokens())?tok.nextToken():"", (String)fieldValues.get(this.cityFieldName), null, (String)fieldValues.get(this.provinceFieldName), null, (String)fieldValues.get(this.poBoxFieldName));

      return true;
    }catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }

  public void initFieldContents(){
    try{
      Address addr = business.getUserAddress1(this.getUserId());

      boolean hasAddress = false;
      if(addr != null){
        hasAddress = true;
      }

      fieldValues.put(this.streetFieldName,(hasAddress) ? addr.getStreetName()+" "+addr.getStreetNumber():"" );
      fieldValues.put(this.cityFieldName,(hasAddress) ? addr.getCity():"" );
      fieldValues.put(this.provinceFieldName,(hasAddress) ? addr.getProvince():"" );
      fieldValues.put(this.postalCodeFieldName,(hasAddress) ? "":"" );
      fieldValues.put(this.countryFieldName,(hasAddress) ? "":"" );
      fieldValues.put(this.poBoxFieldName,(hasAddress) ? addr.getPOBox():"");
      this.updateFieldsDisplayStatus();

    }catch(Exception e){
      System.err.println("AddressInfoTab error initFieldContents, userId : " + getUserId());
    }
  }




} // Class AddressInfoTab
