//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.io.IOException;

import com.idega.presentation.IWContext;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class FileInput extends InterfaceObject {

	public static final String FILE_INPUT_DEFAULT_PARAMETER_NAME = "fileupload";
	
	public FileInput() {
		this(FILE_INPUT_DEFAULT_PARAMETER_NAME);
	}

	public FileInput(String name) {
		this.setName(name);
	}

	public void print(IWContext iwc) throws IOException {
		if (getMarkupLanguage().equals("HTML")) {
			println("<input type=\"file\" name=\"" + getName() + "\" " + getMarkupAttributesString() + " ></input>");
		}
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		Form parentForm = getParentForm();
		if(parentForm!=null) {
			parentForm.setMultiPart();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}