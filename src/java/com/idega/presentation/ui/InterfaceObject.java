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
public abstract class InterfaceObject extends PresentationObject {

	protected boolean keepStatus;
	private boolean _checkObject = false;
	private boolean _disableObject = false;

	public InterfaceObject() {
		super();
		setID();
		keepStatus = false;
	}

	/**
	 * Returns true if the interface object is enclosed by a form object.
	 * @return boolean
	 */
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

	/**
	 * Sets the action to perform when the interface object is on focus.
	 * @param action	The action to perform.
	 */
	public void setOnFocus(String action) {
		setOnAction("onFocus", action);
	}

	/**
	 * Sets the action to perform when the interface object is set out of focus.
	 * @param action	The action to perform.
	 */
	public void setOnBlur(String action) {
		setOnAction("onBlur", action);
	}

	/**
	 * Sets the action to perform when the interface object is selected.
	 * @param action	The action to perform.
	 */
	public void setOnSelect(String action) {
		setOnAction("onSelect", action);
	}

	/**
	 * Sets the action to perform when the interface object is changed.
	 * @param action	The action to perform.
	 */
	public void setOnChange(String action) {
		setOnAction("onChange", action);
	}

	/**
	 * Sets the action to perform when the interface object is on clicked.
	 * @param action	The action to perform.
	 */
	public void setOnClick(String action) {
		setOnAction("onClick", action);
	}

	/**
	 * Sets the action to perform when a key is pressed down in the interface object.
	 * @param action	The action to perform.
	 */
	public void setOnKeyDown(String action) {
		setOnAction("onKeyDown", action);
	}

	/**
	 * Sets the action to perform when a key is released in the interface object.
	 * @param action	The action to perform.
	 */
	public void setOnKeyUp(String action) {
		setOnAction("onKeyUp", action);
	}

	/**
	 * Returns the action to perform when the interface object is in focus.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnFocus() {
		return getAttribute("onFocus");
	}

	/**
	 * Returns the action to perform when the interface object is set out of focus.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnBlur() {
		return getAttribute("onBlur");
	}

	/**
	 * Returns the action to perform when the interface object is in selected.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnSelect() {
		return getAttribute("onSelect");
	}

	/**
	 * Returns the action to perform when the interface object is in changed.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnChange() {
		return getAttribute("onChange");
	}

	/**
	 * Returns the action to perform when the interface object is in clicked.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnClick() {
		return getAttribute("onClick");
	}

	/**
	 * Returns the action to perform when a key is pressed down in the interface object.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnKeyDown() {
		return getAttribute("onKeyDown");
	}

	/**
	 * Returns the action to perform when a key is released in the interface object.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnKeyUp() {
		return getAttribute("onKeyUp");
	}

	/**
	 * Sets the value of the interface object.
	 * @param value	The value to set.
	 */
	public void setValue(String value) {
		setAttribute("value", value);
	}

	/**
	 * Sets the value of the interface object.
	 * @param value	The value to set.
	 */
	public void setValue(int value) {
		setValue(Integer.toString(value));
	}

	/**
	 * Sets the content (value) of the interface object.
	 * @param value	The content to set.
	 */
	public void setContent(String content) {
		setValue(content);
	}

	/**
	 * Returns the value set for the interface object.
	 * @return String	The value set.
	 */
	public String getValue() {
		return getAttribute("value");
	}

	/**
	 * Returns the content (value) set for the interface object.
	 * @return String	The content set.
	 */
	public String getContent() {
		return getValue();
	}

	/**
	 * Sets the action to perform when the parent form is submitted.
	 * @param action	The action to perform.
	 */
	public void setOnSubmit(String action) {
		setOnAction("onSubmit", action);
	}

	/**
	 * Returns the action to perform when the parent form is submitted.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnSubmit() {
		return getAttribute("onSubmit");
	}

	/**
	 * Sets the interface object(s) with the given name to be checked/unchecked when this
	 * object is clicked on.
	 * @param objectName	The name of the interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 */
	public void setToCheckOnClick(String objectName, boolean check) {
		_checkObject = true;
//		this.setOnClick("checkObject(findObj('" + objectName + "'),'"+Boolean.toString(check)+"')");
		this.setOnClick("checkObject(findObj('" + objectName + "'),'"+String.valueOf( check )+"')");
	}
	
	/**
	 * Sets the given interface object(s) to be checked/unchecked when this object is 
	 * clicked on.
	 * @param objectToCheck	The interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 */
	public void setToCheckOnClick(InterfaceObject objectToCheck, boolean check) {
		setToCheckOnClick(objectToCheck.getName(),check);
	}
	
	/**
	 * Sets the interface object(s) with the given name to be enabled when this object is 
	 * clicked on.
	 * @param objectToEnable	The name of the interface object(s) to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnClick(String objectName,boolean disable) {
		_disableObject = true;
//		setOnClick("disableObject(findObj('" + objectName + "'),'"+Boolean.toString(disable)+"')");
		setOnClick("disableObject(findObj('" + objectName + "'),'"+String.valueOf( disable )+"')");
	}
	
	/**
	 * Sets the given interface object to be enabled when this object is clicked on.
	 * @param objectToEnable	The interface object to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnClick(InterfaceObject objectToEnable,boolean disable) {
		setToDisableOnClick(objectToEnable.getName(),disable);
	}
	
	/**
	 * Sets whether the interface object is disabled or not.
	 * @param disabled	The status to set.
	 */
	public void setDisabled(boolean disabled) {
		if (disabled)
			setAttribute("disabled");
		else
			this.removeAttribute("disabled");
	}
	
	/**
	 * Sets the description for the interface object.
	 * @param description	The description to set.
	 */
	public void setDescription(String description) {
		setAttribute("alt",description);
	}
	
	/**
	 * Returns the description set to the interface object.
	 * @return String	The description set, null otherwise.
	 */
	public String getDescription() {
		if ( isAttributeSet("alt") )
			return getAttribute("alt");	
		return null;
	}
	
	public abstract void handleKeepStatus(IWContext iwc);
	
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
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	
	public void _main(IWContext iwc) throws Exception {
		super._main(iwc);
		if (isEnclosedByForm()) {
			if (_checkObject) {
				getScript().addFunction("checkObject", "function checkObject (inputs,value) {\n	for(var i=0;i<inputs.length;i++)\n	\tinputs[i].checked=eval(value);\n	}");
			}
			if (_disableObject) {
				getScript().addFunction("disableObject", "function disableObject (inputs,value) {\n	for(var i=0;i<inputs.length;i++)\n	\tinputs[i].disabled=eval(value);\n	}");
			}
		}
	}

	protected Script getScript() {
		if (getParentForm().getAssociatedFormScript() == null) {
			getParentForm().setAssociatedFormScript(new Script());
		}
		return getParentForm().getAssociatedFormScript();
	}

	/**
	 * Sets the width of the interface object with a style tag.
	 * @param width	The width to set.
	 */
	public void setWidth(String width) {
		setWidthStyle(width);	
	}
	
	/**
	 * Sets the height of the interface object with a style tag.
	 * @param height	The height to set.
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
