package com.idega.business.chooser;

import java.util.List;

import org.jdom2.Document;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.IBOService;

public interface ChooserService extends IBOService {

	/**
	 * @see ChooserServiceBean#updateHandler
	 */
	public boolean updateHandler(String[] values);

	/**
	 * @see ChooserServiceBean#setModuleProperty
	 */
	public boolean setModuleProperty(String moduleId, String propertyName, List<AdvancedProperty> properties);

	/**
	 * @see	ChooserServiceBean#getRenderedPresentationObject
	 */
	public Document getRenderedPresentationObject(String className, String hiddenInputAttribute, String chooserObject, String value, String displayValue,
			boolean cleanHtml);

}