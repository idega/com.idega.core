//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import java.io.*;
import java.util.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Script extends PresentationObject{

private String scriptType;
private Hashtable scriptCode;
private Hashtable variables;
private Hashtable methods;

public Script(){
	this("javascript");
}

public Script(String scriptLanguage){
	super();
	setScriptLanguage(scriptLanguage);
        setType();
	scriptCode = new Hashtable();
}

/*public void setScriptType(String scriptType){
	setAttribute("language",scriptType);
}*/

protected void setType(){
  setType("text/javascript");
}

protected void setType(String type){
  setAttribute("type",type);
}

public void setScriptLanguage(String scriptLanguage){
	setAttribute("language",scriptLanguage);
}

public void setScriptSource(String sourceURL){
	setAttribute("src",sourceURL);
}

/*public void addToScriptCode(String code){
	this.scriptCode=this.scriptCode + "\n" + code;
}

public void setScriptCode(String code){
	this.scriptCode=code;
}

*/

public String getScriptCode(IWContext iwc){
	String returnString="";
	for (Enumeration e = scriptCode.keys(); e.hasMoreElements();){

		Object function=e.nextElement();
		String functionName = (String) function;
		String functionCode = (String) scriptCode.get(function);

		returnString = returnString + "\n\n" + functionCode;
	}

	return returnString;

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
	String returnString="";
	if ( variables != null ) {
		for (Enumeration e = variables.keys(); e.hasMoreElements(); ) {
			Object function=e.nextElement();
			String variableName = (String) function;
			String variableValue = (String) getVariable(variableName);

			if ( variableValue != null )
			      returnString = "var " + variableName + " = " + variableValue + ";\n\n";
			else
			      returnString = "var " + variableName + ";\n\n";
		}
	}

	return returnString;

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
	String returnString="";
	if ( methods != null ) {
		for (Enumeration e = methods.keys(); e.hasMoreElements(); ) {
			Object function=e.nextElement();
			String methodName = (String) function;
			String methodValue = (String) getMethod(methodName);

    			returnString = methodName + " = " + methodValue + ";\n\n";
		}
	}

	return returnString;

}

public String getFunction(String functionName){
	return (String) scriptCode.get(functionName);
}

public void print(IWContext iwc)throws Exception{
	initVariables(iwc);
	if (doPrint(iwc)){
		if (getLanguage().equals("HTML")){

			//if (getInterfaceStyle().equals("something")){
			//}
			//else{
				println("<script "+getAttributeString()+" >");
				println("<!--//");
				if (! isAttributeSet("src")){
					println(getVariables());
					println(getMethods());
          println(getScriptCode(iwc));
				}
				println("//-->");
				println("\n</script>");
				flush();
			//}
		}
		else if (getLanguage().equals("WML")){
			println("");
		}
	}
	else{
		super._print(iwc);
	}
}


  public synchronized Object clone() {
    Script obj = null;
    try {
      obj = (Script)super.clone();
      obj.scriptType = this.scriptType;
      if(this.scriptCode != null){
        obj.scriptCode = (Hashtable)this.scriptCode.clone();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }

}//End class
