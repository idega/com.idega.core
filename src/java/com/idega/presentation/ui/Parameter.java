//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
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
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.getParameter(getName()) != null) {
			setContent(iwc.getParameter(getName()));
		}
	}
	
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
	
	public String[] getDefinedWmlAttributes() {
		String[] definedAttributes = {"value"};//,"class","id"};
		return definedAttributes;
	}
	
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
	public boolean normalPrintSequence() {
		return false;
	}
}