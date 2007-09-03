//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class CheckBox extends GenericInput {
	
	private boolean _mustBeChecked = false;
	private boolean _enableWhenChecked = false;
	private boolean _disableWhenUnchecked = false;
	private boolean _disableWhenChecked = false;
	private boolean _enableWhenUnchecked = false;
	private boolean _checkWhenCheckedUncheckWhenUnchecked = false;
	private String _errorMessage;

	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[3];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(this._mustBeChecked);
		values[2] = this._errorMessage;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._mustBeChecked = ((Boolean) values[1]).booleanValue();
		this._errorMessage = (String)values[2];
	}		
	
	
	/**
	 * Constructs a new <code>CheckBox</code> with name set as "untitled" and value as
	 * "unspecified".
	 */
	public CheckBox() {
		this("untitled");
	}
	
	/**
	 * Constructs a new <code>CheckBox</code> with the given name and value as "unspecified".
	 */
	public CheckBox(String name) {
		this(name, "unspecified");
	}
	
	/**
	 * Constructs a new <code>CheckBox</code> with the given name and value.
	 */
	public CheckBox(String name, String value) {
		super();
		setName(name);
		setContent(value);
		setChecked(false);
		setInputType(INPUT_TYPE_CHECKBOX);
	}
	
	/**
	 * Sets whether the checkbox is checked or not.
	 * @param ifChecked
	 */
	public void setChecked(boolean ifChecked) {
		if (ifChecked) {
			setMarkupAttribute("checked", ifChecked);
		}
		else {
			removeMarkupAttribute("checked");
		}
	}
	
	/**
	 * Sets whether the checkbox is checked or not.
	 * @param ifChecked
	 * @param useXhtml
	 */
	public void setChecked(boolean ifChecked, boolean useXhtml) {
		if (useXhtml) {
			if (ifChecked) {
				setMarkupAttribute("checked", ifChecked);
				return;
			}
		}
		setChecked(ifChecked);
	}
	
	public void main(IWContext iwc) {
		if (isEnclosedByForm()) {
			if (this._mustBeChecked) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("function isChecked(inputs,message) {").append("\n\t");
				buffer.append("if (inputs.length > 1) {").append("\n\t\t");
				buffer.append("for(var i=0;i<inputs.length;i++) {").append("\n\t\t\t");
				buffer.append("if (inputs[i].checked == true)").append("\n\t\t\t\t");
				buffer.append("return true;").append("\n\t\t");
				buffer.append("}").append("\n\t");
				buffer.append("}").append("\n\t");
				buffer.append("else {").append("\n\t\t");
				buffer.append("if (inputs.checked == true)").append("\n\t\t\t");
				buffer.append("return true;").append("\n\t");
				buffer.append("}").append("\n\t");
				buffer.append("alert(message);").append("\n");
				buffer.append("return false;").append("\n}");
				this.setOnSubmitFunction("isChecked", buffer.toString(), this._errorMessage);
			}
			if (this._enableWhenChecked) {
				getScript().addFunction("enableWhenChecked", "function enableWhenChecked (check, input) {\n\t	if (check.checked == true) input.disabled = false; \n}");
			}
			if (this._disableWhenUnchecked) {
				getScript().addFunction("disableWhenUnchecked", "function disableWhenUnchecked (check, input) {\n\t	if (check.checked == false) input.disabled = true; \n}");
			}
			if (this._enableWhenUnchecked) {
				getScript().addFunction("enableWhenUnchecked", "function enableWhenUnchecked (check, input) {\n\t	if (check.checked == false) input.disabled = false; \n}");
			}
			if (this._disableWhenChecked) {
				getScript().addFunction("disableWhenChecked", "function disableWhenChecked (check, input) {\n\t	if (check.checked == true) input.disabled = true; \n}");
			}
			if ( this._checkWhenCheckedUncheckWhenUnchecked ) {
				//getScript().addFunction("toggleOnChange", "function toggleOnChange (check, input) {\n\t	if (check.checked == false)  {input.checked = false;} \n\t else if (check.checked == true) { input.checked = true;} \n}");
				getScript().addFunction("toggleOnChange", "function toggleOnChange (check, inputs) {\n	if (inputs.length > 1) {\n	\tfor(var i=0;i<inputs.length;i++)\n	\t\tinputs[i].checked = check.checked;\n	\t}\n	else\n	\tinputs.checked = check.checked;\n}");
			}
		}
	}
	
	public void setMustBeChecked(String errorMessage) {
		this._mustBeChecked = true;
		this._errorMessage = errorMessage;
	}
	
	public void setToEnableWhenChecked(InterfaceObject object) {
		this._enableWhenChecked = true;
		setOnAction(ACTION_ON_CLICK, "enableWhenChecked(this, findObj('" + object.getName() + "'))");
	}
	
	public void setToDisableWhenUnchecked(InterfaceObject object) {
		this._disableWhenUnchecked = true;
		setOnAction(ACTION_ON_CLICK, "disableWhenUnchecked(this, findObj('" + object.getName() + "'))");
	}
	
	public void setToDisableWhenChecked(InterfaceObject object) {
		this._disableWhenChecked = true;
		setOnAction(ACTION_ON_CLICK, "disableWhenChecked(this, findObj('" + object.getName() + "'))");
	}
	
	public void setToEnableWhenUnchecked(InterfaceObject object) {
		this._enableWhenUnchecked = true;
		setOnAction(ACTION_ON_CLICK, "enableWhenUnchecked(this, findObj('" + object.getName() + "'))");
	}
	
	public void setToCheckWhenCheckedAndUncheckWhenUnchecked(InterfaceObject object) {
		setToCheckWhenCheckedAndUncheckWhenUnchecked(object.getName());
	}
	
	public void setToCheckWhenCheckedAndUncheckWhenUnchecked(String objectName) {
		this._checkWhenCheckedUncheckWhenUnchecked = true;
		setOnAction(ACTION_ON_CLICK, "toggleOnChange(this, findObj('" + objectName + "'))");
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.isParameterSet(this.getName())) {
			String[] values = iwc.getParameterValues(getName());
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				if (value.equals(getValueAsString())) {
					setChecked(true);
				}
			}
		}
	}
	
	public void printWML(IWContext main) {
		print("<option value=\""+getValueAsString()+"\">"+getContent()+"</option>");
	}
}