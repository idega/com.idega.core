package com.idega.presentation;

/**
 * @author laddi
 */
public class FieldSet extends PresentationObjectContainer {
	
	private Legend _legend;
	
	public FieldSet() {
	}
	
	public FieldSet(String legend) {
		this(new Legend(legend));
	}
	
	public FieldSet(Legend legend) {
		this();
		add(legend);	
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			println("<fieldset " + getAttributeString() + ">");
			super.print(iwc);
			println("</fieldset>");	
		}
	}
	
	public void setWidth(String width) {
		this.setWidthStyle(width);
	}
	
	public void setWidth(int width) {
		setWidth(String.valueOf(width));
	}
}