/*
 * $Id: DropdownMenu.java,v 1.9 2002/09/15 14:56:12 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import com.idega.presentation.IWContext;
import com.idega.presentation.Script;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.data.IDOLegacyEntity;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class DropdownMenu extends InterfaceObject {
	private Vector theElements;
	private boolean keepStatus;
	private String selectedElementValue;
	private Script script;
	private final static String untitled = "untitled";

	public DropdownMenu() {
		this(untitled);
	}

	public DropdownMenu(String Name) {
		setName(Name);
		initialize();
	}

	/**
	 * Populates the dropdownMenu from a List of IDOLegacyEntity objects
	 */
	public DropdownMenu(Collection entityList) {
		this(untitled);
		addMenuElements(entityList);
	}

	public DropdownMenu(IDOLegacyEntity[] entity) {
		initialize();
		if (entity != null) {
			if (entity.length > 0) {
				setName(entity[0].getEntityName());
				for (int i = 0; i < entity.length; i++) {
					addMenuElement(entity[i].getID(), entity[i].getName());
				}
			}
			else {
				setName(untitled);
			}
		}
		else {
			setName(untitled);
		}
	}

	public DropdownMenu(IDOLegacyEntity[] entity, String Name) {
		this(entity);
		setName(Name);
	}

	public void initialize() {
		this.theElements = new Vector(10);
		this.keepStatus = false;
	}

	/**	
	 * Populates the dropdownMenu from a List of IDOLegacyEntity objects with a custom name	
	 */
	public DropdownMenu(Collection entityList, String Name) {
		this(entityList);
		setName(Name);
	}

	public void removeElements() {
		theElements.clear();
	}

	public void addMenuElementFirst(String value, String DisplayString) {
		theElements.add(0, new MenuElement(DisplayString, value));
	}

	//Here is a possible bug, if there are many elements with the same value
	public void addMenuElement(String value, String DisplayString) {
		theElements.addElement(new MenuElement(DisplayString, value));
	}

	public void addMenuElement(int value, String DisplayString) {
		addMenuElement(Integer.toString(value), DisplayString);
	}

	public void addMenuElement(String value) {
		theElements.addElement(new MenuElement(value, value));
	}

	public void setMenuElementDisplayString(String elementValue, String displayString) {
		MenuElement element = getMenuElement(elementValue);
		element.setName(displayString);
	}

	/**	
	* Add menu elements from an Collection of IDOLegacyEntity Objects
	*/
	public void addMenuElements(Collection entityList) {
		if (entityList != null) {
			int length = entityList.size();
			IDOLegacyEntity entity = null;
			Iterator iter = entityList.iterator();
			while (iter.hasNext()) {
				entity = (IDOLegacyEntity) iter.next();
				addMenuElement(entity.getID(), entity.getName());
			}
			if (getName().equals(untitled) && entity != null)
				setName(entity.getEntityName());
		}
	}

	public void addSeparator() {
		theElements.addElement(new MenuElement("----------------------------", ""));
	}

	public void setAttributeToElement(String ElementValue, String AttributeName, String AttributeValue) {
		getMenuElement(ElementValue).setAttribute(AttributeName, AttributeValue);
	}

	public void addDisabledMenuElement(String Value, String DisplayString) {
		addMenuElement(Value, DisplayString);
		setDisabled(Value);
	}

	public void setDisabled(String ElementValue) {
		getMenuElement(ElementValue).setDisabled(true);
	}

	private void deselectElements() {
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			((MenuElement) iter.next()).setSelected(false);
		}
	}

	public void setSelectedElement(String ElementValue) {
		deselectElements();
		getMenuElement(ElementValue).setSelected(true);
		selectedElementValue = ElementValue;
	}

	public String getSelectedElementValue() {
		if (selectedElementValue == null) {
			return "";
		}
		else {
			return selectedElementValue;
		}
	}

	public void keepStatusOnAction() {
		this.keepStatus = true;
	}

	public void setToGoToURL() {
		this.setOnChange("location.href=this.form." + getName() + ".options[this.form." + getName() + ".selectedIndex].value");
	}

	/**
	 * Sets the dropdown to submit automatically.
	 * Must add to a form before this function is used!!!!
	 */
	public void setToSubmit() {
		this.setOnChange("this.form.submit()");
	}

	public void setOnSelect(String onValue, DropdownMenu menuToChange, String valueToChangeToInMenu) {
		Script script = this.getAssociatedScript();
		String functionName = "connectDropdown";
		if (script.doesFunctionExist(functionName)) {
			script.addFunction(functionName, "function " + functionName + "(){\r \r}");
			this.setOnChange(functionName + "()");
		}
		script.addToFunction(
			functionName,
			"this.form." + menuToChange.getName() + ".value=this.form." + getName() + ".options[this.form." + getName() + ".selectedIndex].value;\r");
	}

	//Returns the first menuelement in the menu if there is no match
	private MenuElement getMenuElement(String ElementValue) {
		MenuElement theReturn = new MenuElement();
		Iterator iter = theElements.iterator();
		while (iter.hasNext()) {
			MenuElement tempobj = (MenuElement) iter.next();
			if (tempobj.getValue().equals(ElementValue)) {
				theReturn = tempobj;
			}
		}
		return theReturn;
	}

	public void setStyle(String style) {
		setAttribute("class", style);
	}

	public void print(IWContext iwc) throws Exception {
		theElements.trimToSize();
		if (script != null) {
			((PresentationObjectContainer) getParentObject()).add(script);
		}
		if (getLanguage().equals("HTML")) {
			if (this.keepStatus == true) {
				if (iwc.getParameter(getName()) != null) {
					setSelectedElement(iwc.getParameter(getName()));
				}
			}
			if (getInterfaceStyle().equals("default")) {
				println("<select name=\"" + getName() + "\" " + getAttributeString() + " >");
				Iterator iter = theElements.iterator();
				while (iter.hasNext()) {
					MenuElement tempobj = (MenuElement) iter.next();
					tempobj._print(iwc);
				}
				println("</select>");
			}
		}
		else if (getLanguage().equals("WML")) {
			if (this.keepStatus == true) {
				if (iwc.getParameter(getName()) != null) {
					setSelectedElement(iwc.getParameter(getName()));
				}
			}
			if (getInterfaceStyle().equals("default")) {
				println("<select name=\"" + getName() + "\" " + getAttributeString() + " >");
				Iterator iter = theElements.iterator();
				while (iter.hasNext()) {
					MenuElement tempobj = (MenuElement) iter.next();
					tempobj._print(iwc);
				}
				println("</select>");
			}
		}
	}

	public Object clone() {
		DropdownMenu obj = null;
		try {
			obj = (DropdownMenu) super.clone();
			if (this.theElements != null) {
				obj.theElements = (Vector) this.theElements.clone();
				java.util.ListIterator iter = obj.theElements.listIterator();
				while (iter.hasNext()) {
					int index = iter.nextIndex();
					Object item = iter.next();
					if (item instanceof MenuElement) {
						obj.theElements.set(index, ((MenuElement) item).clone());
					}
				}
			}
			obj.keepStatus = this.keepStatus;
			obj.selectedElementValue = this.selectedElementValue;
			if (this.script != null) {
				obj.script = (Script) this.script.clone();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	public void setMenuElements(List elements) {
		removeElements();

		if (elements != null) {
			Iterator it = elements.iterator();
			while (it.hasNext()) {
				String item = (String) it.next();
				StringTokenizer tok = new StringTokenizer(item, "~^");
				String name = null;
				String value = null;
				String selected = null;
				try {
					name = tok.nextToken();
					value = tok.nextToken();
					while (name.startsWith(" "))
						name = name.substring(1);
//					debug("name = " + name);
//					debug("value = " + value);
					addMenuElement(value, name);
					if (tok.hasMoreTokens())
						selected = tok.nextToken();
						
					if (selected != null && selected.equals("selected"))
						setSelectedElement(value);					
				}
				catch (NoSuchElementException e) {
//					debug("Err: name = " + name);
//					debug("Err: value = " + value);
					if (value == null) {
						if (name != null) { 
							while (name.startsWith(" "))
								name = name.substring(1);
							if (name.equals("delim"))
								addSeparator();
							else if (!name.equals(""))
								addMenuElement("",name);
						}
					}
				}
			}
		}
	}
  
	/**
	 * Overrides the dropdown menu with the list of name/value pairs contained in the String.
	 * 
	 * @param elements A comma seperated list of name/value pairs, each pair seperated by a ;.
	 */
	public void setMenuElements(String elements) {
		Vector vec = new Vector();

		if (elements != null && !elements.equals("")) {
			elements = elements.replace('\n', ' ');
			StringTokenizer tok = new StringTokenizer(elements, ";");
			while (tok.hasMoreTokens()) {
				String element = (String) tok.nextToken();
				if (element != null)
					vec.add(element);
			}
		}

		setMenuElements(vec);
	}
	
	/**
	 * Sets the width in pixels or percents
	 */
	public void setWidth(String width)
	{
		setWidthStyle(width);
	}
}