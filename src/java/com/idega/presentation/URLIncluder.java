/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.presentation;



import java.io.*;

import java.net.*;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>, Eirikur Hrafnsson

*@version 1.2

*/

public class URLIncluder extends PresentationObject{





private String URLString;





public URLIncluder(){

	this("");

}



public URLIncluder(String URLString){

	super();

	setURL(URLString);

}



public void setURL(String URLString){

	this.URLString=URLString;

}



public String getURL(){

	return this.URLString;

}



public void print(IWContext iwc)throws Exception{


	if (doPrint(iwc)){

		if (getMarkupLanguage().equals("HTML")){



			//if (getInterfaceStyle().equals("something")){

			//}

			//else{

				try{



				URL url = new URL(getURL());

                                String returnString = "";

			         //URL url = new URL("http://localhost/servlet/plainTextModule");



			         URLConnection uc = url.openConnection();

			         uc.setDoOutput(true);

			         uc.setDoInput(true);

			         uc.setUseCaches(false);



					 uc.setRequestProperty("Content-type", "text/html");

					 //uc.setRequestProperty("Content-type","application/x-www-form-urlencoded");



			         /*DataOutputStream dos = new DataOutputStream(uc.getOutputStream());

			         dos.writeBytes(qry);

			         dos.flush();

			         dos.close();*/



			         InputStreamReader in = new InputStreamReader(uc.getInputStream());



			         int chr = in.read();

			         while (chr != -1) {

			            returnString = returnString + (String.valueOf((char) chr));

			            chr = in.read();

			         }



			         println(returnString);



			         //clean up!



			         in.close();

		         }

		         catch(MalformedURLException e){

		         	throw new IOException(e.getMessage());

		         }



			//}

		}

		else if (getMarkupLanguage().equals("WML")){



		}

	}

	else{

		super.print(iwc);

	}

}





}//End class

