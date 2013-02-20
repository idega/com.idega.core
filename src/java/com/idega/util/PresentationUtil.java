package com.idega.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;

import com.idega.idegaweb.include.PageResourceConstants;
import com.idega.idegaweb.include.RSSLink;
import com.idega.presentation.IWContext;
import com.idega.util.resources.ResourcesAdder;

public class PresentationUtil {

	public static final String ATTRIBUTE_JAVA_SCRIPT_SOURCE_FOR_HEADER = "javaScriptSourceLineForHeaderAttribute";
	public static final String ATTRIBUTE_JAVA_SCRIPT_SOURCE_FOR_BODY = "javaScriptSourceLineForBodyAttribute";
	public static final String ATTRIBUTE_JAVA_SCRIPT_ACTION_FOR_BODY = "javaScriptActionForBodyAttribute";
	public static final String ATTRIBUTE_CSS_SOURCE_LINE_FOR_HEADER = "cssSourceLineForHeaderAttribute";
	public static final String ATTRIBUTE_ADD_CSS_DIRECTLY = "addCSSFilesDirectlyToPage";

	public static boolean addJavaScriptSourcesLinesToHeader(IWContext iwc, List<String> scriptsUris) {
		if (iwc == null || scriptsUris == null) {
			return false;
		}

		for (int i = 0; i < scriptsUris.size(); i++) {
			addJavaScriptSourceLineToHeader(iwc, scriptsUris.get(i));
		}

		return true;
	}

	public static final String getFixedUrl(String url) {
		if (StringUtil.isEmpty(url))
			return url;

		if (url.indexOf(CoreConstants.SEMICOLON) != -1)
			url = url.split(CoreConstants.SEMICOLON)[0];

		return url;
	}
	
	/**
	 * Add javascript resources to the default location of the HTML header.
	 */
	@SuppressWarnings("unchecked")
	public static void addJs(IWContext iwc, Object ... scripts) {
		for (Object s : scripts) {
			if (s instanceof List) {
				addJavaScriptSourcesLinesToHeader(iwc, (List<String>) s);
			}
			if (s instanceof String) {
				addJavaScriptSourceLineToHeader(iwc, (String) s);
			}
		}
	}

	public static boolean addJavaScriptSourceLineToHeader(IWContext iwc, String scriptUri) {
		if (iwc == null || StringUtil.isEmpty(scriptUri))
			return false;

		if (CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			manageCientResource(iwc, ATTRIBUTE_JAVA_SCRIPT_SOURCE_FOR_HEADER, scriptUri);

			return true;
		}

		AddResource adder = getResourceAdder(iwc);
		if (adder == null)
			return false;

		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, getFixedUrl(scriptUri));
		return true;
	}

	public static boolean addJavaScriptSourcesLinesToBody(IWContext iwc, List<String> scriptsUris) {
		if (iwc == null || scriptsUris == null) {
			return false;
		}

		for (int i = 0; i < scriptsUris.size(); i++) {
			addJavaScriptSourceLineToBody(iwc, scriptsUris.get(i));
		}

		return true;
	}

	public static boolean addJavaScriptSourceLineToBody(IWContext iwc, String scriptUri) {
		if (iwc == null || StringUtil.isEmpty(scriptUri))
			return false;

		if (CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			manageCientResource(iwc, ATTRIBUTE_JAVA_SCRIPT_SOURCE_FOR_BODY, scriptUri);

			return true;
		}

		AddResource adder = getResourceAdder(iwc);
		if (adder == null)
			return false;

		adder.addJavaScriptAtPosition(iwc, AddResource.BODY_END, getFixedUrl(scriptUri));
		return true;
	}

	public static boolean addJavaScriptActionOnLoad(IWContext iwc, String action) {
		if (iwc == null || StringUtil.isEmpty(action))
			return false;

		if (CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			manageCientResource(iwc, ATTRIBUTE_JAVA_SCRIPT_ACTION_FOR_BODY, action);

			return true;
		}

		AddResource adder = getResourceAdder(iwc);
		if (adder == null)
			return false;

		adder.addInlineScriptAtPosition(iwc, AddResource.BODY_ONLOAD, action);
		return true;
	}

	public static boolean addJavascriptAlertOnLoad(IWContext iwc, String alert) {
		return addJavaScriptActionOnLoad(iwc, "alert('" + alert + "');");
	}

	public static boolean addJavaScriptActionsToBody(IWContext iwc, List<String> actions) {
		if (iwc == null || actions == null) {
			return false;
		}

		for (int i = 0; i < actions.size(); i++) {
			addJavaScriptActionToBody(iwc, actions.get(i));
		}

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static synchronized void manageCientResource(IWContext iwc, String attributeName, String resource) {
		List<String> resources = null;
		Object o = iwc.getSessionAttribute(attributeName);
		if (o instanceof List) {
			resources = (List) o;
		} else {
			resources = new ArrayList<String>();
		}

		if (!resources.contains(resource)) {
			resources.add(resource);

			iwc.setSessionAttribute(attributeName, resources);
		}
	}

	public static boolean addJavaScriptActionToBody(IWContext iwc, String action) {
		if (iwc == null || StringUtil.isEmpty(action))
			return false;

		if (CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			manageCientResource(iwc, ATTRIBUTE_JAVA_SCRIPT_ACTION_FOR_BODY, action);

			return true;
		}

		AddResource adder = getResourceAdder(iwc);
		if (adder == null)
			return false;

		adder.addInlineScriptAtPosition(iwc, AddResource.BODY_END, action);
		return true;
	}

	public static String getJavaScriptSourceLine(String scriptUri) {
		return getJavaScriptSourceLine(scriptUri, false);
	}

	public static String getJavaScriptSourceLine(String scriptUri, boolean includeOnce) {
		if (scriptUri == null) {
			return null;
		}

		StringBuffer script;

		if (includeOnce) {
			script = new StringBuffer("<script type=\"text/javascript\"> LazyLoader.load('").append(scriptUri).append("', null);</script>\n");
		} else {
			script = new StringBuffer("<script type=\"text/javascript\" src=\"").append(scriptUri).append("\"></script>\n");
		}

		return script.toString();
	}

	public static final String getJavaScriptLinesLoadedLazily(List<String> scriptUris, String callbackFunction) {
		return getJavaScriptLinesLoadedLazily(scriptUris, callbackFunction, false);
	}

	public static final String getJavaScriptLinesLoadedLazily(List<String> scriptUris, String callbackFunction, boolean onWindowLoaded) {
		if (scriptUris == null) {
			return null;
		}

		StringBuffer action = new StringBuffer("LazyLoader.loadMultiple([");
		for (int i = 0; i < scriptUris.size(); i++) {
			action.append(CoreConstants.QOUTE_SINGLE_MARK).append(scriptUris.get(i)).append(CoreConstants.QOUTE_SINGLE_MARK);
			if ((i + 1) < scriptUris.size()) {
				action.append(CoreConstants.COMMA).append(CoreConstants.SPACE);
			}
		}

		if (callbackFunction != null && !callbackFunction.startsWith("function()")) {
			callbackFunction = new StringBuffer("\nfunction() {\n").append(callbackFunction).append("\n}").toString();
		}
		action.append("], ").append(callbackFunction == null ? "null" : callbackFunction).append(");");

		if (onWindowLoaded) {
			action = new StringBuffer("registerEvent(window, 'load', function() {").append(action.toString()).append("});");
		}

		return action.toString();
	}

	public static String getJavaScriptSourceLines(List<String> scriptsUris) {
		if (scriptsUris == null) {
			return null;
		}

		StringBuffer scripts = new StringBuffer();
		for (int i = 0; i < scriptsUris.size(); i++) {
			scripts.append(getJavaScriptSourceLine(scriptsUris.get(i)));
		}

		return scripts.toString();
	}

	public static String getJavaScriptSourceLinesIncludeOnce(Collection<String> scriptsUris) {

		if (scriptsUris == null) {
			return null;
		}

		StringBuffer scripts = new StringBuffer();

		for (String uri : scriptsUris) {

			scripts.append(getJavaScriptSourceLine(uri, true));
		}

		return scripts.toString();
	}

	public static String getJavaScriptAction(String action) {
		if (action == null) {
			return null;
		}

		return new StringBuffer("<script type=\"text/javascript\">").append(action).append("</script>\n").toString();
	}

	public static String getJavaScriptActions(List<String> actions) {
		if (actions == null) {
			return null;
		}

		StringBuffer allActions = new StringBuffer();
		for (int i = 0; i < actions.size(); i++) {
			allActions.append(getJavaScriptAction(actions.get(i)));
		}

		return allActions.toString();
	}

	private static AddResource getResourceAdder(IWContext iwc) {
		try {
			return AddResourceFactory.getInstance(iwc);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Add style sheets to default location of the HTML head.
	 */
	@SuppressWarnings("unchecked")
	public static void addCss(IWContext iwc, Object ... styleSheets) {
		for (Object s : styleSheets) {
			if (s.getClass().equals(List.class)) {
				addStyleSheetsToHeader(iwc, (List<String>) s);
			}
			if (s.getClass().equals(String.class)) {
				addStyleSheetToHeader(iwc, (String) s);
			}
		}
	}

	public static boolean addStyleSheetsToHeader(IWContext iwc, List<String> styleSheetsUris) {
		if (iwc == null || styleSheetsUris == null) {
			return false;
		}

		for (int i = 0; i < styleSheetsUris.size(); i++) {
			addStyleSheetToHeader(iwc, styleSheetsUris.get(i));
		}

		return true;
	}

	public static boolean addStyleSheetToHeader(IWContext iwc, String styleSheetUri) {
		return addStyleSheetToHeader(iwc, styleSheetUri, PageResourceConstants.MEDIA_SCREEN);
	}

	public static boolean addStyleSheetToHeader(IWContext iwc, String styleSheetUri, String mediaType) {
		if (iwc == null || StringUtil.isEmpty(styleSheetUri))
			return false;

		if (CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			manageCientResource(iwc, ATTRIBUTE_CSS_SOURCE_LINE_FOR_HEADER, styleSheetUri);

			return true;
		}

		AddResource adder = getResourceAdder(iwc);
		if (adder == null)
			return false;

		styleSheetUri = getFixedUrl(styleSheetUri);

		if (!StringUtil.isEmpty(mediaType) && adder instanceof ResourcesAdder)
			((ResourcesAdder) adder).addMediaType(styleSheetUri, mediaType);

		adder.addStyleSheet(iwc, AddResource.HEADER_BEGIN, styleSheetUri);
		return true;
	}

	public static String getStyleSheetSourceLine(String styleSheetUri) {
		if (styleSheetUri == null) {
			return null;
		}

		return getCssLine(styleSheetUri, false);
	}

	public static String getCssLine(String cssUri, boolean includeOnce) {

		if (cssUri == null) {
			return null;
		}

		StringBuffer css;

		if (includeOnce) {
			css = new StringBuffer("<script type=\"text/javascript\">LazyLoader.load('").append(cssUri).append("', null);</script>\n");
		} else {
			css = new StringBuffer("<link type=\"text/css\" href=\"").append(cssUri).append("\" rel=\"stylesheet\" media=\"screen\"/>\n");
		}

		return css.toString();
	}

	public static String getStyleSheetsSourceLines(List<String> styleSheetsUris) {
		if (styleSheetsUris == null) {
			return null;
		}

		StringBuffer styles = new StringBuffer();
		for (int i = 0; i < styleSheetsUris.size(); i++) {
			styles.append(getStyleSheetSourceLine(styleSheetsUris.get(i)));
		}

		return styles.toString();
	}

	public static String getStyleSheetsSourceLinesIncludeOnce(Collection<String> styleSheetsUris) {

		if (styleSheetsUris == null) {
			return null;
		}

		StringBuffer styles = new StringBuffer();

		for (String uri : styleSheetsUris) {
			styles.append(getCssLine(uri, true));
		}

		return styles.toString();
	}

	public static boolean addFeedLink(IWContext iwc, RSSLink feedLink) {
		if (iwc == null || feedLink == null)
			return false;

		if (CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			addJavaScriptActionToBody(iwc, new StringBuffer("addFeedSymbolInHeader('").append(feedLink.getUrl()).append("', 'atom', 'Atom 1.0');").toString());
			return true;
		}

		AddResource adder = getResourceAdder(iwc);
		if (!(adder instanceof ResourcesAdder))
			return false;

		((ResourcesAdder) adder).addFeedLink(feedLink);
		return true;
	}
}