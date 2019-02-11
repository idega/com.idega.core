package com.idega.presentation;

import java.rmi.RemoteException;

public class ClickableDiv extends Image{
	
	private String text;
	
	public ClickableDiv(String text){
		this.text = text;
	}
	protected String getHTMLString(IWContext iwc) throws RemoteException{
		return "<div "
				+ "class=\"clickable-div\""
				+ " >"
				+ text
				+ "</div>";
	}
}
