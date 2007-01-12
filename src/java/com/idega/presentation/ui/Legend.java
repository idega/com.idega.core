/*
 * $Id: Image.java,v 1.3 2005/03/02 09:18:49 laddi Exp $
 * Created on 10.11.2003
 *
 * Copyright (C) 2003-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * <p>
 * This class renders out a &lt;legend&gt; element used in forms and in conjunction with the FieldSet component/tag.
 * </p>
 *  Last modified: $Date: 2005/03/02 09:18:49 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">Laddi</a>
 * @version $Revision: 1.3 $
 * @see FieldSet
 */
public class Legend extends PresentationObject {
	
	private String _legend;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = this._legend;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._legend = ((String) values[1]);
	}
	public Legend() {
		this("untitled");
	}
	
	public Legend(String legend) {
		this._legend = legend;
		setTransient(false);
	}

	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<legend " + getMarkupAttributesString() + ">" + this._legend + "</legend>");
		}
	}
	
	public void setLegend(String legend) {
		setName(legend);
	}
}