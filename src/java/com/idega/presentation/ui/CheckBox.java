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
	private String _errorMessage;

	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[3];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(_mustBeChecked);
		values[2] = _errorMessage;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		_mustBeChecked = ((Boolean) values[1]).booleanValue();
		_errorMessage = (String)values[2];
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
			setMarkupAttributeWithoutValue("checked");
		}
		else {
			removeMarkupAttribute("checked");
		}
	}
	
	public void main(IWContext iwc) {
		if (isEnclosedByForm()) {
			if (_mustBeChecked) {
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
				this.setOnSubmitFunction("isChecked", buffer.toString(), _errorMessage);
			}
		}
	}
	public void setMustBeChecked(String errorMessage) {
		_mustBeChecked = true;
		_errorMessage = errorMessage;
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.getParameter(this.getName()) != null) {
			if (iwc.getParameter(this.getName()).equals(this.getValueAsString())) {
				setChecked(true);
			}
		}
	}
	
	public void printWML(IWContext main) {
		print("<option value=\""+getValueAsString()+"\">"+getContent()+"</option>");
	}
}