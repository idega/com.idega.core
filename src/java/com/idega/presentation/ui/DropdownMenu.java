/*
 * $Id: DropdownMenu.java,v 1.27 2006/04/09 12:13:15 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import java.util.Collection;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.presentation.InvisibleInBuilder;
import com.idega.data.IDOEntity;
import com.idega.data.IDOLegacyEntity;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class DropdownMenu extends GenericSelect implements InvisibleInBuilder {
	private final static String untitled = "untitled";

	public DropdownMenu() {
		this(untitled);
	}

	public DropdownMenu(String name) {
		super(name);
	}

	public DropdownMenu(Collection<?> entityList) {
		this(entityList,untitled);
	}

	public DropdownMenu(Collection<?> entityList, String Name) {
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

	public void addMenuElementFirst(String value, String displayString) {
		addFirstOption(new SelectOption(displayString, value));
	}

	public void addMenuElement(String value, String displayString) {
		addOption(new SelectOption(displayString, value));
	}

	public void addMenuElement(int value, String displayString) {
		addOption(new SelectOption(displayString, value));
	}

	public void addMenuElement(char value, String displayString) {
		addOption(new SelectOption(displayString, value));
	}

	public void addMenuElement(String value) {
		addOption(new SelectOption(value, value));
	}

	public void addDisabledMenuElement(String value, String displayString) {
		addDisabledOption(new SelectOption(displayString,value));
	}

	public void setMenuElementFirst(String value, String displayString) {
		addFirstOption(new SelectOption(displayString, value));
	}

	public void setMenuElement(String value, String displayString) {
		addOption(new SelectOption(displayString, value));
	}

	public void setMenuElement(int value, String displayString) {
		addOption(new SelectOption(displayString, value));
	}

	public void setMenuElement(String value) {
		addOption(new SelectOption(value, value));
	}

	public void setDisabledMenuElement(String value, String displayString) {
		addDisabledOption(new SelectOption(displayString,value));
	}

	public void setMenuElementDisplayString(String value, String displayString) {
		setOptionName(value, displayString);
	}

	/**
	* Add menu elements from an Collection of IDOLegacyEntity Objects
	*/
	public void addMenuElements(Collection<?> entityList) {
		if (entityList == null) {
			return;
		}

		for (Object obj: entityList) {
			if (obj instanceof IDOEntity) {
				IDOEntity entity = (IDOEntity) obj;
				addMenuElement(entity.getPrimaryKey().toString(), entity.toString());

				if (getName().equals(untitled) && entity != null) {
					setName(entity.getEntityDefinition().getUniqueEntityName());
				}
			} else if (obj instanceof AdvancedProperty) {
				AdvancedProperty entity = (AdvancedProperty) obj;
				addMenuElement(entity.getId(), entity.getValue());
			}
		}
	}

	/**
	 * Sets the element by value elementValue as selected if it is found in this menu
	 **/
	public void setSelectedElement(String value) {
		setSelectedOption(value);
	}

	/**
	 * Sets the element by value elementValue as selected if it is found in this menu
	 **/
	public void setSelectedElement(int elementValue) {
		setSelectedElement(Integer.toString(elementValue));
	}

	/**
	 * Sets the element by value elementValue as selected if it is found in this menu
	 **/
	public void setSelectedElement(char elementValue) {
		setSelectedElement(String.valueOf(elementValue));
	}

	/**
	 * Gets the elementValue of the selected menuelement if any. If none is selected it returns an empty String.
	 **/
	public String getSelectedElementValue() {
		return getSelectedValue();
	}

	public SelectOption getMenuElement(String elementValue) {
		return getOption(elementValue);
	}

	public void setAttributeToElement(String ElementValue, String AttributeName, String AttributeValue) {
		getMenuElement(ElementValue).setMarkupAttribute(AttributeName, AttributeValue);
	}

	@Override
	public void setValue(String value) {
		Collection<?> entities = null;
		try {
			entities = ELUtil.isExpression(value) ? (Collection<?>) ELUtil.getInstance().evaluateExpression(value) : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (entities == null) {
			super.setValue(value);
		} else {
			addMenuElements(entities);
		}
	}
}