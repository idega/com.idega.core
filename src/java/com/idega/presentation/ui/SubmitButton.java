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

	private boolean usingControlParameter = false;
	private boolean asImageButton = false;
	private boolean encloseByForm = true;

	private boolean _confirmSubmit = false;
	private String _confirmMessage;

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
	 * draws the button with the label specified in displayText.
	 */
	public SubmitButton(String displayText, String parameterName, String parameterValue) {
		this(displayText);
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		usingControlParameter = true;
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
		this.setOnClick("javascript:document." + form.getID() + "." + IWMainApplication.IWEventSessionAddressParameter + ".value=this.id ");
		iwc.setSessionAttribute(eventLocationString, event);
		listenerAdded(true);
	}

	public void main(IWContext iwc) {
		if (usingControlParameter) {
			if (!parameterName.equals(emptyString)) {
				getParentForm().addControlParameter(parameterName, emptyString);
				setValueOnClick(parameterName, parameterValue);
			}
		}
		if (isEnclosedByForm()) {
			if (_confirmSubmit) {
				getScript().addFunction("confirmSubmit", "function confirmSubmit(input) {\n submit = confirm('"+_confirmMessage+"');\n	if (submit==true)\n		\tinput.form.submit();\n}");
				setOnClick("confirmSubmit(this);");
			}
		}
	}

	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			if (_confirmSubmit)
				setInputType(INPUT_TYPE_BUTTON);
			
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
		setInputType(INPUT_TYPE_IMAGE);
		setOnClick("this.form.submit();");
	}
	
	public void setSubmitConfirm(String confirmMessage) {
		_confirmSubmit = true;
		_confirmMessage = confirmMessage;
	}
}