/*
 * Created on 10.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


/**
 * @author laddi
 */
public class Legend extends PresentationObject {
	
	private String _legend;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = _legend;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		_legend = ((String) values[1]);
	}
	public Legend() {
		this("untitled");
	}
	
	public Legend(String legend) {
		_legend = legend;
		setTransient(false);
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