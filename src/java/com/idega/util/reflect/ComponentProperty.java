/*
 * $Id: ComponentProperty.java,v 1.2 2005/12/06 17:11:15 thomas Exp $
 * Created on Dec 5, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.reflect;

import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.apache.myfaces.renderkit.JSFAttr;
import org.apache.myfaces.taglib.UIComponentTagUtils;

/**
 * This class holds an instance of a property with its value(s).
 * A property is in this case an attribute value pair for a JSF component<br>
 * This is used in the Builder where properties are set via this class on JSF components.
 * 
 */ 

public class ComponentProperty extends Property {
	
	public final static String ACTION_LISTENER_ATTR = "actionListener";
	
	public final static String VALUE_CHANGED_LISTENER_ATTR = "valueChangedListener";
	
	private String name = null;
	
	private String clazz = null;
	

/**
 * Constructs a property
 * @param componentProperty componentIdentifier a string in the format ':componentProperty:[componentProperty]:[parameterClass]:' , 
 * example: ':componentProperty:value:java.lang.String:'

 */
	public ComponentProperty(String componentProperty) {
		initialize(componentProperty);
	}

	public void initialize(String componentProperty) {
		StringTokenizer tokenizer = new StringTokenizer(componentProperty ,":");
		if (tokenizer.countTokens() == 3) {
			tokenizer.nextToken();
			name =tokenizer.nextToken();
			clazz = tokenizer.nextToken();
		}
	}
	
	/**
	 * @param propertyValues
	 *            The propertyValues to set.
	 */
	public void setPropertyValues(String[] stringPropertyValues) {
		// tricky: super class has such a method
		setPropertyValues((Object[]) stringPropertyValues);
	}	
	
	/**
	 * Sets the property on the object instance
	 * 
	 * @param instance
	 */
	public void setPropertyOnInstance(Object  instance) {
		Object[] values = getPropertyValues();
		String value = (String) values[0];
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (JSFAttr.ACTION_ATTR.equals(name)) {
			UIComponentTagUtils.setActionProperty(facesContext, (UIComponent) instance, value);
		}
		else if (JSFAttr.CONVERTER_ATTR.equals(name)) {
			UIComponentTagUtils.setConverterProperty(facesContext, (UIComponent) instance, value);
		}
		else if (JSFAttr.VALIDATOR_ATTR.equals(name)) {
			UIComponentTagUtils.setValidatorProperty(facesContext, (UIComponent) instance, value);
		}
		else if (JSFAttr.VALUE_ATTR.equals(name)) {
			UIComponentTagUtils.setValueProperty(facesContext, (UIComponent) instance, value);
		}
		// where is actionListener attribute defined?
		else if (ACTION_LISTENER_ATTR.equals(name)) {
			UIComponentTagUtils.setActionListenerProperty(facesContext, (UIComponent) instance, value);
		}
		// where is valueChangedListener attribute defined?
		else if (VALUE_CHANGED_LISTENER_ATTR.equals(name)) {
			UIComponentTagUtils.setValueChangedListenerProperty(facesContext, (UIComponent) instance, value);
		}
		else if (Integer.class.getName().equals(clazz) || Integer.TYPE.getName().equals(clazz)) {
			UIComponentTagUtils.setIntegerProperty(facesContext, (UIComponent) instance, name, value);
		}
		else if (Boolean.class.getName().equals(clazz) || Boolean.TYPE.getName().equals(clazz)) {
			UIComponentTagUtils.setBooleanProperty(facesContext, (UIComponent) instance, name, value);
		}
		else if (String.class.getName().equals(clazz)) {
			UIComponentTagUtils.setBooleanProperty(facesContext, (UIComponent) instance, name, value);
		}
	}
}
