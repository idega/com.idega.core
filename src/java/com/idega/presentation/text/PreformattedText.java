/*
 * Created on 2.5.2003
 */
package com.idega.presentation.text;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author laddi
 */
public class PreformattedText extends PresentationObject {

	String _text;
	
	public PreformattedText(){
		this("");
	}

	public PreformattedText(String text){
		super();
		setText(text);
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
		return _text;
	}

	public void setText(String text){
		_text = text;
	}
}
