package com.idega.presentation.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.idega.presentation.IWContext;
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
		selectedElements = new ArrayList();
	}

	/**
	 * Removes all <code>SelectOption</code> objects from the select object.
	 */
	public void removeElements() {
		this.getChildren().clear();
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
		if (setToSubmit)
			setOnChange("this.form.submit()");
		else
			removeMarkupAttribute("onChange");
	}

	/**
	 * Sets the select to submit when the specified option is seleced.
	 * Must add to a form before this function is used!!!!
	 */
	public void setToSubmit(String optionValue) {
		_isSetToSubmit = true;
		setOnChange("submitWhenSelected(this,'"+optionValue+"')");
	}

	/**
	 * Adds a <code>SelectOption</code> to the select object.
	 * @param option	The <code>SelectOption</code> to add.
	 */
	public void addOption(SelectOption option) {
		add(option);
		if (option.getSelected())
			setSelectedOption(option.getValueAsString());
	}

	/**
	 * Adds a <code>SelectOption</code> to the select object.
	 * @param option	The <code>SelectOption</code> to add.
	 */
	public void setSelectOption(SelectOption option) {
		addOption(option);
	}

	/**
	 * Adds a <code>SelectOption</code> to the select object as the first option.
	 * @param option	The <code>SelectOption</code> to add.
	 */
	public void addFirstOption(SelectOption option) {
		add(0, option);
		if (option.getSelected())
			setSelectedOption(option.getValueAsString());
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
		add(option);
		if (option.getSelected())
			setSelectedOption(option.getValueAsString());
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
		selectedElements.clear();
	}

	/**
	 * Gets the value of the selected <code>SelectOption</code>.
	 * @return String	The value of the <code>SelectOption</code> in the select object.
	 */
	public String getSelectedValue() {
		Iterator iter = selectedElements.iterator();
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
		if (!getMultiple())
			deselectOptions();
		selectedElements.add(value);
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
			if (element.getValueAsString().equals(value))
				return element;
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
		if (multiple)
			setMarkupAttributeWithoutValue("multiple");
		else
			removeMarkupAttribute("multiple");
	}

	/**
	 * Returns if this select object is set to allow multiple selections.
	 * @return True if allows multiple, false otherwise.
	 */
	protected boolean getMultiple() {
		if (isMarkupAttributeSet("multiple"))
			return true;
		return false;
	}

	public void main(IWContext iwc) throws Exception {
		if (_isSetAsNotEmpty)
			setOnSubmitFunction("warnIfDropdownEmpty", "function warnIfDropdownEmpty (inputbox,warnMsg,emptyValue) {\n\n		if ( inputbox.options[inputbox.selectedIndex].value == emptyValue ) { \n		alert ( warnMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}", _notEmptyErrorMessage, _emptyValue);
//		if (_isSetToDisable) {
		if (getScript() != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("function disableObjectByDropdown (dropdown,inputs,value,selectedValue) {\n	if (dropdown.options[dropdown.selectedIndex].value == eval(selectedValue)) {\n \tif (inputs.length > 1) {\n	\t\tfor(var i=0;i<inputs.length;i++)\n	\t\t\tinputs[i].disabled=eval(value);\n	\t\t}\n	\t\tinputs.disabled=eval(value);\n}\n");
			if (!_isMultiple) {
				buffer.append("else {\n\tif (inputs.length > 1) {\n	\t\tfor(var i=0;i<inputs.length;i++)\n	\t\t\tinputs[i].disabled=!eval(value);\n	\t\t}\n	\t\tinputs.disabled=!eval(value);\n}\n");
			}
			buffer.append("}");
			getScript().addFunction("disableObjectByDropdown", buffer.toString());
		}
//		}
		if (_isSetToSubmit)
			getScript().addFunction("submitWhenSelected", "function submitWhenSelected (dropdown,selectedValue) {\n\tif (dropdown.options[dropdown.selectedIndex].value == eval(selectedValue))\n\t\tdropdown.form.submit();\n}");
	}

	/**
	 * @see com.idega.presentation.PresentationObject#print(IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if (!iwc.isInEditMode()) {
			Iterator iter = getChildren().iterator();
			while (iter.hasNext()) {
				SelectOption option = (SelectOption) iter.next();
				boolean setSelected = ((_allSelected) || selectedElements.contains(option.getValueAsString()));
				option.setSelected(setSelected);
			}
		}

		if (getMarkupLanguage().equals("HTML")) {
			println("<select name=\"" + getName() + "\" " + getMarkupAttributesString() + " >");
			super.print(iwc);
			print("</select>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			println("<select name=\"" + getName() + "\" " + getMarkupAttributesString() + " >");
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
		if (iwc.isParameterSet(getName())) {
			String[] values = iwc.getParameterValues(getName());
			if (values != null) {
				for (int a = 0; a < values.length; a++) {
					setSelectedOption(values[a]);
				}
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
		_allSelected = allSelected;
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
		_isSetToDisable = true;
		_isMultiple = isMultiple;
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
		_isSetAsNotEmpty = true;
		_notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
		_emptyValue = emptyValue;
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
}