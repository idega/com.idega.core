/*
 * $Id: PreformattedText.java,v 1.5 2006/04/09 12:13:16 laddi Exp $
 * Created on 2.5.2003
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;

import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * <p>
 * This component renders out a 'preformatted' or &lt;PRE&gt; element around its children.<br>
 * This can be used to escape out html or xml tags to be presented to a user without rendering.
 * </p>
 *  Last modified: $Date: 2006/04/09 12:13:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>,<a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 */
public class PreformattedText extends PresentationObject {

	private String _text;
	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = this._text;
		return values;
	}
	
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._text = (String)values[1];
	}
	
	public PreformattedText(){
		this("");
	}

	public PreformattedText(String text){
		super();
		setText(text);
		setTransient(false);
	}

	public void print(IWContext iwc)throws Exception{
		if (getMarkupLanguage().equals("HTML")){
			print("<pre "+getMarkupAttributesString()+" >");
			print(getText());
			print("</pre>");
		}
		else if (getMarkupLanguage().equals("WML")){
			print(getText());
		}
	}

	public String getText(){
		return this._text;
	}

	public void setText(String text){
		this._text = text;
	}
}
