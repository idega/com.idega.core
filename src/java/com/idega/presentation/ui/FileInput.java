/*
 * $Id: FileInput.java,v 1.12 2005/03/08 18:58:45 tryggvil Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.io.IOException;

import com.idega.presentation.IWContext;


/**
 * <p>
 * This class renders out a input of type file, used to upload files.
 * </p>
 *  Last modified: $Date: 2005/03/08 18:58:45 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.12 $
 */
public class FileInput extends InterfaceObject {

	public static final String FILE_INPUT_DEFAULT_PARAMETER_NAME = "fileupload";
	
	private boolean multiple;
	
	public FileInput() {
		this(FILE_INPUT_DEFAULT_PARAMETER_NAME);
	}

	/**
	 * This constructor will not work in JSF, use the JSF upload component OR use the default constructor.
	 * The name of the field will then be "fileupload" or use the static variable FILE_INPUT_DEFAULT_PARAMETER_NAME
	 * @param name
	 * @deprecated
	 */
	@Deprecated
	public FileInput(String name) {
		this.setName(name);
		setTransient(false);
	}

	@Override
	public void print(IWContext iwc) throws IOException {
		if (getMarkupLanguage().equals("HTML")) {
			println("<input type=\"file\" name=\"" + getName() + "\" " + getMarkupAttributesString() + " ></input>");
		}
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	@Override
	public void handleKeepStatus(IWContext iwc) {
	}
	
	@Override
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		Form parentForm = getParentForm();
		if(parentForm!=null) {
			parentForm.setMultiPart();
		}
	}
	
	@Override
	public boolean isContainer() {
		return false;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
		
		if (multiple)
			setMarkupAttribute("multiple", "multiple");
		else
			removeMarkupAttribute("multiple");
	}
	
}