/*
 * $Id: Script.java,v 1.32 2008/12/11 08:03:30 laddi Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWConstants;
import com.idega.util.datastructures.map.MapUtil;

/**
 * <p>
 * This class renders out a script element.<br>
 * An instance of this component can be used to define javascript functions and
 * add to a component or a page.
 * </p>
 * Last modified: $Date: 2008/12/11 08:03:30 $ by $Author: laddi $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.32 $
 */
public class Script extends PresentationObject {

	private String scriptType;
	private Map<String, String> scriptCode;
	private Map<String, String> variables;
	private Map<String, String> methods;
	private String scriptLines;

	private static final String SCRIPT_TYPE_JAVACRIPT="javascript";
	private static final String MIMETYPE_JAVACRIPT="text/javascript";
	private static final String ATTRIBUTE_SOURCE="src";
	private static final String ATTRIBUTE_TYPE="type";

	public Script() {
		this(SCRIPT_TYPE_JAVACRIPT);
	}

	public Script(String scriptLanguage) {
		super();
		setType();
		setTransient(false);
	}

	public boolean isEmpty() {
		return getScriptCode().isEmpty() && getMarkupAttribute(ATTRIBUTE_SOURCE) == null;
	}

	protected Map<String, String> getScriptCode() {
		if (this.scriptCode == null) {
			this.scriptCode = new LinkedHashMap<String, String>();
		}
		return this.scriptCode;
	}

	protected void setType() {
		setType(MIMETYPE_JAVACRIPT);
	}

	protected void setType(String type) {
		setMarkupAttribute(ATTRIBUTE_TYPE, type);
	}

	public void setScriptSource(String sourceURL) {
		setMarkupAttribute(ATTRIBUTE_SOURCE, sourceURL);
	}

	public String getScriptCode(IWContext iwc) {
		StringBuffer returnString = new StringBuffer();
		for (Iterator<String> iter = getScriptCode().keySet().iterator(); iter.hasNext();) {
			String functionCode = getScriptCode().get(iter.next());
			returnString.append(functionCode + "\n");
		}
		return returnString.toString();
	}

	public boolean doesFunctionExist(String function) {
		if (getScriptCode().get(function) == null) {
			return false;
		}
		return true;
	}

	public void removeFunction(String functionName) {
		getScriptCode().remove(functionName);
	}

	public void addToFunction(String functionName, String scriptString) {
		if (getScriptCode() != null) {
			String functionCode = getScriptCode().get(functionName);
			if (functionCode != null) {
				String beginString;
				String endString;
				String returnString;
				int lastbracket;
				lastbracket = functionCode.lastIndexOf("}");
				beginString = functionCode.substring(0, lastbracket);
				endString = "}";
				returnString = beginString + "\n" + scriptString + "\n" + endString;
				getScriptCode().put(functionName, returnString);
			}
		}
	}

	public void addToBeginningOfFunction(String functionName, String scriptString) {
		if (getScriptCode() != null) {
			String functionCode = getScriptCode().get(functionName);
			if (functionCode != null) {
				String beginString;
				String endString;
				String returnString;
				int firstBracket;
				firstBracket = functionCode.indexOf("{") + 1;
				beginString = functionCode.substring(0, firstBracket);
				endString = functionCode.substring(firstBracket + 1);
				returnString = beginString + "\n" + scriptString + "\n" + endString;
				getScriptCode().put(functionName, returnString);
			}
		}
	}

	public void addFunction(String functionName, String scriptString) {
		getScriptCode().put(functionName, scriptString);
	}

	public void addVariable(String variableName, String variableValue) {
		if (this.variables == null) {
			this.variables = new Hashtable<String, String>();
		}
		this.variables.put(variableName, variableValue);
	}

	public void addVariable(String variableName) {
		addVariable(variableName, null);
	}

	public String getVariable(String variableName) {
		return this.variables.get(variableName);
	}

	public String getVariables() {
		StringBuffer returnString = new StringBuffer();
		if (this.variables != null) {
			for (Iterator<String> varsIter = variables.keySet().iterator(); varsIter.hasNext();) {
				String variableName = varsIter.next();
				String variableValue = getVariable(variableName);
				if (variableValue != null) {
					returnString.append("var " + variableName + " = " + variableValue + ";\n");
				}
				else {
					returnString.append("var " + variableName + ";\n");
				}
			}
			returnString.append("\n");
		}
		return returnString.toString();
	}

	public void addMethod(String methodName, String methodValue) {
		if (this.methods == null) {
			this.methods = new Hashtable<String, String>();
		}
		this.methods.put(methodName, methodValue);
	}

	public String getMethod(String methodName) {
		return this.methods.get(methodName);
	}

	public String getMethods() {
		StringBuffer returnString = new StringBuffer();
		if (this.methods != null) {
			for (Iterator<String> methodsIter = this.methods.keySet().iterator(); methodsIter.hasNext();) {
				String methodName = methodsIter.next();
				String methodValue = getMethod(methodName);
				returnString.append("" + methodName + " = " + methodValue + ";\n");
			}
			returnString.append("\n");
		}
		return returnString.toString();
	}

	public void addScriptSource(String jsString) {
		if (jsString != null) {
			Script js = new Script();
			js.setScriptSource(jsString);
			//DOCUMENT.WRITE is illegal in XHTML you must use DOM writing instead
			addFunction(jsString,
				"var l=document.createElement('script'); "+
				  "l.setAttribute('src', '"+jsString+"'); "+
				  "l.setAttribute('type', 'text/javascript'); "+
				  "document.getElementsByTagName('head')[0].appendChild(l); \n");
		}
	}

	public String getFunction(String functionName) {
		return getScriptCode().get(functionName);
	}

	@Override
	public void print(IWContext iwc) throws Exception {
		if (doPrint(iwc)) {
			if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
				/*try {
					com.idega.core.builder.data.ICDomain d = iwc.getDomain();
					String serverUrl = d.getURL();
					if (serverUrl != null) {
						String src = getMarkupAttribute("src");
						if (src != null && src.startsWith("/")) {
							String newSrc = serverUrl + src;
							setMarkupAttribute("src",newSrc);
						}
					}
				}
				catch (IDONoDatastoreError de) {
					// de.printStackTrace();
				}*/
				// if (getInterfaceStyle().equals("something")){
				// }
				// else{
				println("<script " + getMarkupAttributesString() + " >");
				println("<!--");
				if (!isMarkupAttributeSet(ATTRIBUTE_SOURCE)) {

					String lines = getScriptLines();
					if(lines!=null){
						print(lines);
					}
					print(getVariables());
					print(getMethods());
					print(getScriptCode(iwc));
				}
				println("//-->");
				println("</script>\n");
				// flush();
				// }
			}
			else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
				println("");
			}
		}
	}

	public void setFunction(String functionName, String functionCode) {
		addFunction(functionName, functionCode);
	}

	public void setVariable(String variableName, String variableValue) {
		addVariable(variableName, variableValue);
	}

	@Override
	public Object clone() {
		Script obj = null;
		try {
			obj = (Script) super.clone();
			obj.scriptType = this.scriptType;
			if (this.scriptCode != null) {
				obj.scriptCode = MapUtil.deepCopy(scriptCode);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.scriptType = (String) values[1];
		this.scriptCode = (Map<String, String>) values[2];
		this.variables = (Map<String, String>) values[3];
		this.methods = (Map<String, String>) values[4];
		this.scriptLines = (String)values[5];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[6];
		values[0] = super.saveState(context);
		values[1] = this.scriptType;
		values[2] = this.scriptCode;
		values[3] = this.variables;
		values[4] = this.methods;
		values[5] = this.scriptLines;
		return values;
	}

	@Override
	public void println(String str) {
		String convertedString = convertStringToUnicode(str);
		super.println(convertedString);
	}

	@Override
	public void print(String str) {
		String convertedString = convertStringToUnicode(str);
		super.print(convertedString);
	}

	private String convertStringToUnicode(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		char c;
		for (int i = 0; i < str.length(); ++i) {
			c = str.charAt(i);
			if (c >= 0x80) {
				//encode all non basic latin characters
				sb.append("\\u");
				String hexCode = Integer.toHexString(c);
				// be sure that the unicode is four digits long, that is
				// add zero digits at the beginning if necessary
				// the hexcode is already at least two digits long
				// because c >= 128
				int length = hexCode.length();
				// keep it simple....
				if (length == 2) {
					sb.append("00");
				}
				else if (length == 3) {
					sb.append('0');
				}
				sb.append(hexCode);
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public String getScriptLines(){
		return this.scriptLines;
	}

	/**
	 * <p>
	 * Adds a single script line to the source body of the script object, not within any function declaration.<br/>
	 * example: "document.myform.myinput.focus();".
	 * </p>
	 * @param singleScriptLine
	 */
	public void addScriptLine(String singleScriptLine) {
		if(scriptLines==null){
			scriptLines=singleScriptLine+"\n";
		}
		else{
			scriptLines=scriptLines+singleScriptLine+"\n";
		}
	}
}
