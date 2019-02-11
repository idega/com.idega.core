package com.idega.presentation;

public class ClickableDiv extends Layer{
	
	private String text;
	
	public ClickableDiv(String text){
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		setStyleClass("clickable-div");
		addText(text);
	}
	
	public void setOnClick(String action){
		setMarkupAttribute("onclick", action);
	}
	
}
