/*
 * Created on 10.7.2004
 */
package com.idega.presentation.text;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;

/**
 * Title: Strong
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: idega Software
 * @author 2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version 1.0
 */
public class Strong  extends PresentationObjectContainer{

	/**
	 * 
	 */
	public Strong() {
		super();
		setTransient(false);
		// TODO Auto-generated constructor stub
	}
	
	public void print(IWContext iwc)throws Exception{
		if (getMarkupLanguage().equals("HTML")){
			println("<strong>");
			super.print(iwc);
			println("</strong>");
		} else if (getMarkupLanguage().equals("WML")){
			if(this.isEmpty()){
				print("<strong/>");
			} else {
				print("<strong>");
				super.print(iwc);
				print("</strong>");		
			}
		}
	}


}
