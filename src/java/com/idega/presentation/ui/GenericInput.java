package com.idega.presentation.ui;

import javax.faces.context.FacesContext;

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

	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = this.inputType;
		return values;
	}
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.inputType = ((String) values[1]);
	}

	public GenericInput() {
		super();
		setTransient(false);
	}

	public String getInputType() {
		return this.inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	@Override
	public void print(IWContext main) throws Exception {
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			String markup = main.getApplicationSettings().getDefaultMarkupLanguage();
			println("<input type=\"" + getInputType() + "\" name=\"" + getName() + "\" " + getMarkupAttributesString() + " "+(!markup.equals(Page.HTML) ? "/" : "")+">");
		}
		else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
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
		String[] definedAttributes = {"emptyok","format","maxlength","size","tabindex","title","class","id"};
		return definedAttributes;
	}

	public void printWML(IWContext main) {
		String[] definedAttributes = getDefinedWmlAttributes();
		print("<input type=\"");
		if(INPUT_TYPE_PASSWORD.equals(this.inputType)) {
			print("password");
		} else {
			print("text");
		}
		print("\" name=\"" + getName() + "\" ");
		String value = getValueAsString();
		if(value !=null && !"".equals(value)){
			print(" value=\"" + value + "\" ");
		}
		for (int i = 0; i < definedAttributes.length; i++) {
			if(isMarkupAttributeSet(definedAttributes[i])) {
				print(definedAttributes[i]+"=\"" + getMarkupAttribute(definedAttributes[i]) + "\" ");
			}
		}
		print("/>");
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	@Override
	public boolean isContainer() {
		return false;
	}

	/**
	 * Sets the accesskey html attribute so you can activate this element (causes a "click" on it) with a keyboard command
	 * @param accessKey
	 */
	public void setAccessKey(String accessKey){
		setMarkupAttribute("accesskey",accessKey);
	}

	/**
	 *
	 * @return The access key that has been set for this element
	 */
	public String getAccessKey(){
		return getMarkupAttribute("accesskey");
	}

    /**
     * Sets if the <code>TextInput</code> should allow autocomplete
     * IE feature ONLY (HTML does not validate)
     * @param allowAutoComplete
     */
    public void setAutoComplete(boolean allowAutoComplete) {
    		if (!allowAutoComplete) {
    			setMarkupAttribute("autocomplete", "off");
    		}
    }

}