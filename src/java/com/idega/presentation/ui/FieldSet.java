package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;

/**
 * @author laddi
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
		values[1] = Boolean.valueOf(_hasLegend);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		_hasLegend = ((Boolean) values[1]).booleanValue();
	}
	public FieldSet() {
		setTransient(false);
	}
	
	public FieldSet(String legend) {
		this(new Legend(legend));
	}
	
	public FieldSet(Legend legend) {
		this();
		add(legend);
		_hasLegend = true;
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
		return _hasLegend;
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