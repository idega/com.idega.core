//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Script extends PresentationObject{

private String scriptType;
private LinkedHashMap scriptCode;
private Hashtable variables;
private Hashtable methods;

public Script(){
	this("javascript");
}

public Script(String scriptLanguage){
	super();
	setType();
	scriptCode = new LinkedHashMap();
}

/*public void setScriptType(String scriptType){
	setAttribute("language",scriptType);
}*/

protected void setType(){
  setType("text/javascript");
}

protected void setType(String type){
  setMarkupAttribute("type",type);
}

public void setScriptSource(String sourceURL){
	setMarkupAttribute("src",sourceURL);
}

/*public void addToScriptCode(String code){
	this.scriptCode=this.scriptCode + "\n" + code;
}

public void setScriptCode(String code){
	this.scriptCode=code;
}

*/

public String getScriptCode(IWContext iwc){
	StringBuffer returnString = new StringBuffer();
	Iterator iter = scriptCode.keySet().iterator();
	
	while (iter.hasNext()) {
		Object function=iter.next();
		String functionCode = (String) scriptCode.get(function);

		returnString.append(functionCode + "\n");
	}

	return returnString.toString();

}

public boolean doesFunctionExist(String function){
  if(scriptCode.get(function)==null){
      return false;
  }
  else{
      return true;
  }
}

public void removeFunction(String functionName){
	scriptCode.remove(functionName);
}

public void addToFunction(String functionName,String scriptString){

	if (scriptCode != null){
		String functionCode = (String) scriptCode.get(functionName);

		if ( functionCode != null){

			String beginString;
			String endString;
			String returnString;

			int lastbracket;
			lastbracket = functionCode.lastIndexOf("}");

			beginString = functionCode.substring(0,lastbracket);
			endString = "}";

			returnString = beginString + "\n" + scriptString + "\n" + endString;

			scriptCode.put(functionName,returnString);
		}
	}
}

public void addToBeginningOfFunction(String functionName,String scriptString){

	if (scriptCode != null){
		String functionCode = (String) scriptCode.get(functionName);

		if ( functionCode != null){

			String beginString;
			String endString;
			String returnString;

			int firstBracket;
			firstBracket = functionCode.indexOf("{") + 1;

			beginString = functionCode.substring(0,firstBracket);
			endString = functionCode.substring(firstBracket + 1);

			returnString = beginString + "\n" + scriptString + "\n" + endString;

			scriptCode.put(functionName,returnString);
		}
	}
}


public void addFunction(String functionName,String scriptString){
	scriptCode.put(functionName,scriptString);
}

public void addVariable(String variableName, String variableValue){
	if ( this.variables == null )
    variables = new Hashtable();

  variables.put(variableName,variableValue);
}

public void addVariable(String variableName){
  addVariable(variableName,null);
}

public String getVariable(String variableName) {
  return (String) variables.get(variableName);
}

public String getVariables(){
	StringBuffer returnString = new StringBuffer();
	if ( variables != null ) {
		Enumeration e = variables.keys();
		while ( e.hasMoreElements() ) {
			Object function=e.nextElement();
			String variableName = (String) function;
			String variableValue = (String) getVariable(variableName);

			if ( variableValue != null )
			      returnString.append("var " + variableName + " = " + variableValue + ";\n");
			else
			      returnString.append("var " + variableName + ";\n");
		}
		returnString.append("\n");
	}

	return returnString.toString();

}

public void addMethod(String methodName, String methodValue){
	if ( this.methods == null )
    methods = new Hashtable();

  methods.put(methodName,methodValue);
}

public String getMethod(String methodName) {
  return (String) methods.get(methodName);
}

public String getMethods(){
	StringBuffer returnString = new StringBuffer();
	if ( methods != null ) {
		for (Enumeration e = methods.keys(); e.hasMoreElements(); ) {
			Object function=e.nextElement();
			String methodName = (String) function;
			String methodValue = (String) getMethod(methodName);

			returnString.append("" + methodName + " = " + methodValue + ";\n");
		}
		returnString.append("\n");
	}

	return returnString.toString();

}

public void addScriptSource(String jsString){
  if(jsString!=null && jsString.endsWith(".js")){
    Script js = new Script();
    js.setScriptSource(jsString);
    addFunction(jsString.substring(0,jsString.indexOf(".js")),"\tif(document){\n\t\t document.write(\"<script src="+jsString+" > </script>\")\n\t}");
    //document.write("<scr"+"ipt src=/js/curtain_menu/menumaker.jsp><"+"/script>")
  }
}

public String getFunction(String functionName){
	return (String) scriptCode.get(functionName);
}

public void print(IWContext iwc)throws Exception{
  if (doPrint(iwc)){
    if (getMarkupLanguage().equals("HTML")){
      com.idega.core.builder.data.ICDomain d = iwc.getDomain();

      if (d.getURL() != null) {
	String src = getMarkupAttribute("src");
	if (src != null && src.startsWith("/")) {
	  setMarkupAttribute("src",d.getURL()+src);
	}
      }

			//if (getInterfaceStyle().equals("something")){
			//}
			//else{
				println("<script "+getMarkupAttributesString()+" >");
				println("<!--//");
				if (! isMarkupAttributeSet("src")){
					print(getVariables());
					print(getMethods());
	  				print(getScriptCode(iwc));
				}
				println("//-->");
				println("</script>\n");
				//flush();
			//}
      }
      else if (getMarkupLanguage().equals("WML")){
			println("");
      }
    }

}

  public void setFunction(String functionName,String functionCode) {
    addFunction(functionName,functionCode);
  }

  public void setVariable(String variableName,String variableValue) {
    addVariable(variableName,variableValue);
  }

  public Object clone() {
    Script obj = null;
    try {
      obj = (Script)super.clone();
      obj.scriptType = this.scriptType;
      if(this.scriptCode != null){
      	obj.scriptCode = (LinkedHashMap)this.scriptCode.clone();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }

}//End class
