/*
 * $Id: TextInput.java,v 1.44.2.3 2007/07/20 12:20:59 sigtryggur Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.text.NumberFormat;
import java.util.Locale;
import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.util.LocaleUtil;
import com.idega.util.text.TextSoap;



/**
 * <p>
 * Class that renders out a input element of type text
 * </p>
 *  Last modified: $Date: 2007/07/20 12:20:59 $ by $Author: sigtryggur $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.44.2.3 $
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
    private boolean isSetEmptyConfirm;
    private boolean isSetToDisableWhenNotEmpty;
    private boolean isNextInputSet;
    private String integersErrorMessage;
    private String floatErrorMessage;
    private String alphabetErrorMessage;
    private String emailErrorMessage;
    private String notEmptyErrorMessage;
    private String posIntegersErrorMessage;
    private String icelandicSSNumberErrorMessage;
    private String notCreditCardErrorMessage;
    private String minimumLengthErrorMessage;
    private String emptyConfirmMessage;
    private String nextInputID;
    private int minimumLength;
    private int decimals = -1;
	
	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[28];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(this.isSetAsIntegers);
		values[2] = Boolean.valueOf(this.isSetAsPosNegIntegers);
		values[3] = Boolean.valueOf(this.isSetAsPosIntegers);
		values[4] = Boolean.valueOf(this.isSetAsFloat);
		values[5] = Boolean.valueOf(this.isSetAsDouble);
		values[6] = Boolean.valueOf(this.isSetAsAlphabetical);
		values[7] = Boolean.valueOf(this.isSetAsEmail);
		values[8] = Boolean.valueOf(this.isSetAsNotEmpty);
		values[9] = Boolean.valueOf(this.isSetAsIcelandicSSNumber);
		values[10] = Boolean.valueOf(this.isSetAsCreditCardNumber);
		values[11] = Boolean.valueOf(this.isSetMinimumLength);
		values[12] = this.integersErrorMessage;
		values[13] = this.floatErrorMessage;
		values[14] = this.alphabetErrorMessage;
		values[15] = this.emailErrorMessage;
		values[16] = this.notEmptyErrorMessage;
		values[17] = this.posIntegersErrorMessage;
		values[18] = this.icelandicSSNumberErrorMessage;
		values[19] = this.notCreditCardErrorMessage;
		values[20] = this.minimumLengthErrorMessage;
		values[21] = new Integer(this.minimumLength);
		values[22] = new Integer(this.decimals);
		values[23] = Boolean.valueOf(this.isSetEmptyConfirm);
		values[24] = this.emptyConfirmMessage;
		values[25] = Boolean.valueOf(this.isSetToDisableWhenNotEmpty);
		values[26] = Boolean.valueOf(isNextInputSet);
		values[27] = nextInputID;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.isSetAsIntegers = ((Boolean) values[1]).booleanValue();
		this.isSetAsPosIntegers = ((Boolean) values[2]).booleanValue();
		this.isSetAsPosIntegers = ((Boolean) values[3]).booleanValue();
		this.isSetAsFloat = ((Boolean) values[4]).booleanValue();
		this.isSetAsDouble = ((Boolean) values[5]).booleanValue();
		this.isSetAsAlphabetical = ((Boolean) values[6]).booleanValue();
		this.isSetAsEmail = ((Boolean) values[7]).booleanValue();
		this.isSetAsNotEmpty = ((Boolean) values[8]).booleanValue();
		this.isSetAsIcelandicSSNumber = ((Boolean) values[9]).booleanValue();
		this.isSetAsCreditCardNumber = ((Boolean) values[10]).booleanValue();
		this.isSetMinimumLength = ((Boolean) values[11]).booleanValue();
		this.isSetEmptyConfirm = ((Boolean) values[23]).booleanValue();
		this.isSetToDisableWhenNotEmpty = ((Boolean) values[25]).booleanValue();
		this.integersErrorMessage = (String) values[12];
		this.floatErrorMessage = (String) values[13];
		this.alphabetErrorMessage = (String) values[14];
		this.emailErrorMessage = (String) values[15];
		this.notEmptyErrorMessage = (String) values[16];
		this.posIntegersErrorMessage = (String) values[17];
		this.icelandicSSNumberErrorMessage = (String) values[18];
		this.notCreditCardErrorMessage = (String) values[19];
		this.minimumLengthErrorMessage = (String) values[20];
		this.emptyConfirmMessage = (String) values[24];
		this.minimumLength = ((Integer)values[21]).intValue();
		this.decimals = ((Integer)values[22]).intValue();
		this.isNextInputSet = ((Boolean) values[26]).booleanValue();
		this.nextInputID = (String) values[27];
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

        this.isSetAsIntegers = false;
        this.isSetAsPosNegIntegers = false;
        this.isSetAsFloat = false;
        this.isSetAsDouble = false;
        this.isSetAsAlphabetical = false;
        this.isSetAsEmail = false;
        this.isSetAsNotEmpty = false;
        this.isSetAsIcelandicSSNumber = false;
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
        this.isSetAsNotEmpty = true;
        this.notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * Sets the text input so that if empty a javascript confirmation is shown. Uses Javascript.
     * 
     * @param confirmMessage
     *            The confirm message to display.
     */
    public void setEmptyConfirm(String confirmMessage) {
        this.isSetEmptyConfirm = true;
        this.emptyConfirmMessage = TextSoap.removeLineBreaks(confirmMessage);
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
        this.isSetMinimumLength = true;
        this.minimumLength = length;
        this.minimumLengthErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        this.isSetAsCreditCardNumber = true;
        this.notCreditCardErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        this.isSetAsEmail = true;
        this.emailErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        this.isSetAsIntegers = true;
        this.integersErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        this.isSetAsPosIntegers = true;
        this.posIntegersErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        this.isSetAsPosNegIntegers = true;
        this.integersErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        setAsFloat(errorMessage, this.decimals);
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
        this.isSetAsFloat = true;
        this.floatErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        setAsDouble(errorMessage, this.decimals);
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
        this.isSetAsDouble = true;
        this.floatErrorMessage = TextSoap.removeLineBreaks(errorMessage);
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
        this.decimals = dec;
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
     * "error" occurs. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsIcelandicSSNumber(String errorMessage) {
        this.isSetAsIcelandicSSNumber = true;
        this.icelandicSSNumberErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    /**
     * Sets the text input so that it must contain a valid personal ID for
     * the Locale given, displays an alert with the given error message if the
     * "error" occurs. Uses Javascript.
     * 
     * @param errorMessage
     *            The error message to display.
     */
    public void setAsPersonalID(Locale locale, String errorMessage) {
			if (locale.equals(LocaleUtil.getIcelandicLocale())) {
				setAsIcelandicSSNumber(errorMessage);
			}
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
        this.isSetAsAlphabetical = true;
        this.alphabetErrorMessage = TextSoap.removeLineBreaks(errorMessage);
    }

    public void main(IWContext iwc) throws Exception {
        if (this.isSetAsNotEmpty) {
					setOnSubmitFunction(
					        "warnIfEmpty",
					        "function warnIfEmpty (inputbox,warnMsg) {\n\n		if ( !inputbox.disabled && inputbox.value == '' ) { \n		alert ( warnMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}",
					        this.notEmptyErrorMessage);
				}

        if (this.isSetEmptyConfirm) {
					setOnSubmitFunction(
                  "confirmIfEmpty",
                  "function confirmIfEmpty(inputbox,confirmMsg) {\n\n		if ( inputbox.value == '' ) { \n		return confirm( confirmMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}",
                  this.emptyConfirmMessage);
				}

        if (this.isSetAsIntegers) {
					setOnSubmitFunction(
					        "warnIfNotIntegers",
					        "function warnIfNotIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	if (inputbox.value.charAt(i) < '0'){	\n alert ( warnMsg );\n		return false; \n	} \n	if(inputbox.value.charAt(i) > '9'){	\n alert ( warnMsg );\n		return false;\n	} \n } \n  return true;\n\n}",
					        this.integersErrorMessage);
				}

        if (this.isSetAsPosIntegers) {
					setOnSubmitFunction(
					        "warnIfNotPosIntegers",
					        "function warnIfNotPosIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n      if (i == 0) {\n        if (inputbox.value.charAt(i) < '1'){	\n          alert ( warnMsg );\n          return false; \n        } \n      } else {\n        if (inputbox.value.charAt(i) < '0'){	\n          alert ( warnMsg );\n          return false; \n        }\n      }\n      if(inputbox.value.charAt(i) > '9'){	\n        alert ( warnMsg );\n        return false;\n      } \n    } \n    return true;\n\n}",
					        this.posIntegersErrorMessage);
				}

        if (this.isSetAsPosNegIntegers) {
					setOnSubmitFunction(
					        "warnIfNotPosNegIntegers",
					        "function warnIfNotPosNegIntegers (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	        if (i==0){\n            if (inputbox.value.charAt(0) == '-'){\n                continue;\n            }\n        }\n        if (inputbox.value.charAt(i) < '0'){	\n alert ( warnMsg );\n		return false; \n	} \n	if(inputbox.value.charAt(i) > '9'){	\n alert ( warnMsg );\n		return false;\n	} \n } \n  return true;\n\n}",
					        this.integersErrorMessage);
				}

        if (this.isSetAsIcelandicSSNumber) {
					setOnSubmitFunction(
					        "warnIfNotIcelandicSSNumber",
					        "function warnIfNotIcelandicSSNumber (inputbox,warnMsg) {\n\tif (inputbox.value.length == 10){ \n\t\tvar var1 = inputbox.value.charAt(0);\n\t\tvar var2 = inputbox.value.charAt(1);\n\t\t\n\t\tvar var3 = inputbox.value.charAt(2);\n\t\tvar var4 = inputbox.value.charAt(3);\n\t\tvar var5 = inputbox.value.charAt(4);\n\t\tvar var6 = inputbox.value.charAt(5);\n\t\tvar var7 = inputbox.value.charAt(6);\n\t\tvar var8 = inputbox.value.charAt(7);\n\t\tvar var9 = inputbox.value.charAt(8);\n\n\t\tvar sum = (3 * var1) + (2 * var2) + (7 * var3) + (6 * var4) + (5 * var5) + (4 * var6) + (3 * var7) + (2 * var8);\n\n\t\tvar result = sum % 11;\n\t\tvar variable = 11 - result;\n\n\t\tif (variable == 10) {\n\t\t\tvariable = 1;\n\t\t}\n\t\telse if (variable == 11) {\n\t\t\tvariable = 0;\n\t\t}\n\n\t\tif (var9 == variable) {\n\t\t\treturn (true);\n\t\t}\n\t}  \n\telse if (inputbox.value.length == 0){\n\t\treturn (true)\n\t}   \n\talert ( warnMsg );\n\treturn false;\n}",
					        this.icelandicSSNumberErrorMessage);
				}

        if (this.isSetAsCreditCardNumber) {
					setOnSubmitFunction(
					        "warnIfNotCreditCardNumber",
					        "function warnIfNotCreditCardNumber (inputbox,warnMsg) {\n  \n   if (inputbox.value.length == 16){ \n    return true; \n   } \n else if (inputbox.value.length == 0){\n return true; \n }   \n     alert ( warnMsg );\n   return false;\n \n }",
					        this.notCreditCardErrorMessage);
				}

        if (this.isSetAsFloat || this.isSetAsDouble) {
            setOnSubmitFunction(
                    "warnIfNotFloat",
                    "function warnIfNotFloat(inputbox,warnMsg,submit) {\n	var ok = false;\n	if (inputbox.value != null) {\n		var inputString = inputbox.value;\n		for(i=0; i < inputString.length; i++) { \n		\tif (inputString.charAt(i) == \",\") { inputString = inputString.substring(0,i) + \".\" + inputString.substring(i+1,inputString.length); }\n		}\n		if (inputString.length == 0) {\n			ok = true;\n		} else if (isNaN(inputString)){\n		\talert ( warnMsg );\n		\tok = false;\n		}else{\n			ok = true;\n		}\n		if (ok && submit){\n			inputbox.value = inputString;\n		}\n	}\n	else {\n		ok = true;\n	}\n 	return ok;\n}",
                    this.floatErrorMessage, "true");
            setOnBlur("return warnIfNotFloat(this, '" + this.floatErrorMessage
                    + "', false)");

            //formating decimal sign (for default Locale) and number of
            // decimals

            NumberFormat numberFormat = NumberFormat.getInstance(iwc
                    .getCurrentLocale());
            numberFormat.setGroupingUsed(false);
            if (this.decimals >= 0) {
                numberFormat.setMaximumFractionDigits(this.decimals);
                numberFormat.setMinimumFractionDigits(this.decimals);
            }
            try {
                // do not forget the cast to Object otherwise the compiler is
                // complaining
                Object number = (this.isSetAsFloat) ? (Object) new Float(
                        getContent()) : (Object) new Double(getContent());
                setContent(numberFormat.format(number));
            } catch (IllegalArgumentException ex) {
                //ex.printStackTrace();
            }
        }

        if (this.isSetMinimumLength) {
            setOnSubmitFunction(
                    "warnIfNotMinimumLength",
                    "function warnIfNotMinimumLength(inputbox,warnMsg) {\n\tif (inputbox.value.length < "
                            + this.minimumLength
                            + ") {\n\t\talert(warnMsg);\n\t\treturn false;\n\t}\n\treturn true;\n}",
                    this.minimumLengthErrorMessage);
        }

        if (this.isSetAsAlphabetical) {
					setOnSubmitFunction(
					        "warnIfNotAlphabetical",
					        "function warnIfNotAlphabetical (inputbox,warnMsg) {\n \n    for(i=0; i < inputbox.value.length; i++) { \n	if ((inputbox.value.charAt(i) > '0') && (inputbox.value.charAt(i) < '9')){	\n alert ( warnMsg );\n		return false; \n	}  \n } \n  return true;\n\n}",
					        this.alphabetErrorMessage);
				}

        if (this.isSetAsEmail) {
					setOnSubmitFunction(
					        "warnIfNotEmail",
					        "function warnIfNotEmail (inputbox,message) {\n \tvar strng = inputbox.value;\n \tif (strng.length == 0)\n \t\treturn true;\n\n \tvar emailFilter=/^.+@.+\\..{2,3}$/;\n \tif (!(emailFilter.test(strng))) {\n \t\talert(message);\n \t\treturn false;\n \t}\n\n \tvar illegalChars= /[\\(\\)\\<\\>\\,\\;\\:\\\\\\/\\\"\\[\\]]/;\n \tif (strng.match(illegalChars)) {\n \t\talert(message);\n \t\treturn false;\n \t}\n \treturn true;\n}",
					        this.emailErrorMessage);
				}
        
        if (this.isSetToDisableWhenNotEmpty) {
  					getScript().addFunction("disableObjectWhenNotEmpty", "function disableObjectWhenNotEmpty(input, otherInput) {\n	if (input.value.length() > 1) {	otherInput[i].disabled=true; }\n	else	otherInput.disabled=false;\n}");
        }
        
        if (this.isNextInputSet) {
        	getScript().addFunction("gotoNextInput","function gotoNextInput(event,source,destination) {\n\tif(((event.keyCode > 31 && event.keyCode < 128) || event.keyCode == 9) \t&& (event.keyCode != 37\t&& event.keyCode != 39) ) {\n\t\tif ((source.value).length == source.maxLength) {\n\t\t\tif(event.keyCode == 9)\t{\n\t\t\t\tsource.focus();\n\t\t\t\tsource.select();\n\t\t\t} else {\n\t\t\t\tdestination.focus();\n\t\t\t\tdestination.select();\n\t\t\t}\n\t\t\treturn 0;\n\t\t}\n\t}\n\treturn 1;\n};");            
        	this.setOnKeyUp("gotoNextInput(event,this,"+ nextInputID +")"); 
        }
        
    }

  	/**
  	 * Sets the interface object(s) with the given name to be enabled when this object
  	 * receives the action specified.
  	 * @param action	The action to perform on.
  	 * @param objectToEnable	The name of the interface object(s) to enable.
  	 * @param enable	Set to true to disable, false will enable.
  	 */
  	public void setToDisableOnWhenNotEmpty(InterfaceObject object) {
  		this.isSetToDisableWhenNotEmpty = true;
  		setOnKeyUp("disableObjectWhenNotEmpty(findObj(this, '" + object.getName() + "'))");
  		setOnKeyDown("disableObjectWhenNotEmpty(findObj(this, '" + object.getName() + "'))");
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
    	if (getIndex() > -1) {
    		String[] parameters = iwc.getParameterValues(getName());
    		if (parameters != null && parameters.length >= getIndex() + 1) {
    			setContent(parameters[getIndex()]);
    		}
    	}
    	else {
        if (iwc.getParameter(getName()) != null) {
            setContent(iwc.getParameter(getName()));
        }
    	}
    }

    /**
     * Sets the <code>TextInput</code> as a password input.
     * 
     * @param asPasswordInput
     *            To set as password, set as true, false otherwise.
     */
    public void setAsPasswordInput(boolean asPasswordInput) {
        if (asPasswordInput) {
					setInputType(INPUT_TYPE_PASSWORD);
				}
				else {
					setInputType(INPUT_TYPE_TEXT);
				}
    }
    
    public void setNextInput(GenericInput input) {
    	if (input != null) {
	    	setNextInput(input.getId());
		}
	}

	public void setNextInput(GenericSelect select) {
		if (select != null) {
			setNextInput(select.getId());
		}
	}

	public void setNextInput(String inputID) {
		if (inputID != null) {
			this.isNextInputSet = true;
			this.nextInputID = inputID;
		}
	}
}