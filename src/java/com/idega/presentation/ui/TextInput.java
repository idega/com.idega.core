//idega 2000 - Tryggvi Larusson
/*
 *Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.presentation.ui;

import java.text.NumberFormat;
import javax.faces.context.FacesContext;

import com.idega.presentation.IWContext;
import com.idega.util.text.TextSoap;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class TextInput extends GenericInput {
    private boolean isSetAsIntegers;
    private boolean isSetAsPosNegIntegers;
    private boolean isSetAsPosIntegers;
    private boolean isSetAsFloat;
    private boolean isSetAsDouble;
    private boolean isSetAsAlphabetical;
    private boolean isSetAsEmail;
    private boolean isSetAsNotEmpty;
    private boolean isSetAsIcelandicSSNumber;
    private boolean isSetAsCreditCardNumber;
    private boolean isSetMinimumLength;
    private String integersErrorMessage;
    private String floatErrorMessage;
    private String alphabetErrorMessage;
    private String emailErrorMessage;
    private String notEmptyErrorMessage;
    private String posIntegersErrorMessage;
    private String icelandicSSNumberErrorMessage;
    private String notCreditCardErrorMessage;
    private String minimumLengthErrorMessage;
    private int minimumLength;
    private int decimals = -1;
	
	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[23];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(isSetAsIntegers);
		values[2] = Boolean.valueOf(isSetAsPosNegIntegers);
		values[3] = Boolean.valueOf(isSetAsPosIntegers);
		values[4] = Boolean.valueOf(isSetAsFloat);
		values[5] = Boolean.valueOf(isSetAsDouble);
		values[6] = Boolean.valueOf(isSetAsAlphabetical);
		values[7] = Boolean.valueOf(isSetAsEmail);
		values[8] = Boolean.valueOf(isSetAsNotEmpty);
		values[9] = Boolean.valueOf(isSetAsIcelandicSSNumber);
		values[10] = Boolean.valueOf(isSetAsCreditCardNumber);
		values[11] = Boolean.valueOf(isSetMinimumLength);
		values[12] = integersErrorMessage;
		values[13] = floatErrorMessage;
		values[14] = alphabetErrorMessage;
		values[15] = emailErrorMessage;
		values[16] = notEmptyErrorMessage;
		values[17] = posIntegersErrorMessage;
		values[18] = icelandicSSNumberErrorMessage;
		values[19] = notCreditCardErrorMessage;
		values[20] = minimumLengthErrorMessage;
		values[21] = new Integer(minimumLength);
		values[22] = new Integer(decimals);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		isSetAsIntegers = ((Boolean) values[1]).booleanValue();
		isSetAsPosIntegers = ((Boolean) values[2]).booleanValue();
		isSetAsPosIntegers = ((Boolean) values[3]).booleanValue();
		isSetAsFloat = ((Boolean) values[4]).booleanValue();
		isSetAsDouble = ((Boolean) values[5]).booleanValue();
		isSetAsAlphabetical = ((Boolean) values[6]).booleanValue();
		isSetAsEmail = ((Boolean) values[7]).booleanValue();
		isSetAsNotEmpty = ((Boolean) values[8]).booleanValue();
		isSetAsIcelandicSSNumber = ((Boolean) values[9]).booleanValue();
		isSetAsCreditCardNumber = ((Boolean) values[10]).booleanValue();
		isSetMinimumLength = ((Boolean) values[11]).booleanValue();
		integersErrorMessage = (String) values[12];
		floatErrorMessage = (String) values[13];
		alphabetErrorMessage = (String) values[14];
		emailErrorMessage = (String) values[15];
		notEmptyErrorMessage = (String) values[16];
		posIntegersErrorMessage = (String) values[17];
		icelandicSSNumberErrorMessage = (String) values[18];
		notCreditCardErrorMessage = (String) values[19];
		minimumLengthErrorMessage = (String) values[20];
		minimumLength = ((Integer)values[21]).intValue();
		decimals = ((Integer)values[22]).intValue();
	}	
	
	
	
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
     * Constructs a new <code>TextInput</code> with the given name and sets
     * the given content.
     */
    public TextInput(String name, String content) {
        super();
        setName(name);
        if (content != null) {
            setContent(content);
        }
        setInputType(INPUT_TYPE_TEXT);

        isSetAsIntegers = false;
        isSetAsPosNegIntegers = false;
        isSetAsFloat = false;
        isSetAsDouble = false;
        isSetAsAlphabetical = false;
        isSetAsEmail = false;
        isSetAsNotEmpty = false;
        isSetAsIcelandicSSNumber = false;
    }

    /**
     * Sets the length (characters) of the text input.
     * 
     * @param length
     *            The length to set.
     */
    public void setLength(int length) {
        setSize(length);
    }

    /**
     * Sets the size (characters) of the text input.
     * 
     * @param size
     *            The size to set.
     */
    public void setSize(int size) {
        setMarkupAttribute("size", Integer.toString(size));

    }

    /**
     * Sets the maximum allowed length (characters) of the text input.
     * 
     * @param length
     *            The maxlength to set.
     */
    public void setMaxlength(int maxlength) {
        setMarkupAttribute("maxlength", Integer.toString(maxlength));
    }

    /**
     * @deprecated Use setAsNotEmpty(String errorMessage) Sets the text input so
     *             that it can not be empty, displays an alert if the "error"
     *             occurs. Uses Javascript.
     */
    public void setAsNotEmpty() {
        this.setAsNotEmpty("Please fill in the box " + this.getName());
    }

    /**
     * Sets the text input so that it can not be empty, displays an alert with
     * the given error message if the "error" occurs. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsNotEmpty(String errorMessage) {
        isSetAsNotEmpty = true;
        notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * Sets the text input so that its contents's length can not have less
     * characters than specified, displays an alert with the given error message
     * if the "error" occurs. Uses Javascript.
     * 
     * @param length
     *            The minimum length of the content.
     * @param errorMessage
     *            The error message to display.
     */
    public void setMininumLength(int length, String errorMessage) {
        isSetMinimumLength = true;
        minimumLength = length;
        minimumLengthErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * @deprecated Use setAsCreditCardNumber(String errorMessage) Sets the text
     *             input so that it must contain a valid credit card number,
     *             displays an alert if the "error" occurs. Uses Javascript.
     */
    public void setAsCreditCardNumber() {
        this.setAsCreditCardNumber("Please enter a valid creditcard number in "
                + this.getName());
    }

    /**
     * Sets the text input so that it must contain a valid credit card number,
     * displays an alert with the given error message if the "error" occurs.
     * Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsCreditCardNumber(String errorMessage) {
        isSetAsCreditCardNumber = true;
        notCreditCardErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * @deprecated Use setAsEmail(String errorMessage) Sets the text input so
     *             that it must contain a valid e-mail address, displays an
     *             alert if the "error" occurs. Uses Javascript.
     */
    public void setAsEmail() {
        this.setAsEmail("This is not an email");
    }

    /**
     * Sets the text input so that it must contain a valid e-mail address,
     * displays an alert with the given error message if the "error" occurs.
     * Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsEmail(String errorMessage) {
        isSetAsEmail = true;
        emailErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * @deprecated Use setAsIntegers(String errorMessage) Sets the text input so
     *             that it must contain an integer, displays an alert if the
     *             "error" occurs. Uses Javascript.
     */
    public void setAsIntegers() {
        this.setAsIntegers("Please use only numbers in " + this.getName());
    }

    /**
     * Sets the text input so that it must contain an integer, displays an alert
     * with the given error message if the "error" occurs. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsIntegers(String errorMessage) {
        isSetAsIntegers = true;
        integersErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * Sets the text input so that it must contain a positive integer, displays
     * an alert with the given error message if the "error" occurs. Uses
     * Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsPositiveIntegers(String errorMessage) {
        isSetAsPosIntegers = true;
        posIntegersErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * Sets the text input so that it must contain a positive or negative
     * integer or zero, displays an alert with the given error message if the
     * "error" occurs. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsPosNegIntegers(String errorMessage) {
        isSetAsPosNegIntegers = true;
        integersErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * @deprecated Use setAsFloat(String errorMessage) Sets the text input so
     *             that it must contain a float, displays an alert if the
     *             "error" occurs. Uses Javascript.
     */
    public void setAsFloat() {
        this.setAsFloat("Please use only numbers in " + this.getName());
    }

    /**
     * Sets the text input so that it must contain a float, displays an alert
     * with the given error message if the "error" occurs. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsFloat(String errorMessage) {
        setAsFloat(errorMessage, decimals);
    }

    /**
     * Sets the text input so that it must contain a float, displays an alert
     * with the given error message if the "error" occurs. Also, sets the
     * (exact) number of decimal to be displayed for a float field. If set to
     * negative number, the value is ignored. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     * @param dec
     *            The number of decimals
     */
    public void setAsFloat(String errorMessage, int decimals) {
        isSetAsFloat = true;
        floatErrorMessage = TextSoap.removeLineBreaks(errorMessage);
        setDecimals(decimals);
    }

    /**
     * @deprecated Use setAsDouble(String errorMessage) Sets the text input so
     *             that it must contain a double, displays an alert if the
     *             "error" occurs. Uses Javascript.
     */
    public void setAsDouble() {
        setAsDouble("Please use only numbers in " + getName());
    }

    /**
     * Sets the text input so that it must contain a double, displays an alert
     * with the given error message if the "error" occurs. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsDouble(String errorMessage) {
        setAsDouble(errorMessage, decimals);
    }

    /**
     * Sets the text input so that it must contain a double, displays an alert
     * with the given error message if the "error" occurs. Also, sets the
     * (exact) number of decimal to be displayed for a double field. If set to
     * negative number, the value is ignored. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     * @param dec
     *            The number of decimals
     */
    public void setAsDouble(String errorMessage, int decimals) {
        isSetAsDouble = true;
        floatErrorMessage = TextSoap.removeLineBreaks(errorMessage);
        setDecimals(decimals);
    }

    /**
     * Sets the (exact) number of decimal to be displayed for a float field. If
     * set to negative number, the value is ignored.
     * 
     * @param dec
     *            The number of decimals
     */
    public void setDecimals(int dec) {
        decimals = dec;
    }

    /**
     * @deprecated Use setAsIcelandicSSNumber(String errorMessage) Sets the text
     *             input so that it must contain a valid Icelandic social
     *             security number, displays an alert if the "error" occurs.
     *             Uses Javascript. Uses Javascript.
     */
    public void setAsIcelandicSSNumber() {
        setAsIcelandicSSNumber("Please only a Icelandic social security number in "
                + this.getName());
    }

    /**
     * Sets the text input so that it must contain a valid Icelandic social
     * security number, displays an alert with the given error message if the
     * "error" occurs. Uses Javascript. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsIcelandicSSNumber(String errorMessage) {
        isSetAsIcelandicSSNumber = true;
        icelandicSSNumberErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * @deprecated Use setAsAlphabetictext(String errorMessage) Sets the text
     *             input so that it must contain alphabetic characters, displays
     *             an alert if the "error" occurs. Uses Javascript.
     */
    public void setAsAlphabetictext() {
        this.setAsAlphabeticText("Please use only alpabetical characters in "
                + this.getName());
    }

    /**
     * Sets the text input so that it must contain alphabetic characters,
     * displays an alert with the given error message if the "error" occurs.
     * Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsAlphabeticText(String errorMessage) {
        isSetAsAlphabetical = true;
        alphabetErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    public void main(IWContext iwc) throws Exception {
        if (isSetAsNotEmpty)
                setOnSubmitFunction(
                        "warnIfEmpty",
                        "function warnIfEmpty (inputbox,warnMsg) {\n\n		if ( inputbox.value == '' ) { \n		alert ( warnMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}",
                        notEmptyErrorMessage);

        if (isSetAsIntegers)
                setOnSubmitFunction(
                        "warnIfNotIntegers",
                        "function warnIfNotIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	if (inputbox.value.charAt(i) < '0'){	\n alert ( warnMsg );\n		return false; \n	} \n	if(inputbox.value.charAt(i) > '9'){	\n alert ( warnMsg );\n		return false;\n	} \n } \n  return true;\n\n}",
                        integersErrorMessage);

        if (isSetAsPosIntegers)
                setOnSubmitFunction(
                        "warnIfNotPosIntegers",
                        "function warnIfNotPosIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n      if (i == 0) {\n        if (inputbox.value.charAt(i) < '1'){	\n          alert ( warnMsg );\n          return false; \n        } \n      } else {\n        if (inputbox.value.charAt(i) < '0'){	\n          alert ( warnMsg );\n          return false; \n        }\n      }\n      if(inputbox.value.charAt(i) > '9'){	\n        alert ( warnMsg );\n        return false;\n      } \n    } \n    return true;\n\n}",
                        posIntegersErrorMessage);

        if (isSetAsPosNegIntegers)
                setOnSubmitFunction(
                        "warnIfNotPosNegIntegers",
                        "function warnIfNotPosNegIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	        if (i==0){\n            if (inputbox.value.charAt(0) == '-'){\n                continue;\n            }\n        }\n        if (inputbox.value.charAt(i) < '0'){	\n alert ( warnMsg );\n		return false; \n	} \n	if(inputbox.value.charAt(i) > '9'){	\n alert ( warnMsg );\n		return false;\n	} \n } \n  return true;\n\n}",
                        integersErrorMessage);

        if (isSetAsIcelandicSSNumber)
                setOnSubmitFunction(
                        "warnIfNotIcelandicSSNumber",
                        "function warnIfNotIcelandicSSNumber (inputbox,warnMsg) {\n\tif (inputbox.value.length == 10){ \n\t\tvar var1 = inputbox.value.charAt(0);\n\t\tvar var2 = inputbox.value.charAt(1);\n\t\t\n\t\tvar var3 = inputbox.value.charAt(2);\n\t\tvar var4 = inputbox.value.charAt(3);\n\t\tvar var5 = inputbox.value.charAt(4);\n\t\tvar var6 = inputbox.value.charAt(5);\n\t\tvar var7 = inputbox.value.charAt(6);\n\t\tvar var8 = inputbox.value.charAt(7);\n\t\tvar var9 = inputbox.value.charAt(8);\n\n\t\tvar sum = (3 * var1) + (2 * var2) + (7 * var3) + (6 * var4) + (5 * var5) + (4 * var6) + (3 * var7) + (2 * var8);\n\n\t\tvar result = sum % 11;\n\t\tvar variable = 11 - result;\n\n\t\tif (variable == 10) {\n\t\t\tvariable = 1;\n\t\t}\n\t\telse if (variable == 11) {\n\t\t\tvariable = 0;\n\t\t}\n\n\t\tif (var9 == variable) {\n\t\t\treturn (true);\n\t\t}\n\t}  \n\telse if (inputbox.value.length == 0){\n\t\treturn (true)\n\t}   \n\talert ( warnMsg );\n\treturn false;\n}",
                        icelandicSSNumberErrorMessage);

        if (isSetAsCreditCardNumber)
                setOnSubmitFunction(
                        "warnIfNotCreditCardNumber",
                        "function warnIfNotCreditCardNumber (inputbox,warnMsg) {\n  \n   if (inputbox.value.length == 16){ \n    return true; \n   } \n else if (inputbox.value.length == 0){\n return true; \n }   \n     alert ( warnMsg );\n   return false;\n \n }",
                        notCreditCardErrorMessage);

        if (isSetAsFloat || isSetAsDouble) {
            setOnSubmitFunction(
                    "warnIfNotFloat",
                    "function warnIfNotFloat(inputbox,warnMsg,submit) {\n	var ok = false;\n	var inputString = inputbox.value;\n	for(i=0; i < inputString.length; i++) { \n	\tif (inputString.charAt(i) == \",\") { inputString = inputString.substring(0,i) + \".\" + inputString.substring(i+1,inputString.length); }\n	}\n	if (inputString.length == 0) {\n		ok = true;\n	} else if (isNaN(inputString)){\n	\talert ( warnMsg );\n	\tok = false;\n	}else{\n		ok = true;\n	}\n	if (ok && submit){\n		inputbox.value = inputString;\n	}\n 	return ok;\n}",
                    floatErrorMessage, "true");
            setOnBlur("return warnIfNotFloat(this, '" + floatErrorMessage
                    + "', false)");

            //formating decimal sign (for default Locale) and number of
            // decimals

            NumberFormat numberFormat = NumberFormat.getInstance(iwc
                    .getCurrentLocale());
            numberFormat.setGroupingUsed(false);
            if (decimals >= 0) {
                numberFormat.setMaximumFractionDigits(decimals);
                numberFormat.setMinimumFractionDigits(decimals);
            }
            try {
                // do not forget the cast to Object otherwise the compiler is
                // complaining
                Object number = (isSetAsFloat) ? (Object) new Float(
                        getContent()) : (Object) new Double(getContent());
                setContent(numberFormat.format(number));
            } catch (IllegalArgumentException ex) {
                //ex.printStackTrace();
            }
        }

        if (isSetMinimumLength) {
            setOnSubmitFunction(
                    "warnIfNotMinimumLength",
                    "function warnIfNotMinimumLength(inputbox,warnMsg) {\n\tif (inputbox.value.length < "
                            + minimumLength
                            + ") {\n\t\talert(warnMsg);\n\t\treturn false;\n\t}\n\treturn true;\n}",
                    minimumLengthErrorMessage);
        }

        if (isSetAsAlphabetical)
                setOnSubmitFunction(
                        "warnIfNotAlphabetical",
                        "function warnIfNotAlphabetical (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	if ((inputbox.value.charAt(i) > '0') && (inputbox.value.charAt(i) < '9')){	\n alert ( warnMsg );\n		return false; \n	}  \n } \n  return true;\n\n}",
                        alphabetErrorMessage);

        if (isSetAsEmail)
                setOnSubmitFunction(
                        "warnIfNotEmail",
                        "function warnIfNotEmail (inputbox,message) {\n \tvar strng = inputbox.value;\n \tif (strng.length == 0)\n \t\treturn true;\n\n \tvar emailFilter=/^.+@.+\\..{2,3}$/;\n \tif (!(emailFilter.test(strng))) {\n \t\talert(message);\n \t\treturn false;\n \t}\n\n \tvar illegalChars= /[\\(\\)\\<\\>\\,\\;\\:\\\\\\/\\\"\\[\\]]/;\n \tif (strng.match(illegalChars)) {\n \t\talert(message);\n \t\treturn false;\n \t}\n \treturn true;\n}",
                        emailErrorMessage);
    }

    public synchronized Object clone() {
        TextInput obj = null;
        try {
            obj = (TextInput) super.clone();
            obj.isSetAsIntegers = this.isSetAsIntegers;
            obj.isSetAsPosNegIntegers = this.isSetAsPosNegIntegers;
            obj.isSetAsFloat = this.isSetAsFloat;
            obj.isSetAsDouble = this.isSetAsDouble;
            obj.isSetAsAlphabetical = this.isSetAsAlphabetical;
            obj.isSetAsEmail = this.isSetAsEmail;
            obj.isSetAsNotEmpty = this.isSetAsNotEmpty;

            obj.integersErrorMessage = this.integersErrorMessage;
            obj.floatErrorMessage = this.floatErrorMessage;
            obj.alphabetErrorMessage = this.alphabetErrorMessage;
            obj.emailErrorMessage = this.emailErrorMessage;
            obj.notEmptyErrorMessage = this.notEmptyErrorMessage;
        } catch (Exception ex) {
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

    /**
     * Sets the <code>TextInput</code> as a password input.
     * 
     * @param asPasswordInput
     *            To set as password, set as true, false otherwise.
     */
    public void setAsPasswordInput(boolean asPasswordInput) {
        if (asPasswordInput)
            setInputType(INPUT_TYPE_PASSWORD);
        else
            setInputType(INPUT_TYPE_TEXT);
    }
}