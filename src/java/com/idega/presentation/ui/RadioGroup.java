//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.RadioButton;
import com.idega.data.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class RadioGroup extends InterfaceObjectContainer{

Vector buttons;
Vector texts;
Table frameTable;
int rowIndex = 1;
int columnIndex = 1;
int rows = 0;
int columns = 0;
boolean fillVertical = true;
String name;

private RadioGroup(){
  buttons = new Vector(0);
  texts = new Vector(0);
  frameTable = new Table();
  add(frameTable);
}

public RadioGroup(String name){
  this();
  this.name = name;
}

public RadioGroup(GenericEntity[] entity){
  this();
  if (entity != null){
    int length=entity.length;
    for (int i = 0; i < length;i++){
      buttons.add(new RadioButton(entity[i].getEntityName(),Integer.toString(entity[i].getID())));
      Text temp = new Text(entity[i].getName());
      temp.setFontSize(1);
      texts.add(temp);
    }
  }
}

public void keepStatusOnAction(){
  if (buttons != null){
    for(int i=0;i<buttons.size();i++){
      ((RadioButton)buttons.get(i)).keepStatusOnAction();
    }
  }
}

public void setVertical(boolean value){
  this.fillVertical = value;
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
  RadioButton button = new RadioButton(name,value);
  buttons.add(button);
  texts.add(DisplayString);
  if(isSelected){
    button.setSelected();
  }
}

public void addRadioButton(int value,Text DisplayString, boolean isSelected){
  RadioButton button = new RadioButton(name,Integer.toString(value));
  buttons.add(button);
  texts.add(DisplayString);
  if(isSelected){
    button.setSelected();
  }
}

public void addRadioButton(int value,Text DisplayString, boolean isSelected, String textStyle, String buttonStyle){
  RadioButton button = new RadioButton(name,Integer.toString(value));
  button.setAttribute("style",buttonStyle);
  DisplayString.setFontStyle(textStyle);
  buttons.add(button);
  texts.add(DisplayString);
  if(isSelected){
    button.setSelected();
  }
}

public void addRadioButton(String value, boolean isSelected){
  RadioButton button = new RadioButton(name,value);
  buttons.add(button);
  texts.add(new Text(value));
  if(isSelected){
    button.setSelected();
  }
}

public void addRadioButton(RadioButton radioButton, Text DisplayText, boolean isSelected){
  buttons.add(radioButton);
  texts.add(DisplayText);
  if(isSelected){
    radioButton.setSelected();
  }
}

public void addRadioButton(RadioButton[] radioButtons, Text[] DisplayTexts, int selectedIndex){
  for (int i = 0; i < radioButtons.length; i++) {
    buttons.add(radioButtons[i]);
    texts.add(DisplayTexts[i]);
    if(i == selectedIndex){
      radioButtons[i].setSelected();
    }
  }
}

/**/

private void heightenIndexes(){
  if(fillVertical){
    if(rows > 0){
      if (rowIndex%rows==0){
        rowIndex = 0;
        columnIndex++;
      }
    }
    rowIndex++;
  }else{
    if(columns > 0){
      if (columnIndex%columns==0){
        columnIndex = 0;
        rowIndex++;
      }
    }
    columnIndex++;
  }
}

public void setHeight(int height){
  this.rows = height;
}

public void setWidth(int width){
  this.columns = width;
}

public void main(IWContext iwc) throws Exception {
  if( columns > 0 && rows > 0){
    frameTable.resize(columns,rows);
    for (int i = 0; i < buttons.size(); i++) {
      if(!(rowIndex > rows) && !(columnIndex > columns)){
        frameTable.add((RadioButton)buttons.get(i),columnIndex*2-1,rowIndex);
        frameTable.add((Text)texts.get(i),columnIndex*2,rowIndex);
        heightenIndexes();
      } else {
        System.err.println("too many Radiobuttons for table cells");
        break;
      }
    }
  }else if(columns > 0){
    frameTable.resize(columns,(int)Math.ceil(buttons.size()/columns));
    for (int i = 0; i < buttons.size(); i++) {
        frameTable.add((RadioButton)buttons.get(i),columnIndex*2-1,rowIndex);
        frameTable.add((Text)texts.get(i),columnIndex*2,rowIndex);
        heightenIndexes();
    }
  }else if(rows > 0){
    frameTable.resize((int)Math.ceil(buttons.size()/rows),rows);
    for (int i = 0; i < buttons.size(); i++) {
        frameTable.add((RadioButton)buttons.get(i),columnIndex*2-1,rowIndex);
        frameTable.add((Text)texts.get(i),columnIndex*2,rowIndex);
        heightenIndexes();
    }
  }else{  // columnIndex == 0 && rowIndex == 0
    frameTable.resize(1,buttons.size());
    for (int i = 0; i < buttons.size(); i++) {
        frameTable.add((RadioButton)buttons.get(i),columnIndex*2-1,rowIndex);
        frameTable.add((Text)texts.get(i),columnIndex*2,rowIndex);
        heightenIndexes();
    }
  }

}


public void setBorder(int i){
  frameTable.setBorder(i);
}

public void setStyle(String style){
  setAttribute("style",style);
}

} // end Class

