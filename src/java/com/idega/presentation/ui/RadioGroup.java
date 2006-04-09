/*

 * $Id: RadioGroup.java,v 1.11 2006/04/09 12:13:16 laddi Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.presentation.ui;



import java.util.Vector;

import java.util.Iterator;

import com.idega.presentation.Table;

import com.idega.presentation.IWContext;

import com.idega.presentation.text.Text;

import com.idega.data.IDOLegacyEntity;
import com.idega.idegaweb.IWConstants;



/**

 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 * @version 1.2

 */

public class RadioGroup extends InterfaceObject {

  Vector _buttons;

  Vector _texts;

  Table _frameTable;

  int _rowIndex = 1;

  int _columnIndex = 1;

  int _rows = 0;

  int _columns = 0;

  boolean _fillVertical = true;

  String _name;



  private RadioGroup(){

    this._buttons = new Vector(0);

    this._texts = new Vector(0);

    this._frameTable = new Table();

    add(this._frameTable);

  }



  public RadioGroup(String name){

    this();

    this._name = name;

  }



  public RadioGroup(IDOLegacyEntity[] entity){

    this();

    if (entity != null){

      int length=entity.length;

      for (int i = 0; i < length;i++){

        this._buttons.add(new RadioButton(entity[i].getEntityName(),Integer.toString(entity[i].getID())));

        Text temp = new Text(entity[i].getName());

        temp.setFontSize(1);

        this._texts.add(temp);

      }

    }

  }



  public void keepStatusOnAction() {

    if (this._buttons != null) {

      Iterator it = this._buttons.iterator();

      while (it.hasNext()) {

        RadioButton b = (RadioButton)it.next();

        b.keepStatusOnAction();

      }

    }

  }



  public void setVertical(boolean value){

    this._fillVertical = value;

  }





  public void addRadioButton(String value, Text DisplayString){

    addRadioButton(value, DisplayString, false);

  }



  public void addRadioButton(int value,Text DisplayString){

    addRadioButton(value, DisplayString, false);

  }



  public void addRadioButton(String value){

    addRadioButton(value, false);

  }



  public void addRadioButton(RadioButton radioButton, Text DisplayText){

      addRadioButton(radioButton, DisplayText, false);

  }



  public void addRadioButton(RadioButton[] radioButtons, Text[] DisplayTexts){

    addRadioButton(radioButtons, DisplayTexts,-1);

  }











  /**/



  public void addRadioButton(String value, Text DisplayString, boolean isSelected){

    RadioButton button = new RadioButton(this._name,value);

    this._buttons.add(button);

    this._texts.add(DisplayString);

    if (isSelected) {

      button.setSelected();

    }

  }



  public void addRadioButton(int value,Text DisplayString, boolean isSelected){

    RadioButton button = new RadioButton(this._name,Integer.toString(value));

    this._buttons.add(button);

    this._texts.add(DisplayString);

    if(isSelected){

      button.setSelected();

    }

  }



  public void addRadioButton(int value,Text DisplayString, boolean isSelected, String textStyle, String buttonStyle){

    RadioButton button = new RadioButton(this._name,Integer.toString(value));

    button.setMarkupAttribute("style",buttonStyle);

    DisplayString.setFontStyle(textStyle);

    this._buttons.add(button);

    this._texts.add(DisplayString);

    if(isSelected){

      button.setSelected();

    }

  }



  public void addRadioButton(String value, boolean isSelected){

    RadioButton button = new RadioButton(this._name,value);

    this._buttons.add(button);

    this._texts.add(new Text(value));

    if (isSelected) {

      button.setSelected();

    }

  }



  public void addRadioButton(RadioButton radioButton, Text DisplayText, boolean isSelected){

    this._buttons.add(radioButton);

    this._texts.add(DisplayText);

    if(isSelected){

      radioButton.setSelected();

    }

  }



  public void addRadioButton(RadioButton[] radioButtons, Text[] DisplayTexts, int selectedIndex){

    for (int i = 0; i < radioButtons.length; i++) {

      this._buttons.add(radioButtons[i]);

      this._texts.add(DisplayTexts[i]);

      if(i == selectedIndex){

        radioButtons[i].setSelected();

      }

    }

  }



  /**/



  private void heightenIndexes(){

    if(this._fillVertical){

      if(this._rows > 0){

        if (this._rowIndex % this._rows == 0) {

          this._rowIndex = 0;

          this._columnIndex++;

        }

      }

      this._rowIndex++;

    }else{

      if(this._columns > 0){

        if (this._columnIndex % this._columns==0){

          this._columnIndex = 0;

          this._rowIndex++;

        }

      }

      this._columnIndex++;

    }

  }



  public void setHeight(int height){

    this._rows = height;

  }



  public void setWidth(int width){

    this._columns = width;

  }



  public void main(IWContext iwc) throws Exception {

    if( this._columns > 0 && this._rows > 0){

      this._frameTable.resize(this._columns,this._rows);

      for (int i = 0; i < this._buttons.size(); i++) {

        if(!(this._rowIndex > this._rows) && !(this._columnIndex > this._columns)){

          this._frameTable.add((RadioButton)this._buttons.get(i),this._columnIndex*2-1,this._rowIndex);

          this._frameTable.add((Text)this._texts.get(i),this._columnIndex*2,this._rowIndex);

          heightenIndexes();

        } else {

          System.err.println("too many Radiobuttons for table cells");

          break;

        }

      }

    }else if(this._columns > 0){

      this._frameTable.resize(this._columns,(int)Math.ceil(this._buttons.size()/this._columns));

      for (int i = 0; i < this._buttons.size(); i++) {

          this._frameTable.add((RadioButton)this._buttons.get(i),this._columnIndex*2-1,this._rowIndex);

          this._frameTable.add((Text)this._texts.get(i),this._columnIndex*2,this._rowIndex);

          heightenIndexes();

      }

    }else if(this._rows > 0){

      this._frameTable.resize((int)Math.ceil(this._buttons.size()/this._rows),this._rows);

      for (int i = 0; i < this._buttons.size(); i++) {

          this._frameTable.add((RadioButton)this._buttons.get(i),this._columnIndex*2-1,this._rowIndex);

          this._frameTable.add((Text)this._texts.get(i),this._columnIndex*2,this._rowIndex);

          heightenIndexes();

      }

    }else{  // columnIndex == 0 && rowIndex == 0

      this._frameTable.resize(1,this._buttons.size());

      for (int i = 0; i < this._buttons.size(); i++) {

          this._frameTable.add((RadioButton)this._buttons.get(i),this._columnIndex*2-1,this._rowIndex);

          this._frameTable.add((Text)this._texts.get(i),this._columnIndex*2,this._rowIndex);

          heightenIndexes();

      }

    }



  }


	public void print(IWContext iwc) throws Exception {
		if(IWConstants.MARKUP_LANGUAGE_WML.equals(iwc.getMarkupLanguage())) {
			print("<select name=\""+this._name+"\" >");
				Iterator buttonIter = this._buttons.iterator();
				Iterator textIterator = this._texts.iterator();
				while(buttonIter.hasNext()) {
					RadioButton button = (RadioButton) buttonIter.next();
					if(textIterator.hasNext()){
						Text text = (Text)textIterator.next();
						print("<option value=\""+button.getValueAsString()+"\">"+text.getText()+"</option>");
					} else {
						print("<option value=\""+button.getValueAsString()+"\"></option>");
					}
				}
			print("</select>");
		} else {
			super.print(iwc);
		}
	}


  public void setBorder(int i){

    this._frameTable.setBorder(i);

  }



  public void setStyle(String style){

    setMarkupAttribute("style",style);

  }



  /**

   * Sets the radio group to submit automatically.

   * Must add to a form before this function is used!!!!

   */

  public void setToSubmit() {

    if (this._buttons != null) {

      Iterator it = this._buttons.iterator();

      while (it.hasNext()) {

        RadioButton b = (RadioButton)it.next();

        b.setToSubmit();

      }

    }

  }



  /**

   *

   */

  public void setSelected(int value) {

    setSelected(Integer.toString(value));

  }



  /**

   *

   */

  public void setSelected(String value) {

    if (this._buttons != null) {

      Iterator it = this._buttons.iterator();

      while (it.hasNext()) {

        RadioButton b = (RadioButton)it.next();

        if (b.getValueAsString().equals(value)) {
					b.setSelected();
				}

      }

    }

  }



  public String getSelected() {

    if (this._buttons != null) {

      Iterator it = this._buttons.iterator();

      while (it.hasNext()) {

        RadioButton b = (RadioButton)it.next();

        System.out.println("Button value = " + b.getValueAsString());

        System.out.println("Button _checked = " + b.getSelected());

        if (b.getSelected()) {
					return(b.getValueAsString());
				}

      }

    }



    return(null);

  }



  /**

   *

   */

  public synchronized Object clone() {

    RadioGroup obj = null;

    try {

      obj = (RadioGroup)super.clone();

      obj._buttons = (Vector)this._buttons.clone();

//      obj._fillVertical = _fillVertical;

      obj._frameTable = (Table)this._frameTable.clone();

      obj._name = this._name;

    }

    catch(Exception ex) {

      ex.printStackTrace(System.err);

    }



    return(obj);

  }

	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}