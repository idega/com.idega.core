//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class TextInput extends GenericInput {

	private boolean isSetAsIntegers;
	private boolean isSetAsFloat;
	private boolean isSetAsAlphabetical;
	private boolean isSetAsEmail;
	private boolean isSetAsNotEmpty;
	private boolean isSetAsIcelandicSSNumber;
	private boolean isSetAsCreditCardNumber;

	private String integersErrorMessage;
	private String floatErrorMessage;
	private String alphabetErrorMessage;
	private String emailErrorMessage;
	private String notEmptyErrorMessage;
	private String icelandicSSNumberErrorMessage;
	private String notCreditCardErrorMessage;

	/**
	 * Constructs a new <code>TextInput</code> with the name "untitled".
	 */
	public TextInput() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>TextInput</code> with the given name.
	 */
	public TextInput(String name) {
		super();
		setName(name);
		setInputType(INPUT_TYPE_TEXT);
	}

	/**
	 * Constructs a new <code>TextInput</code> with the given name and sets the given
	 * content.
	 */
	public TextInput(String name, String content) {
		super();
		setName(name);
		setContent(content);
		setInputType(INPUT_TYPE_TEXT);

		isSetAsIntegers = false;
		isSetAsFloat = false;
		isSetAsAlphabetical = false;
		isSetAsEmail = false;
		isSetAsNotEmpty = false;
		isSetAsIcelandicSSNumber = false;
	}

	/**
	 * Sets the length (characters) of the text input.
	 * @param length	The length to set.
	 */
	public void setLength(int length) {
		setSize(length);
	}

	/**
	 * Sets the size (characters) of the text input.
	 * @param size	The size to set.
	 */
	public void setSize(int size) {
		setAttribute("size", Integer.toString(size));

	}

	/**
	 * Sets the maximum allowed length (characters) of the text input.
	 * @param length	The maxlength to set.
	 */
	public void setMaxlength(int maxlength) {
		setAttribute("maxlength", Integer.toString(maxlength));
	}

	private void setCheckSubmit() {
		if (getScript().getFunction("checkSubmit") == null) {
			getScript().addFunction("checkSubmit", "function checkSubmit(inputs){\n\n}");
		}
	}

	/**
	 * @deprecated	Use setAsNotEmpty(String errorMessage)
	 * Sets the text input so that it can not be empty, displays an alert if the "error"
	 * occurs.  Uses Javascript.
	 */
	public void setAsNotEmpty() {
		this.setAsNotEmpty("Please fill in the box " + this.getName());
	}

	/**
	 * Sets the text input so that it can not be empty, displays an alert with the given 
	 * error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsNotEmpty(String errorMessage) {
		isSetAsNotEmpty = true;
		notEmptyErrorMessage = errorMessage;
	}

	/**
	 * @deprecated	Use setAsCreditCardNumber(String errorMessage)
	 * Sets the text input so that it must contain a valid credit card number, displays an 
	 * alert if the "error" occurs.  Uses Javascript.
	 */
	public void setAsCreditCardNumber() {
		this.setAsCreditCardNumber("Please enter a valid creditcard number in " + this.getName());
	}

	/**
	 * Sets the text input so that it must contain a valid credit card number, displays an 
	 * alert with the given error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsCreditCardNumber(String errorMessage) {
		isSetAsCreditCardNumber = true;
		notCreditCardErrorMessage = errorMessage;
	}

	/**
	 * @deprecated	Use setAsEmail(String errorMessage)
	 * Sets the text input so that it must contain a valid e-mail address, displays an alert
	 * if the "error" occurs.  Uses Javascript.
	 */
	public void setAsEmail() {
		this.setAsEmail("This is not an email");
	}

	/**
	 * Sets the text input so that it must contain a valid e-mail address, displays an alert
	 * with the given error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsEmail(String errorMessage) {
		isSetAsEmail = true;
		emailErrorMessage = errorMessage;
	}

	/**
	 * @deprecated	Use setAsIntegers(String errorMessage)
	 * Sets the text input so that it must contain an integer, displays an alert if the 
	 * "error" occurs.  Uses Javascript.
	 */
	public void setAsIntegers() {
		this.setAsIntegers("Please use only numbers in " + this.getName());
	}

	/**
	 * Sets the text input so that it must contain an integer, displays an alert with the 
	 * given error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsIntegers(String errorMessage) {
		isSetAsIntegers = true;
		integersErrorMessage = errorMessage;
	}

	/**
	 * @deprecated	Use setAsFloat(String errorMessage)
	 * Sets the text input so that it must contain a float, displays an alert if the "error"
	 * occurs.  Uses Javascript.
	 */
	public void setAsFloat() {
		this.setAsFloat("Please use only numbers in " + this.getName());
	}

	/**
	 * Sets the text input so that it must contain a float, displays an alert with the 
	 * given error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsFloat(String errorMessage) {
		isSetAsFloat = true;
		floatErrorMessage = errorMessage;
	}

	/**
	 * @deprecated	Use setAsIcelandicSSNumber(String errorMessage)
	 * Sets the text input so that it must contain a valid Icelandic social security number,
	 * displays an alert if the "error" occurs.  Uses Javascript.
	 * Uses Javascript.
	 */
	public void setAsIcelandicSSNumber() {
		this.setAsFloat("Please only a Icelandic social security number in " + this.getName());
	}

	/**
	 * Sets the text input so that it must contain a valid Icelandic social security number,
	 * displays an alert with the given error message if the "error" occurs.  Uses Javascript.
	 * Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsIcelandicSSNumber(String errorMessage) {
		isSetAsIcelandicSSNumber = true;
		icelandicSSNumberErrorMessage = errorMessage;
	}

	/**
	 * @deprecated	Use setAsAlphabetictext(String errorMessage)
	 * Sets the text input so that it must contain alphabetic characters, displays an alert
	 * if the "error" occurs.  Uses Javascript.
	 */
	public void setAsAlphabetictext() {
		this.setAsAlphabeticText("Please use only alpabetical characters in " + this.getName());
	}

	/**
	 * Sets the text input so that it must contain alphabetic characters, displays an alert
	 * with the given error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsAlphabeticText(String errorMessage) {
		isSetAsAlphabetical = true;
		alphabetErrorMessage = errorMessage;
	}

	public void _main(IWContext iwc) throws Exception {
		if (getParentForm() != null) {
			if (isSetAsNotEmpty) {
				getParentForm().setOnSubmit("return checkSubmit(this)");
				setCheckSubmit();
				getScript().addToFunction("checkSubmit", "if (warnIfEmpty (findObj('" + getName() + "'),'" + notEmptyErrorMessage + "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfEmpty", "function warnIfEmpty (inputbox,warnMsg) {\n\n		if ( inputbox.value == '' ) { \n		alert ( warnMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}");
			}

			if (isSetAsIntegers) {
				getParentForm().setOnSubmit("return checkSubmit(this)");
				setCheckSubmit();
				getScript().addToFunction("checkSubmit", "if (warnIfNotIntegers (findObj('" + getName() + "'),'" + integersErrorMessage + "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfNotIntegers", "function warnIfNotIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	if (inputbox.value.charAt(i) < '0'){	\n alert ( warnMsg );\n		return false; \n	} \n	if(inputbox.value.charAt(i) > '9'){	\n alert ( warnMsg );\n		return false;\n	} \n } \n  return true;\n\n}");
			}
			if (isSetAsIcelandicSSNumber) {
				getParentForm().setOnSubmit("return checkSubmit(this)");
				setCheckSubmit();
				getScript().addToFunction("checkSubmit", "if (warnIfNotIcelandicSSNumber (findObj('" + getName() + "'),'" + icelandicSSNumberErrorMessage + "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfNotIcelandicSSNumber","function warnIfNotIcelandicSSNumber (inputbox,warnMsg) {\n  \n   if (inputbox.value.length == 10){ \n       sum = inputbox.value.charAt(0)*3 + inputbox.value.charAt(1)*2 + inputbox.value.charAt(2)*7 + inputbox.value.charAt(3)*6 + inputbox.value.charAt(4)*5 + inputbox.value.charAt(5)*4 + inputbox.value.charAt(6)*3 + inputbox.value.charAt(7)*2; \n       var rule1 = inputbox.value.charAt(8) == 11 - (sum % 11); \n       var rule2 = inputbox.value.charAt(9) == 0; \n       var rule3 = inputbox.value.charAt(9) == 8; \n       var rule4 = inputbox.value.charAt(9) == 9; \n       var subRule = false; \n       if ( rule2 || rule3 || rule4 ) { subRule = true; } \n       if ( rule1  && subRule ){ \n       return true; \n     }\n   } \n  alert ( warnMsg );\n   return false;\n \n }");
				getScript().addToFunction("checkSubmit", "if (warnIfNotIntegers (findObj('" + getName() + "'),'" + icelandicSSNumberErrorMessage + "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfNotIntegers", "function warnIfNotIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	if (inputbox.value.charAt(i) < '0'){	\n alert ( warnMsg );\n		return false; \n	} \n	if(inputbox.value.charAt(i) > '9'){	\n alert ( warnMsg );\n		return false;\n	} \n } \n  return true;\n\n}");
			}
			if (isSetAsCreditCardNumber) {
				getParentForm().setOnSubmit("return checkSubmit(this)");
				setCheckSubmit();
				getScript().addToFunction("checkSubmit", "if (warnIfNotCreditCardNumber (findObj('" + getName() + "'),'" + notCreditCardErrorMessage + "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfNotCreditCardNumber", "function warnIfNotCreditCardNumber (inputbox,warnMsg) {\n  \n   if (inputbox.value.length == 16){ \n    return true; \n   } \n else if (inputbox.value.length == 0){\n return true; \n }   \n     alert ( warnMsg );\n   return false;\n \n }");
				//not fully implemented such as maybe a checksum check could be added??
			}
			else if (isSetAsFloat) {
				getParentForm().setOnSubmit("return checkSubmit(this)");
				this.setOnBlur("return checkSubmit(this)");
				setCheckSubmit();
				getScript().addToFunction("checkSubmit", "if (warnIfNotFloat (findObj('" + getName() + "'),'" + floatErrorMessage + "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfNotFloat", "function warnIfNotFloat(inputbox,warnMsg) {\n	var inputString = inputbox.value;\n	for(i=0; i < inputString.length; i++) { \n	\tif (inputString.charAt(i) == \",\") { inputString = inputString.substring(0,i) + \".\" + inputString.substring(i+1,inputString.length); }\n	}\n	if (inputString.length == 0) { return true;\n	}\n	if (isNaN(inputString)){\n	\talert ( warnMsg );\n	\treturn false;\n	}\n	return true;\n	}");

			}
			else if (isSetAsAlphabetical) {
				getParentForm().setOnSubmit("return checkSubmit(this)");
				setCheckSubmit();
				getScript().addToFunction("checkSubmit", "if (warnIfNotAlphabetical (findObj('" + getName() + "'),'" + alphabetErrorMessage + "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfNotAlphabetical", "function warnIfNotAlphabetical (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	if ((inputbox.value.charAt(i) > '0') && (inputbox.value.charAt(i) < '9')){	\n alert ( warnMsg );\n		return false; \n	}  \n } \n  return true;\n\n}");
			}

			else if (isSetAsEmail) {
				getParentForm().setOnSubmit("return checkSubmit(this)");
				setCheckSubmit();
				getScript().addToFunction("checkSubmit", "if (warnIfNotEmail (findObj('" + getName() + "'), '" +emailErrorMessage+ "') == false ){\nreturn false;\n}\n");
				getScript().addFunction("warnIfNotEmail", "function warnIfNotEmail (inputbox,message) {\n \tvar strng = inputbox.value;\n \tif (strng.length == 0)\n \t\treturn true;\n\n \tvar emailFilter=/^.+@.+\\..{2,3}$/;\n \tif (!(emailFilter.test(strng))) {\n \t\talert(message);\n \t\treturn false;\n \t}\n\n \tvar illegalChars= /[\\(\\)\\<\\>\\,\\;\\:\\\\\\/\\\"\\[\\]]/;\n \tif (strng.match(illegalChars)) {\n \t\talert(message);\n \t\treturn false;\n \t}\n \treturn true;\n}");
			}
		}
	}

	public synchronized Object clone() {
		TextInput obj = null;
		try {
			obj = (TextInput) super.clone();
			obj.isSetAsIntegers = this.isSetAsIntegers;
			obj.isSetAsFloat = this.isSetAsFloat;
			obj.isSetAsAlphabetical = this.isSetAsAlphabetical;
			obj.isSetAsEmail = this.isSetAsEmail;
			obj.isSetAsNotEmpty = this.isSetAsNotEmpty;

			obj.integersErrorMessage = this.integersErrorMessage;
			obj.floatErrorMessage = this.floatErrorMessage;
			obj.alphabetErrorMessage = this.alphabetErrorMessage;
			obj.emailErrorMessage = this.emailErrorMessage;
			obj.notEmptyErrorMessage = this.notEmptyErrorMessage;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.getParameter(getName()) != null) {
			setContent(iwc.getParameter(getName()));
		}
	}
}
