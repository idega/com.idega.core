package com.idega.presentation.ui;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;

/**
 * @author laddi
 */
public class FieldSet extends PresentationObjectContainer {
	
	private Legend _legend;
	private boolean _hasLegend = false;
	
	public FieldSet() {
	}
	
	public FieldSet(String legend) {
		this(new Legend(legend));
	}
	
	public FieldSet(Legend legend) {
		this();
		add(legend);
		_hasLegend = true;
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