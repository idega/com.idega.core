/*
 * Created on 18.8.2003
 */
package com.idega.presentation.ui.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import com.idega.data.IDOEntity;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.GenericSelect;
import com.idega.presentation.ui.SelectOption;
import com.idega.util.reflect.MethodInvoker;

/**
 * @author laddi
 */
public class SelectorUtility {
	
	public GenericSelect getSelectorFromIDOEntities(GenericSelect selector, Collection entities) {
		return getSelectorFromIDOEntities(selector, entities, null);
	}
	
	public GenericSelect getSelectorFromIDOEntities(GenericSelect selector, Collection entities, String methodName) {
		return getSelectorFromIDOEntities(selector, entities, methodName, null);
	}
	
	public GenericSelect getSelectorFromIDOEntities(GenericSelect selector, Collection entities, String methodName, IWResourceBundle resourceBundle) {
		return getSelectorFromIDOEntities(selector, entities, methodName, resourceBundle, null);
	}
	
	public GenericSelect getSelectorFromIDOEntities(GenericSelect selector, Collection entities, String methodName, IWResourceBundle resourceBundle, String defaultReturnValue) {
		if (entities != null) {
			Iterator iter = entities.iterator();
			while (iter.hasNext()) {
				IDOEntity entity = (IDOEntity) iter.next();
				SelectOption option = new SelectOption(entity.getPrimaryKey().toString());
				if (methodName != null) {
					try {
						String value = MethodInvoker.getInstance().invokeMethodWithNoParameters(entity, methodName).toString();
						if (resourceBundle != null) {
							if (defaultReturnValue == null){
								value = resourceBundle.getLocalizedString(value, value);
							}
							else{
								value = resourceBundle.getLocalizedString(value, defaultReturnValue);
							}
						}
						option.setName(value);
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					catch (NoSuchMethodException e) {
						option.setName(entity.toString());
					}
				}
				else {
					option.setName(entity.toString());
				}
				selector.addOption(option);
			}
		}
		return selector;
	}
}