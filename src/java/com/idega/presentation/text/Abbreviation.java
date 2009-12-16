package com.idega.presentation.text;

public class Abbreviation extends Text {

	public Abbreviation(String abbreviation, String text) {
		super(abbreviation);
		this.setTitle(text);
	}

	@Override
	protected String getTag() {
		return "abbr";
	}

	@Override
	protected boolean showTag() {
		return true;
	}
}