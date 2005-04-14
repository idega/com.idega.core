/*
 * Created on Nov 12, 2003
 */
package com.idega.presentation.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.idega.idegaweb.presentation.StyledIWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Script;

/**
 * Description: <br>
 * Copyright: Idega Software 2003 <br>
 * Company: Idega Software <br>
 * @author <a href="mailto:birna@idega.is">Birna Iris Jonsdottir</a>
 */
public abstract class StyledAbstractChooserWindow extends StyledIWAdminWindow{
	
	String chooserSelectionParameter=AbstractChooser.VALUE_PARAMETER_NAME;
	public static String SELECT_FUNCTION_NAME = "chooserSelect";
	public static String PERFORM_AFTER_SELECT_FUNCTION_NAME = "performAfterSelect";


	protected static final String DISPLAYSTRING_PARAMETER_NAME = AbstractChooser.DISPLAYSTRING_PARAMETER_NAME;
	protected static final String VALUE_PARAMETER_NAME = AbstractChooser.VALUE_PARAMETER_NAME;
	protected static final String SCRIPT_PREFIX_PARAMETER = AbstractChooser.FORM_ID_PARAMETER;
	protected static final String SCRIPT_SUFFIX_PARAMETER = AbstractChooser.SCRIPT_SUFFIX_PARAMETER;

	protected static final String SCRIPT_PREFIX_IN_A_FRAME = "top.";

	private boolean isInAFrame = false;
	private boolean onlyScript = false;
	private boolean noScript = false;


	public StyledAbstractChooserWindow(){
	}

	public StyledAbstractChooserWindow(boolean isInAFrame){
		this.isInAFrame(isInAFrame);
	}


	public void main(IWContext iwc){

		if( (!noScript) && (getSelectionParameter(iwc)!=null) ){
			Page parent = this.getParentPage();
			parent = (parent!=null) ? parent : this;
      
            
			Script script = parent.getAssociatedScript();
      
      
			String prefix = iwc.getParameter(SCRIPT_PREFIX_PARAMETER);
			String suffix = iwc.getParameter(SCRIPT_SUFFIX_PARAMETER);

			String displayString = iwc.getParameter(DISPLAYSTRING_PARAMETER_NAME);
			String valueString = iwc.getParameter(VALUE_PARAMETER_NAME);

			if( prefix == null ) prefix = "";
			if( suffix == null ) suffix = "";
			if( displayString == null ) displayString = "";
			if( valueString == null ) valueString = "";

			if( !onlyScript ){
				HiddenInput hPrefix = new HiddenInput(SCRIPT_PREFIX_PARAMETER,prefix);
				HiddenInput hSuffix = new HiddenInput(SCRIPT_SUFFIX_PARAMETER,suffix);
				HiddenInput hDisplayString = new HiddenInput(DISPLAYSTRING_PARAMETER_NAME,displayString);
				HiddenInput hValueString = new HiddenInput(VALUE_PARAMETER_NAME,valueString);

				add(hPrefix);
				add(hSuffix);
				add(hDisplayString);
				add(hValueString);
			}

			//closes the window and can be overridden
			script.addFunction(PERFORM_AFTER_SELECT_FUNCTION_NAME, "function "+PERFORM_AFTER_SELECT_FUNCTION_NAME+"()"+"{"+getPerformAfterSelectScriptString(iwc)+"}");
			
			//script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){ "+AbstractChooser.DISPLAYSTRING_PARAMETER_NAME+".value=displaystring;"+AbstractChooser.VALUE_PARAMETER_NAME+".value=value;window.close();return false }");
			if( isInAFrame ){
				script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){ "+SCRIPT_PREFIX_IN_A_FRAME+ prefix+displayString+"."+suffix+"=displaystring; "+SCRIPT_PREFIX_IN_A_FRAME+prefix+valueString+".value=value;\n "+PERFORM_AFTER_SELECT_FUNCTION_NAME+"(); }");
			}
			else{
				script.addFunction(SELECT_FUNCTION_NAME,"function "+SELECT_FUNCTION_NAME+"(displaystring,value){ "+prefix+displayString+"."+suffix+"=displaystring;"+prefix+valueString+".value=value;\n "+PERFORM_AFTER_SELECT_FUNCTION_NAME+"(); }");
			}
			
			
			
		}
		displaySelection(iwc);
	}

	/**
	 * Override this method to do something in javascript after the chooser value has been set.
	 * The default behaviour is to close the window and return false
	 * @param iwc
	 * @return
	 */
	protected String getPerformAfterSelectScriptString(IWContext iwc) {
		return "window.close();return false;";
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
		String prefix = iwc.getParameter(SCRIPT_PREFIX_PARAMETER);
		String suffix = iwc.getParameter(SCRIPT_SUFFIX_PARAMETER);
		String displayString = iwc.getParameter(DISPLAYSTRING_PARAMETER_NAME);
		String valueString = iwc.getParameter(VALUE_PARAMETER_NAME);

		Collection coll = new ArrayList();

		coll.add(new Parameter(SCRIPT_PREFIX_PARAMETER,prefix));
		coll.add(new Parameter(SCRIPT_SUFFIX_PARAMETER,suffix));
		coll.add(new Parameter(DISPLAYSTRING_PARAMETER_NAME,displayString));
		coll.add(new Parameter(VALUE_PARAMETER_NAME,valueString));
		return coll;
	}

}




