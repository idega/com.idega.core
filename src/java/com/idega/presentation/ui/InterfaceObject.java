/*
 * $Id: InterfaceObject.java,v 1.36 2006/02/01 07:47:12 laddi Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.io.IOException;
import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Script;

/**
 * <p>
 * This is a base class for Form type (input) elements.<br>
 * In JSF there is now a more recent javax.faces.compoent.UIInput that serves a
 * similar purpose and is recommended to use/extend in newer pure JSF applications.
 * </p>
 *  Last modified: $Date: 2006/02/01 07:47:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.36 $
 */
public abstract class InterfaceObject extends PresentationObjectContainer {

	protected boolean keepStatus = false;

	private boolean _checkObject = false;
	private boolean _disableObject = false;
	private boolean _disableSingleObject = false;
	private boolean _checkDisabled = false;
	private boolean _inFocus = false;
	private boolean _changeValue = false;
	private boolean _selectValues = false;
	
	public static final String ACTION_ON_BLUR = "onblur";
	public static final String ACTION_ON_CHANGE = "onchange";
	public static final String ACTION_ON_CLICK = "onclick";
	public static final String ACTION_ON_FOCUS = "onfocus";
	public static final String ACTION_ON_KEY_PRESS = "onkeypress";
	public static final String ACTION_ON_KEY_DOWN = "onkeydown";
	public static final String ACTION_ON_KEY_UP = "onkeyup";
	public static final String ACTION_ON_SELECT = "onselect";
	public static final String ACTION_ON_SUBMIT = "onsubmit";

	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[8];
		values[0] = super.saveState(ctx);
		values[1] = new Boolean(keepStatus);
		values[2] = new Boolean(_checkObject);
		values[3] = new Boolean(_disableObject);
		values[4] = new Boolean(_checkDisabled);
		values[5] = new Boolean(_inFocus);
		values[6] = new Boolean(_changeValue);
		values[7] = new Boolean(_selectValues);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		keepStatus = ((Boolean) values[1]).booleanValue();
		_checkObject = ((Boolean) values[2]).booleanValue();
		_disableObject = ((Boolean) values[3]).booleanValue();
		_checkDisabled = ((Boolean) values[4]).booleanValue();
		_inFocus = ((Boolean) values[5]).booleanValue();
		_changeValue = ((Boolean) values[6]).booleanValue();
		_selectValues = ((Boolean) values[7]).booleanValue();
	}

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
		if (getForm() != null)
			return true;
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
	protected void setOnAction(String actionType, String action) {
		setMarkupAttributeMultivalued(actionType, action);
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
	 * Sets the action to perform when a key is pressed in the interface object.
	 * @param action	The action to perform.
	 */
	public void setOnKeyPress(String action) {
		setOnAction(ACTION_ON_KEY_PRESS, action);
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
		return getMarkupAttribute(ACTION_ON_FOCUS);
	}

	/**
	 * Returns the action to perform when the interface object is set out of focus.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnBlur() {
		return getMarkupAttribute(ACTION_ON_BLUR);
	}

	/**
	 * Returns the action to perform when the interface object is in selected.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnSelect() {
		return getMarkupAttribute(ACTION_ON_SELECT);
	}

	/**
	 * Returns the action to perform when the interface object is in changed.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnChange() {
		return getMarkupAttribute(ACTION_ON_CHANGE);
	}

	/**
	 * Returns the action to perform when the interface object is in clicked.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnClick() {
		return getMarkupAttribute(ACTION_ON_CLICK);
	}

	/**
	 * Returns the action to perform when a key is pressed down in the interface object.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnKeyDown() {
		return getMarkupAttribute(ACTION_ON_KEY_DOWN);
	}

	/**
	 * Returns the action to perform when a key is pressed in the interface object.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnKeyPress() {
		return getMarkupAttribute(ACTION_ON_KEY_PRESS);
	}

	/**
	 * Returns the action to perform when a key is released in the interface object.
	 * @return String	The action to perform.  Returns null if no action is set.
	 */
	public String getOnKeyUp() {
		return getMarkupAttribute(ACTION_ON_KEY_UP);
	}

	/**
	 * Sets the value of the interface object.
	 * @param value	The value to set.
	 */
	public void setValue(String value) {
		setMarkupAttribute("value", value);
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
	public String getValueAsString() {
		if (isMarkupAttributeSet("value"))
			return getMarkupAttribute("value");
		return "";
	}

	/**
	 * Returns the content (value) set for the interface object.
	 * @return String	The content set.
	 */
	public String getContent() {
		return getValueAsString();
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
		return getMarkupAttribute("onSubmit");
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
		 * Sets the interface object(s) with the given name to be checked/unchecked when this
		 * object is clicked on. Convenient when using javascript function or property to provide the 
		 * boolean value ( checkvalue)
		 * @param objectName	The name of the interface object(s) to check.
		 * @param check	Checks if boolean is true, unchecks otherwise.
		 */
		public void setToCheckOnClick(String objectName, String checkValue) {
			setToCheckOnAction(ACTION_ON_CLICK, objectName, checkValue, true);
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
		 * Sets the given interface object(s) to be checked/unchecked when this object is 
		 * clicked on. Convenient when using javascript function or property to provide the 
		 * boolean value ( checkvalue)
		 * @param objectToCheck	The interface object(s) to check.
		 * @param checkValue	Checks if checkvalue is true, unchecks otherwise. 
		 */
	public void setToCheckOnClick(InterfaceObject objectToCheck, String checkValue) {
		setToCheckOnAction(ACTION_ON_CLICK, objectToCheck.getName(), checkValue, true);
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
		this.setToCheckOnAction(action,objectName,String.valueOf(check),checkDisabled);
	}
	
	/**
		 * Sets the interface object(s) with the given name to be checked/unchecked when this
		 * object receives the action specified.
		 * @param action	The action to perform on.
		 * @param objectName	The name of the interface object(s) to check.
		 * @param checkValue	Checks if boolean is true, unchecks otherwise.
		 * @param checkDisabled	If true checks all, otherwise not disabled objects.
		 */
		public void setToCheckOnAction(String action, String objectName, String checkValue, boolean checkDisabled) {
			_checkObject = true;
			_checkDisabled = checkDisabled;
			if (checkDisabled)
				setOnAction(action, "checkAllObjects(findObj('" + objectName + "')," + checkValue + ")");
			else
				setOnAction(action, "checkEnabledObjects(findObj('" + objectName + "')," +checkValue + ")");
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
	public void setToDisableOnAction(String action, String objectName, boolean disable, boolean multipleObjects) {
		if (multipleObjects) {
			_disableObject = true;
			setOnAction(action, "disableObject(findObj('" + objectName + "'),'" + String.valueOf(disable) + "')");
		}
		else {
			_disableSingleObject = true;
			setOnAction(action, "disableSingleObject(findObj('" + objectName + "'),'" + String.valueOf(disable) + "')");
		}
	}

	/**
	 * Sets the interface object(s) with the given name to be enabled when this object is 
	 * clicked on.
	 * @param objectToEnable	The name of the interface object(s) to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnClick(String objectName, boolean disable, boolean multipleObjects) {
		setToDisableOnAction(ACTION_ON_CLICK,objectName,disable, multipleObjects);
	}

	/**
	 * Sets the given interface object to be enabled when this object is clicked on.
	 * @param objectToEnable	The interface object to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnClick(InterfaceObject objectToEnable, boolean disable, boolean multipleObjects) {
		setToDisableOnClick(objectToEnable.getName(), disable, multipleObjects);
	}

	/**
	 * Sets the interface object(s) with the given name to be enabled when this object
	 * receives the action specified.
	 * @param action	The action to perform on.
	 * @param objectToEnable	The name of the interface object(s) to enable.
	 * @param enable	Set to true to disable, false will enable.
	 */
	public void setToDisableOnAction(String action, String objectName, boolean disable) {
		setToDisableOnAction(action, objectName, disable, true);
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
	 * the action specified. Adds the specified script. 
	 * @param action	The action to perform on.
	 * @param objectName	The name of the interface object to change value of.
	 * @param value	The new value to set.
	 * @param script the sript to be added
	 */
	public void setValueOnActionFollowedByScript(String action, String objectName, String value, String script) {
		_changeValue = true;
		StringBuffer buffer = new StringBuffer("changeValue(findObj('");
		buffer.append(objectName).append("'),'").append(value).append("');").append(script);
		setOnAction(action, buffer.toString());
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
	 * Adds the specified script at the end.
	 * @param objectName	The name of the interface object to change value of.
	 * @param value	The new value to set.
	 * @param script the script to be added
	 */
	public void setValueOnClickFollowedByScript(String objectName, String value, String script) {
		setValueOnActionFollowedByScript(ACTION_ON_CLICK, objectName, value, script);
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
	 * Sets the value of the given interface object when this object changes value.
	 * @param objectToChange	The interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnChange(InterfaceObject objectToChange, String value) {
		setValueOnAction(ACTION_ON_CHANGE, objectToChange.getName(), value);
	}
	
	/**
	 * Sets the value of the interface object with the given name when this object changes value.
	 * @param objectName	The name of the interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnChange(String objectName, String value) {
		setValueOnAction(ACTION_ON_CHANGE, objectName, value);
	}
	
	/**
	 * Sets the given interface object as selected when this object receives the action 
	 * specified.
	 * @param action	The action to perform on.
	 * @param objectToChange	The interface object to set selected value of.
	 * @param selected	Set true to select, false otherwise.
	 */
	public void setSelectedOnAction(String action, InterfaceObject objectToChange, boolean selected) {
		setSelectedOnAction(action, objectToChange.getName(), selected);
	}
	
	/**
	 * Sets the interface object with the given name as selected when this object receives 
	 * the action specified.
	 * @param action	The action to perform on.
	 * @param objectName	The name of the interface object to set selected value of.
	 * @param selected	Set true to select, false otherwise.
	 */
	public void setSelectedOnAction(String action, String objectName, boolean selected) {
		_selectValues = true;
		setOnAction(action, "selectValues(findObj('"+objectName+"'),'"+selected+"');");
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
			setMarkupAttribute("disabled", "disabled");
		else
			this.removeMarkupAttribute("disabled");
	}
	
	/**
	 * Returns the disabled status of the interface object.
	 * @return boolean	True if object is disabled, false otherwise.
	 */
	public boolean getDisabled() {
		if (isMarkupAttributeSet("disabled"))
			return true;
		return false;	
	}

	/**
	 * Sets the description for the interface object.
	 * @param description	The description to set.
	 */
	public void setDescription(String description) {
		setMarkupAttribute("title", description);
	}

	/**
	 * Returns the description set to the interface object.
	 * @return String	The description set, null otherwise.
	 */
	public String getDescription() {
		if (isMarkupAttributeSet("title"))
			return getMarkupAttribute("title");
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

	
	protected void callPrint(FacesContext fc)throws IOException{
		//Overridden here to call the same methods as _print():
		IWContext iwc = castToIWContext(fc);
		if (statusKeptOnAction())
			handleKeepStatus(iwc);
		super.callPrint(fc);
	}
		
	public void _print(IWContext iwc) throws Exception {
		if (statusKeptOnAction())
			handleKeepStatus(iwc);
		super._print(iwc);
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
			if (_disableSingleObject) {
				getScript().addFunction("disableSingleObject", "function disableSingleObject (input,value) {\n	input.disabled=eval(value);\n}");
			}
			if (_changeValue) {
				getScript().addFunction("changeValue", "function changeValue (input,newValue) {\n	input.value=newValue;\n}");
			}
			if (_selectValues) {
				getScript().addFunction("selectValues", "function selectValues (inputs,value) {\n	if (inputs.length > 0) {\n	\tfor(var i=0;i<inputs.length;i++)\n	\t\tinputs[i].selected=eval(value);\n	}\n	}");
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
		if ( getForm() != null ) {
			if (getForm().getAssociatedFormScript() == null) {
				getForm().setAssociatedFormScript(new Script());
			}
			return getForm().getAssociatedFormScript();
		}
		return null;
	}
	
	/**
	 * Returns the enclosing form, returns null if no form present.
	 * @return Form	The form enclosing the interface object.
	 */
	public Form getForm() {
		return getParentForm();
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
		setMarkupAttribute("tabindex", String.valueOf(index));
	}
	
	/**
	 * Returns the tab index set for the interface object.  Returns -1 if no value is set.
	 * @return int
	 */
	public int getTabIndex() {
		if (isMarkupAttributeSet("tabindex"))
			return Integer.parseInt(getMarkupAttribute("tabindex"));
		return -1;
	}

	/**
	 * Sets whether the interface object shall be read only or not.
	 * @param readOnly	The boolean value to set.
	 */
	public void setReadOnly(boolean readOnly) {
		if (readOnly)
			setMarkupAttributeWithoutValue("readonly");
		else
			removeMarkupAttribute("readonly");
	}
	
	/**
	 * Returns true if interface object is set as read only, false otherwise.
	 * @return boolean
	 */
	public boolean getReadOnly() {
		if (isMarkupAttributeSet("readonly"))
			return true;
		return false;	
	}

	protected void setCheckSubmit() {
		if (getScript().getFunction("checkSubmit") == null) {
			getScript().addFunction("checkSubmit", "function checkSubmit(inputs){\n\n}");
		}
	}
	
	/**
	 * Sets a function to perform on submit of the parent form.  If the function returns
	 * false the form will not submit.
	 * @param functionName	The name of the function to check on submit.
	 * @param function			The function to perform.
	 */
	public void setOnSubmitFunction(String functionName, String function) {
		if (getForm() != null) {
			getParentForm().setOnSubmit("return checkSubmit(this)");
			setCheckSubmit();
			getScript().addToBeginningOfFunction("checkSubmit", "if ("+functionName+"() == false ){\nreturn false;\n}\n");
			getScript().addFunction(functionName, function);
		}
	}

	/**
	 * Sets a function to perform on submit of the parent form.  If the function returns
	 * false the form will not submit.
	 * @param functionName	The name of the function to check on submit.
	 * @param function			The function to perform.
	 * @param value				A value to use in the function.
	 */
	protected void setOnSubmitFunction(String functionName, String function, String value) {
		setOnSubmitFunction(functionName, function, value, null);
	}
	
	public void setMaximumChecked(int maximumChecked, String exceedsMaximumErrorMessage) {
		setOnSubmitFunction("warnIfExceedsMaximum", "function warnIfExceedsMaximum (inputs,warnMsg) {\n\tvar maximum = 0;\n\n\tfor(var i=0;i<inputs.length;i++) {\n	\t\tif(inputs[i].checked == true)\n\t\tmaximum++;\n\t}\n\n\tif (maximum <= "+maximumChecked+")\n\t\treturn true;\n\telse {\n\t\talert(warnMsg);\n\t\treturn false;\n\t}\n}", exceedsMaximumErrorMessage);
	}
	
	/**
	 * Sets a function to perform on submit of the parent form.  If the function returns
	 * false the form will not submit.
	 * @param functionName	The name of the function to check on submit.
	 * @param function			The function to perform.
	 * @param value1				A primary value to use in the function.
	 * @param value2				A seconday value to use in the function.
	 */
	protected void setOnSubmitFunction(String functionName, String function, String value1, String value2) {
		if (getForm() != null) {
			getParentForm().setOnSubmit("return checkSubmit(this)");
			setCheckSubmit();
			if (value2 != null)
				getScript().addToBeginningOfFunction("checkSubmit", "if ("+functionName+" (findObj('" + getName() + "'),'" + value1 + "', '"+value2+"') == false ){\nreturn false;\n}\n");
			else
				getScript().addToBeginningOfFunction("checkSubmit", "if ("+functionName+" (findObj('" + getName() + "'),'" + value1 + "') == false ){\nreturn false;\n}\n");
			getScript().addFunction(functionName, function);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public abstract boolean isContainer();
}
