/*
 * $Id: IWContext.java,v 1.164 2009/06/22 09:55:45 valdas Exp $ Created 2000 by
 * Tryggvi Larusson
 *
 * Copyright (C) 2000-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.event.IWEventProcessor;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWSystemProperties;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.io.UploadFile;
import com.idega.presentation.ui.Parameter;
import com.idega.repository.RepositoryService;
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.user.business.UserProperties;
import com.idega.user.data.bean.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.FacesUtil;
import com.idega.util.FileUploadUtil;
import com.idega.util.ListUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.HashtableMultivalued;
import com.idega.util.expression.ELUtil;

/**
 * This class is a context information that lives through each user request in
 * an idegaWeb application. The role of this class is very similar to that of
 * FacesContext in a JSF application. <br>
 * IWContext will be gradually phased out in future versions in favour of just
 * working with the standard FacesContext. <br>
 * This class gives access to Request specific, User specific and Application
 * specific information. <br>
 * An instance of this class should be used under the interfaces
 * com.idega.idegaweb.IWUserContext and com.idega.idegaweb.IWApplicationContext
 * where it is applicable (i.e. when only working with User scoped functionality
 * or Application scoped functionality). <br>
 *
 * Last modified: $Date: 2009/06/22 09:55:45 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a * @version $Revision: 1.164 $
$
 */
public class IWContext extends FacesContext implements IWUserContext, IWApplicationContext {

	private static final long serialVersionUID = 3761970466885022262L;
	private static final Logger LOGGER = Logger.getLogger(IWContext.class.getName());

	private HttpServletRequest _request;
	private HttpServletResponse _response;
	public final static String LOCALE_ATTRIBUTE = "idegaweb_locale";
	public final static String IDEGA_SESSION_KEY = "idega_session_id";
	private final static String WEAK_HASHMAP_KEY = "idegaweb_weak_hashmap";
	private final static String CHARACTER_SET_PREFIX = "; charset=";
	private String markupLanguage; // Variable to set the language i.e. HTML
	private String spokenLanguage;
	private ServletContext servletContext;
	private boolean _doneHandHeldCheck = false;
	private boolean _clientIsHandHeld = false;
	private boolean isCaching = false;
	private PrintWriter cacheWriter;
	private ResponseWriter cacheResponseWriter;
	private PrintWriter writer = null;
	private HashtableMultivalued<String, Collection<String>, String> _multipartParameters = null;
	private UploadFile _uploadedFile = null;
	private FacesContext realFacesContext;
	private static final String IWCONTEXT_REQUEST_KEY = "iwcontext";
	private static final String PRM_HISTORY_ID = ICBuilderConstants.PRM_HISTORY_ID;
	private static final String SESSION_OBJECT_STATE = ICBuilderConstants.SESSION_OBJECT_STATE;
	public static final String[] WML_USER_AGENTS = new String[] { "nokia", "ericsson", "wapman", "upg1", "symbian",
			"wap" }; // NB: must be lowercase

	private static final String COLONSLASHSLASH = "://";
	private static final String HTTP = "http";
	private static final String HTTPS = "https";

	/**
	 * Default constructor
	 */
	protected IWContext() {}

	private IWContext(FacesContext fc) {
		this((HttpServletRequest) fc.getExternalContext().getRequest(), (HttpServletResponse) fc.getExternalContext().getResponse(),
				(ServletContext) fc.getExternalContext().getContext(), false);
		setRealFacesContext(fc);
	}

	private IWContext(HttpServletRequest request, HttpServletResponse response, ServletContext context, boolean createFacesContext) {
		if (createFacesContext && getRealFacesContext() == null) {
			FacesContextInitializer facesContextInitializer = getFacesContextInitializer();
			FacesContext fc = facesContextInitializer.getInitializedFacesContext(context, request, response);
			setRealFacesContext(fc);
		}

		HttpSession session = null;
		try {
			session = request.getSession();
		} catch (Exception e) {}
		if (session == null) {
			if (reInitializeRequest()) {
				request = getRequest();
			} else {
				session = request.getSession(true);
				if (session == null) {
					throw new RuntimeException("There was an error while initializing IWContext: session is not available. Request object: " + request + ", session: " + session);
				}
			}
		}

		setRequest(request);
		setResponse(response);
		setServletContext(context);

		// MUST BE DONE BEFORE ANYTHING IS GOTTEN FROM THE REQUEST!
		initializeAfterRequestIsSet(request);
		setServerURLToSystemProperties();

		// Put it to the request map
		storeContext();
	}

	/**
	 * @param request
	 * @param response
	 * @param context
	 */
	public IWContext(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		this(request, response, context, true);
	}

	protected void initializeAfterRequestIsSet(HttpServletRequest request) {
		setCharactersetEncoding(request);
		// CANNOT BE DONE UNTIL AFTER THE CHARACTER ENCODING IS DONE, OTHERWISE
		// THE ENCODING WILL DEFAULT TO ISO-8859-1 BUT DISPLAY ITSELF AS THE
		// PREFERRED ENCODING!
		setMarkupLanguage(getDetectedClientMarkupLanguage(request));
	}

	/**
	 * <p>
	 * Set and initialize the CharacterSet Encoding on the current Request. This
	 * must be called before any getting of parameters is done (on Tomcat) so
	 * this is called both from IWEncodingFilter (which is the first mapped
	 * filter and the IWContext() constructor.
	 * </p>
	 *
	 * @param request
	 */
	public static void setCharactersetEncoding(HttpServletRequest request) {
		// MUST BE DONE BEFORE ANYTHING IS GOTTEN FROM THE REQUEST!

		HttpSession session = request == null ? null : request.getSession();
		if (session == null) {
			session = request.getSession(true);
		}
		if (session == null) {
			LOGGER.warning("Request: " + request + " and/or session: " + session + " is null");
			RequestResponseProvider requestProvider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
			try {
				session = requestProvider.getRequest().getSession(Boolean.TRUE);
			} catch(Exception e) {
				LOGGER.log(Level.SEVERE, "Error getting session", e);
			}
			if (session != null) {
				LOGGER.info("Session was resolved");
			}
		}

		ServletContext context = session == null ? null : session.getServletContext();
		if (context == null && request != null) {
			context = request.getServletContext();
		}
		IWMainApplication iwma = context == null ? IWMainApplication.getDefaultIWMainApplication() : IWMainApplication.getIWMainApplication(context);
		if (getIfSetRequestCharacterEncoding(iwma)) {
			try {
				String characterSetEncoding = iwma.getSettings().getCharacterEncoding();
				request.setCharacterEncoding(characterSetEncoding);
				//encoding for myfaces and ajax4jsf to pick up
				session.setAttribute(ViewHandler.CHARACTER_ENCODING_KEY,characterSetEncoding);
			}
			catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error setting encoding", e);
				CoreUtil.sendExceptionNotification(e);
			}
		}
		// CANNOT BE DONE UNTIL AFTER THE CHARACTER ENCODING IS DONE, OTHERWISE
		// THE ENCODING WILL DEFAULT TO ISO-8859-1 BUT DISPLAY ITSELF AS THE
		// PREFERRED ENCODING!
		// setMarkupLanguage(getDetectedClientMarkupLanguage(request));
	}

	public static boolean getIfSetRequestCharacterEncoding(IWMainApplication iwma) {
		// TODO: check if this is ok for multipart forms
		// boolean returner =
		// (!isRequestCharacterEncodingSet)&&iwma.getApplicationServer().getSupportsSettingCharactersetInRequest();
		boolean returner = iwma.getApplicationServer().getSupportsSettingCharactersetInRequest();
		return returner;
	}

	/**
	 * This is the method to convert/cast a FacesContext instance to a IWContext
	 * instance. if the FacesContext instance is really a IWContext it upcasts
	 * the instance, else it constructs a new and stores it in the outer
	 * facescontext's request map.
	 */
	public static IWContext getIWContext(FacesContext fc) {
		if (fc instanceof IWContext) {
			return (IWContext) fc;
		}
		IWContext iwc = null;
		// try to look up from request map
		iwc = (IWContext) ((HttpServletRequest) fc.getExternalContext().getRequest()).getAttribute(IWCONTEXT_REQUEST_KEY);

		// reason for the second condition below:
		// After forwarding the faces context has changed, check if the stored
		// iwc holds the same faces context
		// or if a new iw context needs to be created.
		// Forwarding is used when applying navigation rules.
		// If iwc is holding an old faces context the response writer might not
		// be set
		// (that is the response writer is null).
		if (iwc == null || fc != iwc.getRealFacesContext()) {
			//	Create new instance
			iwc = new IWContext(fc);
		}

		return iwc;
	}

	private void storeContext() {
		HttpServletRequest request = null;
		try {
			request = getRequest();
			if (request == null) {
				return;
			}

			request.setAttribute(IWCONTEXT_REQUEST_KEY, this);
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error storing IWContext in request: " + request, e);
		}
	}

	@Override
	public HttpSession getSession() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			return null;
		}

		HttpSession session = null;
		try {
			session = request.getSession();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting session from " + request, e);
		}
		if (session == null) {
			session = request.getSession(true);
		}
		return session;
	}

	public boolean isMultipartFormData() {
		String contentType = this.getRequestContentType();
		if (contentType != null) {
			return (contentType.indexOf("multipart") != -1);
		}
		else {
			return false;
		}
	}

	public void setMultipartParameter(String key, String value) {
		if (this._multipartParameters == null) {
			this._multipartParameters = new HashtableMultivalued<String, Collection<String>, String>();
		}
		this._multipartParameters.putValue(key, value);
	}

	public String getMultipartParameter(String key) {
		if (this._multipartParameters == null) {
			return null;
		}

		Collection<String> values = this._multipartParameters.get(key);
		if (ListUtil.isEmpty(values)) {
			return null;
		}

		StringBuffer value = new StringBuffer();
		for (Iterator<String> valuesIter = values.iterator(); valuesIter.hasNext();) {
			value.append(valuesIter.next());

			if (valuesIter.hasNext()) {
				value.append(CoreConstants.COMMA);
			}
		}

		return value.toString();
	}

	public UploadFile getUploadedFile() {
		if (this._uploadedFile == null) {
			try {
				IWEventProcessor.getInstance().handleMultipartFormData(this);
			}
			catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error handling multipart form data", e);
			}
		}
		return this._uploadedFile;
	}

	public void setUploadedFile(UploadFile file) {
		this._uploadedFile = file;
	}

	public boolean isUploadedFileSet() {
		return this._uploadedFile != null;
	}

	public List<UploadFile> getUploadedFiles() {
		Map<String, UploadFile> uploadedFiles = FileUploadUtil.getAllUploadedFiles(this);
		if(uploadedFiles == null || uploadedFiles.isEmpty()) {
			return null;
		}

		List<String> sortedKeys = new ArrayList<String>(uploadedFiles.keySet());
		Collections.sort(sortedKeys);

		List<UploadFile> uploadedFilesList = new ArrayList<UploadFile>();
		for(String key : sortedKeys) {
			UploadFile file = uploadedFiles.get(key);
			uploadedFilesList.add(file);
		}

		if(ListUtil.isEmpty(uploadedFilesList)) {
			return null;
		}
		return uploadedFilesList;
	}

	public String getUserAgent() {
		return RequestUtil.getUserAgent(getRequest());
	}

	/**
	 * Check whether user agent supports HTML5.
	 */
	public boolean isUserAgentHtml5() {
		if (isIE() && getBrowserVersion() < 9) {
			return false;
		}
		return true;
	}

	public String getReferer() {
		return RequestUtil.getReferer(getRequest());
	}

	public boolean isMacOS() {
		boolean isMac = false;
		String userAgent = getUserAgent();
		if (userAgent != null) {
			if (userAgent.indexOf("Mac") != -1) {
				isMac = true;
			}
			else if (userAgent.indexOf("mac") != -1) {
				isMac = true;
			}
		}
		return isMac;
	}

	public boolean isWindows() {
		String userAgent = getUserAgent();
		if (userAgent == null) {
			return false;
		}
		return userAgent.toLowerCase().indexOf("windows") != -1;
	}

	public boolean isWebDavClient() {
		return isWebDavClient(getRequest());
	}

	public static boolean isWebDavClient(HttpServletRequest request) {
		boolean isDav = false;
		String userAgent = RequestUtil.getUserAgent(request);
		if (userAgent != null) {
			if (userAgent.indexOf("DAV") != -1) {
				isDav = true;
			}
			else if (userAgent.indexOf("dav") != -1) {
				isDav = true;
			}
			else if (userAgent.indexOf("Dav") != -1) {
				isDav = true;
			}
		}
		return isDav;
	}

	public boolean isNetscape() {
		String userAgent = getUserAgent();
		if (userAgent != null) {
			if (userAgent.indexOf("Mozilla") != -1) {
				// if not Internet Explorer then Netscape :)
				if (userAgent.indexOf("MSIE") != -1) {
					return false;
				}
				else {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isIE() {
		return CoreUtil.isIE(getRequest());
	}

	public boolean isOpera() {
		String userAgent = getUserAgent();
		if (userAgent != null) {
			if (userAgent.indexOf("Opera") != -1) {
				return true;
			}
		}
		return false;
	}

	public boolean isMozilla() {
		String userAgent = getUserAgent();
		if (userAgent != null) {
			if (userAgent.indexOf("Mozilla") != -1) {
				return true;
			}
		}
		return false;
	}

	public boolean isSafari() {
		String userAgent = getUserAgent();
		if (userAgent != null) {
			if (userAgent.indexOf("Safari") != -1) {
				return true;
			}
		}
		return false;
	}

	public String getFullBrowserVersion() {
		String userAgent = getUserAgent();

		try {
			if (isIE()) {
				String splitter = "MSIE ";
				String agentInfo[] = userAgent.split(splitter);
				if (agentInfo == null || agentInfo.length <= 1) {
					splitter = "MSIE";
					agentInfo = userAgent.split(splitter);
				}
				if (agentInfo != null && agentInfo.length >=2) {
					String theFirstPart = agentInfo[1];
					if (theFirstPart.indexOf(CoreConstants.SEMICOLON) == -1) {
						LOGGER.warning("Failed to resolve IE browser version from '" + userAgent + "'. Was expecting '" + splitter + "'");
					} else {
						String[] parts = theFirstPart.split(CoreConstants.SEMICOLON);
						if (!ArrayUtil.isEmpty(parts)) {
							return parts[0];
						} else {
							LOGGER.warning("Failed to resolve IE browser version from '" + userAgent + "'. Was expecting '" + splitter + "'");
						}
					}
				}
			} else if (isSafari()) {
				String agentInfo[] = userAgent.split("Version/");
				return agentInfo[1].split(CoreConstants.SPACE)[0];
			} else if (isOpera()) {
				String agentInfo[] = userAgent.split("Version/");
				return agentInfo[1].split(CoreConstants.SPACE)[0];
			} else if (isMozilla()) {
				if (userAgent.indexOf("Camino") != -1) {
					String agentInfo[] = userAgent.split("Camino/");
					return agentInfo[1].split(CoreConstants.SPACE)[0];
				} else if (userAgent.indexOf("Firefox") != -1) {
					String agentInfo[] = userAgent.split("Firefox/");
					return agentInfo[1];
				}
			}

			LOGGER.warning("Couldn't detect version from user agent info:\n" + userAgent);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Some error occured while trying to resolve browser's version from:\n" + userAgent, e);
		}

		return CoreConstants.EMPTY;
	}

	public Double getBrowserVersion() {
		String version = getFullBrowserVersion();
		if (StringUtil.isEmpty(version)) {
			return 0.0;
		}

		if (version.indexOf(CoreConstants.DOT) != -1) {
			version = version.substring(0, version.lastIndexOf(CoreConstants.DOT));
		}

		try {
			return Double.valueOf(version);
		} catch (NumberFormatException e) {
			LOGGER.warning("Error converting to Double: " + version);
		}

		return 0.0;
	}

	public boolean isSearchEngine() {
		String userAgent = getUserAgent();
		if (userAgent != null) {
			if (userAgent.indexOf("Ultraseek") != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the output language for the request, either
	 * <code>IWConstants.MARKUP_LANGUAGE_HTML</code> or
	 * <code>IWConstants.MARKUP_LANGUAGE_WML</code>. This methods just checks
	 * the User-agent header to see if the device is a known wap device,
	 * otherwise html is assumed
	 *
	 * @param request
	 *            The request, needed to find output
	 */
	private String getDetectedClientMarkupLanguage(HttpServletRequest request) {
		// Todo: set the language to WML when the user-agent is of that type
		// --only implemented for the UPG1 test WAP browser
		// @TODO (jonas) use better method to find content types supported by
		// client. Use rdf docs referenced in request headers.x
		String mlParam = null;
		try {
			mlParam = request.getParameter(IWConstants.PARAM_NAME_OUTPUT_MARKUP_LANGUAGE);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting parameter: " + IWConstants.PARAM_NAME_OUTPUT_MARKUP_LANGUAGE, e);
			if (!reInitializeRequest()) {
				CoreUtil.sendExceptionNotification(e);
			}
		}
		if (!StringUtil.isEmpty(mlParam)) {
			return mlParam;
		}
		boolean isWMLAgent = false;
		String user_agent = request.getHeader("User-agent");
		if (user_agent != null) {
			user_agent = user_agent.toLowerCase();
			for (int i = 0; i < WML_USER_AGENTS.length; i++) {
				if (user_agent.indexOf(WML_USER_AGENTS[i]) > -1) {
					isWMLAgent = true;
					break;
				}
			}
		}
		if (isWMLAgent) {
			return IWConstants.MARKUP_LANGUAGE_WML;
		}
		else {
			return IWConstants.MARKUP_LANGUAGE_HTML;
		}
	}

	private boolean reInitializeRequest() {
		try {
			HttpServletRequest request = ELUtil.getInstance().getBean(RequestResponseProvider.class).getRequest();
			if (request == null) {
				LOGGER.warning("Request is null, failed to re-initialize");
				return false;
			}
			HttpSession session = request.getSession(Boolean.FALSE);
			if (session == null) {
				session = request.getSession(true);
			}
			if (session == null) {
				ExternalContext extContext = getExternalContext();
				if (extContext == null) {
					LOGGER.severe(ExternalContext.class.getSimpleName() + " is not provided! Can not initialize HTTP session!");
					return false;
				} else {
					Object o = extContext.getSession(Boolean.TRUE);
					if (o instanceof HttpSession) {
						session = (HttpSession) o;
					}
				}

				if (session == null) {
					LOGGER.warning("Session is null, failed to re-initialize");
					return false;
				}
			}

			LOGGER.fine("Session was re-initialized sucessfully");
			setRequest(request);
			getExternalContext().setRequest(getRequest());
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error while re-initializing request", e);
			return false;
		}
		return true;
	}

	public boolean isParameterSet(String parameterName) {
		return RequestUtil.isParameterSet(getRequest(), parameterName);
	}

	public boolean isParameterSetAsEmpty(String parameterName) {
		return RequestUtil.isParameterSetAsEmpty(getRequest(), parameterName);
	}

	public boolean isParameterSet(Parameter parameter) {
		return isParameterSet(parameter.getName());
	}

	public boolean parameterEquals(Parameter parameter) {
		boolean theReturn = false;
		if (parameter != null) {
			if (getParameter(parameter.getName()) != null) {
				if (getParameter(parameter.getName()).equals(parameter.getValueAsString())) {
					theReturn = true;
				}
			}
		}
		return theReturn;
	}

	public boolean parameterEquals(String parameterName, String parameterValue) {
		boolean theReturn = false;
		if (getParameter(parameterName) != null) {
			if (getParameter(parameterName).equals(parameterValue)) {
				theReturn = true;
			}
		}
		return theReturn;
	}

	public void setRequest(HttpServletRequest request) {
		if (this._request == null) {
			this._request = request;
		}
		else {
			this._request = request;
			initializeAfterRequestIsSet(request);
		}
	}

	protected void setResponse(HttpServletResponse response) {
		this._response = response;
	}

	public void setMarkupLanguage(String language) {
		this.markupLanguage = language;
		if (language.equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			setContentType("text/html");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			setContentType("text/vnd.wap.wml");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)) {
			setContentType("application/pdf");
		}
	}

	public void setSpokenLanguage(String spokenLanguage) {
		this.spokenLanguage = spokenLanguage;
	}

	public HttpServletRequest getRequest() {
		return this._request;
	}

	public Cookie[] getCookies() {
		return this.getRequest().getCookies();
	}

	public void addCookies(Cookie cookie) {
		this.getResponse().addCookie(cookie);
	}

	public boolean isCookieSet(String cookieName) {
		Cookie[] cookies = this.getCookies();
		boolean returner = false;
		if (cookies != null) {
			if (cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals(cookieName)) {
						returner = true;
						continue;
					}
				}
			}
		}
		return returner;
	}

	public String getParameter(String parameterName) {
		String prm = null;
		if (this._multipartParameters != null) {
			prm = getMultipartParameter(parameterName);
		}
		else {
			prm = getRequest().getParameter(parameterName);
		}
		return prm;
	}

	public Boolean getBooleanParameter(String parameterName) {
		if (isParameterSet(parameterName)) {
			return new Boolean(getParameter(parameterName));
		}
		return null;
	}

	public Integer getIntegerParameter(String parameterName) {
		if (isParameterSet(parameterName)) {
			try {
				return Integer.valueOf(getParameter(parameterName));
			}
			catch (NumberFormatException nfe) {
				LOGGER.log(Level.WARNING, "Invalid integer: " + getParameter(parameterName), nfe);
			}
		}

		return null;
	}

	public Float getFloatParameter(String parameterName) {
		if (isParameterSet(parameterName)) {
			try {
				return Float.valueOf(getParameter(parameterName));
			}
			catch (NumberFormatException nfe) {
				LOGGER.log(Level.WARNING, "Invalid float number: " + getParameter(parameterName), nfe);
			}
		}

		return null;
	}

	public Enumeration<String> getParameterNames() {
		if (this._multipartParameters != null) {
			return this._multipartParameters.keys();
		}
		else {
			return getRequest().getParameterNames();
		}
	}

	public String[] getParameterValues(String parameterName) {
		if (this._multipartParameters != null) {
			Collection<String> values = this._multipartParameters.get(parameterName);
			if (values != null) {
				return values.toArray(new String[values.size()]);
			}
			else {
				return null;
			}
		}
		else {
			return getRequest().getParameterValues(parameterName);
		}
	}

	public String getQueryString() {
		return getRequest().getQueryString();
	}

	public HttpServletResponse getResponse() {
		return this._response;
	}

	@Override
	public Object getSessionAttribute(String attributeName) {
		HttpSession session = getSession();
		return session == null ? null : session.getAttribute(attributeName);
	}

	@Override
	public void setSessionAttribute(String attributeName, Object attribute) {
		HttpSession session = getSession();
		if (session != null) {
			getSession().setAttribute(attributeName, attribute);
		}
	}

	@Override
	public String getSessionId() {
		HttpSession session = getSession();
		return session == null ? null : getSession().getId();
	}

	/**
	 * @deprecated Replaced with removeSessionAttribute()
	 */
	@Deprecated
	public void removeAttribute(String attributeName) {
		removeSessionAttribute(attributeName);
	}

	@Override
	public void removeSessionAttribute(String attributeName) {
		HttpSession session = getSession();
		if (session != null) {
			session.removeAttribute(attributeName);
		}
	}

	public String getMarkupLanguage() {
		return this.markupLanguage;
	}

	public String getSpokenLanguage() {
		if (this.spokenLanguage == null) {
			this.setSpokenLanguage("IS");
		}
		return this.spokenLanguage;
	}

	/**
	 * @ deprecated replaced width getApplication
	 */
	public ServletContext getServletContext() {
		return this.servletContext;
	}

	public void setServletContext(ServletContext context) {
		this.servletContext = context;
	}

	private void setServerURLToSystemProperties() {
		//this.getApplicationSettings().setProperty(IWConstants.SERVER_URL_PROPERTY_NAME, this.getServerURL());
	}

	/**
	 * @deprecated UNIMPLEMENTED
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public void maintainParameter(Parameter parameter) {
		Hashtable<String, Parameter> theParameters = (Hashtable<String, Parameter>) this.getSessionAttribute("idega_special_maintained_parameters");
		if (theParameters == null) {
			theParameters = new Hashtable<String, Parameter>();
			theParameters.put(parameter.getName(), parameter);
		}
		else {
			// Parameter previousParameter =
			// theParameters.get(parameter.getName());
			theParameters.put(parameter.getName(), parameter);
		}
	}

	public String getRequestURI() {
		try {
			if (IWMainApplication.useJSF) {
				FacesContext facesContext = getRealFacesContext();
				if (facesContext != null) {
					return FacesUtil.getRequestUri(facesContext);
				}
				else {
					return getRequest().getRequestURI();
				}
			}
			else {
				return getRequest().getRequestURI();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while trying to get a requested URI.", e);
			if (getRequest() != null) {
				return getRequest().getRequestURI();
			}
		}
		return CoreConstants.EMPTY;
	}

	public String getRequestURI(boolean https) {
		String protocol = getProtocol().toLowerCase();
		if (protocol.startsWith(HTTP)) {
			if (protocol.startsWith(HTTPS) || https) {
				return HTTPS + COLONSLASHSLASH + getServerName() + getRequestURI();
			}
			else {
				return getRequestURI();
			}
		}
		return getRequestURI();
	}

	public String getServerName() {
		return getRequest().getServerName();
	}

	public String getProtocol() {
		return getRequest().getProtocol();
	}

	public int getServerPort() {
		return getRequest().getServerPort();
	}

	public PrintWriter getWriter() throws IOException {
		if (this.isCacheing() && this.cacheWriter != null) {
			return this.cacheWriter;
		}
		else {
			if (this.writer == null) {
				this.writer = getResponse().getWriter();
			}
			return this.writer;
		}
	}

	public boolean isWriterNull() {
		return (this.writer == null);
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public void sendRedirect(String URL) {
		try {
			getResponse().sendRedirect(getResponse().encodeRedirectURL(URL));
		}
		catch (IOException e) {
			LOGGER.log(Level.WARNING, "Error redirecting to: " + URL, e);
			CoreUtil.sendExceptionNotification(e);
		}
	}

	@Override
	public void setApplicationAttribute(String attributeName, Object attributeValue) {
		getIWMainApplication().setAttribute(attributeName, attributeValue);
	}

	@Override
	public <V extends Object>V  getApplicationAttribute(String attributeName) {
		return getIWMainApplication().getAttribute(attributeName);
	}

	@Override
	public <V extends Object> V getApplicationAttribute(String attributeName, V defaultObjectToReturnIfValueIsNull) {
		return getIWMainApplication().getAttribute(attributeName, defaultObjectToReturnIfValueIsNull);
	}

	@Override
	public void removeApplicationAttribute(String attributeName) {
		getIWMainApplication().removeAttribute(attributeName);
	}

	@Override
	public IWMainApplication getIWMainApplication() {

		HttpServletRequest request = getRequest();
		if(request!=null){
			return IWMainApplication.getIWMainApplication(request);
		}
		else{
			ServletContext servletContext = getServletContext();
			return IWMainApplication.getIWMainApplication(servletContext);
		}
	}

	@Override
	public IWMainApplicationSettings getApplicationSettings() {
		return getIWMainApplication().getSettings();
	}

	@Override
	public IWSystemProperties getSystemProperties() {
		return getIWMainApplication().getSystemProperties();
	}

	@Override
	public UserProperties getUserProperties() {
		return LoginBusinessBean.getUserProperties(this);
	}

	@Override
	public Locale getCurrentLocale() {
		Locale theReturn = null;
		try {
			theReturn = (Locale) this.getSessionAttribute(LOCALE_ATTRIBUTE);
		}
		catch (IllegalStateException ise) {
			theReturn = null;
		}

		if (theReturn == null) {
			theReturn = getIWMainApplication().getSettings().getDefaultLocale();
			setCurrentLocale(theReturn);
		}
		return theReturn;
	}

	public int getCurrentLocaleId() {
		return ICLocaleBusiness.getLocaleId(getCurrentLocale());
	}

	@Override
	public void setCurrentLocale(Locale locale) {
		try {
			this.setSessionAttribute(LOCALE_ATTRIBUTE, locale);
		}
		catch (IllegalStateException ise) {
			//Do nothign...
		}
	}

	/**
	 * Sets the object with Weak reference so that it could be garbagecollected
	 * anytime
	 */
	public void setSessionAttributeWeak(String attributeName, Object attributeValue) {
		getWeakHashMap().put(attributeName, attributeValue);
	}

	public Object getSessionAttributeWeak(String propertyName) {
		return getWeakHashMap().get(propertyName);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getWeakHashMap() {
		WeakHashMap<String, Object> map = (WeakHashMap<String, Object>) getSessionAttribute(WEAK_HASHMAP_KEY);
		if (map == null) {
			map = new WeakHashMap<String, Object>();
			setSessionAttribute(WEAK_HASHMAP_KEY, map);
		}
		return map;
	}

	/**
	 * Only handles http and https, use getServerURLWithoutProtocol() for other
	 * stuff.
	 *
	 * @return the servername with port and protocol, e.g.
	 *         http://www.idega.com:8080/
	 */
	public String getServerURL() {
		return RequestUtil.getServerURL(getRequest());
	}

	/**
	 *
	 * @return the servername with port and protocol, e.g.
	 *         http://www.idega.com:8080/
	 */
	public String getServerURLWithoutProtocol() {
		StringBuffer buf = new StringBuffer();
		buf.append(getServerName());
		if (getServerPort() != 80) {
			buf.append(":").append(getServerPort());
		}
		buf.append("/");
		return buf.toString();
	}

	public void setContentType(String contentType) {
		HttpServletResponse response = getResponse();
		if(response!=null){
			String encoding = getApplicationSettings().getCharacterEncoding();
			response.setContentType(contentType + CHARACTER_SET_PREFIX + encoding);
		}
		// getResponse().setContentType(contentType);
		// text/html;charset=ISO-8859-1
	}

	void setCacheing(boolean ifCacheing) {
		this.isCaching = ifCacheing;
		if (ifCacheing == false) {
			// make sure these are nulled when stopping cacheing
			this.cacheResponseWriter = null;
			this.cacheWriter = null;
		}
	}

	boolean isCacheing() {
		return this.isCaching;
	}

	public void setCacheWriter(PrintWriter writer) {
		this.cacheWriter = writer;
	}

	public void setCacheResponseWriter(ResponseWriter writer) {
		this.cacheResponseWriter = writer;
	}

	/**
	 * @deprecated Replaced with getCurrentUser()
	 */
	@Override
	@Deprecated
	public User getUser() {
		return (LoginBusinessBean.getUser(this));
	}

	public int getUserId() {
		User usr = getUser();
		if (usr != null) {
			Number id = usr.getId();
			if (id != null) {
				return id.intValue();
			}
		}
		return -1;
	}

	public AccessController getAccessController() {
		return this.getIWMainApplication().getAccessController();
	}

	public String getRequestContentType() {
		return getRequest().getContentType();
	}

	public String getRemoteIpAddress() {
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("X-Forwarded-For");
		if (!isValidIP(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (!isValidIP(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (!isValidIP(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (!isValidIP(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (!isValidIP(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private boolean isValidIP(String ip) {
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "127.0.0.1".equals(ip)) {
			return false;
		}

		return true;
	}

	public String getRemoteHostName() {
		return getRequest().getRemoteHost();
	}

	/**
	 * Checks if the current user has the role specified
	 * @param role
	 * @return
	 */
	public boolean hasRole(String role){
		return this.getAccessController().hasRole(role,this);
	}

	public boolean hasPermission(String permissionKey, Object obj) {
		try {
			return this.getAccessController().hasPermission(permissionKey, obj, this);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error resolving if object " + obj + " has permission: " + permissionKey, ex);
			return false;
		}
	}

	public boolean hasViewPermission(Object obj) {
		return this.hasPermission(AccessController.PERMISSION_KEY_VIEW, obj);
	}

	public boolean hasEditPermission(Object obj) {
		return this.hasPermission(AccessController.PERMISSION_KEY_EDIT, obj);
	}

	public boolean hasPermission(List<Integer> groupIds, String permissionKey, Object obj) {
		try {
			return this.getAccessController().hasPermission(groupIds, permissionKey, obj, this);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error resoving if object " + obj + " has permission: " + permissionKey, ex);
			return false;
		}
	}

	public boolean hasFilePermission(String permissionKey, int id) {
		try {
			return this.getAccessController().hasFilePermission(permissionKey, id, this);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error resolving if file (id=" + id + ") has permission: " + permissionKey, ex);
			return false;
		}
	}

	public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId) {
		try {
			return this.getAccessController().hasDataPermission(permissionKey, obj, entityRecordId, this);
		}
		catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error resovling if object " + obj + " has permission: " + permissionKey , ex);
			return false;
		}
	}

	public boolean hasViewPermission(List<Integer> groupIds, PresentationObject obj) {
		return this.hasPermission(groupIds, AccessController.PERMISSION_KEY_VIEW, obj);
	}

	public boolean hasEditPermission(List<Integer> groupIds, PresentationObject obj) {
		return this.hasPermission(groupIds, AccessController.PERMISSION_KEY_EDIT, obj);
	}

	@Override
	public boolean isSuperAdmin() {
		if(isLoggedOn()){
			try {
				return ELUtil.getInstance().getBean(LoginSession.class).isSuperAdmin();
			}
			catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error resolving if current user is super admin", e);
			}
		}
		return false;
	}

	@Override
	public boolean isLoggedOn() {
		return com.idega.core.accesscontrol.business.LoginBusinessBean.isLoggedOn(this);
	}

	/**
	 * Expensive method, not recommended to use frequently
	 *
	 * @throws UnavailableIWContext
	 *             if the IWContext is not set
	 *
	 */
	public static IWContext getInstance() throws UnavailableIWContext {
		IWContext theReturn = null;
		try {
			// If no IWContext is found then try to get the FacesContext:
			FacesContext fc = FacesContext.getCurrentInstance();
			if (fc != null) {
				theReturn = getIWContext(fc);
			}
			else {
				throw new UnavailableIWContext();
			}
		}
		catch (Exception e) {
			if(e instanceof UnavailableIWContext){
				throw (UnavailableIWContext) e;
			}
			else{
				throw new UnavailableIWContext(e);
			}
		}
		return theReturn;
	}

	public static IWContext getCurrentInstance() {

		try {
			IWContext iwc;
			FacesContext fctx = FacesContext.getCurrentInstance();

			if (fctx != null) {
				iwc = getIWContext(fctx);
			} else {
				iwc = null;
			}

			return iwc;

		} catch (Exception e) {
			throw new UnavailableIWContext(e);
		}
	}

	public String getCurrentState(PresentationObject obj) {
		if (obj != null) {
			return getCurrentState(obj.getParentObjectInstanceID());
		}
		else {
			return null;
		}
	}

	/**
	 * @todo implement
	 */
	@SuppressWarnings("unchecked")
	public String getCurrentState(int instanceId) {
		String historyId = this.getParameter(PRM_HISTORY_ID);
		// System.err.println("in iwc.getCurrentState()");
		if (historyId != null) {
			// System.err.println("historyId != null");
			HttpSession s = this.getSession();
			// System.err.println(" - from Session.hashCode() ->
			// "+s.hashCode());
			List<Object> historyList = (List<Object>) s.getAttribute(SESSION_OBJECT_STATE);
			// List historyList =
			// (List)this.getSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE);
			if (historyList != null && historyList.contains(historyId)) {
				int index = historyList.indexOf(historyId);
				// System.err.println("current state historyIndex = "+index + "
				// for instance " + instanceId);
				Object ob = ((Hashtable<Object, Object>) historyList.get(index + 1)).get(Integer.toString(instanceId));
				// System.err.println("current state = "+ob);
				// System.err.println("iwc.getCurrentState() ends");
				return (String) ob;
			}
		}
		// System.err.println("iwc.getCurrentState() ends");
		return null;
	}

	@Override
	public IWApplicationContext getApplicationContext() {
		return this.getIWMainApplication().getIWApplicationContext();
	}

	/**
	 * Gets if this object is in "Preview" mode in the Builder or in regular
	 * view not inside the Builder.
	 *
	 * @return true if in preview mode
	 */
	public boolean isInPreviewMode() {
		boolean preview = false;
		if (isParameterSet("view")) {
			if (isBuilderApplicationRunning()) {
				String view = getParameter("view");
				if (view.equals("preview")) {
					preview = true;
				}
			}
		}
		return (preview);
	}

	/**
	 * Gets if this object is in "Edit" mode in the Builder
	 *
	 * @return true if in edit mode
	 */
	public boolean isInEditMode() {
		boolean edit = false;
		if (isParameterSet("view")) {
			if (isBuilderApplicationRunning()) {
				String view = getParameter("view");
				if (view.equals("builder")) {
					edit = true;
				}
			}
		}
		return (edit);
	}

	private boolean isBuilderApplicationRunning() {
		return getIWMainApplication().isBuilderApplicationRunning(this);
	}

	/**
	 * @return true if the client is a handheld device such as a PalmPilot, a
	 *         PocketPC device or a phone
	 */
	public boolean isClientHandheld() {
		if (!this._doneHandHeldCheck) {
			String user_agent = this.getUserAgent();
			if (user_agent.indexOf("Windows CE") != -1) {
				this._clientIsHandHeld = true;
			}
			else if (user_agent.indexOf("Palm") != -1) {
				this._clientIsHandHeld = true;
			}
			else if (user_agent.toLowerCase().indexOf("wap") != -1) {
				this._clientIsHandHeld = true;
			}
			else if (user_agent.toLowerCase().indexOf("nokia") != -1) {
				this._clientIsHandHeld = true;
			}
			else if (user_agent.toLowerCase().indexOf("ericsson") != -1) {
				this._clientIsHandHeld = true;
			}
			else if (user_agent.toLowerCase().indexOf("symbian") != -1) {
				this._clientIsHandHeld = true;
			}
			else if (user_agent.toLowerCase().indexOf("wapman") != -1) {
				this._clientIsHandHeld = true;
			}
			this._doneHandHeldCheck = true;
		}
		return this._clientIsHandHeld;
	}

	@Override
	public ICDomain getDomain() {
		//ICDomain domain = getIWMainApplication().getIWApplicationContext().getDomain();
		String serverName = getServerName();
		ICDomain domain = getDomainByServerName(serverName);
		return domain;
	}

	@Override
	public ICDomain getDomainByServerName(String serverName) {
		return getIWMainApplication().getIWApplicationContext().getDomainByServerName(serverName);
	}

	public void forwardToIBPage(Page fromPage, ICPage page) {
		forwardToIBPage(fromPage, ((Integer) page.getPrimaryKey()).intValue());
	}

	public void forwardToIBPage(Page fromPage, ICPage page, int secondInterval) {
		forwardToIBPage(fromPage, ((Integer) page.getPrimaryKey()).intValue(), secondInterval);
	}

	public void forwardToIBPage(Page fromPage, ICPage page, int secondInterval, boolean includeQueryString) {
		forwardToIBPage(fromPage, ((Integer) page.getPrimaryKey()).intValue(), secondInterval, includeQueryString);
	}

	public void forwardToIBPage(Page fromPage, int pageID) {
		forwardToIBPage(fromPage, pageID, 0);
	}

	public void forwardToIBPage(Page fromPage, int pageID, int secondInterval) {
		forwardToIBPage(fromPage, pageID, secondInterval, false);
	}

	public void forwardToIBPage(Page fromPage, int pageID, int secondInterval, boolean includeQueryString) {
		try {
			BuilderService bs;
			bs = BuilderServiceFactory.getBuilderService(this.getApplicationContext());
			String url = bs.getPageURI(pageID);
			forwardToURL(fromPage, url, secondInterval, includeQueryString);
		}
		catch (RemoteException e) {
			LOGGER.log(Level.WARNING, "Error forwarding to: " + pageID + " from: " + fromPage.getPageID(), e);
			CoreUtil.sendExceptionNotification(e);
		}
	}

	/**
	 * Forwards to the url specified by setting a meta (refresh) header into the
	 * page object given by fromPage.
	 *
	 * @param fromPage
	 * @param url
	 */
	public void forwardToURL(Page fromPage, String url) {
		forwardToURL(fromPage, url, -1);
	}

	/**
	 * Forwards to the url specified by setting a meta (refresh) header into the
	 * page object given by fromPage.
	 *
	 * @param fromPage
	 * @param url
	 * @param includeQueryString
	 */
	public void forwardToURL(Page fromPage, String url, boolean includeQueryString) {
		forwardToURL(fromPage, url, -1, includeQueryString);
	}

	/**
	 * Forwards to the url specified by setting a meta (refresh) header into the
	 * page object given by fromPage.
	 *
	 * @param fromPage
	 * @param url
	 * @param secondInterval
	 */
	public void forwardToURL(Page fromPage, String url, int secondInterval) {
		forwardToURL(fromPage, url, secondInterval, true);
	}

	/**
	 * Forwards to the url specified by setting a meta (refresh) header into the
	 * page object given by fromPage.
	 *
	 * @param fromPage
	 * @param url
	 * @param secondInterval
	 * @param includeQueryString
	 */
	public void forwardToURL(Page fromPage, String url, int secondInterval, boolean includeQueryString) {
		/**
		 * @todo temporary workaround find out why this doesn't work This is
		 *       supposed to work but I always get: IllegalStateException:
		 *       cannot forward because writer or stream has been obtained.
		 */

		StringBuffer URL = new StringBuffer(url);

		if (includeQueryString) {
			String requestString = getRequest().getQueryString();
			if (requestString != null) {
				if (url.indexOf("?") == -1) {
					URL.append('?');
				}
				else {
					URL.append('&');
				}
				URL.append(requestString);
			}
		}

		if (secondInterval > 0) {
			fromPage.setToRedirect(URL.toString(), secondInterval);
		}
		else {
			fromPage.setToRedirect(URL.toString());
		}
	}

	/*
	 * Returns null if not found
	 */
	public Cookie getCookie(String cookieName) {
		return RequestUtil.getCookie(getRequest(), cookieName);
	}

	/**
	 * Gets the current user associated with this context <br>
	 * This method is meant to replace getUser()
	 *
	 * @return The current user if there is one associated with the current
	 *         context. If there is none the method returns null.
	 * @throws NotLoggedOnException
	 *             if no user is logged on.
	 */
	@Override
	public com.idega.user.data.User getCurrentUser() throws NotLoggedOnException {
		HttpSession session = getSession();
		LoginBusinessBean loginBean = LoginBusinessBean.getLoginBusinessBean(session);
		return loginBean.getCurrentUserLegacy(session);
	}

	@Override
	public User getLoggedInUser() {
		HttpSession session = getSession();
		LoginBusinessBean loginBean = LoginBusinessBean.getLoginBusinessBean(session);
		return loginBean.getCurrentUser(session);
	}

	/**
	 * Gets the Id of the current user associated with this context <br>
	 * This method is meant to replace getUserId()
	 *
	 * @return The Id of the current user. If there is one associated with the
	 *         current context.
	 * @throws NotLoggedOnException
	 *             if no user is logged on
	 */
	public int getCurrentUserId() {
		return getCurrentUser().getID();
	}

	/**
	 * TODO reimplement
	 *
	 * @return The pageId for the current IBPage that is being displayed.
	 *         Returns -1 if an error occurred.
	 */
	public int getCurrentIBPageID() {
		BuilderService bs;
		try {
			bs = BuilderServiceFactory.getBuilderService(this.getApplicationContext());
			return bs.getCurrentPageId(this);
		}
		catch (RemoteException e) {
			LOGGER.log(Level.WARNING, "Error getting current Builder page", e);
		}
		return -1;
	}

	public boolean isSecure() {
		return getRequest().isSecure();
	}

	/*
	 * BEGIN ABSTRACT METHODS FROM FacesContext
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getLocale()
	 */
	public Locale getLocale() {
		return this.getCurrentLocale();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#release()
	 */
	@Override
	public void release() {
		getRealFacesContext().release();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#renderResponse()
	 */
	@Override
	public void renderResponse() {
		getRealFacesContext().renderResponse();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#responseComplete()
	 */
	@Override
	public void responseComplete() {
		getRealFacesContext().responseComplete();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale arg0) {
		this.setCurrentLocale(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#addMessage(java.lang.String,
	 *      javax.faces.application.FacesMessage)
	 */
	@Override
	public void addMessage(String arg0, FacesMessage arg1) {
		getRealFacesContext().addMessage(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getClientIdsWithMessages()
	 */
	@Override
	public Iterator<String> getClientIdsWithMessages() {
		return getRealFacesContext().getClientIdsWithMessages();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getExternalContext()
	 */
	@Override
	public ExternalContext getExternalContext() {
		ExternalContext extContext = getRealFacesContext() != null ? getRealFacesContext().getExternalContext() : null;
		if (extContext == null) {
			extContext = getFacesContextInitializer().getInitializedExternalContext(getServletContext(), getRequest(), getResponse());
		}
		return extContext;
	}

	@Override
	public ELContext getELContext() {
		ELContext elContext = getRealFacesContext() != null ? getRealFacesContext().getELContext() : null;
		if (elContext == null) {
			throw new IllegalStateException("Real FacesContext is not initialized yet.");
		}

		return elContext;
	}

	private FacesContextInitializer getFacesContextInitializer() {
		return ELUtil.getInstance().getBean(FacesContextInitializer.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getMaximumSeverity()
	 */
	@Override
	public Severity getMaximumSeverity() {
		return getRealFacesContext().getMaximumSeverity();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getMessages()
	 */
	@Override
	public Iterator<FacesMessage> getMessages() {
		return getRealFacesContext().getMessages();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getMessages(java.lang.String)
	 */
	@Override
	public Iterator<FacesMessage> getMessages(String arg0) {
		return getRealFacesContext().getMessages(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getRenderKit()
	 */
	@Override
	public RenderKit getRenderKit() {
		return getRealFacesContext().getRenderKit();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getRenderResponse()
	 */
	@Override
	public boolean getRenderResponse() {
		return getRealFacesContext().getRenderResponse();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getResponseComplete()
	 */
	@Override
	public boolean getResponseComplete() {
		return getRealFacesContext().getResponseComplete();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getResponseStream()
	 */
	@Override
	public ResponseStream getResponseStream() {
		return getRealFacesContext().getResponseStream();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getResponseWriter()
	 */
	@Override
	public ResponseWriter getResponseWriter() {
		if (this.isCacheing() && this.cacheResponseWriter != null) {
			return this.cacheResponseWriter;
		}
		else {
			return getRealFacesContext().getResponseWriter();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getViewRoot()
	 */
	@Override
	public UIViewRoot getViewRoot() {
		return getRealFacesContext().getViewRoot();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#setResponseStream(javax.faces.context.ResponseStream)
	 */
	@Override
	public void setResponseStream(ResponseStream arg0) {
		getRealFacesContext().setResponseStream(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#setResponseWriter(javax.faces.context.ResponseWriter)
	 */
	@Override
	public void setResponseWriter(ResponseWriter arg0) {
		getRealFacesContext().setResponseWriter(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#setViewRoot(javax.faces.component.UIViewRoot)
	 */
	@Override
	public void setViewRoot(UIViewRoot arg0) {
		getRealFacesContext().setViewRoot(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.context.FacesContext#getApplication()
	 */
	@Override
	public Application getApplication() {
		return getRealFacesContext().getApplication();
	}

	/**
	 * Gets the real (underlying) FacesContext instance
	 */
	private FacesContext getRealFacesContext() {
		return this.realFacesContext;
	}

	/**
	 * Sets the real (underlying) FacesContext instance
	 */
	private void setRealFacesContext(FacesContext fc) {
		this.realFacesContext = fc;
	}

	/**
	 * This method gets the header value for the attribute "Authorization" which
	 * is used e.g. for getting username and password in BASIC
	 * Authorization/Authentication request
	 *
	 * @return Returns the header value for "Authorization" attribute
	 */
	public String getAuthorizationHeader() {
		return RequestUtil.getAuthorizationHeader(getRequest());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.idegaweb.IWUserContext#getUserPrincipal()
	 */
	@Override
	public Principal getUserPrincipal() {
		return getRequest().getUserPrincipal();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.idegaweb.IWUserContext#isUserInRole(java.lang.String)
	 */
	@Override
	public boolean isUserInRole(String role) {
		return getRequest().isUserInRole(role);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.idegaweb.IWUserContext#getRemoteUser()
	 */
	@Override
	public String getRemoteUser() {
		return getRequest().getRemoteUser();
	}

	public String getIdegaSessionId() {
		String sessionId = (String) getSessionAttribute(IDEGA_SESSION_KEY);
		if (sessionId == null) {
			sessionId = UUIDGenerator.getInstance().generateUUID();
			setSessionAttribute(IDEGA_SESSION_KEY, sessionId);
		}
		return sessionId;
	}

	@Autowired
	private RepositoryService repository;

	public RepositoryService getRepository() {
		if (repository == null) {
			ELUtil.getInstance().autowire(this);
		}
		return repository;
	}

	public Session getRepositorySession() throws javax.jcr.RepositoryException{
//		 Session session = (Session) getExternalContext().getRequestMap().get(JCR_SESSION_REQUEST_KEY);
//		 if (session == null) {
//			 session = createNewRepositorySession();
//			 getExternalContext().getRequestMap().put(JCR_SESSION_REQUEST_KEY, session);
//		 }
//		 return session;
		return createNewRepositorySession();
	}

	public Session createNewRepositorySession() throws javax.jcr.RepositoryException{
		if (this.isLoggedOn()) {
			return getRepository().login(new SimpleCredentials(getRemoteUser(), CoreConstants.EMPTY.toCharArray()));
		} else {
			return getRepository().login();
		}
	}
}