package com.idega.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

public class CoreUtil {
	
	private static final BASE64Encoder ENCODER_BASE64 = new BASE64Encoder();
	private static final BASE64Decoder DECODER_BASE64 = new BASE64Decoder();
	
	public static IWBundle getCoreBundle() {
		return IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER);
	}
	
	/**
	 * Almost identical method to {@link IWContext#getInstance()} just this one doesn't throw any exception - it's very important
	 * using it with DWR
	 * @return
	 */
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
	
	public static final List<String> getResourcesForChooser(IWContext iwc) {
		IWBundle iwb = getCoreBundle();
		
		List<String> resources = new ArrayList<String>();
		resources.add(iwb.getVirtualPathWithFileNameString("javascript/ChooserHelper.js"));
		resources.add(CoreConstants.DWR_ENGINE_SCRIPT);
		resources.add("/dwr/interface/ChooserService.js");
		resources.add(CoreConstants.GROUP_SERVICE_DWR_INTERFACE_SCRIPT);
		
		IWBundle userBundle = iwc.getIWMainApplication().getBundle(CoreConstants.IW_USER_BUNDLE_IDENTIFIER);
		if (userBundle != null) {
			resources.add(userBundle.getVirtualPathWithFileNameString("javascript/GroupInfoViewerHelper.js"));
			resources.add(userBundle.getVirtualPathWithFileNameString("javascript/GroupHelper.js"));
			resources.add(userBundle.getVirtualPathWithFileNameString("javascript/groupTree.js"));
			resources.add(userBundle.getVirtualPathWithFileNameString("javascript/UserInfoViewerHelper.js"));
		}
		
		return resources;
	}
	
	public static String getEncodedValue(String originalValue) {
		if (originalValue == null) {
			return null;
		}
		return ENCODER_BASE64.encode(originalValue.getBytes());
	}
	
	public static String getDecodedValue(String encodedValue) {
		if (encodedValue == null) {
			return null;
		}
		try {
			return new String(DECODER_BASE64.decodeBuffer(encodedValue));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isIE(HttpServletRequest request) {
		if (request == null) {
			return false;
		}
		
		String userAgent = RequestUtil.getUserAgent(request);
		if (userAgent != null) {
			return userAgent.indexOf("MSIE") != -1;
		}
		
		return false;
	}
	
	public static boolean isSingleComponentRenderingProcess(FacesContext context) {
		if (context == null) {
			return false;
		}
		
		return isSingleComponentRenderingProcess(IWContext.getIWContext(context));
	}

	public static boolean isSingleComponentRenderingProcess(IWContext iwc) {
		if (iwc == null) {
			return false;
		}
		
		Object attribute = iwc.getSessionAttribute(CoreConstants.SINGLE_UICOMPONENT_RENDERING_PROCESS);
		if (attribute instanceof Boolean) {
			return (Boolean) attribute;
		}
		
		return false;
	}
	
	public static Boolean getBooleanValueFromString(String value) {
		if (value == null) {
			return false;
		}
		
		if (value.equalsIgnoreCase(CoreConstants.BUILDER_MODULE_PROPERTY_YES_VALUE)) {
			return true;
		}
		if (value.equalsIgnoreCase("T")) {
			return true;
		}
		if (value.equalsIgnoreCase(CoreConstants.BUILDER_MODULE_PROPERTY_NO_VALUE)) {
			return false;
		}
		if (value.equalsIgnoreCase("F")) {
			return false;
		}
		
		return Boolean.valueOf(value.toLowerCase());
	}
}
