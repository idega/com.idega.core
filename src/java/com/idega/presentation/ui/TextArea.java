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
public class TextArea extends InterfaceObject {
	protected String content;
	protected boolean editable;
	protected boolean keepContent;
	protected boolean wrap = true;
	private static String ROWS_ATTRIBUTE = "rows";
	private static String COLS_ATTRIBUTE = "cols";
	private static String UNTITLED_STRING = "untitled";
	private static String EMPTY_STRING = "";

	private int maximum = -1;
	private boolean asMaximum = false;

	public TextArea() {
		this(UNTITLED_STRING);
	}
	public TextArea(String name) {
		this(name, EMPTY_STRING);
	}
	public TextArea(String name, String content) {
		super();
		setName(name);
		this.content = content;
		editable = true;
		keepContent = false;
	}
	public TextArea(String name, int width, int height) {
		this(name, "");
		setWidth(width);
		setHeight(height);
	}
	public TextArea(String name, String content, int width, int height) {
		this(name, content);
		setWidth(width);
		setHeight(height);
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
	public void setAsEditable() {
		editable = true;
	}
	public void setAsNotEditable() {
		editable = false;
	}
	public void setValue(String value) {
		setContent(value);
	}
	public void setValue(int value) {
		setValue(Integer.toString(value));
	}
	public void setContent(String s) {
		content = s;
	}
	public String getValue() {
		return getContent();
	}
	public void setWrap(boolean wrapping) {
		this.wrap = wrapping;
	}
	//Enables the possibility of maintaining the content of the area between requests.
	public void keepContent() {
		keepContent = true;
	}
	public void keepStatusOnAction() {
		keepContent();
	}
	public String getContent() {
		if (getRequest().getParameter(getName()) == null) {
			return content;
		}
		else {
			if (keepContent == true) {
				return getRequest().getParameter(getName());
			}
			else {
				return content;
			}
		}
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
	}

	public void print(IWContext iwc) throws IOException {
		//if ( doPrint(iwc) ){
		if (getLanguage().equals("HTML")) {
			String EditableString = "";
			if (!editable) {
				EditableString = "READONLY";
			}
			//eiki,idega iceland
			if (!wrap) {
				EditableString += " wrap=\"OFF\"";
			}
			//if (getInterfaceStyle().equals("default"))
			print("<textarea name=\"" + getName() + "\"" + getAttributeString() + EditableString + " >");
			print(getContent());
			print("</textarea>");
			// }
		}
		else if (getLanguage().equals("WML")) {
			if (content != null) {
				setValue(content);
			}
			print("<input type=\"text\" name=\"" + getName() + "\" value=\"" + getContent() + "\" >");
			print("</input>");
		}
		//}
	}
	public void setStyle(String style) {
		setAttribute("class", style);
	}
	/**
	 * Sets the width in pixels or percents
	 */
	public void setWidth(String width) {
		setWidthStyle(width);
	}
	/**
	 * Sets the height in pixels or percents
	 */
	public void setHeight(String height) {
		setHeightStyle(height);
	}
	/**
	 * Sets the number of character columns in this text area
	 */
	public void setColumns(int cols) {
		setAttribute(COLS_ATTRIBUTE, Integer.toString(cols));
	}
	/**
	 * Sets the number of character rows in this text area
	 */
	public void setRows(int rows) {
		setAttribute(ROWS_ATTRIBUTE, Integer.toString(rows));
	}

	public void setMaximumCharacters(int maximum) {
		this.maximum = maximum;
		this.asMaximum = true;
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

}
