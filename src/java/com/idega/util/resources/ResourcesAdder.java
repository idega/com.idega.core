package com.idega.util.resources;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.DefaultAddResource;
import org.apache.myfaces.renderkit.html.util.ResourcePosition;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.ListUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class ResourcesAdder extends DefaultAddResource {
	
	private static final Logger LOGGER = Logger.getLogger(ResourcesAdder.class.getName());
	
	public static final String OPTIMIZIED_RESOURCES = "optimiziedResources_";
	protected static final String FILE_TYPE_JAVA_SCRIPT = ".js";
	private static final String FILE_TYPE_CSS = ".css";
	
	@Override
	public void addJavaScriptAtPosition(FacesContext context, ResourcePosition position, String uri) {
		List<String> javaScriptResources = getJavaScriptResources();
		if (javaScriptResources == null) {
			return;
		}
		
		if (javaScriptResources.contains(uri)) {
			return;
		}
		
		javaScriptResources.add(uri);
	}
	
	@Override
	public void addStyleSheet(FacesContext context, ResourcePosition position, String uri) {
		List<String> cssFiles = getCSSFiles();
		if (cssFiles == null) {
			return;
		}
		
		if (cssFiles.contains(uri)) {
			return;
		}
		
		cssFiles.add(uri);
	}
	
	@Override
	public void addInlineScriptAtPosition(FacesContext context, ResourcePosition position, String inlineScript) {
		List<String> javaScriptActions = getJavaScriptActions();
		if (javaScriptActions == null) {
			return;
		}
		
		if (javaScriptActions.contains(inlineScript)) {
			return;
		}
		
		javaScriptActions.add(inlineScript);
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
	
	private List<String> getJavaScriptResources() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getJavaScriptResources();
	}
	
	private List<String> getCSSFiles() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getCSSFiles();
	}
	
	private List<String> getJavaScriptActions() {
		ResourcesManager manager = getResourcesManager();
		return manager == null ? null : manager.getJavaScriptActions();
	}
	
	private boolean useOptimizer() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("idega_core.optimize_resources", Boolean.TRUE);
	}
	
	private void manageHeader(String serverName) {
		if (ListUtil.isEmpty(getJavaScriptActions()) && ListUtil.isEmpty(getJavaScriptResources()) && ListUtil.isEmpty(getCSSFiles())) {
			return;
		}
		
		boolean useOptimizer = useOptimizer();
		getHeaderBeginInfos().clear();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		//	JavaScript
		if (useOptimizer) {
			addResources(facesContext, getJavaScriptResources(), FILE_TYPE_JAVA_SCRIPT, serverName);
		}
		else {
			for (String uri: getJavaScriptResources()) {
				super.addJavaScriptAtPosition(facesContext, AddResource.HEADER_BEGIN, uri);
			}
		}
		
		//	CSS
		if (useOptimizer) {
			addResources(facesContext, getCSSFiles(), FILE_TYPE_CSS, serverName);
		}
		else {
			for (String uri: getCSSFiles()) {
				super.addStyleSheet(facesContext, AddResource.HEADER_BEGIN, uri);
			}
		}
		
		//	JS actions
		for (String action: getJavaScriptActions()) {
			super.addInlineScriptAtPosition(facesContext, AddResource.BODY_END, action);
		}
		
		try {
			getJavaScriptResources().clear();
			getCSSFiles().clear();
			getJavaScriptActions().clear();
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error emptying lists", e);
		}
	}
	
	private void addResources(FacesContext facesContext, List<String> resources, String fileType, String serverName) {
		if (ListUtil.isEmpty(resources)) {
			return;
		}
		
		ResourcesManager resourcesManager = getResourcesManager();
		if (resources == null) {
			return;
		}

		boolean javaScript = resourcesManager.isJavaScriptFile(fileType);
		String concatenatedResourcesUri = resourcesManager.getConcatenatedResources(resources, fileType, serverName);
		if (!ListUtil.isEmpty(resources)) {
			//	Restoring original resources
			for (String uri: resources) {
				if (javaScript) {
					super.addJavaScriptAtPosition(facesContext, ResourcesAdder.HEADER_BEGIN, uri);
				}
				else {
					super.addStyleSheet(facesContext, ResourcesAdder.HEADER_BEGIN, uri);
				}
			}
		}
		if (!StringUtil.isEmpty(concatenatedResourcesUri)) {
			//	Adding concatenated file to page
			if (javaScript) {
				super.addJavaScriptAtPosition(facesContext, ResourcesAdder.HEADER_BEGIN, concatenatedResourcesUri);
			}
			else {
				super.addStyleSheet(facesContext, ResourcesAdder.HEADER_BEGIN, concatenatedResourcesUri);
			}
		}
	}
}
