//idega 2000 - Tryggvi Larusson
/*

*Copyright 2000 idega.is All Rights Reserved.

*/
package com.idega.presentation.ui;
import java.io.IOException;

import com.idega.presentation.IWContext;
import com.idega.util.text.TextSoap;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/
public class TextArea extends InterfaceObject {

	private boolean isSetAsNotEmpty;
	private String notEmptyErrorMessage;

	private static String ROWS_ATTRIBUTE = "rows";
	private static String COLS_ATTRIBUTE = "cols";
	private static String WRAP_ATTRIBUTE = "wrap";
	private static String UNTITLED_STRING = "untitled";
	private static String EMPTY_STRING = "";

	private String _content = EMPTY_STRING;
	private int maximum = -1;
	private boolean asMaximum = false;

	/**
	 * Constructs a new <code>TextArea</code> with the the default name.
	 */
	public TextArea() {
		this(UNTITLED_STRING);
	}
	
	/**
	 * Constructs a new <code>TextArea</code> with the name specified and no contents.
	 * @param name	The name of the <code>TextArea</code>
	 */
	public TextArea(String name) {
		this(name, EMPTY_STRING);
	}
	
	/**
	 * Constructs a new <code>TextArea</code> with the parameters specified.
	 * @param name	The name of the <code>TextArea</code>
	 * @param content	The content of the <code>TextArea</code>
	 */
	public TextArea(String name, String content) {
		super();
		setName(name);
		setContent(content);
	}

	/**
	 * Constructs a new <code>TextArea</code> with the parameters specified.
	 * @param name	The name of the <code>TextArea</code>
	 * @param columns	The width of the <code>TextArea</code>
	 * @param rows	The height of the <code>TextArea</code>
	 */
	public TextArea(String name, int columns, int rows) {
		this(name, "", columns, rows);
	}

	/**
	 * Constructs a new <code>TextArea</code> with the parameters specified.
	 * @param name	The name of the <code>TextArea</code>
	 * @param content	The content of the <code>TextArea</code>
	 * @param columns	The width of the <code>TextArea</code>
	 * @param rows	The height of the <code>TextArea</code>
	 */
	public TextArea(String name, String content, int columns, int rows) {
		this(name, content);
		setColumns(columns);
		setRows(rows);
	}

	public void _main(IWContext iwc) throws Exception {
		if (isEnclosedByForm()) {
			if (asMaximum) {
				this.setOnChange("countCharacters(findObj('" + getName() + "')," + maximum + ")");
				this.setOnBlur("countCharacters(findObj('" + getName() + "')," + maximum + ")");
				this.setOnFocus("countCharacters(findObj('" + getName() + "')," + maximum + ")");
				this.setOnKeyDown("countCharacters(findObj('" + getName() + "')," + maximum + ")");
				this.setOnKeyUp("countCharacters(findObj('" + getName() + "')," + maximum + ")");
				getScript().addFunction("countCharacters", "function countCharacters(msgText,maxChar) {\n	var length = msgText.value.length;\n	if (length > maxChar ) {\n	\tmsgText.value = msgText.value.substr(0,maxChar);\n	}\n	}");
			}
		}

		if (isSetAsNotEmpty)
			setOnSubmitFunction("warnIfEmpty", "function warnIfEmpty (inputbox,warnMsg) {\n\n		if ( inputbox.value == '' ) { \n		alert ( warnMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}", notEmptyErrorMessage);
	}

	public void print(IWContext iwc) throws IOException {
		if (getMarkupLanguage().equals("HTML")) {
			print("<textarea name=\"" + getName() + "\"" + getMarkupAttributesString() + " >");
			print(getContent());
			print("</textarea>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			print("<input type=\"text\" name=\"" + getName() + "\" value=\"" + getContent() + "\" >");
			print("</input>");
		}
	}

	/**
	 * Sets the number of character columns in this text area
	 */
	public void setColumns(int cols) {
		setMarkupAttribute(COLS_ATTRIBUTE, Integer.toString(cols));
	}
	
	/**
	 * Sets the number of character rows in this text area
	 */
	public void setRows(int rows) {
		setMarkupAttribute(ROWS_ATTRIBUTE, Integer.toString(rows));
	}

	/**
	 * Sets the maximum allowed characters to be entered into the textarea
	 * @param maximum	The number of characters allowed.
	 */
	public void setMaximumCharacters(int maximum) {
		this.maximum = maximum;
		this.asMaximum = true;
	}

	/**
	 * Sets whether the text in the textarea should automatically wrap at the end of a line.
	 * @param wrapping	True if text should wrap, false otherwise.
	 */
	public void setWrap(boolean wrapping) {
		if (wrapping)
			removeMarkupAttribute(WRAP_ATTRIBUTE);
		else
			setMarkupAttribute(WRAP_ATTRIBUTE,"OFF");
	}

	/**
	 * Sets the width in columns
	 * @deprecated Replaced with setWidth(String) or setColumnn(int)
	 */
	public void setWidth(int columns) {
		setColumns(columns);
	}

	/**
	 * Sets the height in rows
	 * @deprecated Replaced with setHeight(String) or setRows(int)
	 */
	public void setHeight(int rows) {
		setRows(rows);
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.isParameterSet(getName()))
			this.setContent(iwc.getParameter(getName()));
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#getContent()
	 */
	public String getContent() {
		return _content;
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#setContent(String)
	 */
	public void setContent(String content) {
		_content = content;
	}

	/**
	 * Sets the text input so that it can not be empty, displays an alert with the given 
	 * error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsNotEmpty(String errorMessage) {
		isSetAsNotEmpty = true;
		notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		setContent(value);
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#setValue(int)
	 */
	public void setValue(int value) {
		setContent(String.valueOf(value));
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}