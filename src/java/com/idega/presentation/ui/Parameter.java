/*
 * $Id: Parameter.java,v 1.10 2005/03/08 20:39:42 tryggvil Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * <p>
 * This class is used to render out an (invisible hidden input) to add a parameter to a Form.
 * This parameter has a name/value that has only a meaning inside a Form and is submitted with the form in a POST or GET.
 * </p>
 *  Last modified: $Date: 2005/03/08 20:39:42 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.10 $
 */
public class Parameter extends GenericInput {

	/**
	 * Constructs a new <code>Parameter</code> with the name "untitled".
	 */
	public Parameter() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>Parameter</code> with the given name and the value "unspecified".
	 */
	public Parameter(String name) {
		this(name, "unspecified");
	}

	/**
	 * Constructs a new <code>Parameter</code> with the given name and sets the given
	 * value.
	 */
	public Parameter(String name, String value) {
		super();
		setName(name);
		setContent(value);
		setInputType(INPUT_TYPE_HIDDEN);
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	@Override
	public void handleKeepStatus(IWContext iwc) {
		try {
			super.handleKeepStatus(iwc);
		} catch (AssertionError e) {
			return;
		}

		if (iwc.getParameter(getName()) != null) {
			setContent(iwc.getParameter(getName()));
		}
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Parameter){
			Parameter pObj = (Parameter)obj;
			boolean name = this.getName().equals(pObj.getName());
			boolean content = this.getContent().equals(pObj.getContent());
			boolean inputType = this.getInputType().equals(pObj.getInputType());
			return (name && content && inputType);
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String[] getDefinedWmlAttributes() {
		String[] definedAttributes = {"value"};//,"class","id"};
		return definedAttributes;
	}

	@Override
	public void printWML(IWContext main) {
		String[] definedAttributes = getDefinedWmlAttributes();
		print("<postfield name=\"" + getName() + "\" ");
		for (int i = 0; i < definedAttributes.length; i++) {
			if(isMarkupAttributeSet(definedAttributes[i])) {
				print(definedAttributes[i]+"=\"" + getMarkupAttribute(definedAttributes[i]) + "\" ");
			}
		}
		print("/>");
	}

	/**
	 * @return
	 */
	@Override
	public boolean normalPrintSequence() {
		return false;
	}
}