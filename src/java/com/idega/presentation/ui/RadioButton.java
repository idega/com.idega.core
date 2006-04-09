/*
 * $Id: RadioButton.java,v 1.14 2006/04/09 12:13:16 laddi Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;

/**
 * <p>
 * This component renders out an input of type radiobutton.
 * </p>
 *  Last modified: $Date: 2006/04/09 12:13:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.14 $
 */
public class RadioButton extends GenericInput {

	private boolean _mustBeSelected = false;
	private String _errorMessage;
	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[3];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(this._mustBeSelected);
		values[2] = this._errorMessage;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._mustBeSelected = ((Boolean) values[1]).booleanValue();
		this._errorMessage = (String) values[2];
	}

	/**
	 * Constructs a new <code>RadioButton</code> with the name "untitled" and the value
	 * "unspecified".
	 */
	public RadioButton() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>RadioButton</code> with the given name and the value
	 * "unspecified".
	 */
	public RadioButton(String name) {
		this(name, "unspecified");
	}

	/**
	 * Constructs a new <code>RadioButton</code> with the given name and value.
	 */
	public RadioButton(String name, String value) {
		super();
		setName(name);
		setContent(value);
		setInputType(INPUT_TYPE_RADIO);
	}

	/**
	 * Sets the radio button as selected.
	 */
	public void setSelected() {
		setSelected(true);
	}

	/**
	 * Sets the radio button as selected.
	 */
	public void setSelected(boolean selected) {
		if (selected) {
			setMarkupAttributeWithoutValue("checked");
		}
		else {
			removeMarkupAttribute("checked");
		}
	}

	/**
	 * Returns whether the radion button is selected.
	 * @return boolean	True if selected, false otherwise.
	 */
	public boolean getSelected() {
		if (isMarkupAttributeSet("checked")) {
			return true;
		}
		return false;
	}

	public void main(IWContext iwc) {
		if (isEnclosedByForm()) {
			if (this._mustBeSelected) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("function isSelected(inputs,message) {").append("\n\t");
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
				this.setOnSubmitFunction("isSelected", buffer.toString(), this._errorMessage);
			}
		}
	}
	public void setMustBeSelected(String errorMessage) {
		this._mustBeSelected = true;
		this._errorMessage = errorMessage;
	}
	
	public void handleKeepStatus(IWContext iwc) {
		String[] parameters = iwc.getParameterValues(getName());
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i].equals(getValueAsString())) {
					setSelected();
				}
			}
		}
	}
	
	public void printWML(IWContext main) {
		print("<option value=\""+getValueAsString()+"\">"+getContent()+"</option>");
	}
}