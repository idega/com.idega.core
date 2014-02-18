package com.idega.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.reflections.Reflections;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.IDOEntity;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.user.data.User;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.presentation.JSFUtil;

public class CoreUtil {

	private static final Logger LOGGER = Logger.getLogger(CoreUtil.class.getName());

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
		return new String(Base64.encodeBase64(originalValue.getBytes()));
	}

	public static String getDecodedValue(String encodedValue) {
		if (encodedValue == null) {
			return null;
		}
		return new String(Base64.decodeBase64(encodedValue.getBytes()));
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
		return sendExceptionNotification(null, exception);
	}

	public static boolean sendExceptionNotification(final String message, final Throwable exception) {
		return sendExceptionNotification(message, exception, new File[0]);
	}

	public static boolean sendExceptionNotification(final String message, final Throwable exception, final File... attachments) {
		RequestResponseProvider reqResProvider = null;
		try {
			reqResProvider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
		} catch(Exception e) {}
    	HttpServletRequest request = reqResProvider == null ? null : reqResProvider.getRequest();
    	String serverName = null;
    	String requestUri = null;
		if (request != null) {
	    	serverName = request.getServerName();
	    	requestUri = request.getRequestURI();

	    	serverName = StringUtil.isEmpty(serverName) ? "unknown" : serverName;
	    	requestUri = StringUtil.isEmpty(requestUri) ? "unknown" : requestUri;
		}

		String user = null;
    	try {
    		LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
    		User loggedInUser = loginSession == null ? null : loginSession.getUser();
    		user = loggedInUser == null ? null : (loggedInUser.getName() + ", user ID: " + loggedInUser.getId());
    	} catch (Exception e) {}
    	user = StringUtil.isEmpty(user) ? "not logged in" : user;

		final String server = serverName;
		final String requestedUri = requestUri;
		final String userFullName = user;
		Thread sender = new Thread(new Runnable() {

			@Override
			public void run() {
				IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();

		    	Writer writer = null;
		    	PrintWriter printWriter = null;
		    	StringBuffer notification = null;
		    	try {
		    		if (exception != null) {
			    		writer = new StringWriter();
			    		printWriter = new PrintWriter(writer);
			    		exception.printStackTrace(printWriter);
		    		}

		    		notification = new StringBuffer("Requested uri: ").append(requestedUri).append("\n");
		    		notification.append("User: ").append(userFullName).append("\n");
		    		if (!StringUtil.isEmpty(message))
		    			notification.append("Message: ").append(message).append("\n");
		    		notification.append("Stack trace:\n").append(writer == null ? "Unavailable" : writer.toString());

		    		SendMail.send("idegaweb@idega.com", settings.getProperty("exception_report_receiver", "programmers@idega.com"), null, null, null,
		    				null, "EXCEPTION: on ePlatform, server: " + server,notification.toString(), false, true, attachments);
		        } catch(Exception e) {
		        	LOGGER.log(Level.WARNING, "Error sending notification: " + notification, e);
		        } finally {
		        	IOUtil.closeWriter(writer);
		        	IOUtil.closeWriter(printWriter);
		        }
			}
		});
		sender.start();

    	return true;
	}

	public static final Locale getCurrentLocale() {
		try {
			Locale locale = null;

			LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
			if (loginSession != null) {
				//	1. Trying to get from request
				locale = loginSession.getCurrentLocale();
				if (locale == null) {
					User currentUser = loginSession.getUser();
					if (currentUser != null) {
						//	2. Trying to get from user settings
						String preferredLocaleId = currentUser.getPreferredLocale();
						locale = ICLocaleBusiness.getLocaleFromLocaleString(preferredLocaleId);
					}
				}
			}

			if (locale == null) {
				//	3. Trying to get from IWContext
				IWContext iwc = getIWContext();
				if (iwc != null) {
					locale = iwc.getCurrentLocale();
				}
			}

			if (locale == null) {
				//	4. Trying to get from application settings
				locale = IWMainApplication.getDefaultIWMainApplication().getDefaultLocale();
			}

			return locale == null ? Locale.ENGLISH : locale;
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting current locale", e);
		}
		return Locale.ENGLISH;
	}

	public static final boolean isSQLMeasurementOn() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("measure_sql_queries", Boolean.FALSE);
	}

	public static final boolean isMobileClient(IWContext iwc) {
		return CoreConstants.PAGE_VIEW_TYPE_MOBILE.equals(iwc.getSessionAttribute(CoreConstants.PARAMETER_PAGE_VIEW_TYPE));
	}

	public static List<String> getIds(Collection<? extends IDOEntity> entities) {
		if(ListUtil.isEmpty(entities)){
			return Collections.emptyList();
		}
		List<String> ids = new ArrayList<String>(entities.size());
		for(IDOEntity entity : entities){
			Object id = entity.getPrimaryKey();
			if(id == null){
				continue;
			}
			String strId = String.valueOf(id);
			ids.add(strId);
		}
		return ids;
	}

	public static List<Integer> getIdsAsIntegers(Collection<? extends IDOEntity> entities) {
		if(ListUtil.isEmpty(entities)){
			return Collections.emptyList();
		}
		List<Integer> ids = new ArrayList<Integer>(entities.size());
		for(IDOEntity entity : entities){
			Object id = entity.getPrimaryKey();
			if(id == null){
				continue;
			}
			String strId = String.valueOf(id);
			try{
				ids.add(Integer.valueOf(strId));
			}catch (Exception e) {
			}
		}
		return ids;
	}

	public static final void doEnsureScopeIsSet(FacesContext context) {
		if (context == null) {
			LOGGER.warning("FacesContext is not provided");
			return;
		}

		Map<?, ?> utils = WebApplicationContextUtils.getWebApplicationContext(IWMainApplication.getDefaultIWMainApplication().getServletContext())
				.getBeansOfType(JSFUtil.class);
		if (MapUtil.isEmpty(utils))
			return;

		for (Object bean: utils.values()) {
			if (bean instanceof JSFUtil)
				((JSFUtil) bean).setFacesScope(context);
		}
    }

	public static boolean doWriteFileToRepository(String pathInRepository, String fileName, InputStream stream) throws IOException {
		String realPath = getRealPathToRepository(pathInRepository);
		if (!realPath.endsWith(File.separator))
			realPath = realPath.concat(File.separator);
		realPath = realPath.concat(fileName).concat("_1.0");
		File tmp = new File(realPath);
		FileUtil.createFileIfNotExistent(tmp);
		FileUtil.streamToFile(stream, tmp);

		return true;
	}

	private static String getRealPathToRepository(String pathInRepository) throws IOException {
		if (StringUtil.isEmpty(pathInRepository))
			throw new IOException("Path in repository is not defined: " + pathInRepository);

		String realPath = System.getProperty("catalina.base");
		if (StringUtil.isEmpty(realPath)) {
			realPath = IWMainApplication.getDefaultIWMainApplication().getApplicationRealPath();
			if (StringUtil.isEmpty(realPath))
				throw new IOException("Unknown real path of the application: " + realPath);

			String webappsFolder = File.separator.concat("webapps").concat(File.separator);
			int webappsIndex = realPath.indexOf(webappsFolder);
			if (webappsIndex <= 0)
				throw new IOException("It is unknown how to navigate to repository folder given real path of the application: " + realPath);

			realPath = realPath.substring(0, webappsIndex);
		}

		String temp = realPath.concat(File.separator).concat("bin").concat(File.separator).concat("store");
		File tempDir = new File(temp);
		if (!tempDir.exists())
			temp = realPath.concat(File.separator).concat("store");

		realPath = temp;
		if (!pathInRepository.startsWith(CoreConstants.WEBDAV_SERVLET_URI))
			realPath = realPath.concat(CoreConstants.WEBDAV_SERVLET_URI);
		realPath = realPath.concat(pathInRepository);

		return realPath;
	}

	public static File getFileFromRepository(String pathInRepository) throws IOException {
		String realPath = getRealPathToRepository(pathInRepository);
		return new File(realPath);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getUnProxied(Object bean) {
		if (bean instanceof Advised && AopUtils.isAopProxy(bean)) {
			try {
				bean = ((Advised) bean).getTargetSource().getTarget();
				return getUnProxied(bean);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error while unproxying " + bean + " (" + bean.getClass().getName() + ")", e);
			}
		}

		return (T) bean;
	}

	public static <T> Set<Class<? extends T>> getSubTypesOf(Class<T> theClass) {
		return getSubTypesOf(theClass, false);
	}
	public static <T> Set<Class<? extends T>> getSubTypesOf(Class<T> theClass, boolean onlyInterfaces) {
		if (theClass == null) {
			return null;
		}

		Map<String, Set<Class<? extends T>>> cache = IWCacheManager2.getInstance(
				IWMainApplication.getDefaultIWMainApplication()).getCache("iwSubTypesCache", 1000, true, false, 604800);
		String key = theClass.getName() + onlyInterfaces;
		Set<Class<? extends T>> cachedSubTypes = cache.get(key);
		if (cachedSubTypes != null) {
			return cachedSubTypes;
		}

		Reflections reflections = new Reflections("com.idega", "is.idega", "sv.idega");
		Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(theClass);
		if (subTypes == null) {
			subTypes = new HashSet<Class<? extends T>>();
		}
		if (!onlyInterfaces) {
			cache.put(key, subTypes);
			return subTypes;
		}

		Set<Class<? extends T>> subInterfaces = new HashSet<Class<? extends T>>();
		for (Class<? extends T> subType: subTypes) {
			if (subType.isInterface()) {
				subInterfaces.add(subType);
			}
		}
		cache.put(key, subInterfaces);
		return subInterfaces;
	}
}