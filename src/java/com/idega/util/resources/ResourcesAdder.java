package com.idega.util.resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.DefaultAddResource;
import org.apache.myfaces.renderkit.html.util.ResourcePosition;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HTML;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.include.ExternalLink;
import com.idega.idegaweb.include.JavaScriptLink;
import com.idega.idegaweb.include.RSSLink;
import com.idega.idegaweb.include.StyleSheetLink;
import com.idega.util.ListUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class ResourcesAdder extends DefaultAddResource {
	
	private static final Logger LOGGER = Logger.getLogger(ResourcesAdder.class.getName());
	
	public static final String OPTIMIZE_RESOURCES = "idega_core.optimize_resources";
	public static final String OPTIMIZE_JAVA_SCRIPT = "idega_core.optimize_js";
	public static final String OPTIMIZE_STYLE_SHEET = "idega_core.optimize_css";
	
	public static final String OPTIMIZIED_RESOURCES = "optimizedResources_";
	public static final String FILE_TYPE_JAVA_SCRIPT = ".js";
	private static final String FILE_TYPE_CSS = ".css";
	
	@Override
	public void addJavaScriptAtPosition(FacesContext context, ResourcePosition position, String uri) {
		List<JavaScriptLink> javaScriptResources = getJavaScriptResources();
		
		if (containsResource(javaScriptResources, uri)) {
			return;
		}
		
		javaScriptResources.add(new JavaScriptLink(uri));
	}
	
	@Override
	public void addStyleSheet(FacesContext context, ResourcePosition position, String uri) {
		List<StyleSheetLink> cssFiles = getCSSFiles();

		if (containsResource(cssFiles, uri)) {
			return;
		}
		
		cssFiles.add(new StyleSheetLink(uri, getMediaMap().get(uri)));
	}
	
	@Override
	public void addInlineScriptAtPosition(FacesContext context, ResourcePosition position, String inlineScript) {
		List<JavaScriptLink> javaScriptActions = getJavaScriptActions();

		if (containsResource(javaScriptActions, inlineScript)) {
			return;
		}
		
		JavaScriptLink action = new JavaScriptLink();
		action.addAction(inlineScript);
		javaScriptActions.add(action);
	}
	
	private boolean containsResource(List<? extends ExternalLink> resources, String uri) {
		if (ListUtil.isEmpty(resources) || StringUtil.isEmpty(uri)) {
			return false;
		}
		
		for (ExternalLink resource: resources) {
			if (uri.equals(resource.getUrl())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void writeMyFacesJavascriptBeforeBodyEnd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		manageHeader(RequestUtil.getServerURL(request));
		
		super.writeMyFacesJavascriptBeforeBodyEnd(request, response);
	}
	
	@Override
	public void writeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		manageHeader(RequestUtil.getServerURL(request));
		
		super.writeResponse(request, response);
	}
	
	@Override
	public void writeWithFullHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
		manageHeader(RequestUtil.getServerURL(request));
		
		super.writeWithFullHeader(request, response);
	}
	
	@Override
	public String getResourceUri(FacesContext context, String uri, boolean withContextPath) {
		if (withContextPath) {
			try {
				return super.getResourceUri(context, uri, withContextPath);
			} catch(Exception e) {}
		}
		return uri;
	}
	
	private ResourcesManager getResourcesManager() {
		try {
			return ELUtil.getInstance().getBean(ResourcesManager.SPRING_BEAN_IDENTIFIER);
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error getting resources manager", e);
		}
		return null;
	}
	
	private List<RSSLink> getFeedResources() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getFeedLinks();
	}
	
	private List<JavaScriptLink> getJavaScriptResources() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getJavaScriptResources();
	}
	
	private List<StyleSheetLink> getCSSFiles() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getCSSFiles();
	}
	
	private List<JavaScriptLink> getJavaScriptActions() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getJavaScriptActions();
	}
	
	private Map<String, String> getMediaMap() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getMediaMap();
	}
	
	private static boolean useOptimizer(String applicationPropertyName, Boolean defaultValue) {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean(applicationPropertyName, defaultValue);
	}
	
	public static boolean isOptimizationTurnedOn(String applicationPropertyName) {
		return useOptimizer(applicationPropertyName, Boolean.TRUE);
	}
	
	public static boolean isCSSOptimizationTurnedOn() {
		return isOptimizationTurnedOn(ResourcesAdder.OPTIMIZE_RESOURCES) && isOptimizationTurnedOn(ResourcesAdder.OPTIMIZE_STYLE_SHEET);
	}
	
	private void manageHeader(String serverName) {
		if (ListUtil.isEmpty(getJavaScriptActions()) && ListUtil.isEmpty(getJavaScriptResources()) && ListUtil.isEmpty(getCSSFiles()) && ListUtil.isEmpty(getFeedResources())) {
			return;
		}
		
		boolean useOptimizer = useOptimizer(OPTIMIZE_RESOURCES, Boolean.TRUE);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		//	CSS
		if (useOptimizer && useOptimizer(OPTIMIZE_STYLE_SHEET, Boolean.TRUE)) {
			addResources(facesContext, getCSSFiles(), FILE_TYPE_CSS, serverName);
		} else {
			for (StyleSheetLink css: getCSSFiles()) {
				super.addStyleSheet(facesContext, AddResource.HEADER_BEGIN, css.getUrl());
			}
		}
		
		//	JavaScript
		if (useOptimizer && useOptimizer(OPTIMIZE_JAVA_SCRIPT, Boolean.TRUE)) {
			addResources(facesContext, getJavaScriptResources(), FILE_TYPE_JAVA_SCRIPT, serverName);
		} else {
			for (JavaScriptLink script: getJavaScriptResources()) {
				super.addJavaScriptAtPosition(facesContext, AddResource.BODY_END, script.getUrl());
			}
		}
		
		//	JS actions
		for (JavaScriptLink action: getJavaScriptActions()) {
			for (String scriptAction: action.getActions()) {
				super.addInlineScriptAtPosition(facesContext, AddResource.BODY_END, scriptAction);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addResources(FacesContext facesContext, List<? extends ExternalLink> resources, String fileType, String serverName) {
		if (ListUtil.isEmpty(resources)) {
			return;
		}
		
		ResourcesManager resourcesManager = getResourcesManager();
		if (resources == null) {
			return;
		}

		String concatenatedResourcesUri = resourcesManager.getConcatenatedResources(resources, fileType, serverName);
		if (!ListUtil.isEmpty(resources)) {
			//	Restoring original resources
			for (final ExternalLink link: resources) {
				if (link instanceof JavaScriptLink) {
					super.addJavaScriptAtPosition(facesContext, AddResource.BODY_END, link.getUrl());
				} else if (link instanceof StyleSheetLink) {
					super.addStyleSheet(facesContext, AddResource.HEADER_BEGIN, link.getUrl());
				}
			}
		}
		if (!StringUtil.isEmpty(concatenatedResourcesUri)) {
			//	Adding concatenated file to page
			if (concatenatedResourcesUri.endsWith(FILE_TYPE_JAVA_SCRIPT)) {
				super.addJavaScriptAtPosition(facesContext, AddResource.BODY_END, concatenatedResourcesUri);
			} else {
				super.addStyleSheet(facesContext, AddResource.HEADER_BEGIN, concatenatedResourcesUri);
			}
		}
		
		for (RSSLink feed: getFeedResources()) {
			getHeaderBeginInfos().add(new FeedInfo(feed));
		}
	}
	
	public void addMediaType(String resourceUri, String mediaType) {
		if (StringUtil.isEmpty(resourceUri) || StringUtil.isEmpty(mediaType)) {
			return;
		}
		
		Map<String, String> mediaMap = getMediaMap();
		if (mediaMap == null) {
			return;
		}
		
		mediaMap.put(resourceUri, mediaType);
	}
	
	public void addFeedLink(RSSLink feedLink) {
		if (!getFeedResources().contains(feedLink)) {
			getFeedResources().add(feedLink);
		}
	}
	
	private class FeedInfo implements WritablePositionedInfo {
		
		private RSSLink feed;
		
		private FeedInfo(RSSLink feed) {
			this.feed = feed;
		}
		
		public void writePositionedInfo(HttpServletResponse response, ResponseWriter writer) throws IOException {
			writer.startElement(HTML.LINK_ELEM, null);
			writer.writeAttribute(HTML.REL_ATTR, StringUtil.isEmpty(feed.getRelationship()) ? "alternate" : feed.getRelationship(), null);
			writer.writeAttribute(HTML.HREF_ATTR, response.encodeURL(feed.getUrl()), null);
			writer.writeAttribute(HTML.TYPE_ATTR, feed.getType(), null);
			writer.writeAttribute(HTML.TITLE_ATTR, StringUtil.isEmpty(feed.getTitle()) ? "Feed" : feed.getTitle(), null);
			writer.endElement(HTML.LINK_ELEM);
		}
	}
}