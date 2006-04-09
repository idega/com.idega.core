package com.idega.presentation.ui;

import java.util.ArrayList;
import java.util.Collection;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Script;
import com.idega.presentation.text.Link;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class AbstractChooserWindow extends IWAdminWindow {

  String chooserSelectionParameter=AbstractChooser.VALUE_PARAMETER_NAME;
  public static String SELECT_FUNCTION_NAME = "chooserSelect";


  protected static final String DISPLAYSTRING_PARAMETER_NAME = AbstractChooser.DISPLAYSTRING_PARAMETER_NAME;
  protected static final String VALUE_PARAMETER_NAME = AbstractChooser.VALUE_PARAMETER_NAME;
  protected static final String FORM_ID_PARAMETER = AbstractChooser.FORM_ID_PARAMETER;
  protected static final String SCRIPT_SUFFIX_PARAMETER = AbstractChooser.SCRIPT_SUFFIX_PARAMETER;

  protected static final String SCRIPT_PREFIX_IN_A_FRAME = "top.";

  private boolean isInAFrame = false;
  private boolean onlyScript = false;
  private boolean noScript = false;


  public AbstractChooserWindow(){
  }

  public AbstractChooserWindow(boolean isInAFrame){
    this.isInAFrame(isInAFrame);
  }


  public void main(IWContext iwc){

    if( (!this.noScript) && (getSelectionParameter(iwc)!=null) ){
      Page parent = this.getParentPage();
      parent = (parent!=null) ? parent : this;
      
            
      Script script = parent.getAssociatedScript();
      
      
      String prefix = iwc.getParameter(FORM_ID_PARAMETER);
      String suffix = iwc.getParameter(SCRIPT_SUFFIX_PARAMETER);

      String displayString = iwc.getParameter(DISPLAYSTRING_PARAMETER_NAME);
      String valueString = iwc.getParameter(VALUE_PARAMETER_NAME);

      if( prefix == null ) {
				prefix = "";
			}
      if( suffix == null ) {
				suffix = "";
			}
      if( displayString == null ) {
				displayString = "";
			}
      if( valueString == null ) {
				valueString = "";
			}

      if( !this.onlyScript ){
        HiddenInput hPrefix = new HiddenInput(FORM_ID_PARAMETER,prefix);
        HiddenInput hSuffix = new HiddenInput(SCRIPT_SUFFIX_PARAMETER,suffix);
        HiddenInput hDisplayString = new HiddenInput(DISPLAYSTRING_PARAMETER_NAME,displayString);
        HiddenInput hValueString = new HiddenInput(VALUE_PARAMETER_NAME,valueString);

        add(hPrefix);
        add(hSuffix);
        add(hDisplayString);
        add(hValueString);
      }

      //script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){ "+AbstractChooser.DISPLAYSTRING_PARAMETER_NAME+".value=displaystring;"+AbstractChooser.VALUE_PARAMETER_NAME+".value=value;window.close();return false }");
      if( this.isInAFrame ){
        script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){ "+SCRIPT_PREFIX_IN_A_FRAME+"window.opener.document.getElementById(\""+prefix+"\")."+displayString+"."+suffix+"=displaystring; "+SCRIPT_PREFIX_IN_A_FRAME+"window.opener.document.getElementById(\""+prefix+"\")."+valueString+".value=value;window.close();return false;}");
      }
      else{
        script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){ window.opener.document.getElementById(\""+prefix+"\")."+displayString+"."+suffix+"=displaystring;window.opener.document.getElementById(\""+prefix+"\")."+valueString+".value=value;window.close();return false;}");
      }
    }
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
    return iwc.getParameter(this.chooserSelectionParameter);
  }

  public void isInAFrame(boolean isInAFrame){
    this.isInAFrame = isInAFrame;
  }

  public void setOnlyScript(boolean onlyScript){
    this.onlyScript = onlyScript;
  }

  public void setNoScript(boolean noScript){
    this.noScript = noScript;
  }

  public Collection getHiddenParameters(IWContext iwc) {
    String prefix = iwc.getParameter(FORM_ID_PARAMETER);
    String suffix = iwc.getParameter(SCRIPT_SUFFIX_PARAMETER);
    String displayString = iwc.getParameter(DISPLAYSTRING_PARAMETER_NAME);
    String valueString = iwc.getParameter(VALUE_PARAMETER_NAME);

    Collection coll = new ArrayList();

    coll.add(new Parameter(FORM_ID_PARAMETER,prefix));
    coll.add(new Parameter(SCRIPT_SUFFIX_PARAMETER,suffix));
    coll.add(new Parameter(DISPLAYSTRING_PARAMETER_NAME,displayString));
    coll.add(new Parameter(VALUE_PARAMETER_NAME,valueString));
    return coll;
  }
  
  protected void maintainParameter(IWContext iwc, Link linkToMaintainParameters) {
	  linkToMaintainParameters.maintainParameter(FORM_ID_PARAMETER, iwc);
	  linkToMaintainParameters.maintainParameter(SCRIPT_SUFFIX_PARAMETER, iwc);
	  linkToMaintainParameters.maintainParameter(DISPLAYSTRING_PARAMETER_NAME, iwc);
	  linkToMaintainParameters.maintainParameter(VALUE_PARAMETER_NAME, iwc);
  }
}
