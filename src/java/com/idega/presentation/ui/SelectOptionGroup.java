package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * @author laddi
 */
public class SelectOptionGroup extends InterfaceObject {

	public SelectOptionGroup() {
		this("untitled");
	}

	public SelectOptionGroup(String name) {
		super();
		setMarkupAttribute("label", name);
		setDisabled(false);
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			print("<optgroup " + getMarkupAttributesString() + " >");
			super.print(iwc);
			println("</optgroup>");
		}
		else if (getLanguage().equals("WML")) {
			print("<optgroup " + getMarkupAttributesString() + " >");
			super.print(iwc);
			println("</optgroup>");
		}
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return true;
	}
}