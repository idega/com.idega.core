/*
 * $Id: DropdownMenu.java,v 1.12 2002/10/22 21:41:19 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import java.util.Collection;
import java.util.Iterator;

import com.idega.data.IDOLegacyEntity;
import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class DropdownMenu extends GenericSelect {
	private final static String untitled = "untitled";

	public DropdownMenu() {
		this(untitled);
	}

	public DropdownMenu(String name) {
		super(name);
	}

	public DropdownMenu(Collection entityList) {
		this(entityList,untitled);
	}

	public DropdownMenu(Collection entityList, String Name) {
		this(Name);
		addMenuElements(entityList);
	}

	public DropdownMenu(IDOLegacyEntity[] entity) {
		super();
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

	/**
	 * @deprecated
	 */
	public void addMenuElementFirst(String value, String displayString) {
		addFirstOption(new Option(displayString, value));
	}

	/**
	 * @deprecated
	 */
	public void addMenuElement(String value, String displayString) {
		addOption(new Option(displayString, value));
	}

	/**
	 * @deprecated
	 */
	public void addMenuElement(int value, String displayString) {
		addOption(new Option(displayString, Integer.toString(value)));
	}

	/**
	 * @deprecated
	 */
	public void addMenuElement(String value) {
		addOption(new Option(value, value));
	}

	/**
	 * @deprecated
	 */
	public void setMenuElementDisplayString(String value, String displayString) {
		this.setOptionName(value, displayString);
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

	/**
	 * @deprecated
	 **/
	public void addDisabledMenuElement(String value, String displayString) {
		addDisabledOption(new Option(displayString,value));
	}

	/**
	 * @deprecated
	 * Sets the element by value elementValue as selected if it is found in this menu
	 **/
	public void setSelectedElement(String value) {
		setSelectedOption(value);
	}

	/**
	 * @deprecated
	 * Sets the element by value elementValue as selected if it is found in this menu
	 **/
	public void setSelectedElement(int elementValue) {
		setSelectedElement(Integer.toString(elementValue));
	}

	/**
	 * Gets the elementValue of the selected menuelement if any. If none is selected it returns an empty String.
	 **/
	public String getSelectedElementValue() {
		return getSelectedValue();
	}
}