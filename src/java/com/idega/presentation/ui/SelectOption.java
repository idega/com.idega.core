package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * @author laddi
 */
public class SelectOption extends InterfaceObject {

	public SelectOption() {
		this("untitled");
	}

	public SelectOption(String value) {
		this(value, value);
	}
	
	public SelectOption(String name, int value) {
		this(name, String.valueOf(value));	
	}
	
	public SelectOption(String name, char value) {
		this(name, String.valueOf(value));	
	}

	public SelectOption(String name, String value) {
		super();
		setName(name);
		setValue(value);
		setSelected(false);
		setDisabled(false);
	}

	/**
	 * Sets whether the <code>SelectOption</code> is selected or not.
	 * @param selected	The status to set.
	 */
	public void setSelected(boolean selected) {
		if (selected)
			setMarkupAttribute("selected", "selected");
		else
			this.removeMarkupAttribute("selected");
	}
	
	/**
	 * Sets the label for the <code>SelectOption</code>.
	 * @param label	The label to set.
	 */
	public void setLabel(String label) {
		setMarkupAttribute("label", label);
	}
	
	/**
	 * Returns the selected status of the <code>SelectOption</code>.
	 * @return boolean	True if <code>SelectOption</code> is selected, false otherwise.
	 */
	public boolean getSelected() {
		if (isMarkupAttributeSet("selected"))
			return true;
		return false;	
	}

	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<option " + getMarkupAttributesString() + " >");
			print(getName());
			println("</option>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			print("<option value=\"" + getValueAsString() + "\" >");
			print(getName());
			println("</option>");
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
		return false;
	}
}