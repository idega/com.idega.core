package com.idega.presentation.ui;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Script;
import com.idega.presentation.PresentationObject;
import java.util.List;
import java.util.Vector;

/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class ResultOutput extends PresentationObjectContainer {

  String functionName = "resultOutputFunction";

  List moduleObjects = new Vector();
  List onChangeVector = new Vector();
  List extraTextVector = new Vector();
  List operatorVector = new Vector();

  public static final String OPERATOR_PLUS = "+";
  public static final String OPERATOR_SUBTRACT = "-";
  public static final String OPERATOR_MULTIPLY = "*";
  public static final String OPERATOR_DIVIDE = "/";

  private int size = -1;
  private String content;
  private String name;

  private String extraForTotal = "";
  private String extraForEach = "";


  public ResultOutput() {
    this("unspecified","");
  }

  public ResultOutput(String name) {
    this(name, "");
  }

  public ResultOutput(String name, String content) {
    this.functionName = name;
    this.name = name + "_RO_input";
    this.content = content;
  }

  public void _main(IWContext iwc) {
      Script script = getParentPage().getAssociatedScript();

      TextInput resultOutput = new TextInput(name, content);

      PresentationObject moduleObject = null;
      String extraTxt = "";

      if (moduleObjects.size() > 0) {
        StringBuffer theScript = new StringBuffer();
          theScript.append("function "+functionName+"(myForm) {");
          theScript.append("\n  myForm."+resultOutput.getName()+".value=(");
          for (int i = 0; i < moduleObjects.size(); i++) {
              if (i != 0) theScript.append((String) operatorVector.get(i));
              if (moduleObjects.get(i) instanceof TextInput)
                moduleObject = (TextInput) moduleObjects.get(i);
              else if (moduleObjects.get(i) instanceof ResultOutput)
                moduleObject = (ResultOutput) moduleObjects.get(i);
              extraTxt = (String) extraTextVector.get(i);
              theScript.append("(1*myForm."+moduleObject.getName()+".value");
              theScript.append(")");
              theScript.append(extraForEach);
              theScript.append(extraTxt);
          }
          theScript.append(")");
          theScript.append(extraForTotal);
          theScript.append(";");


          theScript.append("\n}");

        script.addFunction(functionName, theScript.toString());
      }

      resultOutput.setDisabled(true);
      if (this.size > 0) resultOutput.setSize(size);
      for (int i = 0; i < onChangeVector.size(); i++) {
        resultOutput.setOnChange((String ) onChangeVector.get(i) );
      }

      super.add(resultOutput);
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setOnChange(String s) {
      onChangeVector.add(s);
  }

  public List getAddedObjects() {
    return this.moduleObjects;
  }


  public String getName() {
    return this.name;
  }

  public void setExtraForEach(String s) {
    this.extraForEach = s;
  }

  public void setExtraForTotal(String s) {
    this.extraForTotal = s;
  }

  public void add(PresentationObject mo) {
    add(mo, "");
  }

  public void add(PresentationObject mo, String extraText) {
    add(mo, OPERATOR_PLUS, extraText);
  }

  public void add(PresentationObject mo, String operatori, String extraText) {
    if (mo instanceof TextInput) {
      TextInput temp = (TextInput) mo;
        temp.setOnChange(functionName+"(this.form)");
        moduleObjects.add(temp);
        operatorVector.add(operatori);
        if (extraText == null) extraText = "";
        extraTextVector.add(extraText);
    }
    else if (mo instanceof ResultOutput) {
      handleAddResultOutput((ResultOutput) mo, operatori);
    }
  }


  private void handleAddResultOutput(ResultOutput resOut, String operatori) {
      List list = resOut.getAddedObjects();
      for ( int a = 0; a < list.size(); a++ ) {
        if (list.get(a) instanceof TextInput) {
          TextInput text = (TextInput) list.get(a);
            text.setOnChange(functionName+"(this.form)");
        }else if (list.get(a) instanceof  ResultOutput) {
            handleAddResultOutput((ResultOutput) list.get(a), operatori);
        }
      }
      moduleObjects.add(resOut);
      operatorVector.add(operatori);
  }

  public void setContent(String content) {
    this.content = content;
  }
}