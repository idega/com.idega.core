//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class SubmitButton extends GenericButton {

	private static final String emptyString = "";

	private String parameterName;
	private String parameterValue;
	private String onClickScript = null;

	private boolean usingControlParameter = false;
	// not used private boolean asImageButton = false;
	private boolean encloseByForm = true;

	private boolean _enabledWhenChecked = false;
	private boolean _confirmSubmit = false;
	private boolean _confirmSingleSubmit = false;
	private String _checkedObjectName;
	private String _confirmMessage;
	private String _confirmSingleMessage;


	/**
	 * Constructs a new <code>SubmitButton</code> with the default name set and value set
	 * as "Submit".
	 */
	public SubmitButton() {
		this(emptyString, "Submit");
		setName(getDefaultName());
	}

	/**
	 * Constructs a new <code>SubmitButton</code> with the default name set, value set as 
	 * "default" and displays the image specified.
	 */
	public SubmitButton(Image defaultImage) {
		this(defaultImage, "default");
		setName(getDefaultName());
		parameterName = getDefaultName();
	}

	/**
	 * Constructs a new <code>SubmitButton</code> with the given name set, value set as 
	 * "default" and displays the image specified.
	 */
	public SubmitButton(Image defaultImage, String name) {
		this(name, "default");
		setButtonImage(defaultImage);
		setInputType(INPUT_TYPE_IMAGE);
	}

	/**
	 * Constructs a new <code>SubmitButton</code> with the given name and value set and 
	 * displays the image specified.
	 */
	public SubmitButton(Image defaultImage, String name, String value) {
		this(name, value);
		setButtonImage(defaultImage);
		setInputType(INPUT_TYPE_IMAGE);
		setName(getID());
		parameterName = name;
		parameterValue = value;
		usingControlParameter = true;
	}

	/**
	 * Constructs a new <code>SubmitButton</code> with the given name and value set and 
	 * displays the image specified. 
	 * Adds the specified script as "onClick" value at the end(!) of the "changeValue" function script, 
	 * that is set as "onClick" attribute by this class. See main method of this class.
	 * The method setOnClick(String) adds also a script, but it is not predictable if the script is added before or after adding the 
	 * "changeValue" function.  
	 */
	public SubmitButton(Image defaultImage, String name, String value, String onClickScript) {
		this(defaultImage, name, value);
		this.onClickScript = onClickScript;
	}

	/**
	 * Constructs a new <code>SubmitButton</code> with the given name and value set and
	 * draws the button with the label specified in displayText.
	 */
	public SubmitButton(String displayText, String parameterName, String parameterValue) {
		this(displayText);
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		usingControlParameter = true;
	}

	/**
	 * Constructs a new <code>SubmitButton</code> with the given name and value set and
	 * draws the button with the label specified in displayText. 
	 * Adds the specified script as "onClick" value at the end(!) of the "changeValue" function script, 
	 * that is set as "onClick" attribute by this class. See main method of this class.
	 * The method setOnClick(String) adds also a script, but it is not predictable if the script is added before or after adding the
	 * "changeValue" function.  
	 */
	public SubmitButton(String displayText, String parameterName, String parameterValue, String onClickScript) {
		this(displayText, parameterName, parameterValue);
		this.onClickScript = onClickScript;
	}
	
	/**
	 * Constructs a new <code>SubmitButton</code> with the default name and value set and
	 * draws the button with the label specified in displayText.
	 */
	public SubmitButton(String displayText) {
		this(emptyString, displayText);
		setName(getDefaultName());
	}

	/**
	 * Constructs a new <code>SubmitButton</code> with the given nameset, value set to 
	 * default value and draws the button with the label specified in displayText.
	 */
	public SubmitButton(String name, String displayText) {
		super(name, displayText);
		setInputType(INPUT_TYPE_SUBMIT);
	}

	private String getDefaultName() {
		return "sub" + getID();
	}

	/**
	 * @deprecated	Do not use this function.
	 */
	public void addIWSubmitListener(IWSubmitListener l, Form form, IWContext iwc) {
		if (!listenerAdded()) {
			postIWSubmitEvent(iwc, form);
		}
		super.addIWSubmitListener(l, iwc);
	}

	/**
	 * @deprecated	Do not use this function.
	 */
	private void postIWSubmitEvent(IWContext iwc, Form form) {
		eventLocationString = this.getID();
		IWSubmitEvent event = new IWSubmitEvent(this, IWSubmitEvent.SUBMIT_PERFORMED);
		//this.setOnClick("javascript:document." + form.getID() + "." + IWMainApplication.IWEventSessionAddressParameter + ".value=this.id ");
		this.setOnClick("javascript:document.forms['"+form.getID()+"']." + IWMainApplication.IWEventSessionAddressParameter + ".value=this.id ");
		iwc.setSessionAttribute(eventLocationString, event);
		listenerAdded(true);
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	public void main(IWContext iwc) {
		if (usingControlParameter) {
			if (!parameterName.equals(emptyString)) {
				getParentForm().addControlParameter(parameterName, emptyString);
				if (onClickScript == null) {
					setValueOnClick(parameterName, parameterValue);
				}
				else {
					setValueOnClickFollowedByScript(parameterName, parameterValue, onClickScript);
				}
			}
		}
		if (isEnclosedByForm()) {
			if (_confirmSubmit) {
				//getScript().addFunction("confirmSubmit", "function confirmSubmit(message) {\n	submit = confirm(message);\n	if (submit==true)\n		\tfindObj('"+getForm().getName()+"').submit();\n}");
				setOnClick("return confirm('"+_confirmMessage+"');return false;");
			}
			if (_confirmSingleSubmit) {
				this.setOnSubmitFunction("confirmSingleSubmit", "function confirmSingleSubmit(input,message) {\n	return confirm(message);\n}", _confirmSingleMessage);
			}
			if (_enabledWhenChecked) {
				getScript().addFunction("enableButton","function enableButton(inputs,button) {\n	\tif (validateInputs(inputs)) \n	\t\tbutton.disabled=eval('false');\n	\telse\n	\t\tbutton.disabled=eval('true');\n }");
				getScript().addFunction("validateInputs","function validateInputs(inputs) {\n	if (inputs.length > 1) {\n	\tfor (var a = 0; a < inputs.length; a++) {\n	\t\tif (inputs[a].checked == true)\n	\t\t\treturn true;\n	\t}\n	}\n	else {\n	\tif(inputs.checked == true)\n	\t\treturn true;\n	}\n	return false;\n }");
				getForm().setOnClick("enableButton(findObj('"+_checkedObjectName+"'),findObj('"+getName()+"'));");
			}
		}
	}

	/**
	 * @see com.idega.presentation.PresentationObject#print(IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			if (encloseByForm) {
				if (isEnclosedByForm()) {
					super.print(iwc);
				}
				else {
					Form form = new Form();
					form.setParentObject(getParentObject());
					this.setParentObject(form);
					form.add(this);
					form._print(iwc);
				}
			}
			else {
				super.print(iwc);
			}
		}
	}

	/**
	 * Sets a <code>Form</code> around the button if not present.
	 * @param enclose	True to enclose form around button.
	 */
	public void setToEncloseByForm(boolean enclose) {
		encloseByForm = enclose;
	}

	/**
	 * @see com.idega.presentation.ui.GenericButton#setAsImageButton(boolean)
	 */
	public void setAsImageButton(boolean asImageButton) {
		super.setAsImageButton(asImageButton);
		if (asImageButton)
			setInputType(INPUT_TYPE_IMAGE);
	}
	
	/**
	 * Sets to bring up a confirm window when button is pressed.  Only when 'OK' is pressed
	 * in the confirm window is the parent <code>Form</code> submitted.
	 * @param confirmMessage	The message to display in the confirm window.
	 */
	public void setSubmitConfirm(String confirmMessage) {
		_confirmSubmit = true;
		_confirmMessage = confirmMessage;
	}
	
	/**
	 * Sets to bring up a confirm window when button is pressed.  Only when 'OK' is pressed
	 * in the confirm window is the parent <code>Form</code> submitted.  Only works when one submit button is set on the form.
	 * @param confirmMessage	The message to display in the confirm window.
	 */
	public void setSingleSubmitConfirm(String confirmMessage) {
		_confirmSingleSubmit = true;
		_confirmSingleMessage = confirmMessage;
	}
	
	/**
	 * Sets to enable the <code>SubmitButton</code> only when the <code>CheckBox</code> 
	 * with the name specified is checked. Remains disabled otherwise.
	 * @param checkedObjectName	The name of the <code>CheckBox</code> that enables the button when checked.
	 */
	public void setToEnableWhenChecked(String checkBoxName) {
		_enabledWhenChecked = true;
		_checkedObjectName = checkBoxName;
		setDisabled(true);
	}
	
	/**
	 * Sets to enable the <code>SubmitButton</code> only when the <code>CheckBox</code> 
	 * specified is checked. Remains disabled otherwise.
	 * @param checkBox	The <code>CheckBox</code> that enables the button when checked.
	 */
	public void setToEnableWhenChecked(CheckBox checkBox) {
		setToEnableWhenChecked(checkBox.getName());
	}
	
	/**
	 * Sets to enable the <code>SubmitButton</code> only when the
	 * <code>RadioButton</code> with the name specified is selected. Remains
	 * disabled otherwise.
	 * @param selectedButtonName	The name of the <code>RadioButton</code> that
	 * enables the button when checked.
	 */
	public void setToEnableWhenSelected(String selectedButtonName) {
		setToEnableWhenChecked(selectedButtonName);
	}
	
	/**
	 * Sets to enable the <code>SubmitButton</code> only when the
	 * <code>RadioButton</code> specified is selected. Remains disabled otherwise.
	 * @param radioButton	The <code>RadioButton</code> that enables the button
	 * when selected.
	 */
	public void setToEnableWhenSelected(RadioButton radioButton) {
		setToEnableWhenChecked(radioButton.getName());
	}
	
	/**
	 * @see com.idega.presentation.ui.GenericButton#setButtonImage(Image)
	 */
	public void setButtonImage(Image image) {
		super.setButtonImage(image);
		setInputType(INPUT_TYPE_IMAGE);
	}
}