//idega 2000 - Tryggvi Larusson
/*

*Copyright 2000 idega.is All Rights Reserved.

*/
package com.idega.presentation.ui;
import java.io.*;
import java.util.*;
import com.idega.presentation.*;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/
public class InterfaceObject extends PresentationObject {

	protected boolean keepStatus;
	private String precedingText;

	public InterfaceObject() {
		super();
		setID();
		keepStatus = false;
	}

	protected boolean isEnclosedByForm() {
		PresentationObject obj = getParentObject();
		while (obj != null) {
			if (obj instanceof Form) {
				return true;
			}
			obj = obj.getParentObject();
		}
		return false;

	}

	private void setOnAction(String actionType, String action) {
		String attributeName = actionType;
		String previousAttribute = getAttribute(attributeName);
		if (previousAttribute == null) {
			setAttribute(attributeName, action);
		}
		else {
			setAttribute(attributeName, previousAttribute + ";" + action);
		}
	}

	public void setOnFocus(String s) {
		setOnAction("onFocus", s);
	}

	public void setOnBlur(String s) {
		setOnAction("onBlur", s);
	}

	public void setOnSelect(String s) {
		setOnAction("onSelect", s);
	}

	public void setOnChange(String s) {
		setOnAction("onChange", s);
	}

	public void setOnClick(String s) {
		setOnAction("onClick", s);
	}

	public void setOnKeyDown(String s) {
		setOnAction("onKeyDown", s);
	}

	public void setOnKeyUp(String s) {
		setOnAction("onKeyUp", s);
	}

	public String getOnFocus() {
		return getAttribute("onFocus");
	}

	public String getOnBlur() {
		return getAttribute("onBlur");
	}

	public String getOnSelect() {
		return getAttribute("onSelect");
	}

	public String getOnChange() {
		return getAttribute("onChange");
	}

	public String getOnClick() {
		return getAttribute("onClick");
	}

	public String getOnKeyDown() {
		return getAttribute("onKeyDown");
	}

	public String getOnKeyUp() {
		return getAttribute("onKeyUp");
	}

	public void setValue(String s) {
		setAttribute("value", s);
	}

	public void setValue(int i) {
		setAttribute("value", Integer.toString(i));
	}

	public void setContent(String s) {
		setValue(s);
	}

	public String getValue() {
		return getAttribute("value");
	}

	public String getContent() {
		return getValue();
	}

	public void setPrecedingText(String theText) {
		precedingText = theText;
	}

	public String getPrecedingText() {
		return precedingText;
	}

	public void setOnSubmit(String OnSubmitString) {
		setOnAction("OnSubmit", OnSubmitString);
	}

	public String getOnSubmit() {
		return getAttribute("OnSubmit");
	}

	public Form getParentForm() {
		Form returnForm = null;
		PresentationObject obj = getParentObject();
		while (obj != null) {
			if (obj instanceof Form) {
				returnForm = (Form) obj;
			}
			obj = obj.getParentObject();
		}
		return returnForm;
	}

	public String getActionString(IWContext iwc) throws IOException {
		// eiki jan 2001
		StringBuffer ActionString = new StringBuffer();
		if (getLanguage().equals("HTML")) {
			if (getOnFocus() != null) {
				ActionString.append(" ONFOCUS=\"");
				ActionString.append(getOnFocus());
				ActionString.append("\" ");
			}
			if (getOnBlur() != null)	{
				ActionString.append(" ONBLUR=\"");
				ActionString.append(getOnBlur());
				ActionString.append("\" ");
			}
			if (getOnSelect() != null) {
				ActionString.append(" ONSELECT=\"");
				ActionString.append(getOnSelect());
				ActionString.append("\" ");
			}
			if (getOnChange() != null) {
				ActionString.append(" ONCHANGE=\"");
				ActionString.append(getOnChange());
				ActionString.append("\" ");
			}
			if (getOnClick() != null) {
				ActionString.append(" ONCLICK=\"");
				ActionString.append(getOnClick());
				ActionString.append("\" ");
			}
		}
		return ActionString.toString();
	}
	
	public void setToDisableOnClick(InterfaceObject objectToDisable) {
		this.setOnClick("this.form." + objectToDisable.getName() + ".disabled = true");
	}
	
	public void setToEnableOnClick(InterfaceObject objectToEnable) {
		this.setOnClick("this.form." + objectToEnable.getName() + ".disabled = false");
	}
	
	public void setDisabled(boolean disabled) {
		if (disabled)
			setAttribute("disabled");
		else
			this.removeAttribute("disabled");
	}
	
	public void setDescription(String description) {
		setAttribute("alt","description");
	}
	
	public void handleKeepStatus(IWContext iwc) {
		if (statusKeptOnAction()) {
			if (iwc.getParameter(this.getName()) != null) {
				//does nothing
			}
		}
	}
	
	public boolean statusKeptOnAction() {
		return keepStatus;
	}
	
	public void keepStatusOnAction() {
		keepStatus = true;
	}
	
	public void print(IWContext iwc) throws Exception {
		handleKeepStatus(iwc);
		super.print(iwc);
	}
	
	public Object clone() {
		InterfaceObject obj = null;
		try {
			obj = (InterfaceObject) super.clone();
			obj.keepStatus = this.keepStatus;
			obj.precedingText = this.precedingText;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	
	/**
	 * Sets the width of the interface object with a style tag.
	 */
	public void setWidth(String width) {
		setWidthStyle(width);	
	}
	
	/**
	 * Sets the height of the interface object with a style tag.
	 */
	public void setHeight(String height) {
		setHeightStyle(height);	
	}
	
	/**
	 * Sets the tab index for the interface object, that is the index for where in the tab
	 * row the object is in the parent form.
	 * @param index	The index to set.
	 */
	public void setTabIndex(int index) {
		setAttribute("tabindex",String.valueOf(index));	
	}
	
	/**
	 * Sets whether the interface object shall be read only or not.
	 * @param readOnly	The boolean value to set.
	 */
	public void setReadOnly(boolean readOnly) {
		if ( readOnly )
			setAttribute("readonly");
		else
			removeAttribute("readonly");	
	}
}
