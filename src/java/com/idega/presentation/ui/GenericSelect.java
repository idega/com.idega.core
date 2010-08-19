package com.idega.presentation.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.data.IDOEntity;
import com.idega.presentation.IWContext;
import com.idega.presentation.Script;
import com.idega.util.text.TextSoap;

/**
 * @author laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GenericSelect extends InterfaceObject {

	private boolean _isSetToSubmit;
	private boolean _isSetToDisable;
	private boolean _isSetAsNotEmpty;
	private String _notEmptyErrorMessage;
	private String _emptyValue;

	private List selectedElements;
	private boolean _allSelected = false;
	private boolean _isMultiple = false;
	
	private boolean addSelectScript = false;
	
	public static final String SET_TO_SUBMIT_PROPERTY = "setToSubmit";
	public static final String OPTIONS_PROPERTY = "options";
	public static final String ENTITIES_PROPERTY = "entities";
	public static final String OBJECTS_PROPERTY = "objects";
	public static final String SELECTED_PROPERTY = "selected";
	
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[10];
		values[0] = super.saveState(ctx);
		values[1] = new Boolean(this._isSetToSubmit);
		values[2] = new Boolean(this._isSetToDisable);
		values[3] = new Boolean(this._isSetAsNotEmpty);
		values[4] = this._notEmptyErrorMessage;
		values[5] = this._emptyValue;
		values[6] = this.selectedElements;
		values[7] = new Boolean(this._allSelected);
		values[8] = new Boolean(this._isMultiple);
		values[9] = new Boolean(this.addSelectScript);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this._isSetToSubmit = ((Boolean) values[1]).booleanValue();
		this._isSetToDisable = ((Boolean) values[2]).booleanValue();
		this._isSetAsNotEmpty = ((Boolean) values[3]).booleanValue();
		this._notEmptyErrorMessage = (String) values[4];
		this._emptyValue = (String) values[5];
		this.selectedElements = (List) values[6];
		this._allSelected = ((Boolean) values[7]).booleanValue();
		this._isMultiple = ((Boolean) values[8]).booleanValue();
		this.addSelectScript = ((Boolean) values[9]).booleanValue();
	}
	
    @Override
	public void encodeBegin(FacesContext context) throws IOException { 
    	ValueExpression ve = getValueExpression(SET_TO_SUBMIT_PROPERTY);
    	if (ve != null) {
	    	boolean setToSubmit = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setToSubmit(setToSubmit);
    	}
    	
		ve = getValueExpression(SELECTED_PROPERTY);
    	if (ve != null) {
	    	String selected = (String) ve.getValue(context.getELContext());
    		setSelectedOption(selected);
    	}    
    	
		ve = getValueExpression(OPTIONS_PROPERTY);
    	if (ve != null) {
    		List<SelectOption> options = (List<SelectOption>) ve.getValue(context.getELContext());
    		setSelectOptions(options);
    	}    
    	
		ve = getValueExpression(ENTITIES_PROPERTY);
    	if (ve != null) {
    		List<IDOEntity> entities = (List<IDOEntity>) ve.getValue(context.getELContext());
    		setEntities(entities);
    	}    
    	
		ve = getValueExpression(OBJECTS_PROPERTY);
    	if (ve != null) {
    		List<Object> objects = (List<Object>) ve.getValue(context.getELContext());
    		if (objects != null) {
	    		for (Object object : objects) {
					addOption(new SelectOption(object != null ? object.toString() : ""));
				}
    		}
    	}    
    	
    	super.encodeBegin(context);
    }
    
	private String setSelectedOption() {
		String val = null;
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Object optionObj = iter.next();
			if(optionObj instanceof SelectOption) {
				SelectOption option = (SelectOption) optionObj;
				boolean setSelected = ((this._allSelected) || this.selectedElements.contains(option.getValueAsString()) || this.selectedElements.contains(option.getName(false)));
				option.setSelected(setSelected);
				if(setSelected){
					val = option.getValueAsString();
				}
			}
			else if (optionObj instanceof UIComponent) {
				UIComponent comp = (UIComponent) optionObj;
				List<UIComponent> list = comp.getChildren();
				for (UIComponent uiComponent : list) {
					if (uiComponent instanceof SelectOption) {
						SelectOption option = (SelectOption) uiComponent;
						boolean setSelected = ((this._allSelected) || this.selectedElements.contains(option.getValueAsString()) || this.selectedElements.contains(option.getName(false)));
						option.setSelected(setSelected);
						if(setSelected){
							val = option.getValueAsString();
						}
					}
				}
			}
		}

		return val;
	}
	
	/**
	 * Creates a new <code>GenericSelect</code> with the name "undefined".
	 */
	public GenericSelect() {
		this("undefined");
	}

	/**
	 * Creates a new <code>GenericSelect</code> with the given name.
	 * @param name	The name of the <code>GenericSelect</code> object.
	 */
	public GenericSelect(String name) {
		setName(name);
		setTransient(false);
		this.selectedElements = new ArrayList();
	}

	/**
	 * Removes all <code>SelectOption</code> objects from the select object.
	 */
	public void removeElements() {
		this.getChildren().clear();
		if(this.selectedElements!=null){
			this.selectedElements.clear();
		}
	}

	/**
	 * Sets the select to submit automatically.
	 * Must add to a form before this function is used!!!!
	 */
	public void setToSubmit() {
		setToSubmit(true);
	}
	
	/**
	 * Returns whether this <code>GenericSelect<code> has any <code>SelectOption</code> objects added.
	 */
	public boolean isEmpty() {
		return getChildren().isEmpty();
	}

	/**
	 * Sets the select to submit automatically.
	 * Must add to a form before this function is used!!!!
	 */
	public void setToSubmit(boolean setToSubmit) {
		if (setToSubmit) {
			setOnChange("this.form.submit()");
		}
		else {
			removeMarkupAttribute("onChange");
		}
	}

	/**
	 * Sets the select to submit when the specified option is seleced.
	 * Must add to a form before this function is used!!!!
	 */
	public void setToSubmit(String optionValue) {
		this._isSetToSubmit = true;
		setOnChange("submitWhenSelected(this,'"+optionValue+"')");
	}

	/**
	 * Adds a <code>SelectOption</code> to the select object.
	 * @param option	The <code>SelectOption</code> to add.
	 */
	public void addOption(SelectOption option) {
		if (!getChildren().contains(option)) {
			add(option);
		}
		if (option.getSelected()) {
			setSelectedOption(option.getValueAsString());
		}
	}

	/**
	 * Adds a <code>SelectOption</code> to the select object.
	 * @param option	The <code>SelectOption</code> to add.
	 */
	public void setSelectOption(SelectOption option) {
		addOption(option);
	}
	
	public void setSelectOptions(List<SelectOption> options) {
		for (SelectOption selectOption : options) {
			addOption(selectOption);
		}
	}
	
	public void setEntities(List<IDOEntity> entities) {
		for (IDOEntity entity : entities) {
			addOption(new SelectOption(entity.toString(), entity.getPrimaryKey().toString()));
		}
	}

	/**
	 * Adds a <code>SelectOption</code> to the select object as the first option.
	 * @param option	The <code>SelectOption</code> to add.
	 */
	public void addFirstOption(SelectOption option) {
		if (!getChildren().contains(option)) {
			add(0, option);
		}
		if (option.getSelected()) {
			setSelectedOption(option.getValueAsString());
		}
	}
	
	/**
	 * Adds a <code>SelectOption</code> to the select object as the first option.
	 * @param option	The <code>SelectOption</code> to add.
	 */
	public void setFirstSelectOption(SelectOption option) {
		addFirstOption(option);
	}
	
	/**
	 * @deprecated Use getOptionCount() instead.
	 * Returns the number of <code>SelectOption</code> objects added to this <code>GenericSelect</code>.
	 * @return
	 */
	public int getOptionCount() {
		return getChildren().size();
	}

	/**
	 * Adds a disabled <code>SelectOption</code> to the select object.
	 * @param option	The disabled <code>SelectOption</code> to add.
	 */
	public void addDisabledOption(SelectOption option) {
		option.setDisabled(true);
		if (!getChildren().contains(option)) {
			add(option);
		}
		if (option.getSelected()) {
			setSelectedOption(option.getValueAsString());
		}
	}

	/**
	 * Adds a disabled <code>SelectOption</code> to the select object.
	 * @param option	The disabled <code>SelectOption</code> to add.
	 */
	public void setDisabledSelectOption(SelectOption option) {
		addDisabledOption(option);
	}

	/**
	 * Adds a separator into the select object.
	 */
	public void addSeparator() {
		SelectOption option = new SelectOption("----------------------------", "separator");
		option.setDisabled(true);
		addOption(option);
	}

	/**
	 * Sets all <code>SelectOption</code> object in the select object as not selected.
	 */
	protected void deselectOptions() {
		this.selectedElements.clear();
	}

	/**
	 * Gets the value of the selected <code>SelectOption</code>.
	 * @return String	The value of the <code>SelectOption</code> in the select object.
	 */
	public String getSelectedValue() {
		Iterator iter = this.selectedElements.iterator();
		while (iter.hasNext()) {
			return (String) iter.next();
		}
		return "";
	}

	/**
	 * Sets the <code>SelectOption</code> with the given value as selected.  If the select object
	 * allows multiple values this selected value is added to existing selected values.
	 * @param value	The value of the <code>SelectOption</code> to set as selected.
	 */
	public void setSelectedOption(String value) {
		if (!getMultiple()) {
			deselectOptions();
		}
		this.selectedElements.add(value);
	}

	/**
	 * Gets the <code>SelectOption</code> with the given value.
	 * @param value	The value of the <code>SelectOption</code>.
	 * @return SelectOption
	 */
	public SelectOption getOption(String value) {
		SelectOption theReturn = new SelectOption();
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			SelectOption element = (SelectOption) iter.next();
			if (element.getValueAsString().equals(value)) {
				return element;
			}
		}
		return theReturn;
	}

	/**
	 * Sets the name of the <code>SelectOption</code> with the given value.
	 * @param value	The value of the <code>SelectOption</code>
	 * @param name	The new name of the <code>SelectOption</code>.
	 */
	public void setOptionName(String value, String name) {
		SelectOption option = getOption(value);
		option.setName(name);
	}

	/**
	 * Sets this select object to allow multiple selections.
	 * @param multiple	True to allow multiple, false otherwise.
	 */
	protected void setMultiple(boolean multiple) {
		if (multiple) {
			setMarkupAttributeWithoutValue("multiple");
		}
		else {
			removeMarkupAttribute("multiple");
		}
	}

	/**
	 * Returns if this select object is set to allow multiple selections.
	 * @return True if allows multiple, false otherwise.
	 */
	protected boolean getMultiple() {
		if (isMarkupAttributeSet("multiple")) {
			return true;
		}
		return false;
	}

	public void main(IWContext iwc) throws Exception {
		if (this._isSetAsNotEmpty) {
			setOnSubmitFunction("warnIfDropdownEmpty", "function warnIfDropdownEmpty (inputbox,warnMsg,emptyValue) {\n\n		if ( inputbox.options[inputbox.selectedIndex].value == emptyValue ) { \n		alert ( warnMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}", this._notEmptyErrorMessage, this._emptyValue);
		}
		if (_isSetToDisable && getScript() != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("function disableObjectByDropdown (dropdown,inputs,value,selectedValue) {\n	if (dropdown.options[dropdown.selectedIndex].value == eval(selectedValue)) {\n \tif (inputs.length > 1) {\n	\t\tfor(var i=0;i<inputs.length;i++)\n	\t\t\tinputs[i].disabled=eval(value);\n	\t\t}\n	\t\tinputs.disabled=eval(value);\n}\n");
			if (!this._isMultiple) {
				buffer.append("else {\n\tif (inputs.length > 1) {\n	\t\tfor(var i=0;i<inputs.length;i++)\n	\t\t\tinputs[i].disabled=!eval(value);\n	\t\t}\n	\t\tinputs.disabled=!eval(value);\n}\n");
			}
			buffer.append("}");
			getScript().addFunction("disableObjectByDropdown", buffer.toString());
		}
		if (this._isSetToSubmit) {
			getScript().addFunction("submitWhenSelected", "function submitWhenSelected (dropdown,selectedValue) {\n\tif (dropdown.options[dropdown.selectedIndex].value == eval(selectedValue))\n\t\tdropdown.form.submit();\n}");
		}
	}

	/**
	 * @see com.idega.presentation.PresentationObject#print(IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if (this.addSelectScript) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("function navHandler(input) {").append("\n\t");
			buffer.append("var URL = input.options[input.selectedIndex].value;").append("\n\t");
			buffer.append("if (URL.length > 0) {").append("\n\t\t");
			buffer.append("var index = -1;").append("\n\t\t");
			buffer.append("var targetIndex = -1;").append("\n\t\t");
			buffer.append("for (var a = 0; a < URL.length; a++) {").append("\n\t\t\t");
			buffer.append("if (URL.charAt(a) == '$') {").append("\n\t\t\t\t");
			buffer.append("if (index == -1) {").append("\n\t\t\t\t\t");
			buffer.append("index = a;").append("\n\t\t\t\t");
			buffer.append("}").append("\n\t\t\t\t");
			buffer.append("else {").append("\n\t\t\t\t\t");
			buffer.append("targetIndex = a;").append("\n\t\t\t\t");
			buffer.append("}").append("\n\t\t\t");
			buffer.append("}").append("\n\t\t");
			buffer.append("}").append("\n\t\t");
			buffer.append("window.open(URL.substring(0, index), URL.substring(targetIndex+1, URL.length), URL.substring(index+1, targetIndex));").append("\n\t");
			buffer.append("}").append("\n\t");
			buffer.append("var option = input.options[0];").append("\n\t");
			buffer.append("option.selected = true;").append("\n");
			buffer.append("}");
			
			Script script = new Script();//getScript();
			//if (script == null) {
			//	script = new Script();
			//	this.getParentPage().add(script);
			//}
			script.addFunction("navHandler", buffer.toString());
			renderChild(iwc, script);
			setOnChange("navHandler(this)");
		}
		
		String val = setSelectedOption();

		if (getMarkupLanguage().equals("HTML")) {
			println("<select name=\"" + getName() + "\" " + getMarkupAttributesString() + " >");
			super.print(iwc);
			print("</select>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			print("<select name=\"" + getName() + "\" ");
			if(val!=null){
				print(" value=\"" + val + "\" ");
			}
			println(getMarkupAttributesString() + " >");
			super.print(iwc);
			println("</select>");
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		GenericSelect obj = null;
		try {
			obj = (GenericSelect) super.clone();
			if (this.selectedElements != null) {
				obj.selectedElements = (List) ((ArrayList) this.selectedElements).clone();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (this._isMultiple) {
			if (iwc.isParameterSet(getName())) {
				String[] values = iwc.getParameterValues(getName());
				if (values != null) {
					for (int a = 0; a < values.length; a++) {
						setSelectedOption(values[a]);
					}
				}
			}
		}
		else if (getIndex() > -1) {
    		String[] parameters = iwc.getParameterValues(getName());
    		if (parameters != null && parameters.length >= getIndex() + 1) {
    			setSelectedOption(parameters[getIndex()]);
    		}
		}
    	else {
	        if (iwc.getParameter(getName()) != null) {
	        	setSelectedOption(iwc.getParameter(getName()));
	        }
    	}
	}

	/**
	 * @deprecated Use getOptionCount() instead.
	 * Returns the number of <code>SelectOption</code> objects added to this <code>GenericSelect</code>.
	 * @return
	 */
	public int getNumberOfElemetent() {
		return getChildren().size();	
	}

	/**
	 * Returns a <code>List</code> of <code>SelectOption</code> objects added to this <code>GenericSelect</code>.
	 * @return
	 */
	public List getOptions() {
		return getChildren();
	}

	protected void setAllSelected(boolean allSelected) {
		this._allSelected = allSelected;
	}

	/**
	 * Disables/Enables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisable	The interface object(s) to disable/enable.
	 * @param selectedValue		The selected value of the <code>GenericSelect</code> to use.
	 * @param disable					Disables if boolean is true, enables otherwise.
	 */
	public void setToDisableWhenSelected(InterfaceObject objectToDisable, String selectedValue, boolean disable) {

	}	
	/**
	 * Disables/Enables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisable	The interface object(s) to disable/enable.
	 * @param selectedValue		The selected value of the <code>GenericSelect</code> to use.
	 * @param disable					Disables if boolean is true, enables otherwise.
	 * @param isMultiple					Must be used when multiple values can disable...
	 */
	public void setToDisableWhenSelected(InterfaceObject objectToDisable, String selectedValue, boolean disable, boolean isMultiple) {
		setToDisableWhenSelected(objectToDisable.getName(), selectedValue, disable, isMultiple);
	}

	/**
	 * Disables/Enables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisableName	The name of the interface object(s) to disable/enable.
	 * @param selectedValue				The selected value of the <code>GenericSelect</code> to use.
	 * @param disable							Disables if boolean is true, enables otherwise.
	 */
	public void setToDisableWhenSelected(String objectToDisableName, String selectedValue, boolean disable) {
		setToDisableWhenSelected(objectToDisableName, selectedValue, disable, false);
	}
	
	/**
	 * Disables/Enables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisableName	The name of the interface object(s) to disable/enable.
	 * @param selectedValue				The selected value of the <code>GenericSelect</code> to use.
	 * @param disable							Disables if boolean is true, enables otherwise.
	 * @param isMultiple					Must be used when multiple values can disable...
	 */
	public void setToDisableWhenSelected(String objectToDisableName, String selectedValue, boolean disable, boolean isMultiple) {
		this._isSetToDisable = true;
		this._isMultiple = isMultiple;
		this.setOnChange("disableObjectByDropdown(this,findObj('"+objectToDisableName+"'),'"+String.valueOf(disable)+"','"+selectedValue+"')");
	}

	/**
	 * Disables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisable	The interface object(s) to disable.
	 * @param selectedValue		The selected value of the <code>GenericSelect</code> to use.
	 */
	public void setToDisableWhenSelected(InterfaceObject objectToDisable, String selectedValue) {
		setToDisableWhenSelected(objectToDisable.getName(), selectedValue, true);
	}

	/**
	 * Disables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisableName	The name of the interface object(s) to disable.
	 * @param selectedValue				The selected value of the <code>GenericSelect</code> to use.
	 */
	public void setToDisableWhenSelected(String objectToDisableName, String selectedValue) {
		setToDisableWhenSelected(objectToDisableName, selectedValue, true);
	}

	/**
	 * Enables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisable	The interface object(s) to enable.
	 * @param selectedValue		The selected value of the <code>GenericSelect</code> to use.
	 */
	public void setToEnableWhenSelected(InterfaceObject objectToDisable, String selectedValue) {
		setToDisableWhenSelected(objectToDisable.getName(), selectedValue, false);
	}

	/**
	 * Enables an <code>InterfaceObject</code> when the selected value is selected in the <code>GenericSelect</code>.
	 * Uses Javascript.
	 * @param objectToDisableName	The name of the interface object(s) to enable.
	 * @param selectedValue				The selected value of the <code>GenericSelect</code> to use.
	 */
	public void setToEnableWhenSelected(String objectToDisableName, String selectedValue) {
		setToDisableWhenSelected(objectToDisableName, selectedValue, false);
	}

	/**
	 * Sets the selection so that it can not be empty, displays an alert with the given 
	 * error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 * @param emptyValue		The value representing the "empty" value.
	 */
	public void setAsNotEmpty(String errorMessage, String emptyValue) {
		this._isSetAsNotEmpty = true;
		this._notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
		this._emptyValue = emptyValue;
	}

	/**
	 * Sets the selection so that it can not be empty, displays an alert with the given 
	 * error message if the "error" occurs, uses -1 as the empty value.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsNotEmpty(String errorMessage) {
		setAsNotEmpty(errorMessage, "-1");
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
	
	protected void addSelectScript(boolean addScript) {
		this.addSelectScript = addScript;
	}
} 