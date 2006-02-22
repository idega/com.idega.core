/*

 * $Id: IWProperty.java,v 1.15 2006/02/22 20:52:47 laddi Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */
package com.idega.idegaweb;
import java.text.Collator;

import com.idega.xml.XMLElement;
/**
 * <p>
 * This class is used by IWPropertyList to hold each property or each
 * &lt;key&gt; tag.
 * </p>
 * Copyright: Copyright (c) 2001-2005 idega software<br/>
 * Last modified: $Date: 2006/02/22 20:52:47 $ by $Author: laddi $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.15 $
 */
public class IWProperty implements Comparable
{
	private XMLElement propertyElement;
	private IWPropertyList parentList;
	static String mapTag = IWPropertyList.mapTag;
	static String nameTag = IWPropertyList.nameTag;
	static String arrayTag = IWPropertyList.arrayTag;
	static String keyTag = IWPropertyList.keyTag;
	static String valueTag = IWPropertyList.valueTag;
	static String typeTag = IWPropertyList.typeTag;
	static String stringTag = IWPropertyList.stringTag;
	static String stringString = IWPropertyList.stringString;
	public static String MAP_TYPE = mapTag;
	IWProperty(IWPropertyList parentList)
	{
		this(null, parentList);
	}
	IWProperty(XMLElement propertyElement, IWPropertyList parentList)
	{
		this.propertyElement = propertyElement;
		this.parentList = parentList;
	}
	private IWPropertyList getParentList()
	{
		return parentList;
	}
	public String getKey()
	{
		return this.getName();
	}
	public String getName()
	{
		return IWProperty.getPropertyName(getKeyElement());
	}
	public String getType()
	{
		return IWProperty.getPropertyType(getKeyElement());
	}
	public boolean getBooleanValue()
	{
		String value = getValue();
		if (value != null)
		{
			if (value.equalsIgnoreCase("true"))
			{
				return true;
			}
			else if (value.equalsIgnoreCase("false"))
			{
				return false;
			}
			else if (value.equalsIgnoreCase("y"))
			{
				return true;
			}
			else if (value.equalsIgnoreCase("n"))
			{
				return false;
			}
			else
			{
				return false;
			}
		}
		return false;
	}
	public String getValue()
	{
		return IWProperty.getPropertyValue(getKeyElement());
	}
	public void setValue(String sValue)
	{
		XMLElement key = getKeyElement();
		XMLElement value = null;
		if (key == null)
		{
			key = createKeyElement();
			value = new XMLElement(valueTag);
		}
		else
		{
			value = key.getChild(valueTag);
			if (value == null)
			{
				value = new XMLElement(valueTag);
			}
		}
		value.setText(sValue);
		key.addContent(value);
		setType(stringString);
	}
	public void setValue(int iValue)
	{
		setValue(new Integer(iValue));
	}
	public void setValue(boolean bValue)
	{
		setValue(new Boolean(bValue));
	}
	public void setValue(Object oValue)
	{
		XMLElement key = getKeyElement();
		XMLElement value = null;
		if (key == null)
		{
			key = createKeyElement();
			value = new XMLElement(valueTag);
		}
		else
		{
			value = key.getChild(valueTag);
			if (value == null)
			{
				value = new XMLElement(valueTag);
			}
		}
		value.setText(oValue.toString());
		key.addContent(value);
		setType(oValue.getClass().getName());
	}
	private void setType(String sType)
	{
		setType(getKeyElement(), getParentList(), sType);
	}
	static void setType(XMLElement key, IWPropertyList plist, String sType)
	{
		XMLElement type = null;
		if (key == null)
		{
			key = createKeyElement(plist);
			type = new XMLElement(typeTag);
		}
		else
		{
			type = key.getChild(typeTag);
			if (type == null)
			{
				type = new XMLElement(typeTag);
			}
		}
		type.setText(sType);
		key.addContent(type);
	}
	public void setName(String sName)
	{
		XMLElement key = getKeyElement();
		XMLElement name = null;
		if (key == null)
		{
			key = createKeyElement();
			name = new XMLElement(nameTag);
		}
		else
		{
			name = key.getChild(nameTag);
			if (name == null)
			{
				name = new XMLElement(nameTag);
			}
		}
		name.setText(sName);
		key.addContent(name);
	}
	public void setProperty(String key, int value)
	{
		setProperty(key, new Integer(value));
	}
	public void setProperty(String key, boolean value)
	{
		setProperty(key, new Boolean(value));
	}
	public void setProperty(String key, String value)
	{
		setProperty(key, value, stringString);
	}
	public void setProperty(String key, Object[] value)
	{
		XMLElement keyElement = this.getKeyElement();
		setProperty(keyElement, key, value, getParentList());
	}
	private void setProperty(String key, Object value, String type)
	{
		XMLElement keyElement = getKeyElement();
		setProperty(keyElement, key, value, type, getParentList());
	}
	static void setProperty(XMLElement keyElement, String key, Object value, String type, IWPropertyList list)
	{
		if (keyElement == null)
		{
			addProperty(key, value, type, list);
		}
		else
		{
			XMLElement typeElement = keyElement.getChild(typeTag);
			if (typeElement.getText().equals(arrayTag))
			{
				keyElement.getChild(valueTag);
			}
			else
			{
				keyElement.removeChild(nameTag);
				keyElement.removeChild(valueTag);
				keyElement.removeChild(typeTag);
				addNewProperty(keyElement, key, value, type);
			}
		}
	}
	static void setProperty(XMLElement keyElement, String key, Object[] value, IWPropertyList list)
	{
		if (keyElement == null)
		{
			addProperty(key, value, arrayTag, list);
		}
		else
		{
			keyElement.removeChild(nameTag);
			keyElement.removeChild(valueTag);
			keyElement.removeChild(typeTag);
			addNewProperty(keyElement, key, value, arrayTag);
		}
	}
	public void setProperty(String key, Object value)
	{
		setProperty(key, value.toString(), value.getClass().getName());
	}
	/**
	
	 * Throws IWNotPropertyListException if this IWProperty has a Single Property not a PropertyList
	
	 */
	public IWPropertyList getPropertyList() throws IWNotPropertyListException
	{
		return getPropertyList(getKeyElementAndCreateIfNotExists());
	}
	/**
	
	 * Throws IWNotPropertyListException if this IWProperty has a Single Property not a PropertyList
	
	 */
	static IWPropertyList getPropertyList(XMLElement keyElement) throws IWNotPropertyListException
	{
		XMLElement valueElement = getValueElement(keyElement);
		String type = getPropertyType(keyElement);
		if (type != null)
		{
			if (type.equals(mapTag))
			{
				if (valueElement != null)
				{
					return new IWPropertyList(valueElement);
				}
			}
			else
			{
				throw new IWNotPropertyListException(getPropertyName(keyElement));
			}
		}
		return null;
	}
	public IWPropertyList getNewPropertyList(String key)
	{
		setName(key);
		return getNewPropertyList();
	}
	public IWPropertyList getNewPropertyList()
	{
		return getNewPropertyList(getKeyElementAndCreateIfNotExists(), getParentList());
	}
	public void setPropertyList(IWPropertyList childPropertyList){
		XMLElement keyElement = getKeyElementAndCreateIfNotExists();
		XMLElement valueElement = getValueElement(keyElement);
		setChildPropertyList(keyElement,valueElement,getParentList(),childPropertyList);
	}
	static IWPropertyList getNewPropertyList(XMLElement keyElement, IWPropertyList plist)
	{
		XMLElement valueElement = getValueElement(keyElement);
		IWPropertyList childList = new IWPropertyList(valueElement);
		setChildPropertyList(keyElement,valueElement,plist,childList);
		return childList;
	}
	static void setChildPropertyList(XMLElement keyElement,XMLElement valueElement, IWPropertyList parentPropertyList,IWPropertyList childPropertyList)
	{
		if (valueElement != null)
		{
			valueElement.removeChildren();
			//IWPropertyList list = new IWPropertyList(valueElement);
			childPropertyList.setParentElement(valueElement);
			setType(keyElement, parentPropertyList, mapTag);
		}
	}
	
	static void addProperty(String key, Object value, String type, IWPropertyList plist)
	{
		XMLElement keyElement = createKeyElement(plist);
		addNewProperty(keyElement, key, value, type);
	}
	private XMLElement getKeyElementAndCreateIfNotExists()
	{
		XMLElement key = getKeyElement();
		if (key == null)
		{
			key = createKeyElement();
		}
		return key;
	}
	private XMLElement createKeyElement()
	{
		this.propertyElement = createKeyElement(getParentList());
		return propertyElement;
	}
	static XMLElement createKeyElement(IWPropertyList list)
	{
		XMLElement keyElement = new XMLElement(keyTag);
		if (list != null)
		{
			list.getMapElement().addContent(keyElement);
		}
		return keyElement;
	}
	static XMLElement createKeyElement(IWPropertyList list, String keyName)
	{
		XMLElement keyElement = new XMLElement(keyTag);
		XMLElement nameElement = new XMLElement(nameTag);
		nameElement.addContent(keyName);
		keyElement.addContent(nameElement);
		if (list != null)
		{
			list.getMapElement().addContent(keyElement);
		}
		return keyElement;
	}
	private XMLElement getKeyElement()
	{
		return this.propertyElement;
	}
	static String getPropertyName(XMLElement keyElement)
	{
		if (keyElement != null)
			return keyElement.getChild(nameTag).getText();
		return null;
	}
	static String getPropertyType(XMLElement keyElement)
	{
		if (keyElement != null)
		{
			XMLElement child = keyElement.getChild(typeTag);
			if (child != null)
				return child.getText();
		}
		return null;
	}
	static String getPropertyValue(XMLElement keyElement)
	{
		if (keyElement != null)
			return keyElement.getChild(valueTag).getText();
		return null;
	}
	static XMLElement createArrayElement(XMLElement valueElement)
	{
		XMLElement arrayElement = new XMLElement(arrayTag);
		valueElement.addContent(arrayElement);
		return arrayElement;
	}
	static XMLElement getValueElement(XMLElement keyElement)
	{
		XMLElement value = keyElement.getChild(valueTag);
		if (value == null)
		{
			value = createValueElement(keyElement);
		}
		return value;
	}
	static XMLElement createValueElement(XMLElement parent)
	{
		XMLElement valueElement = new XMLElement(valueTag);
		parent.addContent(valueElement);
		return valueElement;
	}
	static boolean valueContains(XMLElement valueElement, Object value)
	{
		return valueElement.getText().equals(value.toString());
	}
	static void setValue(XMLElement valueElement, Object value)
	{
		valueElement.addContent(value.toString());
	}
	static void addNewProperty(XMLElement key, String keyName, Object value, String type)
	{
		XMLElement nameElement = new XMLElement(nameTag);
		nameElement.addContent(keyName);
		XMLElement typeElement = new XMLElement(typeTag);
		typeElement.addContent(type);
		XMLElement valueElement = new XMLElement(valueTag);
		if (type.equals(arrayTag))
		{
			XMLElement arrayElement = new XMLElement(arrayTag);
			valueElement.addContent(arrayElement);
			try
			{
				Object[] theArray = (Object[]) value;
				for (int i = 0; i < theArray.length; i++)
				{
					XMLElement newValueElement = new XMLElement(valueTag);
					setValue(newValueElement, theArray[i]);
					arrayElement.addContent(newValueElement);
				}
			}
			catch (ClassCastException ex)
			{
				XMLElement newValueElement = new XMLElement(valueTag);
				setValue(newValueElement, value);
				arrayElement.addContent(newValueElement);
			}
		}
		else
		{
			setValue(valueElement, value);
		}
		key.addContent(nameElement);
		key.addContent(typeElement);
		key.addContent(valueElement);
	}
	public String toString()
	{
		return getValue();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		int returner = 0;
		if (o instanceof IWProperty) {
			IWProperty object = (IWProperty) o;
			returner = Collator.getInstance().compare(this.getName(), object.getName());
			if (returner == 0) {
				returner = Collator.getInstance().compare(this.getValue(), object.getValue());
			}
		}
		return returner;
	}
}
