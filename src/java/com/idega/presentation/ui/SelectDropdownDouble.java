/*
 * Created on 31.3.2003
 */
package com.idega.presentation.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.idega.presentation.IWContext;
import com.idega.presentation.Script;
import com.idega.presentation.Table;

/**
 * @author laddi
 */
public class SelectDropdownDouble extends InterfaceObjectContainer {

	private String _primarySelected;
	private String _secondarySelected;
	private String primaryName = "primary";
	private String secondaryName = "secondary";
	private DropdownMenu primary = null;
	private DropdownMenu secondary = null;
	private Collection _primaryCollection;
	private Map _secondaryMap;
	
	private int _spaceBetween = 3;
	
	public SelectDropdownDouble() {
	}
	
	public SelectDropdownDouble(String primaryName,String secondaryName) {
		this.primaryName = primaryName;
		this.secondaryName = secondaryName;
	}

	public void main(IWContext iwc) throws Exception {
		if (getStyleAttribute() != null) {
			getPrimaryDropdown().setStyleAttribute(getStyleAttribute());
			getSecondaryDropdown().setStyleAttribute(getStyleAttribute());
		}
		
		addElementsToPrimary();
		getPrimaryDropdown().setOnChange("setDropdownOptions(this, findObj('"+secondaryName+"'), -1);");
		getSecondaryDropdown().addMenuElement(-1,"");
		getSecondaryDropdown().setSelectedElement(-1);

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		add(table);
		int column = 1;

		table.add(getPrimaryDropdown(), column++, 1);
		if (_spaceBetween > 0)
			table.setWidth(column++, _spaceBetween);
		table.add(getSecondaryDropdown(), column, 1);

		//add the script
		Script script = getParentPage().getAssociatedScript();
		script.addFunction("arrayVariables", getArrayVariables());
		script.addFunction("setDropdownOptions", getSelectorScript());
		
		if (_secondarySelected == null)
			_secondarySelected = "-1";
		getParentPage().setOnLoad("setDropdownOptions(findObj('"+primaryName+"'),findObj('"+secondaryName+"'), '"+_secondarySelected+"')");
	}
	
	private void addElementsToPrimary() {
		if (_primaryCollection != null) {
			Iterator iter = _primaryCollection.iterator();
			boolean hasSelected = false;
			while (iter.hasNext()) {
				SelectOption option = (SelectOption) iter.next();
				getPrimaryDropdown().addOption(option);
				if (!hasSelected) {
					getPrimaryDropdown().setSelectedOption(option.getValue());
					hasSelected = true;
				}
			}
			if (_primarySelected != null)
				getPrimaryDropdown().setSelectedElement(_primarySelected);
		}
	}

	private String getArrayVariables() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("var dropdownValues = new Array();").append("\n");
		
		int column = 0;
		if (_secondaryMap != null) {
			Iterator iter = _secondaryMap.keySet().iterator();
			while (iter.hasNext()) {
				column = 0;
				String key = (String) iter.next();
				Map map = (Map) _secondaryMap.get(key);
				buffer.append("\n").append("dropdownValues[\""+key+"\"] = new Array();").append("\n");
				
				Iterator iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String element = (String) iterator.next();
					String value = (String) map.get(element);
					buffer.append("dropdownValues[\""+key+"\"]["+column+++"] = new Option('"+value+"','"+element+"');").append("\n");
				}
			}
		}
		
		return buffer.toString();
	}
	
	private String getSelectorScript() {
		StringBuffer s = new StringBuffer();
		s.append("function setDropdownOptions(input, inputToChange, selected) {").append("\n\t");
		s.append("var chosen = input.options[input.selectedIndex].value;").append("\n\t");
		s.append("inputToChange.options.length = 0;").append("\n\n\t");
		s.append("var array = dropdownValues[chosen];").append("\n\t");
		s.append("for (var a=0; a < array.length; a++)").append("{\n\t\t");
		s.append("inputToChange.options[inputToChange.options.length] = array[a];").append("\n\t\t");
		s.append("var option = inputToChange.options[inputToChange.options.length - 1];").append("\n\t\t");
		s.append("if (option == selected)").append("\n\t\t\t");
		s.append("option.selected = 'true';").append("\n\t");
		s.append("}").append("\n").append("}");

		return s.toString();
	}
	
	public void addMenuElement(String value, String name, Map values) {
		if (_primaryCollection == null)
			_primaryCollection = new Vector();
		if (_secondaryMap == null)
			_secondaryMap = new HashMap();
		
		_primaryCollection.add(new SelectOption(name, value));
		_secondaryMap.put(value, values);
	}
	
	public void addEmptyElement(String primaryDisplayString, String secondaryDisplayString) {
		Map map = new HashMap();
		map.put("-1", secondaryDisplayString);
		addMenuElement("-1", primaryDisplayString, map);
	}
	
	public DropdownMenu getPrimaryDropdown() {
		if( primary == null )
			primary = new DropdownMenu(primaryName);
		return primary;		
	}

	public DropdownMenu getSecondaryDropdown() {
		if( secondary == null )
			secondary = new DropdownMenu(secondaryName);
		return secondary;		
	}

	/**
	 * @return
	 */
	public String getPrimaryName() {
		return primaryName;
	}

	/**
	 * @return
	 */
	public String getSecondaryName() {
		return secondaryName;
	}

	/**
	 * @param i
	 */
	public void setSpaceBetween(int spaceBetween) {
		_spaceBetween = spaceBetween;
	}

	/**
	 * @param string
	 */
	public void setPrimaryName(String string) {
		primaryName = string;
	}

	/**
	 * @param string
	 */
	public void setSecondaryName(String string) {
		secondaryName = string;
	}

	public void setSelectedValues(String primaryValue, String secondaryValue) {
		_primarySelected = primaryValue;
		_secondarySelected = secondaryValue;
	}
}