/*
 * $Id: Strong.java,v 1.3 2005/03/08 19:25:19 tryggvil Exp $
 * Created on 10.7.2004
 *
 * Copyright (C) 2004-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;

/**
 * <p>
 * This component renders out a &lt;STRONG&gt; element around its children
 * </p>
 *  Last modified: $Date: 2005/03/08 19:25:19 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>,<a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class Strong  extends PresentationObjectContainer{

	/**
	 * 
	 */
	public Strong() {
		super();
		setTransient(false);
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
