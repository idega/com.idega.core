package com.idega.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;

@Service("webUtil")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class WebUtil extends DefaultSpringBean {

	private static final Logger LOGGER = Logger.getLogger(WebUtil.class.getName());
	
	public String getLocalizedString(String bundleIdentifier, String key, String returnValueIfNotFound) {
		return getMultipleLocalizedStrings(bundleIdentifier, Arrays.asList(
				new AdvancedProperty(key, returnValueIfNotFound)
		)).get(0);
	}
	
	public List<String> getMultipleLocalizedStrings(String bundleIdentifier, List<AdvancedProperty> multipleRequiredLocalizations) {
		List<String> defaultValues = getDefaultValues(multipleRequiredLocalizations);
		if (ListUtil.isEmpty(multipleRequiredLocalizations)) {
			return defaultValues;
		}
		
		IWBundle bundle = getBundle(bundleIdentifier);
		if (bundle == null) {
			LOGGER.warning("Bundle was not found by identifier: ".concat(bundleIdentifier));
			return defaultValues;
		}
		
		IWResourceBundle iwrb = getResourceBundle(bundle);
		if (iwrb == null) {
			LOGGER.warning("Unable to resolve resource bundle from bundle: " + bundle);
			return defaultValues;
		}
		
		List<String> localizations = new ArrayList<String>(multipleRequiredLocalizations.size());
		for (AdvancedProperty localizationRequest: multipleRequiredLocalizations) {
			localizations.add(iwrb.getLocalizedString(localizationRequest.getId(), localizationRequest.getValue()));
		}
		return localizations;
	}
	
	private List<String> getDefaultValues(List<AdvancedProperty> parameters) {
		if (ListUtil.isEmpty(parameters)) {
			return null;
		}
		
		List<String> defaultValues = new ArrayList<String>(parameters.size());
		for (AdvancedProperty parameter: parameters) {
			defaultValues.add(parameter.getValue());
		}
		return defaultValues;
	}
}