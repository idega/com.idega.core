//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import com.idega.builder.data.IBDomain;
import com.idega.builder.business.BuilderLogic;
import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.idegaweb.IWMainApplication;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class SubmitButton extends GenericButton{

private Window window;
private Image defaultImage;
//private Parameter includedParameter;
private boolean usingControlParameter=false;
private String parameterName;
private String parameterValue;
private String headerText;
private boolean asImageButton = false;

private static final String emptyString = "";

private boolean encloseByForm = true;

public SubmitButton(){
	this(emptyString,"Submit");
	setName(getDefaultName());
}

public SubmitButton(Image defaultImage){
	this(defaultImage,"default");
	setName(getDefaultName());
  this.parameterName=getDefaultName();
}

public SubmitButton(Image defaultImage, String name){
	this(name,"default");
	this.setButtonImage(defaultImage);
}

public SubmitButton(Image defaultImage, String name, String value){
	this(name,value);
	this.setButtonImage(defaultImage);
  this.setName(this.getID());
	this.parameterName=name;
	this.parameterValue=value;
	usingControlParameter=true;
}

/**
 * Constructor that includes another parameter/parametervalue with the submitbutton
 */
public SubmitButton(String displayText,String parameterName,String parameterValue){
  this(displayText);
  //this.includedParameter = new Parameter(parameterName,parameterValue);
  this.parameterName=parameterName;
  this.parameterValue=parameterValue;
  usingControlParameter=true;
  /*System.out.println("Inni i constructor");
  if (includedParameter==null){
    System.out.println("includedParameter==null");
  }*/
}

/**
 * Constructor that generates a parametername and the value is the displayText
 */
public SubmitButton(String displayText){
	this(emptyString,displayText);
	setName(getDefaultName());
}

public SubmitButton(String name,String displayText){
	super(name,displayText);
}


public String getDefaultName(){
	return "sub"+getID();
}

/**
*Only works if no form is around the button and it is used to open a new Window.
*/
public SubmitButton(Window window){
	this(emptyString,"Submit");
	setName(getDefaultName());
	this.window=window;
}

/**
*Only works if no form is around the button and it is used to open a new Window.
*/
public SubmitButton(Window window,String displayString){
	this(emptyString,displayString);
	setName(getDefaultName());
	this.window=window;
}

public SubmitButton(Window window,String name,String value){
	super(name,value);
	this.window=window;
}

private String generateScriptCode(Window myWindow){
	return emptyString;
}

public void setOnSubmit(String script){
	setAttribute("OnSubmit",script);
}

public void setTarget(String target){
	setAttribute("target",target);
}



  public void addIWSubmitListener(IWSubmitListener l, Form form, IWContext iwc){
    if (!listenerAdded()){
      postIWSubmitEvent(iwc, form);
    }
    super.addIWSubmitListener(l, iwc);
  }


  private void postIWSubmitEvent(IWContext iwc, Form form){
      eventLocationString = this.getID();
      IWSubmitEvent event = new IWSubmitEvent(this,IWSubmitEvent.SUBMIT_PERFORMED);
      //this.addParameter(sessionEventStorageName,eventLocationString);
      this.setOnClick("javascript:document."+form.getID()+"."+IWMainApplication.IWEventSessionAddressParameter+".value=this.id ");
      iwc.setSessionAttribute(eventLocationString, event);
      listenerAdded(true);
  }


public void setStyle(String style) {
  setAttribute("class",style);
}


public void main(IWContext iwc){
  if (usingControlParameter){
    if(!parameterName.equals(emptyString)){
	this.getParentForm().addControlParameter(parameterName,emptyString);
	this.setOnClick("this.form."+parameterName+".value='"+parameterValue+"'");
      /*if(this.defaultImage==null){
	this.getParentForm().setControlParameter(parameterName,emptyString);
	this.setOnClick("this.form."+parameterName+".value='"+parameterValue+"'");
      }
      else{
	Form form = getParentForm();
	form.setControlParameter(parameterName,emptyString);
	Parameter par = form.getControlParameter();
	if(par!=null){
	  this.setOnClick("this.form."+par.getID()+".value='"+parameterValue+"'");
	}
	else{
	  throw new RuntimeException("ControlParameter is null in parent form");
	}
      }*/

    }
  }

}

public void setButtonImage(Image image){
  this.defaultImage=image;
}

public void setAsImageButton(boolean asImageButton) {
  this.asImageButton = asImageButton;
}


private void printButton(IWContext iwc) throws IOException{
	if ( asImageButton ) {
	  defaultImage = iwc.getApplication().getCoreBundle().getImageButton(getValue());
	}

	if (defaultImage == null){

		print("<input type=\"submit\" name=\""+getName()+"\" "+getAttributeString()+" >");
	}
	else{
		setAttribute("border","0");
		String URL = defaultImage.getURL();
		if ( URL == null ){
		  URL = defaultImage.getMediaURL(iwc);
		}

		IBDomain d = BuilderLogic.getInstance().getCurrentDomain(iwc);
		if (d.getURL() != null) {
		  if (URL.startsWith("/")) {
                    String protocol;
//@todo this is case sensitive and could break! move to IWContext. Also done in Link, SubmitButton, Image and PageIncluder, Page
                    if( iwc.getRequest().isSecure() ){
                      protocol = "https://";
                    }
                    else{
                      protocol = "http://";
                    }
                    URL = protocol+d.getURL()+URL;
		  }
		}

		print("<input type=\"image\" src=\""+URL+"\" name=\""+getName()+"\" "+getAttributeString()+" >");
	}
}


public void print(IWContext iwc) throws Exception{

	//if ( doPrint(iwc) ) {
		if (getLanguage().equals("HTML")){

			if ( encloseByForm ) {
				if(isEnclosedByForm()){
	
				//if (getInterfaceStyle().equals("something")){
				//}
				//else{
	
					printButton(iwc);
	
				//}
				}
				else{
					Form myForm = new Form();
					myForm.setParentObject(getParentObject());
					this.setParentObject(myForm);
					myForm.add(this);
	
					//If a window is put inside the submit button, only implemented so if you have no form default around the button
					if (window != null){
						getParentForm().setAction(window.getURL(iwc));
						getParentForm().setTarget(window.getTarget());
						//getParentForm().setTarget("#");
						getParentForm().setOnSubmit(window.getCallingScriptStringForForm(iwc));
					}
					myForm._print(iwc);
				}
			}
			else {
				printButton(iwc);	
			}
		}
	//}
}

public void setToEncloseByForm(boolean enclose) {
	encloseByForm = enclose;
}


}
