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
import com.idega.presentation.text.Text;

/**
 * @author laddi
 */
public class SelectDropdownDouble extends InterfaceObject {
	
	public final static int LAYOUT_HORIZONTAL = 1;
	public final static int LAYOUT_VERTICAL = 2;

	private String _styleClass;
	private String _primarySelected;
	private String _secondarySelected;
	private String primaryName = "primary";
	private String secondaryName = "secondary";
	private DropdownMenu primary = null;
	private DropdownMenu secondary = null;
	private Collection _primaryCollection;
	protected Map _secondaryMap;
	
	private int _spaceBetween = 3;
	private SelectDropdownDouble _objectToDisable;
	private String _disableValue;
	
	private boolean _disabled = false;
	private int layout = 1;
	private Text primaryLabel = null;
	private Text secondaryLabel = null;
	
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
		if (_objectToDisable != null) {
			getSecondaryDropdown().setToDisableWhenSelected(_objectToDisable.getPrimaryName(), _disableValue);
			getSecondaryDropdown().setToDisableWhenSelected(_objectToDisable.getSecondaryName(), _disableValue);
		}
		
		getPrimaryDropdown().setDisabled(_disabled);
		getSecondaryDropdown().setDisabled(_disabled);

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		add(table);
		
		// Layout:
		if(layout!=LAYOUT_VERTICAL){
			int column = 1;
			table.add(getPrimaryDropdown(), column++, 1);
			if (_spaceBetween > 0)
				table.setWidth(column++, _spaceBetween);
			table.add(getSecondaryDropdown(), column, 1);
		}
		else {
			if(primaryLabel!=null)
				table.add(primaryLabel,1,1);
			if(secondaryLabel!=null)
				table.add(secondaryLabel,1,2);
			table.setWidth(2,_spaceBetween);
			table.add(getPrimaryDropdown(),3,1);
			table.add(getSecondaryDropdown(),3,2);
		
		}
		
		if (_styleClass != null) {
			getPrimaryDropdown().setStyleClass(_styleClass);
			getSecondaryDropdown().setStyleClass(_styleClass);
		}

		//add the script
		Script script = getParentPage().getAssociatedScript();
		script.addFunction("setDropdownOptions", getSelectorScript(iwc));
		
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
					getPrimaryDropdown().setSelectedOption(option.getValueAsString());
					hasSelected = true;
				}
			}
			if (_primarySelected != null)
				getPrimaryDropdown().setSelectedElement(_primarySelected);
		}
	}

	private String getSelectorScript(IWContext iwc) {
		StringBuffer s = new StringBuffer();
		s.append("function setDropdownOptions(input, inputToChange, selected) {").append("\n\t");
		s.append("var dropdownValues = new Array();").append("\n\t");
		
		int column = 0;
		if (_secondaryMap != null) {
			Iterator iter = _secondaryMap.keySet().iterator();
			while (iter.hasNext()) {
				column = 0;
				String key = (String) iter.next();
				Map map = (Map) _secondaryMap.get(key);
				s.append("\n\t").append("dropdownValues[\""+key+"\"] = new Array();").append("\n\t");
				
				Iterator iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					Object element = iterator.next();
					String secondKey = getKey(iwc, element);
					String value = getValue(iwc, map.get(element));
					s.append("dropdownValues[\""+key+"\"]["+column+++"] = new Option('"+value+"','"+secondKey+"');").append("\n\t");
				}
			}
		}
		s.append("\n\t");
		s.append("var chosen = input.options[input.selectedIndex].value;").append("\n\t");
		s.append("inputToChange.options.length = 0;").append("\n\n\t");
		s.append("var array = dropdownValues[chosen];").append("\n\t");
		s.append("for (var a=0; a < array.length; a++)").append("{\n\t\t");
		s.append("var index = inputToChange.options.length;").append("\n\t\t");
		s.append("inputToChange.options[index] = array[a];").append("\n\t\t");
		s.append("var option = inputToChange.options[index];").append("\n\t\t");
		s.append("if (option.value == selected)").append("\n\t\t\t");
		s.append("option.selected = true;").append("\n\t\t");
		s.append("else").append("\n\t\t\t");
		s.append("option.selected = false;").append("\n\t");
		s.append("}").append("\n").append("}");

		return s.toString();
	}
	
	protected String getKey(IWContext iwc, Object key) {
		return (String) key;
	}
	
	protected String getValue(IWContext iwc, Object value) {
		return (String) value;
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
	
	public void setToEnableWhenNotSelected(SelectDropdownDouble doubleDropdown, String disableValue) {
		_objectToDisable = doubleDropdown;
		_disableValue = disableValue;
	}
	
	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}
	
	public void setStyleClass(String styleClass) {
		_styleClass = styleClass;
	}
	
	/**
	 * @return
	 */
	protected Map getSecondaryMap() {
		return _secondaryMap;
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
	/**
	 * @param layout The layout to set.
	 */
	public void setLayout(int layout) {
		this.layout = layout;
	}
	
	/**
	 * 
	 * @param vertical
	 */
	public void setLayoutVertical(boolean vertical){
		setLayout(LAYOUT_VERTICAL);
	}
	/**
	 * @param primaryLabel The primaryLabel to set.
	 */
	public void setPrimaryLabel(Text primaryLabel) {
		this.primaryLabel = primaryLabel;
	}
	/**
	 * @param secondaryLabel The secondaryLabel to set.
	 */
	public void setSecondaryLabel(Text secondaryLabel) {
		this.secondaryLabel = secondaryLabel;
	}
}