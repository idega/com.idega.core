package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;


public class RadioButton extends InterfaceObject{

private String checked;

public RadioButton(){
	this("untitled");
}

public RadioButton(String name){
	this(name,"unspecified");
}

public RadioButton(String name,String value){
	super();
	setName(name);
	setContent(value);
	checked="";
}


public void setSelected(){
	this.checked="checked";
}

public void print(IWContext iwc)throws IOException{
	initVariables(iwc);
	//if ( doPrint(iwc) ){
		if (getLanguage().equals("HTML")){
			//if (getInterfaceStyle().equals("something")){
			//
			//}
			//else{
                          if (statusKeptOnAction()){
                            String[] parameters = iwc.getParameterValues(this.getName());
                            if(parameters != null){
                              for(int i = 0;i<parameters.length;i++){
                                if(parameters[i].equals(this.getValue())){
                                  setSelected();
                                }
                              }

                            }
                          }

                            println("<input type=\"radio\" name=\""+getName()+"\" "+checked+"  "+getAttributeString()+" >");
				//println("</input>");

			//}
		}
	//}
}


}

