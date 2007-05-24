package com.idega.util;

import javax.faces.context.FacesContext;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

public class CoreUtil {
	
	public static IWBundle getCoreBundle() {
		return IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER);
	}
	
	public static IWContext getIWContext() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context == null) {
				return null;
			}
			return IWContext.getIWContext(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addJavaSciptForChooser(IWContext iwc) {
		AddResource adder = AddResourceFactory.getInstance(iwc);
		
		IWBundle iwb = getCoreBundle();
		
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, iwb.getVirtualPathWithFileNameString("javascript/ChooserHelper.js"));
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, "/dwr/engine.js");
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, "/dwr/interface/ChooserService.js");
	}

}
