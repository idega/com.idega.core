package com.idega.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.reflections.Reflections;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.builder.business.BuilderLogicWrapper;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.IDOContainer;
import com.idega.data.IDOEntity;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.user.data.bean.User;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.presentation.JSFUtil;
import com.idega.util.timer.DateUtil;

@SuppressWarnings("deprecation")
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
		resources.add(CoreConstants.DWR_ENGINE_SCRIPT);
		resources.add("/dwr/interface/ChooserService.js");
		resources.add(CoreConstants.GROUP_SERVICE_DWR_INTERFACE_SCRIPT);

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
		try {
			return new String(Base64.decodeBase64(encodedValue.getBytes()));
		} catch (Exception e) {
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
			IWContext iwc = getIWContext();
	    	serverName = iwc == null ? null : iwc.getServerURL();
	    	serverName = serverName == null ? request.getServerName() : serverName;
	    	requestUri = request.getRequestURI();

	    	serverName = StringUtil.isEmpty(serverName) ? "unknown" : serverName;
	    	requestUri = StringUtil.isEmpty(requestUri) ? "unknown" : requestUri;
		}

		String user = null;
    	try {
    		LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
    		User loggedInUser = loginSession == null ? null : loginSession.getUserEntity();
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
		    	StringBuffer notification = null;
		    	try {
		    		writer = new StringWriter();
		    		doPrintStackTrace(exception, writer);

		    		notification = new StringBuffer("Requested uri: ").append(requestedUri).append("\n");
		    		notification.append("User: ").append(userFullName).append("\n");
		    		if (!StringUtil.isEmpty(message))
		    			notification.append("Message: ").append(message).append("\n");
		    		notification.append("Stack trace:\n").append(writer == null ? "Unavailable" : writer.toString());

		    		String receiver = settings.getProperty("exception_report_receiver", "abuse@idega.com");
		    		String subject = "EXCEPTION: on ePlatform, server: " + server;
		    		String text = notification.toString();
		    		if (EmailValidator.getInstance().validateEmail(receiver)) {
		    			SendMail.send(
		    					"idegaweb@idega.com",
		    					receiver,
		    					null,
		    					null,
		    					null,
		    					null,
		    					subject,
		    					text,
		    					false,
		    					true,
		    					attachments
		    			);
		    		} else {
		    			LOGGER.warning(subject + "\n" + text);
		    		}
		        } catch(Exception e) {
		        	LOGGER.log(Level.WARNING, "Error sending notification: " + notification, e);
		        } finally {
		        	IOUtil.closeWriter(writer);
		        }
			}
		});
		sender.start();

    	return true;
	}

	private static void doPrintStackTrace(Throwable e, Writer writer) {
		if (e == null) {
			return;
		}

		PrintWriter printer = new PrintWriter(writer);
		e.printStackTrace(printer);
		IOUtil.close(printer);
	}

	public static final Locale getCurrentLocale() {
		try {
			Locale locale = null;

			LoginSession loginSession = null;
			try {
				loginSession = ELUtil.getInstance().getBean(LoginSession.class);
			} catch (Exception e) {}
			if (loginSession != null) {
				//	1. Trying to get from request
				locale = loginSession.getCurrentLocale();
				if (locale == null) {
					User currentUser = loginSession.getUserEntity();
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
		IWMainApplication mainApplication = IWMainApplication.getDefaultIWMainApplication();
		if (mainApplication != null) {
			IWMainApplicationSettings settings = mainApplication.getSettings();
			if (settings != null) {
				return settings.getBoolean("measure_sql_queries", Boolean.FALSE);
			}
		}

		return Boolean.FALSE;
	}

	public static final boolean isUIRenderingMeasurementOn() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("measure_ui_rendering", Boolean.FALSE);
	}

	public static final void doDebugSQL(long start, long end, String query) {
		doDebugSQL(start, end, query, null);
	}
	public static final void doDebugSQL(long start, long end, String query, Collection<?> params) {
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		long minExecutionTime = Long.valueOf(settings.getProperty("sql_debug_min_exec_time", String.valueOf(100)));

		long executionTime = end - start;
		if (executionTime < minExecutionTime) {
			return;
		}

		IWContext iwc = getIWContext();
		String request = iwc == null ? "unknown" : iwc.getRequestURI();
		Map<String, String[]> parameters = iwc == null ? null : iwc.getRequest().getParameterMap();

		StringBuffer message = new StringBuffer("Query '").append(query).append("' ");
		if (!ListUtil.isEmpty(params)) {
			message.append("with parameters ").append(params).append(" ");
		}

		message.append("executed in ").append(executionTime).append(" ms");
		boolean printStackTrace = settings.getBoolean("print_stack_trace_for_sql_debug", Boolean.FALSE);
		if (printStackTrace) {
			try {
				throw new RuntimeException("Testing stack trace for SQL query: " + query + (ListUtil.isEmpty(params) ? CoreConstants.EMPTY : ". Parameters: " + params));
			} catch (Exception e) {
				StringWriter writer = new StringWriter();
				doPrintStackTrace(e, writer);
				message.append(". Stack trace:\n").append(writer.toString());
				IOUtil.close(writer);
			}
		}

		if (request != null) {
			message.append("\nRequest URI: ").append(request);
		}
		if (!MapUtil.isEmpty(parameters)) {
			message.append("\nParameters: ");
			for (String param: parameters.keySet()) {
				String[] values = parameters.get(param);
				if (!ArrayUtil.isEmpty(values)) {
					message.append(param).append(CoreConstants.EQ).append(Arrays.asList(values));
				}
			}
		}

		LOGGER.info(message.toString());
		if (settings.getBoolean("email_sql_debug_message", false)) {
			sendExceptionNotification(message.toString(), null);
		}
	}

	public static final void doDebug(long start, long end, String method) {
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		long minExecutionTime = Long.valueOf(settings.getProperty("debug_min_exec_time", String.valueOf(100)));

		long executionTime = end - start;
		if (executionTime < minExecutionTime) {
			return;
		}

		IWContext iwc = getIWContext();
		String request = iwc == null ? "unknown" : iwc.getRequestURI();
		Map<String, String[]> parameters = iwc == null ? null : iwc.getRequest().getParameterMap();

		StringBuffer message = new StringBuffer("Method '").append(method).append("' ");

		message.append("executed in ").append(executionTime).append(" ms");
		boolean printStackTrace = settings.getBoolean("print_stack_trace_for_debug", Boolean.FALSE);
		if (printStackTrace) {
			try {
				throw new RuntimeException("Testing stack trace for method: " + method);
			} catch (Exception e) {
				StringWriter writer = new StringWriter();
				doPrintStackTrace(e, writer);
				message.append(". Stack trace:\n").append(writer.toString());
				IOUtil.close(writer);
			}
		}

		if (request != null) {
			message.append("\nRequest URI: ").append(request);
		}
		if (!MapUtil.isEmpty(parameters)) {
			message.append("\nParameters: ");
			for (String param: parameters.keySet()) {
				String[] values = parameters.get(param);
				if (!ArrayUtil.isEmpty(values)) {
					message.append(param).append(CoreConstants.EQ).append(Arrays.asList(values));
				}
			}
		}

		LOGGER.info(message.toString());
		if (settings.getBoolean("email_debug_message", false)) {
			sendExceptionNotification(message.toString(), null);
		}
	}

	public static final void doDebugUI(long start, long end, UIComponent component, FacesContext context) {
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		long minExecutionTime = Long.valueOf(settings.getProperty("ui_debug_min_exec_time", String.valueOf(100)));

		long executionTime = end - start;
		if (executionTime < minExecutionTime) {
			return;
		}

		IWContext iwc = IWContext.getIWContext(context);
		String request = iwc == null ? "unknown" : iwc.getRequestURI();
		Map<String, String[]> parameters = iwc == null ? null : iwc.getRequest().getParameterMap();
		String sessionId = iwc == null ? "unknown" : iwc.getRequest().getSession(true).getId();
		User user = iwc == null ? null : iwc.isLoggedOn() ? iwc.getLoggedInUser() : null;

		StringBuffer message = new StringBuffer("UI component '").append(component.getClass().getName()).append("' ");

		message.append("rendered into HTML in ").append(executionTime).append(" ms");
		boolean printStackTrace = settings.getBoolean("print_stack_trace_for_ui_debug", Boolean.FALSE);
		if (printStackTrace) {
			try {
				throw new RuntimeException("Testing stack trace for UI rendering: " + component.getClass().getName());
			} catch (Exception e) {
				StringWriter writer = new StringWriter();
				doPrintStackTrace(e, writer);
				message.append(". Stack trace:\n").append(writer.toString());
				IOUtil.close(writer);
			}
		}

		if (request != null) {
			message.append("\nRequest URI: ").append(request);
		}
		if (!MapUtil.isEmpty(parameters)) {
			message.append("\nParameters: ");
			for (String param: parameters.keySet()) {
				String[] values = parameters.get(param);
				if (!ArrayUtil.isEmpty(values)) {
					message.append(param).append(CoreConstants.EQ).append(Arrays.asList(values));
				}
			}
		}
		message.append("\nHTTP session ID: ").append(sessionId);
		if (user != null) {
			message.append("\nUser: ").append(user.getName()).append(", ID: ").append(user.getId()).append(", personal ID: ").append(user.getPersonalID());
		}

		LOGGER.info(message.toString());
		if (settings.getBoolean("email_ui_debug_message", false)) {
			sendExceptionNotification(message.toString(), null);
		}
	}

	public static final boolean isMobileClient(IWContext iwc) {
		return CoreConstants.PAGE_VIEW_TYPE_MOBILE.equals(iwc.getSessionAttribute(CoreConstants.PARAMETER_PAGE_VIEW_TYPE));
	}

	public static List<String> getIds(Collection<? extends IDOEntity> entities) {
		if (ListUtil.isEmpty(entities)) {
			return Collections.emptyList();
		}

		List<String> ids = new ArrayList<String>(entities.size());
		for (IDOEntity entity : entities) {
			Object id = entity.getPrimaryKey();
			if (id == null)
				continue;

			ids.add(id.toString());
		}
		return ids;
	}

	public static List<Integer> getIdsAsIntegers(Collection<? extends IDOEntity> entities) {
		if (ListUtil.isEmpty(entities)) {
			return Collections.emptyList();
		}

		List<Integer> ids = new ArrayList<Integer>(entities.size());
		for (IDOEntity entity : entities) {
			Object id = entity.getPrimaryKey();
			if (id == null)
				continue;

			try {
				ids.add(Integer.valueOf(id.toString()));
			} catch (Exception e) {}
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

	public static File getFileFromRepository(String pathInRepository) throws IOException {
		if (StringUtil.isEmpty(pathInRepository)) {
			return null;
		}

		if (pathInRepository.endsWith("_1.0")) {
			pathInRepository = StringHandler.replace(pathInRepository, "_1.0", CoreConstants.EMPTY);
		}

		try {
			RepositoryService service = ELUtil.getInstance().getBean(RepositoryService.BEAN_NAME);
			return service.getRepositoryItemAsRootUser(pathInRepository);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String getHost() {
		ICDomain domain = null;
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			domain = IWMainApplication.getDefaultIWApplicationContext().getDomain();
		} else {
			domain = iwc.getDomain();
		}
		if (domain == null) {
			LOGGER.warning("Domain is unknown!");
			return CoreConstants.EMPTY;
		}

		String host = null;
		String protocol = domain.getServerProtocol();
		String name = domain.getServerName();
		if (StringUtil.isEmpty(protocol) || StringUtil.isEmpty(name)) {
			return CoreConstants.EMPTY;
		} else {
			host = protocol.concat("://").concat(name);
		}
		int port = domain.getServerPort();
		if (port > 0) {
			host = host.concat(CoreConstants.COLON).concat(String.valueOf(port));
		}
		return host;
	}

	public static void clearAllCaches() {
		try {
			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();

			BuilderLogicWrapper blw = ELUtil.getInstance().getBean(BuilderLogicWrapper.SPRING_BEAN_NAME_BUILDER_LOGIC_WRAPPER);
			blw.getBuilderService(iwma.getIWApplicationContext()).clearAllCaches();
			IBOLookup.clearAllCache();
			IDOContainer.getInstance().flushAllBeanCache();
			IDOContainer.getInstance().flushAllQueryCache();
			IWCacheManager2 iwcm2 = IWCacheManager2.getInstance(iwma);
			iwcm2.reset();
			IWCacheManager.getInstance(iwma).clearAllCaches();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error clearing all caches", e);
		}
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

		Reflections reflections = new Reflections("com.idega", "is.idega", "sv.idega", "is.illuminati");
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

	public static Long getAge(Date dateOfBirth) {
		if (dateOfBirth == null) {
			return null;
		}

		LocalDate localeDateOfBirth = DateUtil.getDate(dateOfBirth);
		if (localeDateOfBirth == null) {
			return null;
		}

		LocalDate dateToday = DateUtil.getDate(new Date(System.currentTimeMillis()));
		return (dateToday.toEpochDay() - localeDateOfBirth.toEpochDay()) / 365;
	}

	private static boolean isValidServerName(String serverName) {
		if (StringUtil.isEmpty(serverName)) {
			return false;
		}

		if (DefaultIWBundle.isProductionEnvironment() && (serverName.indexOf("127.0.0.1") != -1 || serverName.indexOf("localhost") != -1)) {
			return false;
		}

		return true;
	}

	public static String getServerName(HttpServletRequest request) {
		String serverName = request == null ? null : request.getServerName();

		IWContext iwc = null;
		try {
			if (!isValidServerName(serverName)) {
				iwc = getIWContext();
				IWMainApplication iwma = iwc == null ? IWMainApplication.getDefaultIWMainApplication() : iwc.getIWMainApplication();
				ICDomain domain = iwma.getIWApplicationContext().getDomain();
				serverName = domain == null ? null : domain.getServerName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		IWMainApplicationSettings settings = null;
		if (!isValidServerName(serverName)) {
			settings = iwc == null ? IWMainApplication.getDefaultIWMainApplication().getSettings() : iwc.getIWMainApplication().getSettings();
			serverName = settings.getProperty(IWConstants.SERVER_URL_PROPERTY_NAME);
		}
		if (!isValidServerName(serverName)) {
			settings = settings == null ?
					iwc == null ? IWMainApplication.getDefaultIWMainApplication().getSettings() : iwc.getIWMainApplication().getSettings():
					settings;
			serverName = settings.getProperty(IWMainApplication.PROPERTY_DEFAULT_SERVICE_URL);
		}
		if (!isValidServerName(serverName)) {
			settings = settings == null ?
					iwc == null ? IWMainApplication.getDefaultIWMainApplication().getSettings() : iwc.getIWMainApplication().getSettings():
					settings;
			serverName = settings.getProperty(IWConstants.DEFAULT_SERVER_URL_PROPERTY_NAME);
		}

		serverName =  serverName == null ? "127.0.0.1" : serverName;
		return serverName;
	}

	public static String getServerURL(HttpServletRequest request) {
		String serverName = getServerName(request);

		if (request == null) {
			return serverName;
		}

		StringBuffer buf = new StringBuffer();

		String protocol = request.getScheme().concat("://");
		if (!serverName.startsWith(protocol)) {
			buf.append(protocol);
		}

		buf.append(serverName);
		int port = request.getServerPort();
		if (port == 80 || port == 443 || (port > 1024 && DefaultIWBundle.isProductionEnvironment())) {
			//do not add port to url
		} else {
			buf.append(CoreConstants.COLON).append(port);
		}
		buf.append(CoreConstants.SLASH);
		return buf.toString();
	}

}
