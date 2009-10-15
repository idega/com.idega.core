package com.idega.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.RequestProvider;
import com.idega.user.data.User;
import com.idega.util.expression.ELUtil;

public class CoreUtil {
	
	private static final Logger LOGGER = Logger.getLogger(CoreUtil.class.getName());
	
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
			LOGGER.log(Level.WARNING, IWContext.class.getName() + " is unavailable", e);
			return null;
		}
	}
	
	public static final List<String> getResourcesForChooser(IWContext iwc) {
		IWBundle iwb = getCoreBundle();
		
		List<String> resources = new ArrayList<String>();
		
		//	DWR
		resources.add("/dwr/interface/ChooserService.js");
		resources.add(CoreConstants.GROUP_SERVICE_DWR_INTERFACE_SCRIPT);
		resources.add(CoreConstants.DWR_ENGINE_SCRIPT);
		
		//	Chooser
		resources.add(iwb.getVirtualPathWithFileNameString("javascript/ChooserHelper.js"));
		
		//	Groups and users choosers
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
			LOGGER.log(Level.WARNING, "Error decoding: " + encodedValue, e);
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
	
	public static boolean sendExceptionNotification(Throwable exception) {
    	String host = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(CoreConstants.PROP_SYSTEM_SMTP_MAILSERVER);
    	if (StringUtil.isEmpty(host)) {
    		LOGGER.log(Level.WARNING, "Unable to send email about exception", exception);
    		return false;
    	}
    	
    	String serverName = null;
    	String requestUri = null;
    	try {
    		HttpServletRequest request = ELUtil.getInstance().getBean(RequestProvider.class).getRequest();
    		serverName = request.getServerName();
    		requestUri = request.getRequestURI();
    	} catch(Exception e) {
    		LOGGER.log(Level.WARNING, "Error getting " + RequestProvider.class, e);
    	}
    	serverName = StringUtil.isEmpty(serverName) ? "unknown" : serverName;
    	requestUri = StringUtil.isEmpty(requestUri) ? "unknown" : requestUri;

    	String userFullName = null;
    	try {
    		LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
    		User loggedInUser = loginSession.getUser();
    		userFullName = loggedInUser == null ? null : (loggedInUser.getName() + ", user ID: " + loggedInUser.getId());
    	} catch (Exception e) {
    		LOGGER.log(Level.WARNING, "Error getting " + LoginSession.class, e);
    	}
    	userFullName = StringUtil.isEmpty(userFullName) ? "not logged in" : userFullName;
    	
    	Writer writer = null;
    	PrintWriter printWriter = null;
    	StringBuffer notification = null;
    	try {
    		writer = new StringWriter();
    		printWriter = new PrintWriter(writer);
    		exception.printStackTrace(printWriter);
    		
    		notification = new StringBuffer("Requested uri: ").append(requestUri).append("\n");
    		notification.append("User: ").append(userFullName).append("\n");
    		notification.append("Stack trace:\n").append(writer.toString());
    		
        	SendMail.send("idegaweb@idega.com", "programmers@idega.com", null, null, host, "EXCEPTION: on ePlatform, server: " + serverName,
        			notification.toString());
        } catch(Exception e) {
        	LOGGER.log(Level.WARNING, "Error sending notification: " + notification, e);
        	return false;
        } finally {
        	IOUtil.closeWriter(writer);
        	IOUtil.closeWriter(printWriter);
        }
    	return true;
	}
	
	public static final Locale getCurrentLocale() {
		try {
			LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
			if (loginSession == null) {
				LOGGER.warning("LoginSession was not found");
				return null;
			}
			
			//	1. Trying to get from request
			Locale locale = loginSession.getCurrentLocale();
			if (locale == null) {
				User currentUser = loginSession.getUser();
				if (currentUser != null) {
					//	2. Trying to get from user settings
					String preferredLocaleId = currentUser.getPreferredLocale();
					locale = ICLocaleBusiness.getLocaleFromLocaleString(preferredLocaleId);
				}
			}
			if (locale == null) {
				//	3. Trying to get from application settings
				locale = IWMainApplication.getDefaultIWMainApplication().getDefaultLocale();
			}
			
			return locale;
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting current locale", e);
		}
		return null;
	}
}
