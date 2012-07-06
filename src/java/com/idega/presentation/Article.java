package com.idega.presentation;

import javax.faces.component.UIComponent;

public class Article extends Layer {

	private static final String TYPE = "article";
	
	public Article() {
		super(TYPE);
	}
	
	public Article(UIComponent addedComponent) {
		this();
		add(addedComponent);
	}
	
}