package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * @author laddi
 */
public class Option extends InterfaceObject {

	public Option() {
		this("untitled");
	}

	public Option(String name) {
		this(name, name);
	}

	public Option(String name, String value) {
		super();
		setName(name);
		setValue(value);
		setSelected(false);
		setDisabled(false);
	}

	/**
	 * Sets whether the <code>Option</code> is selected or not.
	 * @param selected	The status to set.
	 */
	public void setSelected(boolean selected) {
		if (selected)
			setAttribute("selected");
		else
			this.removeAttribute("selected");
	}
	
	/**
	 * Returns the selected status of the <code>Option</code>.
	 * @return boolean	True if <code>Option</code> is selected, false otherwise.
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
