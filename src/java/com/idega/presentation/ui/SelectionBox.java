//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.idega.data.IDOLegacyEntity;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class SelectionBox extends InterfaceObject
{
	private Vector theElements;
	private boolean keepStatus;
	private String multipleString;
	private boolean movers = false;
	private Table outerTable;
	private boolean tableAlreadyPrinted = false;
	private boolean selectAllOnSubmit = false;
	private Text textHeading;
	private boolean headerTable = false;
	private boolean allSelected = false;
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
		multipleString = "multiple";
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
		setAttribute("CLASS", "select");
		multipleString = "multiple";
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
	public SelectionBox(List entityList)
	{
		super();
		this.theElements = new Vector(10);
		this.keepStatus = false;
		setAttribute("CLASS", "select");
		multipleString = "multiple";
		if (entityList != null)
		{
			int length = entityList.size();
			if (length > 0)
			{
				IDOLegacyEntity entity = (IDOLegacyEntity) entityList.get(0);
				setName(entity.getEntityName());
				addMenuElements(entityList);
			} else
			{
				setName("untitled");
			}
		} else
		{
			setName("untitled");
		}
	}
	public void setTextHeading(String textHeading)
	{
		setTextHeading(new Text(textHeading));
	}
	public void setTextHeading(Text textHeading)
	{
		this.textHeading = textHeading;
		headerTable = true;
	}
	/**
	
	 * Returns null if no text in heading
	
	 */
	public String getTextHeadingString()
	{
		if (textHeading != null)
		{
			return textHeading.getText();
		} else
			return null;
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
		theElements.addElement(new MenuElement(DisplayString, Value));
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
	public void addMenuElements(List entityList)
	{
		if (entityList != null)
		{
			int length = entityList.size();
			IDOLegacyEntity entity;
			for (int i = 0; i < length; i++)
			{
				entity = (IDOLegacyEntity) entityList.get(i);
				//if(entity[i].getID() != -1 && entity[i].getName() != null){
				addMenuElement(entity.getID(), entity.getName());
				//}
			}
		}
	}
	/**
	
	 * Add menu elements from an List of IDOLegacyEntity Objects and column name to
	
	 * get the display string from.
	
	 */
	public void addMenuElements(List entityList, String columnToView)
	{
		if (entityList != null)
		{
			int length = entityList.size();
			IDOLegacyEntity entity;
			Object object;
			for (int i = 0; i < length; i++)
			{
				entity = (IDOLegacyEntity) entityList.get(i);
				object = entity.getColumnValue(columnToView);
				if (object instanceof String)
				{
					addMenuElement(entity.getID(), (String) object);
				} else
				{
					addMenuElement(entity.getID(), object.toString());
				}
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
		theElements.addElement(new MenuElement("----------------------------", ""));
	}
	/**
	
	 * Adds up and down move buttons to move MenuElements up and down
	
	 */
	public void addUpAndDownMovers()
	{
		movers = true;
	}
	public void selectAllOnSubmit()
	{
		this.selectAllOnSubmit = true;
	}
	public void main(IWContext iwc)
	{
		if (this.headerTable)
		{
		}
		if (movers)
		{
			outerTable = new Table(2, 1);
			outerTable.setParentObject(this.getParentObject());
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
			outerTable.add(this, 1, 1);
			GenericButton up = new GenericButton(this.getName() + "_up", " /\\ ");
			up.setOnClick("moveUp(this.form." + this.getName() + ")");
			GenericButton down = new GenericButton(this.getName() + "_down", " \\/ ");
			down.setOnClick("moveDown(this.form." + this.getName() + ")");
			outerTable.add(up, 2, 1);
			outerTable.addBreak(2, 1);
			outerTable.add(down, 2, 1);
			outerTable.addBreak(2, 1);
		}
		if (selectAllOnSubmit)
		{
			Script script = this.getParentPage().getAssociatedScript();
			script.addFunction(
				"selectAllInSelectionBox",
				"function selectAllInSelectionBox(input){\n  for( i=0;i<input.length; i++ ) {\n	input[i].selected=1;\n    }\n}");
			this.getParentForm().setOnSubmit("selectAllInSelectionBox(this." + this.getName() + ")");
		}
	}
	public void setAttributeToElement(String ElementValue, String AttributeName, String AttributeValue)
	{
		getMenuElement(ElementValue).setAttribute(AttributeName, AttributeValue);
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
			multipleString = "multiple";
		} else
		{
			multipleString = "";
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
		setAttribute("size", height);
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
		for (Enumeration e = theElements.elements(); e.hasMoreElements();)
		{
			MenuElement tempobj = (MenuElement) e.nextElement();
			if (tempobj.getValue().equals(ElementValue))
			{
				theReturn = tempobj;
			}
		}
		return theReturn;
	}
	public void print(IWContext iwc) throws Exception
	{
		theElements.trimToSize();
		if (movers)
		{
			if (!tableAlreadyPrinted)
			{
				tableAlreadyPrinted = true;
				movers = false;
				outerTable._print(iwc);
				movers = false;
			} else
			{
				tableAlreadyPrinted = false;
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
			if (getLanguage().equals("HTML"))
			{
				if (this.keepStatus == true)
				{
					if (iwc.getParameter(getName()) != null)
					{
						String[] selectedValues = iwc.getParameterValues(getName());
						setSelectedElements(selectedValues);
					}
				}
				if (getInterfaceStyle().equals("default"))
				{
					println("<select name=\"" + getName() + "\" " + getAttributeString() + " " + multipleString + " >");
					for (Enumeration e = theElements.elements(); e.hasMoreElements();)
					{
						MenuElement tempobj = (MenuElement) e.nextElement();
						if (allSelected)
							tempobj.setSelected(true);
						tempobj._print(iwc);
					}
					println("</select>");
				}
			} else if (getLanguage().equals("WML"))
			{
				if (this.keepStatus == true)
				{
					if (iwc.getParameter(getName()) != null)
					{
						setSelectedElement(iwc.getParameter(getName()));
					}
				}
				if (getInterfaceStyle().equals("default"))
				{
					println("<select name=\"" + getName() + "\" " + getAttributeString() + " >");
					for (Enumeration e = theElements.elements(); e.hasMoreElements();)
					{
						MenuElement tempobj = (MenuElement) e.nextElement();
						tempobj._print(iwc);
					}
					println("</select>");
				}
			}
			//}
		}
	}
	/**
	 * Sets the allSelected.
	 * @param allSelected The allSelected to set
	 */
	public void setAllSelected(boolean allSelected)
	{
		this.allSelected = allSelected;
	}
}
