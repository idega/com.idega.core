package com.idega.presentation.ui;

import com.idega.idegaweb.IWConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

/**
 * @author Laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class GenericInput extends InterfaceObject {

	public static final String INPUT_TYPE_TEXT = "text";
	public static final String INPUT_TYPE_PASSWORD = "password";
	public static final String INPUT_TYPE_CHECKBOX = "checkbox";
	public static final String INPUT_TYPE_RADIO = "radio";
	public static final String INPUT_TYPE_SUBMIT = "submit";
	public static final String INPUT_TYPE_RESET = "reset";
	public static final String INPUT_TYPE_FILE = "file";
	public static final String INPUT_TYPE_HIDDEN = "hidden";
	public static final String INPUT_TYPE_IMAGE = "image";
	public static final String INPUT_TYPE_BUTTON = "button";
	
	private String inputType = INPUT_TYPE_TEXT;

	public GenericInput() {
		super();
	}

	public String getInputType() {
		return inputType;
	}
	
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	
	public void print(IWContext main) throws Exception {
		if (getLanguage().equals("HTML")) {
			String markup = main.getApplicationSettings().getProperty(Page.MARKUP_LANGUAGE, Page.HTML);
			println("<input type=\"" + getInputType() + "\" name=\"" + getName() + "\" " + getMarkupAttributesString() + " "+(!markup.equals(Page.HTML) ? "/" : "")+">");
		}
		else if (getLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			if(normalPrintSequence()) {
				printWML(main);
			}
		}
	}

	/**
	 * @return
	 */
	public boolean normalPrintSequence() {
		return true;
	}

	public String[] getDefinedWmlAttributes() {
		String[] definedAttributes = {"emptyok","format","maxlength","size","tabindex","title","value","class","id"};
		return definedAttributes;
	}

	public void printWML(IWContext main) {
		String[] definedAttributes = getDefinedWmlAttributes();
		print("<input type=\"");
		if(INPUT_TYPE_PASSWORD.equals(inputType)) {
			print("password");
		} else {
			print("text");
		}
		print("\" name=\"" + getName() + "\" ");
		for (int i = 0; i < definedAttributes.length; i++) {
			if(isMarkupAttributeSet(definedAttributes[i])) {
				print(definedAttributes[i]+"=\"" + getMarkupAttribute(definedAttributes[i]) + "\" ");
			}
		} 		
		print("/>");
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public abstract void handleKeepStatus(IWContext iwc);

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}