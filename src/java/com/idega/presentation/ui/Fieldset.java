/*
 * Created on 3.1.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * Title:		Fieldset
 * Description:
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class Fieldset extends InterfaceObjectContainer {

	/**
	 * 
	 */
	public Fieldset() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	public void print(IWContext iwc) throws Exception {
		
		//<fieldset dir="ltl"><legend> "name" </legend> "content" </fieldset>
		
		if (getLanguage().equals("HTML")) {
			println("<fieldset " +getMarkupAttributesString()+ " ><legend>"+getName()+"</legend>");
			//		Catch all exceptions that are thrown in print functions of objects stored inside
			try {
				super.print(iwc);
			}
			catch (Exception ex) {
				println("<h1>Villa var&eth;!</h1>");
				println("IW Error");
				println("<pre>");
				println(ex.getMessage());
				ex.printStackTrace(System.err);
				println("</pre>");
			}
			println("</fieldset>");
		}
		else if (getLanguage().equals("WML")) {
			//println("<fieldset " +getMarkupAttributesString()+ " ><legend>"+getName()+"</legend>");
			//		Catch all exceptions that are thrown in print functions of objects stored inside
			try {
				super.print(iwc);
			}
			catch (Exception ex) {
				println("<h1>Villa var&eth;!</h1>");
				println("IW Error");
				println("<pre>");
				println(ex.getMessage());
				ex.printStackTrace(System.err);
				println("</pre>");
			}
			//println("</fieldset>");
		}
	}

}
