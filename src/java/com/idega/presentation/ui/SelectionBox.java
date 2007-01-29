//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.idega.data.IDOEntity;
import com.idega.data.IDOLegacyEntity;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.util.text.TextSoap;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class SelectionBox extends InterfaceObject
{
	private final static String untitled = "untitled";
	protected final static String PARAM_NUMBER_OF_ELEMENTS_IN_SELECTIONBOX = "number_of_elements_in_selectionbox";


	private Vector theElements;
	private String multipleString;
	private boolean movers = false;
	private Table outerTable;
	private boolean tableAlreadyPrinted = false;
	private boolean selectAllOnSubmit = false;
	private boolean selectAllOnSubmitIfNoneSelected = false;
	private Text textHeading;
	private boolean headerTable = false;
	private boolean allSelected = false;

	private boolean isSetAsNotEmpty = false;
	private String notEmptyErrorMessage;
	private boolean showOnlySelected = false;
	private int size = -1;
	
	public SelectionBox()
	{
		this("untitled");
	}
	public SelectionBox(String name)
	{
		super();
		setName(name);
		this.theElements = new Vector(10);
		this.keepStatus = false;
		this.multipleString = "multiple";
	}
	public SelectionBox(String name, String textHeading)
	{
		this(name);
		setTextHeading(textHeading);
	}
	public SelectionBox(IDOLegacyEntity[] entity)
	{
		super();
		setName("untitled");
		this.theElements = new Vector(10);
		this.keepStatus = false;
		setMarkupAttribute("CLASS", "select");
		this.multipleString = "multiple";
		if (entity != null)
		{
			if (entity.length > 0)
			{
				setName(entity[0].getEntityName());
				for (int i = 0; i < entity.length; i++)
				{
					//if(entity[i].getID() != -1 && entity[i].getName() != null){
					addMenuElement(entity[i].getID(), entity[i].getName());
					//}
				}
			}
		}
	}
	public SelectionBox(Collection entityList)
	{
		super();
		this.theElements = new Vector(10);
		this.keepStatus = false;
		setMarkupAttribute("CLASS", "select");
		this.multipleString = "multiple";
		
		setName(untitled);
		addMenuElements(entityList);
	}
	public void setTextHeading(String textHeading)
	{
		setTextHeading(new Text(textHeading));
	}
	public void setTextHeading(Text textHeading)
	{
		this.textHeading = textHeading;
		this.headerTable = true;
	}
	/**
	
	 * Returns null if no text in heading
	
	 */
	public String getTextHeadingString()
	{
		if (this.textHeading != null)
		{
			return this.textHeading.getText();
		}
		else {
			return null;
		}
	}
	private Text getTextHeading()
	{
		return this.textHeading;
	}
	public void addElement(String value, String displayString)
	{
		addMenuElement(value, displayString);
	}
	//Here is a possible bug, if there are many elements with the same value
	public void addMenuElement(String Value, String DisplayString)
	{
		this.theElements.addElement(new MenuElement(DisplayString, Value));
	}
	public void setMenuElement(String value, String displayString)
	{
		addMenuElement(value, displayString);
	}
	public void addMenuElement(int value, String DisplayString)
	{
		addMenuElement(Integer.toString(value), DisplayString);
	}
	/**
	
	 * Add menu elements from an List of IDOLegacyEntity Objects and uses
	
	 * getName() to get string to display.
	
	 */
	//public void addMenuElements(List entityList)
	public void addMenuElements(Collection entityList) {
		if (entityList != null) {
			IDOEntity entity = null;
			Iterator iter = entityList.iterator();
			while (iter.hasNext()) {
				entity = (IDOEntity) iter.next();
				
				addMenuElement(entity.getPrimaryKey().toString(), entity.toString());
			}
			if (getName().equals(untitled) && entity != null) {
				setName(entity.getEntityDefinition().getUniqueEntityName());
			}
		}
	}	

	/**
	 * Add menu elements from an List of IDOLegacyEntity Objects and column name to
	 * get the display string from.
	 */
	public void addMenuElements(List entityList, String columnToView)
	{
		if (entityList != null) {
			IDOEntity entity = null;
			Iterator iter = entityList.iterator();
			Object object;
			while (iter.hasNext()) {
				entity = (IDOEntity) iter.next();
				if (columnToView != null && entity instanceof IDOLegacyEntity) {
					object = ((IDOLegacyEntity) entity).getColumnValue(columnToView);
				} else {
					object = entity.toString();
				}
				if (object instanceof String)
				{
					addMenuElement(entity.getPrimaryKey().toString(), (String) object);
				} else
				{
					addMenuElement(entity.getPrimaryKey().toString(), object.toString());
				}				
			}
			
			if (getName().equals(untitled) && entity != null) {
				setName(entity.getEntityDefinition().getUniqueEntityName());
			}
		}		
	}
	/**
	
	 * Add menu elements from an Array of IDOLegacyEntity Objects and column name to
	
	 * get the display string from.
	
	 */
	public void addMenuElements(IDOLegacyEntity[] entityArray, String columnToView)
	{
		if (entityArray != null)
		{
			int length = entityArray.length;
			if (entityArray[0].getColumnValue(columnToView) instanceof String)
			{
				for (int i = 0; i < length; i++)
				{
					addMenuElement(entityArray[i].getID(), (String) entityArray[i].getColumnValue(columnToView));
				}
			} else
			{
				for (int i = 0; i < length; i++)
				{
					addMenuElement(entityArray[i].getID(), entityArray[i].getColumnValue(columnToView).toString());
				}
			}
		}
	}
	public void addSeparator()
	{
		this.theElements.addElement(new MenuElement("----------------------------", ""));
	}
	/**
	
	 * Adds up and down move buttons to move MenuElements up and down
	
	 */
	public void addUpAndDownMovers()
	{
		this.movers = true;
	}
	public void selectAllOnSubmit()
	{
		this.selectAllOnSubmit = true;
	}
	public void selectAllOnSubmitIfNoneSelected()
	{
		this.selectAllOnSubmitIfNoneSelected = true;
	}
	public void main(IWContext iwc)
	{
		if (this.headerTable)
		{
		}
		if (this.movers)
		{
			this.outerTable = new Table(2, 1);
			this.outerTable.setParentObject(this.getParentObject());
			//Script script = new Script();
			//outerTable.add(script);
			Page parentPage = this.getParentPage();
			//parentPage.initializeAssociatedScript();
			Script script = parentPage.getAssociatedScript();
			if (script == null)
			{
				System.err.println("script == null i SelectionBox");
			}
			script.addFunction(
				"addOpt",
				"function addOpt( list, val, text, idx, selected ) {  if( selected == null ) selected = false;  if( idx != null ) {          list.options[idx] = new Option( text, val, false, selected );  } else {          list.options[list.length] = new Option( text, val, false, selected );  }}");
			script.addFunction(
				"moveUp",
				"function moveUp(YLoad) {  if( YLoad.selectedIndex != 0 &&    YLoad.selectedIndex != -1 &&    YLoad.length > 1) {        var selIdx = YLoad.selectedIndex;    var selVal = YLoad.options[selIdx].value;    var selText = YLoad.options[selIdx].text;      var aboveVal = YLoad.options[selIdx-1].value;    var aboveText = YLoad.options[selIdx-1].text;      addOpt( YLoad, selVal, selText, selIdx-1, true );    addOpt( YLoad, aboveVal, aboveText, selIdx );  }}");
			script.addFunction(
				"moveDown",
				"function moveDown(YLoad) {  if( YLoad.selectedIndex != YLoad.length-1 &&    YLoad.selectedIndex != -1 &&    YLoad.length > 1) {        var selIdx = YLoad.selectedIndex;    var selVal = YLoad.options[selIdx].value;    var selText = YLoad.options[selIdx].text;      var belowVal = YLoad.options[selIdx+1].value;    var belowText = YLoad.options[selIdx+1].text;      addOpt( YLoad, selVal, selText, selIdx+1, true );    addOpt( YLoad, belowVal, belowText, selIdx );  }}");
			this.outerTable.add(this, 1, 1);
			GenericButton up = new GenericButton(this.getName() + "_up", " /\\ ");
			up.setOnClick("moveUp(this.form." + this.getName() + ")");
			GenericButton down = new GenericButton(this.getName() + "_down", " \\/ ");
			down.setOnClick("moveDown(this.form." + this.getName() + ")");
			this.outerTable.add(up, 2, 1);
			this.outerTable.addBreak(2, 1);
			this.outerTable.add(down, 2, 1);
			this.outerTable.addBreak(2, 1);
		}
		if (this.selectAllOnSubmit)
		{
			Script script = this.getParentPage().getAssociatedScript();
			script.addFunction(
				"selectAllInSelectionBox",
				"function selectAllInSelectionBox(input){\n  for( i=0;i<input.length; i++ ) {\n	input[i].selected=1;\n    }\n}");
			this.getParentForm().setOnSubmit("selectAllInSelectionBox(this." + this.getName() + ")");
		}
		if (this.selectAllOnSubmitIfNoneSelected)
		{
			Script script = this.getParentPage().getAssociatedScript();
			script.addFunction(
				"selectAllInSelectionBoxIfNoneSelected",
				"function selectAllInSelectionBoxIfNoneSelected(input){\n  noElementsSelected = true;\n  for( i=0;i<input.length; i++ ) {\n    if(input[i].selected==1) {\n      noElementsSelected=false;\n    }\n  }\n  if (noElementsSelected){\n    for( i=0;i<input.length; i++ ) {\n	input[i].selected=1;\n    }\n  }\n}");
			this.getParentForm().setOnSubmit("selectAllInSelectionBoxIfNoneSelected(this." + this.getName() + ")");
		}
		if (this.isSetAsNotEmpty) {
			setOnSubmitFunction("warnIfNonSelected", "function warnIfNonSelected (inputbox,warnMsg) {\n\n		if ( inputbox.length == 0 ) { \n		alert ( warnMsg );\n		return false;\n	}\n	else{\n		return true;\n}\n\n}", this.notEmptyErrorMessage);
		}

	}

	public void setAttributeToElement(String ElementValue, String AttributeName, String AttributeValue)
	{
		getMenuElement(ElementValue).setMarkupAttribute(AttributeName, AttributeValue);
	}
	public void addDisabledMenuElement(String Value, String DisplayString)
	{
		addMenuElement(Value, DisplayString);
		setDisabled(Value);
	}
	public void setDisabled(String ElementValue)
	{
		getMenuElement(ElementValue).setDisabled(true);
	}
	/**
	 * Sets the element by value elementValue as selected if it is found in this selectionbox
	 **/
	public void setSelectedElement(String elementValue)
	{
		getMenuElement(elementValue).setSelected(true);
	}
	/**
	 * Sets the element by value elementValue as selected if it is found in this selectionbox
	 **/
	public void setSelectedElement(int elementValue)
	{
		setSelectedElement(Integer.toString(elementValue));
	}	
	
	/**
	 * Sets all the elements by values in elementValues as selected if it is found in this selectionbox
	 **/
	public void setSelectedElements(String[] elementValues)
	{
		if(elementValues !=null){
			for (int i = 0; i < elementValues.length; i++)
			{
				String value = elementValues[i];
				setSelectedElement(value);
			}
		}
	}
	/**
	 * Sets all the elements by values in elementValues as selected if it is found in this selectionbox
	 **/
	public void setSelectedElements(int[] elementValues)
	{
		if(elementValues !=null){
			for (int i = 0; i < elementValues.length; i++)
			{
				int value = elementValues[i];
				setSelectedElement(value);
			}
		}
	}
	
	/**
	 * Maintains all the previously selected elements over a request submit
	 **/
	public void keepStatusOnAction()
	{
		this.keepStatus = true;
	}
	public void setToGoToURL()
	{
		this.setOnChange("location.href=this.form." + getName() + ".options[this.form." + getName() + ".selectedIndex].value");
	}
	/**
	 * Sets the box to be multiply selectable or not.
	**/
	public void setMultiple(boolean ifMultiple)
	{
		if (ifMultiple)
		{
			this.multipleString = "multiple";
		} else
		{
			this.multipleString = "";
		}
	}
	/**
	 * Sets the SelectionBox to submit automatically.
	 * Must add to a form before this function is used!!!!
	 */
	public void setToSubmit()
	{
		this.setOnChange("this.form.submit()");
	}
	public void setHeight(int height)
	{
		setHeight(Integer.toString(height));
	}
	public void setHeight(String height)
	{
		setMarkupAttribute("size", height);
	}
	/**
	 * Sets the width in pixels or percents
	 */
	public void setWidth(String width)
	{
		setWidthStyle(width);
	}
	//Returns the first menuelement in the menu if there is no match
	public MenuElement getMenuElement(String ElementValue)
	{
		MenuElement theReturn = new MenuElement();
		Iterator iter = this.theElements.iterator();
		while (iter.hasNext())
		{
			MenuElement tempobj = (MenuElement) iter.next();
			if (tempobj.getValueAsString().equals(ElementValue))
				{
					theReturn = tempobj;
				}
		}
		return theReturn;
	}
	public void print(IWContext iwc) throws Exception
	{
		this.theElements.trimToSize();
		if (this.movers)
		{
			if (!this.tableAlreadyPrinted)
			{
				this.tableAlreadyPrinted = true;
				this.movers = false;
				this.outerTable._print(iwc);
				this.movers = false;
			} else
			{
				this.tableAlreadyPrinted = false;
			}
		} else
		{
			Text theHeader = getTextHeading();
			if (theHeader != null)
			{
				theHeader.addBreak();
				theHeader._print(iwc);
			}
			//if ( doPrint(iwc) ){
			if (getMarkupLanguage().equals("HTML"))
			{
				if (this.keepStatus == true)
				{
					if (iwc.getParameter(getName()) != null)
					{
						String[] selectedValues = iwc.getParameterValues(getName());
						setSelectedElements(selectedValues);
					}
				}
					println("<select name=\"" + getName() + "\" " + getMarkupAttributesString() + " " + this.multipleString + " " + getSizeString() +" >");
					Iterator iter = this.theElements.iterator();
					while (iter.hasNext())
					{
						MenuElement tempobj = (MenuElement) iter.next();
						if (this.allSelected){
							tempobj.setSelected(true);
						}
						if(this.showOnlySelected){
							if(tempobj.isSelected()){
								tempobj._print(iwc);
							}
						}
						else{
							tempobj._print(iwc);
						}
					}
					println("</select>");
			} else if (getMarkupLanguage().equals("WML"))
			{
				if (this.keepStatus == true)
				{
					if (iwc.getParameter(getName()) != null)
					{
						setSelectedElement(iwc.getParameter(getName()));
					}
				}
					println("<select name=\"" + getName() + "\" " + getMarkupAttributesString() + " >");
					Iterator iter = this.theElements.iterator();
					while (iter.hasNext())
					{
						MenuElement tempobj = (MenuElement) iter.next();
						if (this.allSelected){
							tempobj.setSelected(true);
						}
						
						if(this.showOnlySelected){
							if(tempobj.isSelected()){
								tempobj._print(iwc);
							}
						}
						else{
							tempobj._print(iwc);
						}
					}
					println("</select>");
			}
			//}
		}
		HiddenInput numberOfElements = new HiddenInput(PARAM_NUMBER_OF_ELEMENTS_IN_SELECTIONBOX+"_"+this.getClassName(), String.valueOf(this.theElements.size()));
		numberOfElements.print(iwc);
	}
	
	/**
	 * Sets if all values should be selected.
	 * @param allSelected True if all values whould be selected, false otherwise.
	 */
	public void setAllSelected(boolean allSelected)
	{
		this.allSelected = allSelected;
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/**
	 * Sets the text input so that it can not be empty, displays an alert with the given 
	 * error message if the "error" occurs.  Uses Javascript.
	 * @param errorMessage	The error message to display.
	 */
	public void setAsNotEmpty(String errorMessage) {
		this.isSetAsNotEmpty = true;
		this.notEmptyErrorMessage = TextSoap.removeLineBreaks(errorMessage);
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
	
	public void setToShowOnlySelected(boolean showOnlySelected){
		this.showOnlySelected  = showOnlySelected;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getSizeString() {
		if (getSize() == -1) {
			return "";
		} else {
			return "size = " + getSize();
		}
	}
	
	public void setSize(int size) {
		this.size = size;
	}
}