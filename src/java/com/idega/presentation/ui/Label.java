/*
 * $Id: Label.java,v 1.3 2005/03/02 09:18:49 laddi Exp $
 *
 * Copyright (C) 2002-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWConstants;
import com.idega.presentation.IWContext;

/**
 * <p>
 * This class renders out a &lt;label&gt; element used in forms.
 * </p>
 *  Last modified: $Date: 2005/03/02 09:18:49 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">Laddi</a>
 * @version $Revision: 1.3 $
 */
public class Label extends InterfaceObject {
		
	private String _label;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = _label;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		_label = (String) values[1];
	}
	
	public Label() {
		super();
	}
	
	public Label(InterfaceObject object) {
		this("", object);
	}
	
	public Label(String label, InterfaceObject object) {
		_label = label;
		setMarkupAttribute("for", object.getID());
		setTransient(false);
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<label "+getMarkupAttributesString()+" >");
			print(_label);
			println("</label>");	
		} else if (IWConstants.MARKUP_LANGUAGE_WML.equals(getMarkupLanguage())) {	
			print(_label);
			print("<br/>");	
		}
	}	
	
	public void setLabel(String label) {
		this._label = label;
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}