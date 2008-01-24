package com.idega.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.idega.business.SpringBeanName;

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
	public static final String AMP = "&";
	public static final String EQ = "=";
	public static final String QMARK = "?";
	public static final String COLON = ":";
	public static final String NEWLINE = "\n";
	public static final String STAR = "*";
	public static final String MINUS = "-";
	public static final String PERCENT = "%";
	public static final String PLUS = "+";
	
	public static final String CONTENT_TYPE_TEXT_CSS = "text/css";
	
	public static final String PROP_SYSTEM_SMTP_MAILSERVER = "messagebox_smtp_mailserver";
	public static final String PROP_SYSTEM_MAIL_FROM_ADDRESS = "messagebox_from_mailaddress";

	public static final String HANDLER_PARAMETER = "handler_parameter";
	public static final String BUILDER_PORPERTY_SETTER_STYLE_CLASS = "modulePropertySetter";

	public static final String PATH_FILES_ROOT = "/files";
	public static final String CONTENT_PATH = PATH_FILES_ROOT + "/cms";
	public static final String PAGES_PATH = CONTENT_PATH + "/pages";
	public static final String PUBLIC_PATH = CONTENT_PATH + "/public";

	public static final String ARTICLE_CONTENT_PATH = "/article";
	public final static String ARTICLE_FILENAME_SCOPE = "article";
	public final static String ARTICLE_RESOURCE_PATH_PROPERTY_NAME = "resourcePath";

	public static final String IW_USER_BUNDLE_IDENTIFIER = "com.idega.user";

	public static final String GROUP_SERVICE_DWR_INTERFACE_SCRIPT = "/dwr/interface/GroupService.js";
	
	public static final String SCHEDULE_SESSION_DWR_INTERFACE_SCRIPT = "/dwr/interface/ScheduleSession.js";
	
	public static final String APPLICATION_PROPERTY_TO_USE_OLD_THEME_PREVIEW_GENERATOR = "useOldThemeGenerator";

	public static final String WEBDAV_SERVLET_URI = "/content";
	
	public static final String ENCODING_UTF8 = "UTF-8";
	
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
				if (result instanceof Class) {
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
	
	public static final String CONTENT_VIEW_MANAGER_ID = "content";
	public static final String PAGES_VIEW_MANAGER_ID = "pages";

}