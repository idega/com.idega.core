package com.idega.presentation.ui;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.Script;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class AbstractChooserWindow extends Window {

  String chooserSelectionParameter;
  protected static String SELECT_FUNCTION_NAME = "chooserSelect";


  protected static final String DISPLAYSTRING_PARAMETER_NAME = AbstractChooser.DISPLAYSTRING_PARAMETER_NAME;
  protected static final String VALUE_PARAMETER_NAME = AbstractChooser.VALUE_PARAMETER_NAME;
  protected static final String SCRIPT_PREFIX_PARAMETER = AbstractChooser.SCRIPT_PREFIX_PARAMETER;
  protected static final String SCRIPT_SUFFIX_PARAMETER = AbstractChooser.SCRIPT_SUFFIX_PARAMETER;

  public AbstractChooserWindow(){
  }


  public void main(IWContext iwc){
    Script script = this.getAssociatedScript();
    String prefix = iwc.getParameter(SCRIPT_PREFIX_PARAMETER);
    String suffix = iwc.getParameter(SCRIPT_SUFFIX_PARAMETER);
    String displayString = iwc.getParameter(DISPLAYSTRING_PARAMETER_NAME);

    String valueString = iwc.getParameter(VALUE_PARAMETER_NAME);

    //script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){ "+AbstractChooser.DISPLAYSTRING_PARAMETER_NAME+".value=displaystring;"+AbstractChooser.VALUE_PARAMETER_NAME+".value=value;window.close();return false }");
    script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){"+prefix+displayString+"."+suffix+"=displaystring;"+prefix+valueString+".value=value;window.close();return false;}");

    displaySelection(iwc);
  }

  public abstract void displaySelection(IWContext iwc);


  public String getOnSelectionCode(String displayString){
    return getOnSelectionCode(displayString,displayString);
  }

  public String getOnSelectionCode(String displayString,String value){
    return SELECT_FUNCTION_NAME+"("+displayString+","+value+")";
  }


  public String getSelectionParameter(IWContext iwc){
    return iwc.getParameter(chooserSelectionParameter);
  }

}