//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.presentation.ui;



import com.idega.presentation.*;

import java.io.*;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class IntegerInput extends TextInput{



public IntegerInput(){

	this("untitled");

	setAsIntegers();

}



public IntegerInput(String name){

	super(name);

	setAsIntegers();

}



public IntegerInput(String name,String errorWarning){

	super(name);

	setAsIntegers(errorWarning);

}



public IntegerInput(String name,int value){

	super(name,Integer.toString(value));

	setAsIntegers();

}



public IntegerInput(String name,int value,String errorWarning){

	super(name,Integer.toString(value));

	setAsIntegers(errorWarning);

}





public void setValue(int value){

	setValue(Integer.toString(value));

}



public void setValue(Integer value){

	setValue(value.toString());

}





public void print(IWContext iwc)throws IOException{


	//if ( doPrint(iwc) ){

		if (getLanguage().equals("HTML")){



		if (keepStatus){

			if(iwc.getParameter(this.getName()) != null){

				setContent(iwc.getParameter(getName()));

			}

		}

			//if (getInterfaceStyle().equals("default"))

				println("<input type=\"text\" name=\""+getName()+"\" "+getMarkupAttributesString()+" >");

				//println("</input>");

			// }

		}



		else if (getLanguage().equals("WML")){

			print("<input type=\"text\" format=\"NN\" name=\""+getName()+"\" value=\""+getValueAsString()+"\" />");

		}

	//}

}







}

