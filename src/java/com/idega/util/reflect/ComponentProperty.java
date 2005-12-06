/*
 * $Id: ComponentProperty.java,v 1.3 2005/12/06 19:03:22 tryggvil Exp $
 * Created on Dec 5, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.reflect;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1065802679114981731L;

	public final static String ACTION_LISTENER_ATTR = "actionListener";
	
	public final static String VALUE_CHANGED_LISTENER_ATTR = "valueChangedListener";
	
	private String name = null;
	
	//private String clazz = null;
	

	/**
	 * Constructs a property with the propertyName (Java beans convention)
	 */
	public ComponentProperty(String propertyName) {
		initialize(propertyName);
	}

	public void initialize(String componentProperty) {
		this.name=componentProperty;
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
		else{
			Class propertyType=null;
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(instance.getClass());
				PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
				for (int i = 0; i < descriptors.length; i++) {
					PropertyDescriptor descriptor = descriptors[i];
					if(descriptor.getName().equals(this.name)){
						propertyType=descriptor.getPropertyType();
						break;
					}
				}
			}
			catch (IntrospectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (Integer.class.equals(propertyType) || Integer.TYPE.equals(propertyType)) {
				UIComponentTagUtils.setIntegerProperty(facesContext, (UIComponent) instance, name, value);
			}
			else if (Boolean.class.equals(propertyType) || Boolean.TYPE.equals(propertyType)) {
				UIComponentTagUtils.setBooleanProperty(facesContext, (UIComponent) instance, name, value);
			}
			else if (String.class.equals(propertyType)) {
				UIComponentTagUtils.setStringProperty(facesContext, (UIComponent) instance, name, value);
			}
			else{
				UIComponent componentInstance = (UIComponent)instance;
				componentInstance.getAttributes().put(name,value);
			}
		}
	}
}
