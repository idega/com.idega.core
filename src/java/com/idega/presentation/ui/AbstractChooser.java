/*
 * $Id: AbstractChooser.java,v 1.10 2002/03/27 17:23:41 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import com.idega.idegaweb.IWConstants;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.Image;
import com.idega.idegaweb.IWBundle;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>,<a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public abstract class AbstractChooser extends PresentationObjectContainer {
  static final String CHOOSER_SELECTION_PARAMETER = "iw_ch_p";
  static final String DISPLAYSTRING_PARAMETER = "iw_ch_d";
  static final String VALUE_PARAMETER = "iw_ch_v";
  static final String DISPLAYSTRING_PARAMETER_NAME = "iw_ch_d_n";
  static final String VALUE_PARAMETER_NAME = "iw_ch_v_n";
  static final String SCRIPT_PREFIX_PARAMETER = "iw_ch_ch_p";
  static final String SCRIPT_SUFFIX_PARAMETER = "iw_ch_s";

  public String chooserParameter = VALUE_PARAMETER;
  public String displayInputName = DISPLAYSTRING_PARAMETER;
  private boolean _addForm = true;
  private boolean  _addTextInput = true;
  private Form _form = null;
  private Image _buttonImage = null;
  private String _style = IWConstants.BUILDER_FONT_STYLE_INTERFACE;
  private String _stringValue;
  private String _stringDisplay;
  private String _attributeValue;
  private String _attributeName;
  private Link link = null;
  private boolean disabled = true;

  /**
  *
  * @param aDisabled - the new value for disabled
  */
  public void setDisabled(boolean aDisabled){
	  disabled = aDisabled;
  }

  /**
   *
   */
  public AbstractChooser() {
  }

  /**
   *
   */
  public abstract Class getChooserWindowClass();

  /**
   *
   */
  public String getChooserParameter() {
    return(chooserParameter);
  }

  /**
   *
   */
  public void setChooserParameter(String parameterName) {
    chooserParameter = parameterName;
    if (displayInputName == DISPLAYSTRING_PARAMETER) {
      displayInputName = parameterName + "_displaystring";
    }
  }

  protected void setChooserValue(String displayString,String valueString){
    this._stringValue=valueString;
    this._stringDisplay=displayString;
  }

  protected void setChooserValue(String displayString,int valueInt){
    setChooserValue(displayString,Integer.toString(valueInt));
  }

  public void setValue(Object objectValue){
    setValue(objectValue.toString());
  }

  public void setValue(String stringValue){
    setChooserValue(stringValue,stringValue);
  }


  public String getChooserValue(){
   return _stringValue;
  }

  /**
   *
   */
  public void setName(String name) {
    displayInputName = name;
    if (chooserParameter == VALUE_PARAMETER) {
      chooserParameter = name + "_chooser";
    }
  }

  /**
   *
   */
  public String getName() {
    return(displayInputName);
  }

  /**
   *
   */
  public void _main(IWContext iwc)throws Exception{
    super._main(iwc);
    IWBundle bundle = getBundle(iwc);
    if(_addForm){
      _form = new Form();
      _form.setWindowToOpen(getChooserWindowClass());
      add(_form);
      _form.add(getTable(iwc,bundle));
    }
    else{
      add(getTable(iwc,bundle));
      _form = getParentForm();
    }

  }

  /**
   *
   */
  public PresentationObject getTable(IWContext iwc,IWBundle bundle) {
    Table table = new Table(2,1);
    table.setCellpadding(0);
    table.setCellspacing(0);

    Parameter value = new Parameter(getChooserParameter(),"");
    if(_stringValue!=null){
      value.setValue(_stringValue);
    }
    table.add(value);

    PresentationObject object = getPresentationObject(iwc);

    table.add(new Parameter(VALUE_PARAMETER_NAME,value.getName()));
    //GenericButton button = new GenericButton("chooserbutton",bundle.getResourceBundle(iwc).getLocalizedString(chooserText,"Choose"));
    if (_addForm) {
      SubmitButton button = new SubmitButton("Choose");
      table.add(button,2,1);
      _form.addParameter(CHOOSER_SELECTION_PARAMETER,getChooserParameter());
      _form.addParameter(SCRIPT_PREFIX_PARAMETER,"window.opener.document."+_form.getID()+".");
      _form.addParameter(SCRIPT_SUFFIX_PARAMETER,"value");
    }
    else {
      getLink();

      link.setWindowToOpen(getChooserWindowClass());
      link.addParameter(CHOOSER_SELECTION_PARAMETER,getChooserParameter());

      link.addParameter(SCRIPT_PREFIX_PARAMETER,"window.opener.document."+getParentFormString(this));
      link.addParameter(SCRIPT_SUFFIX_PARAMETER,"value");
      link.addParameter(DISPLAYSTRING_PARAMETER_NAME,object.getName());
      link.addParameter(VALUE_PARAMETER_NAME,value.getName());
      if ( _attributeName != null && _attributeValue != null ) {
	link.addParameter(_attributeName,_attributeValue);
      }
      table.add(link,2,1);
    }

    if( _addTextInput ){
      table.add(object,1,1);
    }else {
      HiddenInput hInput = new HiddenInput(displayInputName);
      table.add(hInput,1,1);
    }


    table.add(new Parameter(DISPLAYSTRING_PARAMETER_NAME,"151324213"));
    return(table);
  }

  public PresentationObject getPresentationObject(IWContext iwc) {
    TextInput input = new TextInput(displayInputName);
    input.setDisabled(disabled);

    if (_style != null) {
      input.setAttribute("style",_style);
    }

    if(_stringDisplay != null){
      input.setValue(_stringDisplay);
    }

    return input;
  }

  /*
   *
   */
  private String getParentFormString(PresentationObject obj) {
    String returnString = "";

    if (obj.getParentObject() != null) {
      Object newObj = obj.getParentObject();
      if (!(newObj instanceof Form)) {
	returnString = getParentFormString((PresentationObject)newObj);
      }
      else {
	returnString =  ((PresentationObject)newObj).getID()+".";
      }
    }

    return(returnString);
  }

  public void setInputStyle(String style) {
    _style = style;
  }

  public void addForm(boolean addForm){
    _addForm = addForm;
  }

  public void setChooseButtonImage(Image buttonImage){
    _buttonImage = buttonImage;
  }

  public void setParameterValue(String attributeName, String attributeValue) {
    _attributeName = attributeName;
    _attributeValue = attributeValue;
  }

  public void addParameterToChooserLink(String param, String value){
    getLink().addParameter(param,value);
  }

  private Link getLink(){
    if( link==null ){
      if (_buttonImage == null){
	link = new Link("Choose");
      }
      else{
	_buttonImage.setHorizontalSpacing(3);
	link = new Link(_buttonImage);
      }
    }

    return link;
  }

  public void addTextInput(boolean addTextInput){
    this._addTextInput = addTextInput;
  }

}