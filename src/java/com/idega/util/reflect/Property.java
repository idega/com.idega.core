/*
 * $Id: Property.java,v 1.20 2009/01/21 10:03:33 valdas Exp $ Created on 21.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.util.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.business.IBOLookup;
import com.idega.business.chooser.helper.CalendarsChooserHelper;
import com.idega.business.chooser.helper.GroupsChooserHelper;
import com.idega.cal.bean.CalendarPropertiesBean;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.handlers.IWDatePickerHandler;
import com.idega.user.bean.PropertiesBean;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;

/**
 *
 * This class holds an instance of a property with its value(s).
 * A property is in this case a setter method that has attatched set values (as a String or Object array).<br>
 * This is used in the Builder where properties are set via this class on PresentationObject instances.
 *
 * Last modified: $Date: 2009/01/21 10:03:33 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvi@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.20 $
 */
public class Property implements Serializable {

	private static final long serialVersionUID = 4451503674746022678L;

	private String propertyName;

	private Method method;

	private Object[] propertyValues;

	public Property() {
		// doing nothing, called by subclass
	}

	/**
	 * Construct a property from a so called methodIdentifier that is used to construct a Method instance.
	 * @param methodIdentifier a string in the format ':method:[modifierInt]:[classIdentifier]:[returnClass]:[methodName]:[parameterClass]:' , example: ':method:1:com.idega.presentation.Table:void:setWidth:java.lang.String:'
	 */
	public Property(String methodIdentifier) {
		this(methodIdentifier, null);
	}

	/**
	 * Construct a property from a so called methodIdentifier that is used to construct a Method instance.
	 * @param methodIdentifier a string in the format ':method:[modifierInt]:[classIdentifier]:[returnClass]:[methodName]:[parameterClass]:' , example: ':method:1:implied:void:setWidth:java.lang.String:'
	 * @param declaringClass The class that the method is declared in.
	 */
	public Property(String methodIdentifier, Class<?> declaringClass) {
		if (declaringClass != null) {
			setMethod(getMethodFinder().getMethod(methodIdentifier, declaringClass));
		}
		else {
			setMethod(getMethodFinder().getMethod(methodIdentifier));
		}
	}

	/**
	 * Construct a property from a given Method instance.
	 * @param method
	 */
	public Property(Method method) {
		this.setMethod(method);
	}

	private MethodFinder getMethodFinder() {
		return MethodFinder.getInstance();
	}

	/**
	 * @return Returns the method.
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * @param method
	 *            The method to set.
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * @return Returns the propertyName.
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * @param propertyName
	 *            The propertyName to set.
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return Returns the propertyValues.
	 */
	public Object[] getPropertyValues() {
		return this.propertyValues;
	}

	/**
	 * @param propertyValues
	 *            The propertyValues to set.
	 */
	public void setPropertyValues(String[] stringPropertyValues) {
		//this.propertyValues = propertyValues;
		Object[] args = new Object[stringPropertyValues.length];
		Class<?>[] parameterTypes = getMethod().getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i] != null) {
				String aString = stringPropertyValues[i];
				try {
					args[i] = convertStringToObject(parameterTypes[i], aString);
				}
				catch (Exception e) {
					System.err.println("Error in property '" + this.method.toString());
					e.printStackTrace();
				}
			}
		}
		setPropertyValues(args);
	}

	/**
	 * @param propertyValues
	 *            The propertyValues to set.
	 */
	public void setPropertyValues(Object[] propertyValues) {
		this.propertyValues = propertyValues;
	}

	/**
	 * Sets the property on the object instance
	 *
	 * @param instance
	 */
	public void setPropertyOnInstance(Object  instance) {
		try {
			getMethod().invoke(instance, getPropertyValues());
		}
		catch (Exception e) {
			if (instance instanceof PresentationObject) {
				PresentationObject po = (PresentationObject) instance;
				System.err.println("Error in property '" + getMethod().toString() + "' for "+instance.getClass().getName()+" with ICObjectInstanceId="
					+ po.getICObjectInstanceID());
			}
			else {
				System.err.println("Error in property '" + getMethod().toString() + "' for instance="
					+ instance.toString());
			}
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T>T getValueFromExpression(String expression, Class<T> expectedResultType) {
		if (StringUtil.isEmpty(expression) || expectedResultType == null) {
			return null;
		}
		if (!expression.startsWith("#{") && !expression.endsWith("}")) {
			return null;
		}

		FacesContext fcContext = FacesContext.getCurrentInstance();
		ELContext elContext = fcContext.getELContext();
		ValueExpression ve = fcContext.getApplication().getExpressionFactory().createValueExpression(elContext, expression, expectedResultType);
		return (T) ve.getValue(elContext);
	}

	//Moved from ComponentPropertyHandler (in builder)
	protected Object convertStringToObject(Class<?> parameterType, String stringValue) throws Exception {
		Object argument = null;

		try {
			argument = getValueFromExpression(stringValue, parameterType);
		} catch(Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Failed to get by value from ValueExpression by expression: " + stringValue, e);
		}
		if (argument != null)
			return argument;

		try {
			if (parameterType.equals(Integer.class) || parameterType.equals(Integer.TYPE)) {
				argument = Integer.valueOf(stringValue);
			}
			else if (parameterType.equals(String.class)) {
				argument = stringValue;
			}
			else if (parameterType.equals(Boolean.class) || parameterType.equals(Boolean.TYPE)) {
				argument = CoreUtil.getBooleanValueFromString(stringValue);
			}
			else if (parameterType.equals(Float.class) || parameterType.equals(Float.TYPE)) {
				argument = new Float(stringValue);
			}
			else if (parameterType.equals(ICPage.class)) {
				argument = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(stringValue));
			}
			else if (parameterType.equals(ICFile.class)) {
				argument = ((com.idega.core.file.data.ICFileHome) com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(
							stringValue));
			}
			else if (parameterType.equals(Image.class)) {
				argument = new Image(Integer.parseInt(stringValue));
			}
			//REMOVE AND MAKE GENERIC! ask tryggvi and eiki
			else if (parameterType.equals(Group.class)) {
				argument = ((GroupHome) com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer(
							stringValue.substring(stringValue.lastIndexOf('_') + 1, stringValue.length())));
			}
			// added because of JSF methods
			else if (parameterType.equals(Object.class)) {
				// nothing to cast
				argument = stringValue;
			}
			else if (parameterType.equals(PasswordInput.class)) {
				argument = new PasswordInput();
				if (stringValue != null) {
					((PasswordInput) argument).setContent(stringValue);
				}
			}
			else if (parameterType.equals(List.class)) {
				argument = StringUtil.getValuesFromString(stringValue, ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR);
			}
			else if (parameterType.equals(PropertiesBean.class)) {
				GroupsChooserHelper helper = new GroupsChooserHelper();
				argument = helper.getExtractedPropertiesFromString(stringValue);
			}
			else if (parameterType.equals(CalendarPropertiesBean.class)) {
				CalendarsChooserHelper helper = new CalendarsChooserHelper();
				argument = helper.getExtractedPropertiesFromString(stringValue);
			}
			else if (parameterType.equals(Locale.class)) {
				argument = ICLocaleBusiness.getLocaleFromLocaleString(stringValue);
			}
			else if (parameterType.equals(java.util.Date.class)) {
				argument = IWDatePickerHandler.getParsedDate(stringValue);
			}
			else if (parameterType.equals(ICPage.class)) {
				BuilderService builder = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), BuilderService.class);
				argument = builder.getICPage(stringValue);
			}
		} catch(Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Failed to get value from expression: " + stringValue + ", expected type: " +
					parameterType);
		}

		return argument;
	}
}