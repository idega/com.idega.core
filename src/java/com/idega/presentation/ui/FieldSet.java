/*
 * $Id: Image.java,v 1.3 2005/03/02 09:18:49 laddi Exp $
 * Created in 2002
 *
 * Copyright (C) 2002-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;

/**
 * <p>
 * This class renders out a &lt;fieldset&gt; element used in forms to group together form inputs.
 * </p>
 *  Last modified: $Date: 2005/03/02 09:18:49 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">Laddi</a>
 * @version $Revision: 1.3 $
 * @see Legend
 */
public class FieldSet extends PresentationObjectContainer {
	
	//private Legend _legend;
	private boolean _hasLegend = false;
	
	public static final String ACTION_ON_BLUR = "onblur";
	public static final String ACTION_ON_CHANGE = "onchange";
	public static final String ACTION_ON_CLICK = "onclick";
	public static final String ACTION_ON_FOCUS = "onfocus";
	public static final String ACTION_ON_KEY_PRESS = "onkeypress";
	public static final String ACTION_ON_KEY_DOWN = "onkeydown";
	public static final String ACTION_ON_KEY_UP = "onkeyup";
	public static final String ACTION_ON_SELECT = "onselect";
	public static final String ACTION_ON_SUBMIT = "onsubmit";
	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(this._hasLegend);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._hasLegend = ((Boolean) values[1]).booleanValue();
	}
	public FieldSet() {
		initialize();
	}
	
	public FieldSet(String legend) {
		this(new Legend(legend));
	}
	
	public FieldSet(Legend legend) {
		this();
		add(legend);
		this._hasLegend = true;
		initialize();
	}
	
	private void initialize() {
		setTransient(false);
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			println("<fieldset " + getMarkupAttributesString() + ">");
			super.print(iwc);
			println("</fieldset>");	
		} else {
			super.print(iwc);
		}
	}
	
	public boolean hasLegend() {
		return this._hasLegend;
	}
	
	public Legend getLegend() {
		return (Legend) getContainedObject(Legend.class);
	}
	
	public void setWidth(String width) {
		this.setWidthStyle(width);
	}
	
	public void setWidth(int width) {
		setWidth(String.valueOf(width));
	}
	
	public void setLegend(String legend) {
		if (hasLegend()) {
			Legend element = getLegend();
			if (element != null) {
				element.setName(legend);
			}
			else {
				add(new Legend(legend));
			}
		}
		else {
			add(new Legend(legend));
		}
	}
}