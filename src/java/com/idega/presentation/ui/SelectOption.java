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
			setAttribute("selected");
		else
			this.removeAttribute("selected");
	}
	
	/**
	 * Returns the selected status of the <code>SelectOption</code>.
	 * @return boolean	True if <code>SelectOption</code> is selected, false otherwise.
	 */
	public boolean getSelected() {
		if (isAttributeSet("selected"))
			return true;
		return false;	
	}

	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			print("<option " + getAttributeString() + " >");
			print(getName());
			println("</option>");
		}
		else if (getLanguage().equals("WML")) {
			print("<option value=\"" + getValue() + "\" >");
			print(getName());
			println("</option>");
		}
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}
}
