package com.idega.core.usermodule.presentation;

import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.interfaceobject.TextInput;
import com.idega.jmodule.object.interfaceobject.TextArea;
import com.idega.jmodule.object.interfaceobject.DateInput;
import com.idega.jmodule.object.interfaceobject.DropdownMenu;
import com.idega.jmodule.object.interfaceobject.FramePane;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.textObject.Text;
import com.idega.util.datastructures.Collectable;
import java.util.Hashtable;
import java.util.StringTokenizer;



/**
 * Title:        UserModule
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AddressInfoTab extends Table implements Collectable {

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

  private Hashtable fieldValues;


  private String columnHeight = "37";
  private int fontSize = 2;


  public AddressInfoTab() {
    super();
    this.setCellpadding(0);
    this.setCellspacing(0);
    this.setName("Address");
    this.setWidth("370");
    streetFieldName += this.getID();
    cityFieldName += this.getID();
    provinceFieldName += this.getID();
    postalCodeFieldName += this.getID();
    countryFieldName += this.getID();
    poBoxFieldName += this.getID();
    initializeFields();
    initializeTexts();
    initializeFieldValues();
    lineUpFields();
  }

  public AddressInfoTab(int MemberID){
    this();
    this.initFieldContents(MemberID);
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

    countryField = new TextInput(countryFieldName);
    countryField.setLength(20);

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


  public boolean collect(ModuleInfo modinfo){

    if(modinfo != null){
      String street = modinfo.getParameter(this.streetFieldName);
      String city = modinfo.getParameter(this.cityFieldName);
      String province = modinfo.getParameter(this.provinceFieldName);
      String postal = modinfo.getParameter(this.postalCodeFieldName);
      String country = modinfo.getParameter(this.countryFieldName);
      String poBox = modinfo.getParameter(this.poBoxFieldName);

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

  public boolean store(ModuleInfo modinfo){
    System.err.println(this.getClass().getName() + ": in method store");
    return true;
  }

  public void initFieldContents(int MemberID){

  }




} // Class AddressInfoTab