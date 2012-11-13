package com.idega.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.idega.business.SpringBeanName;
import com.idega.core.messaging.MessagingSettings;

@SpringBeanName("coreConstants")
public class CoreConstants {

	private static Object ARTICLE_CONSTANTS = null;
	private static Class<?> ARTICLE_ITEM_VIEWER_NAME = null;

	public static final String CORE_IW_BUNDLE_IDENTIFIER = "com.idega.core";

	private static final String[] _HEXIDECIMAL_LETTERS = new String[] {"a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"};
	public static final List <String> HEXIDECIMAL_LETTERS = Collections.unmodifiableList(Arrays.asList(_HEXIDECIMAL_LETTERS));

	public static final String EMPTY = "";
	public static final String SLASH = "/";
	public static final String BACK_SLASH = "\\";
	public static final String SPACE = " ";
	public static final String NUMBER_SIGN = "#";
	public static final String SEMICOLON = ";";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String UNDER = "_";
	public static final String AT = "@";
	public static final String AMP = "&";
	public static final String EQ = "=";
	public static final String QMARK = "?";
	public static final String COLON = ":";
	public static final String NEWLINE = "\n";
	public static final String DOUBLENEWLINE = "\n";
	public static final String STAR = "*";
	public static final String MINUS = "-";
	public static final String PERCENT = "%";
	public static final String PLUS = "+";
	public static final String CURLY_BRACKET_LEFT = "{";
	public static final String CURLY_BRACKET_RIGHT = "}";
	public static final String BRACKET_LEFT = "(";
	public static final String BRACKET_RIGHT = ")";
	public static final String QOUTE_MARK = "\"";
	public static final String QOUTE_SINGLE_MARK = "'";

	public static final String JS_STR_PARAM_SEPARATOR = "','";
	public static final String JS_STR_PARAM_END = "');";
	public static final String JS_STR_INITIALIZATION_END = "';";

	public static final String PROP_SYSTEM_SMTP_MAILSERVER = MessagingSettings.PROP_SYSTEM_SMTP_MAILSERVER;
	public static final String PROP_SYSTEM_MAIL_FROM_ADDRESS = MessagingSettings.PROP_MESSAGEBOX_FROM_ADDRESS;
	public static final String PROP_SYSTEM_SMTP_USER = MessagingSettings.PROP_SYSTEM_SMTP_USER_NAME;
	public static final String PROP_SYSTEM_SMTP_PASSWORD = MessagingSettings.PROP_SYSTEM_SMTP_PASSWORD;
	public static final String PROP_SYSTEM_SMTP_USE_AUTHENTICATION = MessagingSettings.PROP_SYSTEM_SMTP_USE_AUTHENTICATION;
	public static final String PROP_SYSTEM_ACCOUNT = "mail_user_account";
	public static final String PROP_SYSTEM_MAIL_HOST = "mail_host";
	public static final String PROP_SHOW_ADMIN_TOOLBAR = "show.admin.toolbar";
	
	/**
	 * <p>Use this property for all stupid code hacks, when that code is 
	 * needed only when developing. For example, if you want to generate some 
	 * fake data to fill your fields in form or log some more information, this
	 * could be used.</p>
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static final String DEVELOPEMENT_STATE_PROPERTY = "is_developement_mode";

	public static final String HANDLER_PARAMETER = "handler_parameter";
	public static final String BUILDER_PORPERTY_SETTER_STYLE_CLASS = "modulePropertySetter";

	public static final String PATH_FILES_ROOT = "/files";
	public static final String CONTENT_PATH = PATH_FILES_ROOT + "/cms";
	public static final String PAGES_URI_PREFIX = "/pages";
	public static final String PAGES_PATH = CONTENT_PATH + PAGES_URI_PREFIX;
	public static final String PUBLIC_PATH = PATH_FILES_ROOT + "/public";

	public static final String ARTICLE_CONTENT_PATH = "/article";
	public final static String ARTICLE_FILENAME_SCOPE = "article";
	public final static String ARTICLE_RESOURCE_PATH_PROPERTY_NAME = "resourcePath";

	public static final String IW_USER_BUNDLE_IDENTIFIER = "com.idega.user";

	public static final String GROUP_SERVICE_DWR_INTERFACE_SCRIPT = "/dwr/interface/GroupService.js";

	public static final String SCHEDULE_SESSION_DWR_INTERFACE_SCRIPT = "/dwr/interface/ScheduleSession.js";

	public static final String APPLICATION_PROPERTY_TO_USE_OLD_THEME_PREVIEW_GENERATOR = "useOldThemeGenerator";

	public static final String WEBDAV_SERVLET_URI = "/content";

	public static final String ENCODING_UTF8 = "UTF-8";

	public static final String DWR_ENGINE_SCRIPT = "/dwr/engine.js";
	public static final String DWR_UTIL_SCRIPT = "/dwr/util.js";
	public static final String HIDDEN_PAGE_IN_MENU_STYLE_CLASS = "hiddenPageInNavigationMenu";
	
	public static final String UNDEFINED_VALUE = "-1";

	public void setArticleConstantsInstance(Object o) {
		CoreConstants.ARTICLE_CONSTANTS = o;
	}

	public static Class<?> getArticleItemViewerClass() {
		if (ARTICLE_ITEM_VIEWER_NAME != null) {
			return ARTICLE_ITEM_VIEWER_NAME;
		}

		if (ARTICLE_CONSTANTS == null) {
			return null;
		}

		try {
			Class<?> clazz = Class.forName(ARTICLE_CONSTANTS.getClass().getName());
			Method[] methods = clazz.getDeclaredMethods();
			Method m = null;
			String name = null;
			Object[] params = null;
			Object result = null;
			for (int i = 0; (i < methods.length && ARTICLE_ITEM_VIEWER_NAME == null); i++) {
				m = methods[i];
				name = m.getName();
				params = m.getParameterTypes();
				result = m.invoke(name, params);
				if (result instanceof Class<?>) {
					ARTICLE_ITEM_VIEWER_NAME = (Class<?>) result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return ARTICLE_ITEM_VIEWER_NAME;
	}

	public static final String SINGLE_UICOMPONENT_RENDERING_PROCESS = "singleUIComponentRenderingProcess";

	public static final String WORKSPACE_VIEW_MANAGER_ID = "workspace";
	public static final String CONTENT_VIEW_MANAGER_ID = "content";
	public static final String PAGES_VIEW_MANAGER_ID = "pages";

	public static final String WORKSPACE_BUNDLE_IDENTIFIER = "com.idega.workspace";

	public static final String BUILDER = "builder";
	public static final String BUILDER_APPLICATION = "builderApplication";
	public static final String BUILDER_MODULE_PROPERTY_YES_VALUE = "Y";
	public static final String BUILDER_MODULE_PROPERTY_NO_VALUE = "N";

	public static final String PAGE_ERROR_403_HANDLER_PORPERTY = HttpServletResponse.SC_FORBIDDEN + "_PAGE_URI";
	public static final String PAGE_ERROR_404_HANDLER_PORPERTY = HttpServletResponse.SC_NOT_FOUND + "_PAGE_URI";

	public static final String SYSTEM_RESTART_IN_PROPERTY = "system_restart_in";

	public static final String PARAMETER_SESSION_ID = "JSESSIONID";

	public static final String EMAIL_DEFAULT_FROM = "staff@idega.is",
								EMAIL_DEFAULT_HOST = "smtp.emailsrvr.com";

	public static final String APPLICATION_PROPERTY_OMIT_DECLARATION = "xhtml.format.omit.declaration",
	
								PARAMETER_PAGE_VIEW_TYPE = "page_view_type",
								PAGE_VIEW_TYPE_REGULAR = "regular",
								PAGE_VIEW_TYPE_MOBILE = "mobile",
								
								PARAMETER_CHECK_HTML_HEAD_AND_BODY = "check_html_head_and_body";
	public static final String PROPERTIES_FOLDER_NAME_IN_JAR = "properties";
}