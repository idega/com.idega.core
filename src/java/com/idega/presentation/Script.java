/*
 * $Id: Script.java,v 1.27 2006/01/03 20:39:27 tryggvil Exp $ 
 * Created in 2000 by Tryggvi Larusson
 * 
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import com.idega.data.IDONoDatastoreError;

/**
 * <p>
 * This class renders out a script element.<br>
 * An instance of this component can be used to define javascript functions and
 * add to a component or a page.
 * </p>
 * Last modified: $Date: 2006/01/03 20:39:27 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.27 $
 */
public class Script extends PresentationObject {

	private String scriptType;
	private Map scriptCode;
	private Hashtable variables;
	private Hashtable methods;

	public Script() {
		this("javascript");
	}

	public Script(String scriptLanguage) {
		super();
		setType();
		setTransient(false);
		// scriptCode = new LinkedHashMap();
	}

	/*
	 * public void setScriptType(String scriptType){
	 * setAttribute("language",scriptType); }
	 */
	protected Map getScriptCode() {
		if (scriptCode == null) {
			scriptCode = new LinkedHashMap();
		}
		return scriptCode;
	}

	protected void setType() {
		setType("text/javascript");
	}

	protected void setType(String type) {
		setMarkupAttribute("type", type);
	}

	public void setScriptSource(String sourceURL) {
		setMarkupAttribute("src", sourceURL);
	}

	/*
	 * public void addToScriptCode(String code){ this.scriptCode=this.scriptCode +
	 * "\n" + code; }
	 * 
	 * public void setScriptCode(String code){ this.scriptCode=code; }
	 * 
	 */
	public String getScriptCode(IWContext iwc) {
		StringBuffer returnString = new StringBuffer();
		Iterator iter = getScriptCode().keySet().iterator();
		while (iter.hasNext()) {
			Object function = iter.next();
			String functionCode = (String) getScriptCode().get(function);
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
			String functionCode = (String) getScriptCode().get(functionName);
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
			String functionCode = (String) getScriptCode().get(functionName);
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
		if (this.variables == null)
			variables = new Hashtable();
		variables.put(variableName, variableValue);
	}

	public void addVariable(String variableName) {
		addVariable(variableName, null);
	}

	public String getVariable(String variableName) {
		return (String) variables.get(variableName);
	}

	public String getVariables() {
		StringBuffer returnString = new StringBuffer();
		if (variables != null) {
			Enumeration e = variables.keys();
			while (e.hasMoreElements()) {
				Object function = e.nextElement();
				String variableName = (String) function;
				String variableValue = getVariable(variableName);
				if (variableValue != null)
					returnString.append("var " + variableName + " = " + variableValue + ";\n");
				else
					returnString.append("var " + variableName + ";\n");
			}
			returnString.append("\n");
		}
		return returnString.toString();
	}

	public void addMethod(String methodName, String methodValue) {
		if (this.methods == null)
			methods = new Hashtable();
		methods.put(methodName, methodValue);
	}

	public String getMethod(String methodName) {
		return (String) methods.get(methodName);
	}

	public String getMethods() {
		StringBuffer returnString = new StringBuffer();
		if (methods != null) {
			for (Enumeration e = methods.keys(); e.hasMoreElements();) {
				Object function = e.nextElement();
				String methodName = (String) function;
				String methodValue = getMethod(methodName);
				returnString.append("" + methodName + " = " + methodValue + ";\n");
			}
			returnString.append("\n");
		}
		return returnString.toString();
	}

	public void addScriptSource(String jsString) {
		if (jsString != null && jsString.endsWith(".js")) {
			Script js = new Script();
			js.setScriptSource(jsString);
			//DOCUMENT.WRITE is illegal in XHTML you must use DOM writing instead
			addFunction(jsString,
				"var l=document.createElement('script'); "+
				  "l.setAttribute('src', '"+jsString+"'); "+
				  "l.setAttribute('type', 'text/javascript'); "+
				  "document.getElementsByTagName('head')[0].appendChild(l); \n");
			
			
			// document.write("<scr"+"ipt
			// src=/js/curtain_menu/menumaker.jsp><"+"/script>")
		}
	}

	public String getFunction(String functionName) {
		return (String) getScriptCode().get(functionName);
	}

	public void print(IWContext iwc) throws Exception {
		if (doPrint(iwc)) {
			if (getMarkupLanguage().equals("HTML")) {
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
				if (!isMarkupAttributeSet("src")) {
					print(getVariables());
					print(getMethods());
					print(getScriptCode(iwc));
				}
				println("//-->");
				println("</script>\n");
				// flush();
				// }
			}
			else if (getMarkupLanguage().equals("WML")) {
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

	public Object clone() {
		Script obj = null;
		try {
			obj = (Script) super.clone();
			obj.scriptType = this.scriptType;
			if (this.scriptCode != null) {
				obj.scriptCode = (Map) ((LinkedHashMap) this.scriptCode).clone();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext,
	 *      java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.scriptType = (String) values[1];
		this.scriptCode = (Map) values[2];
		this.variables = (Hashtable) values[3];
		this.methods = (Hashtable) values[4];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[5];
		values[0] = super.saveState(context);
		values[1] = scriptType;
		values[2] = scriptCode;
		values[3] = variables;
		values[4] = methods;
		return values;
	}
	
	public void println(String str) {
		String convertedString = convertStringToUnicode(str);
		super.println(convertedString);
	}
	
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
}
