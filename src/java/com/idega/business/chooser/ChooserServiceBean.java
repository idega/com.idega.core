package com.idega.business.chooser;

import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.jdom.Document;

import com.idega.bean.AdvancedProperty;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.util.AbstractChooserBlock;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;

public class ChooserServiceBean extends IBOServiceBean implements ChooserService {

	private static final long serialVersionUID = -6325150611506329083L;
	
	private BuilderService service = null;
	
	private synchronized BuilderService getBuilderService(IWContext iwc) {
		if (service == null) {
			if (iwc == null) {
				iwc = CoreUtil.getIWContext();
			}
			try {
				service = BuilderServiceFactory.getBuilderService(iwc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}
	
	public boolean updateHandler(String[] values) {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}
		
		HttpSession session = iwc.getSession();
		if (session == null) {
			return false;
		}
		
		Object o = session.getAttribute(CoreConstants.HANDLER_PARAMETER);
		if (!(o instanceof ICPropertyHandler)) {
			return false;
		}
		ICPropertyHandler handler = (ICPropertyHandler) o;
		if (handler == null) {
			return false;
		}
		
		handler.onUpdate(values, iwc);
		
		return true;
	}
	
	private String[] getPropertyValueForGroupsChooser(IWContext iwc, List<AdvancedProperty> properties) {
		String server = null;
		String login = null;
		String password = null;
		String uniqueIds = null;
		
		String connection = findPropertyValue(properties, "connection");
		if (connection == null) {
			return null;
		}
		
		if (connection.equals(ICBuilderConstants.GROUPS_CHOOSER_REMOTE_CONNECTION)) {
			//	Settings for remote connection
			server = findPropertyValue(properties, "server");
			login = findPropertyValue(properties, "login");
			password = CoreUtil.getEncodedValue(findPropertyValue(properties, "password"));
		}
		else {
			//	Settings for local connection
			server = connection;
			login = connection;
			password = connection;
		}
		uniqueIds = findPropertyValue(properties, "uniqueids");
		
		if (server == null || login == null || password == null || uniqueIds == null) {
			return null;
		}
		StringBuffer value = new StringBuffer(server).append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR);
		value.append(login).append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR).append(password);
		value.append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR).append(uniqueIds);
		value.append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR).append(connection);
		return new String[] {value.toString()};
	}
	
	public boolean setModuleProperty(String moduleId, String propertyName, List<AdvancedProperty> properties) {
		if (propertyName == null) {
			return false;
		}
		if (properties == null) {
			return false;
		}
		if (properties.size() == 0) {
			return false;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}
		
		String[] parsedProperties = null;
		if (propertyName.equals(":method:1:implied:void:setGroups:com.idega.bean.PropertiesBean:")) {
			parsedProperties = getPropertyValueForGroupsChooser(iwc, properties);
			if (parsedProperties == null) {
				return false;
			}
		}
		else {
			parsedProperties = new String[properties.size()];
			for (int i = 0; i < properties.size(); i++) {
				parsedProperties[i] = properties.get(i).getValue();
			}
		}
		
		String pageKey = null;
		try {
			pageKey = getBuilderService(iwc).getCurrentPageKey(iwc);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		
		return getBuilderService(null).setModuleProperty(pageKey, moduleId, propertyName, parsedProperties);
	}
	
	public Document getRenderedPresentationObject(String className, String hiddenInputAttribute, boolean cleanHtml) {
		Object o = getObjectInstance(className);
		if (hiddenInputAttribute != null) {
			if (o instanceof AbstractChooserBlock) {
				((AbstractChooserBlock) o).setHiddenInputAttribute(hiddenInputAttribute);
			}
		}
		return getRenderedPresentationObject(o, cleanHtml);
	}
	
	private Document getRenderedPresentationObject(Object object, boolean cleanHtml) {
		if (object instanceof PresentationObject) {
			IWContext iwc = CoreUtil.getIWContext();
			return getBuilderService(iwc).getRenderedPresentationObject(iwc, (PresentationObject) object, cleanHtml);
		}
		return null;
	}
	
	private Object getObjectInstance(String className) {
		if (className == null) {
			return null;
		}
		Class objectClass = null;
		try {
			objectClass = RefactorClassRegistry.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		Object o = null;
		try {
			o = objectClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return o;
	}
	
	private String findPropertyValue(List<AdvancedProperty> properties, String id) {
		String value = null;
		boolean found = false;
		AdvancedProperty property = null;
		for (int i = 0; (i < properties.size() && !found); i++) {
			property = properties.get(i);
			if (id.equals(property.getId())) {
				value = property.getValue();
				found = true;
			}
		}
		return value;
	}

}
