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
	private boolean _checkDisabled = false;
	private boolean _inFocus = false;
	private boolean _changeValue = false;
	
	public static final String ACTION_ON_BLUR = "onBlur";
	public static final String ACTION_ON_CHANGE = "onChange";
	public static final String ACTION_ON_CLICK = "onClick";
	public static final String ACTION_ON_FOCUS = "onFocus";
	public static final String ACTION_ON_KEY_DOWN = "onKeyDown";
	public static final String ACTION_ON_KEY_UP = "onKeyUp";
	public static final String ACTION_ON_SELECT = "onSelect";
	public static final String ACTION_ON_SUBMIT = "onSubmit";

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

	/**
	 * Returns true if the interface object in on a page.
	 * @return boolean
	 */
	protected boolean hasParentPage() {
		if (getParentPage() != null)
			return true;
		return false;
	}

	/**
	 * Sets an action event to perform on this object.
	 * @param actionType	The type of action.
	 * @param action	The action to perform.
	 */
	private void setOnAction(String actionType, String action) {
		setAttributeMultivalued(actionType, action);
	}

	/**
	 * Sets the action to perform when the interface object is on focus.
	 * @param action	The action to perform.
	 */
	public void setOnFocus(String action) {
		setOnAction(ACTION_ON_FOCUS, action);
	}

	/**
	 * Sets the action to perform when the interface object is set out of focus.
	 * @param action	The action to perform.
	 */
	public void setOnBlur(String action) {
		setOnAction(ACTION_ON_BLUR, action);
	}

	/**
	 * Sets the action to perform when the interface object is selected.
	 * @param action	The action to perform.
	 */
	public void setOnSelect(String action) {
		setOnAction(ACTION_ON_SELECT, action);
	}

	/**
	 * Sets the action to perform when the interface object is changed.
	 * @param action	The action to perform.
	 */
	public void setOnChange(String action) {
		setOnAction(ACTION_ON_CHANGE, action);
	}

	/**
	 * Sets the action to perform when the interface object is on clicked.
	 * @param action	The action to perform.
	 */
	public void setOnClick(String action) {
		setOnAction(ACTION_ON_CLICK, action);
	}

	/**
	 * Sets the action to perform when a key is pressed down in the interface object.
	 * @param action	The action to perform.
	 */
	public void setOnKeyDown(String action) {
		setOnAction(ACTION_ON_KEY_DOWN, action);
	}

	/**
	 * Sets the action to perform when a key is released in the interface object.
	 * @param action	The action to perform.
	 */
	public void setOnKeyUp(String action) {
		setOnAction(ACTION_ON_KEY_UP, action);
	}

	/**
	 * Returns the action to perform when the interface object is in focus.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnFocus() {
		return getAttribute(ACTION_ON_FOCUS);
	}

	/**
	 * Returns the action to perform when the interface object is set out of focus.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnBlur() {
		return getAttribute(ACTION_ON_BLUR);
	}

	/**
	 * Returns the action to perform when the interface object is in selected.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnSelect() {
		return getAttribute(ACTION_ON_SELECT);
	}

	/**
	 * Returns the action to perform when the interface object is in changed.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnChange() {
		return getAttribute(ACTION_ON_CHANGE);
	}

	/**
	 * Returns the action to perform when the interface object is in clicked.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnClick() {
		return getAttribute(ACTION_ON_CLICK);
	}

	/**
	 * Returns the action to perform when a key is pressed down in the interface object.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnKeyDown() {
		return getAttribute(ACTION_ON_KEY_DOWN);
	}

	/**
	 * Returns the action to perform when a key is released in the interface object.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnKeyUp() {
		return getAttribute(ACTION_ON_KEY_UP);
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
		if (isAttributeSet("value"))
			return getAttribute("value");
		return "";
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
	 * Sets the given interface object(s) to be checked/unchecked when this object is 
	 * clicked on.
	 * @param action	The action to perform on.
	 * @param objectToCheck	The interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 */
	public void setToCheckOnAction(String action, String objectToCheck, boolean check) {
		setToCheckOnAction(action, objectToCheck, check, true);
	}

	/**
	 * Sets the interface object(s) with the given name to be checked/unchecked when this
	 * object is clicked on.
	 * @param objectName	The name of the interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 */
	public void setToCheckOnClick(String objectName, boolean check) {
		setToCheckOnAction(ACTION_ON_CLICK, objectName, check, true);
	}

	/**
	 * Sets the given interface object(s) to be checked/unchecked when this object is 
	 * clicked on.
	 * @param objectToCheck	The interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 */
	public void setToCheckOnClick(InterfaceObject objectToCheck, boolean check) {
		setToCheckOnAction(ACTION_ON_CLICK, objectToCheck.getName(), check, true);
	}

	/**
	 * Sets the interface object(s) with the given name to be checked/unchecked when this
	 * object receives the action specified.
	 * @param action	The action to perform on.
	 * @param objectName	The name of the interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 * @param checkDisabled	If true checks all, otherwise not disabled objects.
	 */
	public void setToCheckOnAction(String action, String objectName, boolean check, boolean checkDisabled) {
		_checkObject = true;
		_checkDisabled = checkDisabled;
		if (checkDisabled)
			setOnAction(action, "checkAllObjects(findObj('" + objectName + "'),'" + String.valueOf(check) + "')");
		else
			setOnAction(action, "checkEnabledObjects(findObj('" + objectName + "'),'" + String.valueOf(check) + "')");
	}

	/**
	 * Sets the interface object(s) with the given name to be checked/unchecked when this
	 * object is clicked on.
	 * @param objectName	The name of the interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 * @param checkDisabled	If true checks all, otherwise not disabled objects.
	 */
	public void setToCheckOnClick(String objectName, boolean check, boolean checkDisabled) {
		setToCheckOnAction(ACTION_ON_CLICK, objectName, check, checkDisabled);
	}

	/**
	 * Sets the given interface object(s) to be checked/unchecked when this object is 
	 * clicked on.
	 * @param objectToCheck	The interface object(s) to check.
	 * @param check	Checks if boolean is true, unchecks otherwise.
	 * @param checkDisabled	If true checks all, otherwise not disabled objects.
	 */
	public void setToCheckOnClick(InterfaceObject objectToCheck, boolean check, boolean checkDisabled) {
		setToCheckOnAction(ACTION_ON_CLICK, objectToCheck.getName(), check, checkDisabled);
	}

	/**
	 * Sets the interface object(s) with the given name to be enabled when this object
	 * receives the action specified.
	 * @param action	The action to perform on.
	 * @param objectToEnable	The name of the interface object(s) to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnAction(String action, String objectName, boolean disable) {
		_disableObject = true;
		setOnAction(action, "disableObject(findObj('" + objectName + "'),'" + String.valueOf(disable) + "')");
	}

	/**
	 * Sets the interface object(s) with the given name to be enabled when this object is 
	 * clicked on.
	 * @param objectToEnable	The name of the interface object(s) to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnClick(String objectName, boolean disable) {
		setToDisableOnAction(ACTION_ON_CLICK,objectName,disable);
	}

	/**
	 * Sets the given interface object to be enabled when this object is clicked on.
	 * @param objectToEnable	The interface object to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnClick(InterfaceObject objectToEnable, boolean disable) {
		setToDisableOnClick(objectToEnable.getName(), disable);
	}

	/**
	 * Sets the value of the given interface object when this object receives the action 
	 * specified.
	 * @param action	The action to perform on.
	 * @param objectToChange	The interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnAction(String action, InterfaceObject objectToChange, String value) {
		setValueOnAction(action, objectToChange.getName(), value);
	}
	
	/**
	 * Sets the value of the interface object with the given namewhen this object receives 
	 * the action specified.
	 * @param action	The action to perform on.
	 * @param objectName	The name of the interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnAction(String action, String objectName, String value) {
		_changeValue = true;
		setOnAction(action, "changeValue(findObj('"+objectName+"'),'"+value+"');");
	}
	
	/**
	 * Sets the value of the given interface object when this object is clicked.
	 * @param objectToChange	The interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnClick(InterfaceObject objectToChange, String value) {
		setValueOnAction(ACTION_ON_CLICK, objectToChange.getName(), value);
	}
	
	/**
	 * Sets the value of the interface object with the given name when this object is clicked.
	 * @param objectName	The name of the interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnClick(String objectName, String value) {
		setValueOnAction(ACTION_ON_CLICK, objectName, value);
	}
	
	
	/**
	 * Sets the interface object in focus on page load.
	 * @param inFocus	Set to true to set focus on object, false otherwise.
	 */
	public void setInFocusOnPageLoad(boolean inFocus) {
		_inFocus = inFocus;
	}

	/**
	 * Sets the interface object to submit its parent form on click.
	 * Must have a parent form to function correctly.
	 */
	public void setToSubmit() {
		setOnClick("this.form.submit()");
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
	 * Returns the disabled status of the interface object.
	 * @return boolean	True if object is disabled, false otherwise.
	 */
	public boolean getDisabled() {
		if (isAttributeSet("disabled"))
			return true;
		return false;	
	}

	/**
	 * Sets the description for the interface object.
	 * @param description	The description to set.
	 */
	public void setDescription(String description) {
		setAttribute("title", description);
	}

	/**
	 * Returns the description set to the interface object.
	 * @return String	The description set, null otherwise.
	 */
	public String getDescription() {
		if (isAttributeSet("title"))
			return getAttribute("title");
		return null;
	}

	/**
	 * A method that handles the actions to perform to keep the status of the interface
	 * object on performed actions.  Override if interface object can keep its status on
	 * actions.
	 * @param iwc
	 */
	public abstract void handleKeepStatus(IWContext iwc);

	/**
	 * Returns true if interface object is to keep its status when an action is performed.
	 * @return boolean	True if status is kept, false otherwise.
	 */
	public boolean statusKeptOnAction() {
		return keepStatus;
	}

	/**
	 * Sets to keep the status on the interface object when an action is performed.
	 * @param boolean	True if interface object is to keep status, false otherwise.
	 */
	public void keepStatusOnAction(boolean keepStatus) {
		this.keepStatus = keepStatus;
	}

	/**
	 * Sets to keep the status on the interface object when an action is performed.
	 */
	public void keepStatusOnAction() {
		keepStatusOnAction(true);
	}

	public void print(IWContext iwc) throws Exception {
		if (statusKeptOnAction())
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
				if (_checkDisabled)
					getScript().addFunction("checkAllObjects", "function checkAllObjects (inputs,value) {\n	if (inputs.length > 1) {\n	\tfor(var i=0;i<inputs.length;i++)\n	\t\tinputs[i].checked=eval(value);\n	\t}\n	else\n	\tinputs.checked=eval(value);\n}");
				else
					getScript().addFunction("checkEnabledObjects", "function checkEnabledObjects (inputs,value) {\n	if (inputs.length > 1) {\n	\tfor(var i=0;i<inputs.length;i++)\n	\tif ( inputs[i].disabled == false )\n	\t\tinputs[i].checked=eval(value);\n	\t}\n	else\n	\tif (inputs.disabled == false)\n	\t\tinputs.checked=eval(value);\n}");
			}
			if (_disableObject) {
				getScript().addFunction("disableObject", "function disableObject (inputs,value) {\n	if (inputs.length > 1) {\n	\tfor(var i=0;i<inputs.length;i++)\n	\t\tinputs[i].disabled=eval(value);\n	\t}\n	else\n	inputs.disabled=eval(value);\n}");
			}
			if (_changeValue) {
				getScript().addFunction("changeValue", "function changeValue (input,newValue) {\n	input.value=newValue;\n}");
			}
		}
		if (_inFocus && hasParentPage()) {
			getParentPage().setOnLoad("findObj('" + getName() + "').focus();");
		}
	}

	/**
	 * Returns the <code>Script</code> associated with the parent <code>Form</code>. If the
	 * interface object has no parent form this method returns null.
	 * @return Script
	 */
	protected Script getScript() {
		if ( getParentForm() != null ) {
			if (getParentForm().getAssociatedFormScript() == null) {
				getParentForm().setAssociatedFormScript(new Script());
			}
			return getParentForm().getAssociatedFormScript();
		}
		return null;
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
		setAttribute("tabindex", String.valueOf(index));
	}
	
	/**
	 * Returns the tab index set for the interface object.  Returns -1 if no value is set.
	 * @return int
	 */
	public int getTabIndex() {
		if (isAttributeSet("tabindex"))
			return Integer.parseInt(getAttribute("tabindex"));
		return -1;
	}

	/**
	 * Sets whether the interface object shall be read only or not.
	 * @param readOnly	The boolean value to set.
	 */
	public void setReadOnly(boolean readOnly) {
		if (readOnly)
			setAttribute("readonly");
		else
			removeAttribute("readonly");
	}
	
	/**
	 * Returns true if interface object is set as read only, false otherwise.
	 * @return boolean
	 */
	public boolean getReadOnly() {
		if (isAttributeSet("readonly"))
			return true;
		return false;	
	}
}
