//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.data.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class DropdownMenu extends InterfaceObject{

private Vector theElements;
private boolean keepStatus;
private String selectedElementValue;
private Script script;

public DropdownMenu(){
	this("untitled");
}

public DropdownMenu(String Name){
	//super();
	setName(Name);
	initialize();
	//setAttribute("CLASS","select");
}

/**
 * Populates the dropdownMenu from a List of GenericEntity objects
 */
public DropdownMenu(List entityList){
	//super();

        initialize();
	//setAttribute("CLASS","select");

	if(entityList != null){
          int length = entityList.size();
		if(length > 0){
                  GenericEntity entity = (GenericEntity) entityList.get(0);
			setName(entity.getEntityName());
                        addMenuElements(entityList);
		}
                else{
                  setName("untitled");
                }
	}
        else{
          setName("untitled");
        }
}



public DropdownMenu(GenericEntity[] entity){
	//super();
	//setName("untitled");
        initialize();
	//setAttribute("CLASS","select");

	if(entity != null){
		if(entity.length > 0){
			setName(entity[0].getEntityName());
                          for (int i=0;i<entity.length;i++){
                                  //if(entity[i].getID() != -1 && entity[i].getName() != null){
                                          addMenuElement(entity[i].getID(),entity[i].getName());
                                  //}
                          }
		}
                else{
                  setName("untitled");
                }
	}
        else{
          setName("untitled");
        }
}

public DropdownMenu(GenericEntity[] entity, String Name){
  this(entity);
  setName(Name);
}

public void initialize(){
	this.theElements = new Vector(10);
	this.keepStatus = false;
}

/**
 * Populates the dropdownMenu from a List of GenericEntity objects with a custom name
 */
public DropdownMenu(List entityList, String Name){
  this(entityList);
  setName(Name);
}

public void removeElements(){
  theElements.clear();
}


public void addMenuElementFirst(String value,String DisplayString){
	theElements.add(0,new MenuElement(DisplayString,value));
}

//Here is a possible bug, if there are many elements with the same value
public void addMenuElement(String value, String DisplayString){
	theElements.addElement( new MenuElement(DisplayString,value));
}

public void addMenuElement(int value,String DisplayString){
	addMenuElement(Integer.toString(value),DisplayString);
}

public void addMenuElement(String value){
	theElements.addElement(new MenuElement(value,value));
}

public void setMenuElementDisplayString(String elementValue,String displayString){
  MenuElement element = getMenuElement(elementValue);
  element.setName(displayString);
}

/**
* Add menu elements from an List of GenericEntity Objects
*/
public void addMenuElements(List entityList){
	if(entityList != null){
          int length = entityList.size();
          GenericEntity entity;
          for (int i=0;i<length;i++){
            entity = (GenericEntity) entityList.get(i);
            //if(entity[i].getID() != -1 && entity[i].getName() != null){
            addMenuElement(entity.getID(),entity.getName());
            //}
          }
	}
}

public void addSeparator(){
	theElements.addElement( new MenuElement("----------------------------",""));
}

public void setAttributeToElement(String ElementValue,String AttributeName, String AttributeValue){
	getMenuElement(ElementValue).setAttribute(AttributeName,AttributeValue);
}

public void addDisabledMenuElement(String Value, String DisplayString){
	addMenuElement(Value,DisplayString);
	setDisabled(Value);
}

public void setDisabled(String ElementValue){
	getMenuElement(ElementValue).setDisabled(true);
}

private void deselectElements(){
  for (Enumeration e = theElements.elements(); e.hasMoreElements(); ){
    ((MenuElement)e.nextElement()).setSelected(false);
  }
}

public void setSelectedElement(String ElementValue){
        deselectElements();
	getMenuElement(ElementValue).setSelected(true);
	selectedElementValue=ElementValue;
}

public String getSelectedElementValue(){
	if (selectedElementValue == null){
		return "";
	}
	else{
		return selectedElementValue;
	}
}


public void keepStatusOnAction(){
	this.keepStatus=true;
}

public void setToGoToURL(){
        this.setOnChange("location.href=this.form."+getName()+".options[this.form."+getName()+".selectedIndex].value");
}

/**

 * Sets the dropdown to submit automatically.

 * Must add to a form before this function is used!!!!

 */
public void setToSubmit(){
        this.setOnChange("this.form.submit()");
}

public void setOnSelect(String onValue,DropdownMenu menuToChange,String valueToChangeToInMenu){
  Script script = this.getAssociatedScript();
  String functionName="connectDropdown";
  if(script.doesFunctionExist(functionName)){
    script.addFunction(functionName,"function "+functionName+"(){\r \r}");
    this.setOnChange(functionName+"()");
  }
  script.addToFunction(functionName,"this.form."+menuToChange.getName()+".value=this.form."+getName()+".options[this.form."+getName()+".selectedIndex].value;\r");
}

//Returns the first menuelement in the menu if there is no match
private MenuElement getMenuElement(String ElementValue){

	MenuElement theReturn = new MenuElement();

	for (Enumeration e = theElements.elements(); e.hasMoreElements(); ){


		MenuElement tempobj = (MenuElement)  e.nextElement();
		if (tempobj.getValue().equals(ElementValue)){
			theReturn=tempobj;
		}

	}
	return theReturn;
}

public void setStyle(String style) {
  setAttribute("class",style);
}

public void print(IWContext iwc)throws IOException{

	theElements.trimToSize();
	initVariables(iwc);
	//if ( doPrint(iwc) ){
                if(script!=null){
                  ((PresentationObjectContainer)getParentObject()).add(script);
                }
		if (getLanguage().equals("HTML")){

			if (this.keepStatus==true){
				if(getRequest().getParameter(getName()) != null){
					setSelectedElement(getRequest().getParameter(getName()));
				}
			}

			if (getInterfaceStyle().equals("default")){
				println("<select name=\""+getName()+"\" "+getAttributeString()+" >");


				for (Enumeration e = theElements.elements(); e.hasMoreElements(); ){

					MenuElement tempobj = (MenuElement)  e.nextElement();
					tempobj.print(iwc);

				}

				println("</select>");
			}
		}

		else if (getLanguage().equals("WML")){

			if (this.keepStatus==true){
				if(getRequest().getParameter(getName()) != null){
					setSelectedElement(getRequest().getParameter(getName()));
				}
			}

			if (getInterfaceStyle().equals("default")){
				println("<select name=\""+getName()+"\" "+getAttributeString()+" >");


				for (Enumeration e = theElements.elements(); e.hasMoreElements(); ){

					MenuElement tempobj = (MenuElement)  e.nextElement();
					tempobj.print(iwc);

				}

				println("</select>");
			}
		}

	//}
}


  public synchronized Object clone() {
    DropdownMenu obj = null;
    try {
      obj = (DropdownMenu)super.clone();
      if(this.theElements != null){
        obj.theElements = (Vector)this.theElements.clone();
      }
      obj.keepStatus = this.keepStatus;
      obj.selectedElementValue = this.selectedElementValue;
      if(this.script != null){
        obj.script = (Script)this.script.clone();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }

}

