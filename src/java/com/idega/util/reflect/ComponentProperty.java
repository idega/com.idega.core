/*
 * $Id: ComponentProperty.java,v 1.8 2008/03/18 12:52:29 valdas Exp $
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

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.apache.myfaces.shared_tomahawk.renderkit.JSFAttr;
import org.apache.myfaces.shared_tomahawk.taglib.UIComponentELTagUtils;
import org.apache.myfaces.shared_tomahawk.taglib.UIComponentTagUtils;

import com.idega.util.CoreUtil;

/**
 * This class holds an instance of a property with its value(s).
 * A property is in this case an attribute value pair for a JSF component<br>
 * This is used in the Builder where properties are set via this class on JSF components.
 * 
 */ 
public class ComponentProperty extends Property {
	
	private static final long serialVersionUID = 1065802679114981731L;

	public final static String ACTION_LISTENER_ATTR = "actionListener";
	public final static String VALUE_CHANGED_LISTENER_ATTR = "valueChangedListener";
	
	private String name = null;
	private Class<?> componentClass;
	private Class<?> propertyType;
	
	/**
	 * Constructs a property with the propertyName (Java beans convention) and the componentClass 
	 * declaring this property
	 */
	public ComponentProperty(String propertyName, Class<?> componentClass) {
		initialize(propertyName,componentClass);
	}

	public void initialize(String componentProperty, Class<?> componentClass) {
		this.name=componentProperty;
		this.componentClass=componentClass;
	}
	
	/**
	 * @param propertyValues
	 *            The propertyValues to set.
	 */
	@Override
	public void setPropertyValues(String[] stringPropertyValues) {
		// tricky: super class has such a method
		setPropertyValues((Object[]) stringPropertyValues);
	}	
	
	/**
	 * Sets the property on the object instance
	 * 
	 * @param instance
	 */
	@Override
	public void setPropertyOnInstance(Object instance) {
		Object[] values = getPropertyValues();
		String value = (String) values[0];

		UIComponent component = (UIComponent) instance;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExpressionFactory exprFactory = facesContext.getApplication().getExpressionFactory();
		
		Class<?> propertyType = getPropertyType();
		if (JSFAttr.ACTION_ATTR.equals(this.name)) {
//			UIComponentELTagUtils.setActionProperty(facesContext, component, exprFactory.createMethodExpression(facesContext.getELContext(), value, propertyType,
//			null));
			UIComponentTagUtils.setActionProperty(facesContext, component, value);
		}
		else if (JSFAttr.CONVERTER_ATTR.equals(this.name)) {
			UIComponentTagUtils.setConverterProperty(facesContext, component, value);
		}
		else if (JSFAttr.VALIDATOR_ATTR.equals(this.name)) {
			UIComponentTagUtils.setValidatorProperty(facesContext, component, value);
		}
		else if (JSFAttr.VALUE_ATTR.equals(this.name)) {
			UIComponentTagUtils.setValueProperty(facesContext, component, value);
		}
		// where is actionListener attribute defined?
		else if (ACTION_LISTENER_ATTR.equals(this.name)) {
			UIComponentTagUtils.setActionListenerProperty(facesContext, component, value);
		}
		// where is valueChangedListener attribute defined?
		else if (VALUE_CHANGED_LISTENER_ATTR.equals(this.name)) {
			UIComponentTagUtils.setValueChangedListenerProperty(facesContext, component, value);
		} else {
			boolean booleanType = Boolean.class.equals(propertyType) || Boolean.TYPE.equals(propertyType);
			value = booleanType ? String.valueOf(CoreUtil.getBooleanValueFromString(value)) : value;
			ValueExpression ve = exprFactory.createValueExpression(facesContext.getELContext(), value, propertyType);
			
			if (Integer.class.equals(propertyType) || Integer.TYPE.equals(propertyType)) {
				UIComponentELTagUtils.setIntegerProperty(component, this.name, ve);
			} else if (booleanType) {
				UIComponentELTagUtils.setBooleanProperty(component, this.name, ve);
			} else if (Long.class.equals(propertyType) || Long.TYPE.equals(propertyType)) {
				UIComponentELTagUtils.setLongProperty(component, this.name, ve);
			} else if (String.class.equals(propertyType)) {
				UIComponentELTagUtils.setStringProperty(component, this.name, ve);
			} else {
				UIComponentELTagUtils.setValueBinding(facesContext, component, name, ve);
			}
		}
	}
	
	public Class<?> getPropertyType(){
		if(this.propertyType==null){
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(this.componentClass);
				PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
				for (int i = 0; i < descriptors.length; i++) {
					PropertyDescriptor descriptor = descriptors[i];
					if(descriptor.getName().equals(this.name)){
						this.propertyType=descriptor.getPropertyType();
						break;
					}
				}
			}
			catch (IntrospectionException e) {
				e.printStackTrace();
			}
		}
		return this.propertyType;
	}
}
