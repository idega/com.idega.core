/*
 * Created on 31.3.2003
 */
package com.idega.presentation.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	//private Collection _primaryCollection;
	protected Map _secondaryMap;
	
	private int _spaceBetween = 3;
	private int verticalSpaceBetween = 5;
	private SelectDropdownDouble _objectToDisable;
	private String _disableValue;
	
	private boolean _disabled = false;
	private int layout = 1;
	private Text primaryLabel = null;
	private Text secondaryLabel = null;
	private String scriptName = null;
	
	
	public SelectDropdownDouble() {
		this.scriptName = "setDropdownOptions";
	}
	
	public SelectDropdownDouble(String primaryName,String secondaryName) {
		this.primaryName = primaryName;
		this.secondaryName = secondaryName;
		this.scriptName = "setDropdownOptions_" + primaryName; 
	}

	public void main(IWContext iwc) throws Exception {
	    boolean addPrimary = getPrimaryDropdown().getParent()==null;
	    boolean addSecondary = getSecondaryDropdown().getParent()==null;
		if (getStyleAttribute() != null) {
			getPrimaryDropdown().setStyleAttribute(getStyleAttribute());
			getSecondaryDropdown().setStyleAttribute(getStyleAttribute());
		}
		
		//addElementsToPrimary();
		getPrimaryDropdown().setOnChange(this.scriptName + "(this, findObj('"+this.secondaryName+"'), -1);");
		if (this._objectToDisable != null) {
			getSecondaryDropdown().setToDisableWhenSelected(this._objectToDisable.getPrimaryName(), this._disableValue);
			getSecondaryDropdown().setToDisableWhenSelected(this._objectToDisable.getSecondaryName(), this._disableValue);
		}
		
		getPrimaryDropdown().setDisabled(this._disabled);
		getSecondaryDropdown().setDisabled(this._disabled);

		if(addPrimary || addSecondary){
			Table table = new Table();
			table.setCellpadding(0);
			table.setCellspacing(0);
			add(table);
			
			// Layout:
			if(this.layout!=LAYOUT_VERTICAL){
				int column = 1;
				if(addPrimary){
				    table.add(getPrimaryDropdown(), column++, 1);
				    if (this._spaceBetween > 0) {
							table.setWidth(column++, this._spaceBetween);
						}
				}
				if(addSecondary) {
					table.add(getSecondaryDropdown(), column, 1);
				}
			}
			else {
				if(addPrimary && this.primaryLabel!=null) {
					table.add(this.primaryLabel,1,1);
				}
				if(addSecondary && this.secondaryLabel!=null) {
					table.add(this.secondaryLabel,1,3);
				}
				if(this.verticalSpaceBetween>0) {
					table.setHeight(2,this.verticalSpaceBetween);
				}
				if (this._spaceBetween > 0) {
					table.setWidth(2, this._spaceBetween);
				}
				if(addPrimary) {
					table.add(getPrimaryDropdown(),3,1);
				}
				if(addSecondary) {
					table.add(getSecondaryDropdown(),3,3);
				}
			
			}
		}
		
		if (this._styleClass != null) {
			getPrimaryDropdown().setStyleClass(this._styleClass);
			getSecondaryDropdown().setStyleClass(this._styleClass);
		}

		//add the script
		Script script = getParentPage().getAssociatedScript();
		script.addFunction(this.scriptName, getSelectorScript(iwc));
		
		if (this._secondarySelected == null) {
			this._secondarySelected = "-1";
		}
		getParentPage().setOnLoad(this.scriptName + "(findObj('"+this.primaryName+"'),findObj('"+this.secondaryName+"'), '"+this._secondarySelected+"')");
	}
	/*
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
	}*/

	private String getSelectorScript(IWContext iwc) {
		StringBuffer s = new StringBuffer();
		s.append("function " + this.scriptName + "(input, inputToChange, selected) {").append("\n\t");
		s.append("var dropdownValues = new Array();").append("\n\t");
		
		int column = 0;
		if (this._secondaryMap != null) {
			Iterator iter = this._secondaryMap.keySet().iterator();
			while (iter.hasNext()) {
				column = 0;
				String key = (String) iter.next();
				Map map = (Map) this._secondaryMap.get(key);
				if(map!=null){
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
		//if (_primaryCollection == null)
		//	_primaryCollection = new Vector();
		if (this._secondaryMap == null) {
			this._secondaryMap = new HashMap();
		}
		
		//_primaryCollection.add(new SelectOption(name, value));
		getPrimaryDropdown().addOption(new SelectOption(name, value));
		this._secondaryMap.put(value, values);
	}
	
	public void addEmptyElement(String primaryDisplayString, String secondaryDisplayString) {
		Map map = new HashMap();
		map.put("-1", secondaryDisplayString);
		addMenuElement("-1", primaryDisplayString, map);
	}
	
	public DropdownMenu getPrimaryDropdown(){
	    if( this.primary == null ) {
				this.primary = new DropdownMenu(this.primaryName);
			}
		return this.primary;
	}
	
	public DropdownMenu getSecondaryDropdown(){
	    if( this.secondary == null ) {
				this.secondary = new DropdownMenu(this.secondaryName);
			}
		return this.secondary;	
	}

	/**
	 * @return
	 */
	public String getPrimaryName() {
		return this.primaryName;
	}

	/**
	 * @return
	 */
	public String getSecondaryName() {
		return this.secondaryName;
	}

	/**
	 * @param i
	 */
	public void setSpaceBetween(int spaceBetween) {
		this._spaceBetween = spaceBetween;
	}

	/**
	 * @param string
	 */
	public void setPrimaryName(String string) {
		this.primaryName = string;
	}

	/**
	 * @param string
	 */
	public void setSecondaryName(String string) {
		this.secondaryName = string;
	}

	public void setSelectedValues(String primaryValue, String secondaryValue) {
		this._primarySelected = primaryValue;
		getPrimaryDropdown().setSelectedElement(this._primarySelected);
		this._secondarySelected = secondaryValue;
	}
	
	public void setToEnableWhenNotSelected(SelectDropdownDouble doubleDropdown, String disableValue) {
		this._objectToDisable = doubleDropdown;
		this._disableValue = disableValue;
	}
	
	public void setDisabled(boolean disabled) {
		this._disabled = disabled;
	}
	
	public void setStyleClass(String styleClass) {
		this._styleClass = styleClass;
	}
	
	/**
	 * @return
	 */
	protected Map getSecondaryMap() {
		return this._secondaryMap;
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
	/**
	 * @param verticalSpaceBetween The verticalSpaceBetween to set.
	 */
	public void setVerticalSpaceBetween(int verticalSpaceBetween) {
		this.verticalSpaceBetween = verticalSpaceBetween;
	}
}