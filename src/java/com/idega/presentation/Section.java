package com.idega.presentation;

import javax.faces.component.UIComponent;

public class Section extends Layer {

	private static final String TYPE = "section";
	
	public Section() {
		super(TYPE);
	}
	
	public Section(UIComponent addedComponent) {
		this();
		add(addedComponent);
	}
	
}