/*
 * Created on 10.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


/**
 * @author laddi
 */
public class Legend extends PresentationObject {
	
	private String _legend;
	
	public Legend() {
		this("untitled");
	}
	
	public Legend(String legend) {
		_legend = legend;
	}

	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<legend " + getMarkupAttributesString() + ">" + _legend + "</legend>");
		}
	}
	
	public void setLegend(String legend) {
		setName(legend);
	}
}