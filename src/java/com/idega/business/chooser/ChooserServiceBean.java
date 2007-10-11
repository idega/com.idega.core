package com.idega.business.chooser;

import java.rmi.RemoteException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import org.jdom.Document;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
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
		
		handler.onUpdate(values, iwc);
		
		return true;
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
		
		String pageKey = null;
		try {
			pageKey = getBuilderService(iwc).getCurrentPageKey(iwc);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		
		return getBuilderService(iwc).setProperty(iwc, pageKey, moduleId, propertyName, properties);
	}
	
	public Document getRenderedPresentationObject(String className, String hiddenInputAttribute, String chooserObject, boolean cleanHtml) {
		Object o = getObjectInstance(className);
		
		if (hiddenInputAttribute != null) {
			if (o instanceof AbstractChooserBlock) {
				((AbstractChooserBlock) o).setHiddenInputAttribute(hiddenInputAttribute);
			}
		}
		if ((chooserObject != null) && (o instanceof AbstractChooserBlock)) {
			((AbstractChooserBlock) o).setChooserObject(chooserObject);
		}
		
		return getRenderedPresentationObject(o, cleanHtml);
	}
	
	private Document getRenderedPresentationObject(Object object, boolean cleanHtml) {
		if (object instanceof UIComponent) {
			IWContext iwc = CoreUtil.getIWContext();
			return getBuilderService(iwc).getRenderedComponent(iwc, (UIComponent) object, cleanHtml);
		}
		return null;
	}
	
	private Object getObjectInstance(String className) {
		if (className == null) {
			return null;
		}
		Class<?> objectClass = null;
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

}
