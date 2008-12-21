package com.idega.util.resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.DefaultAddResource;
import org.apache.myfaces.renderkit.html.util.ResourcePosition;

import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class ResourcesAdder extends DefaultAddResource {
	
	private static final Logger LOGGER = Logger.getLogger(ResourcesAdder.class.getName());
	
	private List<String> javaScriptActions;
	private List<String> javaScriptResources;
	private List<String> cssFiles;
	
	private List<String> getJavaScriptResources() {
		if (javaScriptResources == null) {
			javaScriptResources = new ArrayList<String>();
		}
		return javaScriptResources;
	}
	
	private List<String> getJavaScriptActions() {
		if (javaScriptActions == null) {
			javaScriptActions = new ArrayList<String>();
		}
		return javaScriptActions;
	}
	
	private List<String> getCSSFiles() {
		if (cssFiles == null) {
			cssFiles = new ArrayList<String>();
		}
		return cssFiles;
	}
	
	@Override
	public void addJavaScriptAtPosition(FacesContext context, ResourcePosition position, String uri) {
		if (getJavaScriptResources().contains(uri)) {
			return;
		}
		
		javaScriptResources.add(uri);
	}
	
	@Override
	public void addStyleSheet(FacesContext context, ResourcePosition position, String uri) {
		if (getCSSFiles().contains(uri)) {
			return;
		}
		
		cssFiles.add(uri);
	}
	
	@Override
	public void addInlineScriptAtPosition(FacesContext context, ResourcePosition position, String inlineScript) {
		if (getJavaScriptActions().contains(inlineScript)) {
			return;
		}
		
		javaScriptActions.add(inlineScript);
	}
	
	@Override
	public void writeMyFacesJavascriptBeforeBodyEnd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		manageHeader();
		
		super.writeMyFacesJavascriptBeforeBodyEnd(request, response);
	}
	
	@Override
	public void writeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		manageHeader();
		
		super.writeResponse(request, response);
	}
	
	@Override
	public void writeWithFullHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
		manageHeader();
		
		super.writeWithFullHeader(request, response);
	}
	
	private boolean useOptimizer() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("idega_core.optimize_resources", Boolean.TRUE);
	}
	
	private synchronized void manageHeader() {
		if (ListUtil.isEmpty(getJavaScriptActions()) && ListUtil.isEmpty(getJavaScriptResources()) && ListUtil.isEmpty(getCSSFiles())) {
			return;
		}
		
		boolean useOptimizer = useOptimizer();
		getHeaderBeginInfos().clear();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		//	JavaScript
		if (useOptimizer) {
			addResources(facesContext, getJavaScriptResources(), ".js");
		}
		else {
			for (String uri: getJavaScriptResources()) {
				super.addJavaScriptAtPosition(facesContext, AddResource.HEADER_BEGIN, uri);
			}
		}
		
		//	CSS
		if (useOptimizer) {
			addResources(facesContext, getCSSFiles(), ".css");
		}
		else {
			for (String uri: getCSSFiles()) {
				super.addStyleSheet(facesContext, AddResource.HEADER_BEGIN, uri);
			}
		}
		
		//	JS actions	//	TODO: make optimizer?
		for (String action: getJavaScriptActions()) {
			super.addInlineScriptAtPosition(facesContext, AddResource.BODY_END, action);
		}
		
		javaScriptActions.clear();
		javaScriptResources.clear();
		cssFiles.clear();
	}
	
	private void addResources(FacesContext facesContext, List<String> resources, String fileType) {
		if (ListUtil.isEmpty(resources)) {
			return;
		}
		
		boolean javaScript = fileType.equals(".js");
		
		if (!javaScript) {	//	TODO: make it usable not only for JavaScript
			for (String uri: resources) {
				super.addStyleSheet(facesContext, ResourcesAdder.HEADER_BEGIN, uri);
			}
			return;
		}
		
		String concatinatedResourcesUri = getConcatinatedResources(resources, fileType);
		if (!ListUtil.isEmpty(resources)) {
			//	Restoring original resources
			for (String uri: resources) {
				super.addJavaScriptAtPosition(facesContext, ResourcesAdder.HEADER_BEGIN, uri);
			}
		}
		if (!StringUtil.isEmpty(concatinatedResourcesUri)) {
			//	Adding concatenated file to page
			super.addJavaScriptAtPosition(facesContext, ResourcesAdder.HEADER_BEGIN, concatinatedResourcesUri);
		}
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
            
	private String getConcatinatedResources(List<String> resources, String fileType) {
		if (ListUtil.isEmpty(resources)) {
			return null;
		}
		
		//	Making one big file from all requested resources
		String resourceContent = null;
		List<String> uris = new ArrayList<String>();
		Map<String, String> addedResources = new HashMap<String, String>();
		for (String resourceUri: resources) {
			resourceContent = getResource("idegaCoreWebPageResources", resourceUri);
			if (!StringUtil.isEmpty(resourceContent)) {
				uris.add(resourceUri);
				addedResources.put(resourceUri, resourceContent);
			}
		}
		if (ListUtil.isEmpty(addedResources.values())) {
			return null;
		}
		
		resources.removeAll(uris);
		
		//	Will make cache name from all resources URIs
		StringBuilder cacheName = new StringBuilder();
		for (String resourceUri: uris) {
			cacheName.append(resourceUri);
		}
		String concatinatedResourcesUri = getCache("idegaCoreConcatinatedRecources").get(cacheName.toString());
		if (!StringUtil.isEmpty(concatinatedResourcesUri)) {
			return concatinatedResourcesUri;
		}
		
		StringBuilder allResources = null;
		if (".js".equals(fileType)) {
			allResources = new StringBuilder("var IdegaResourcesHandler = [");
			for (Iterator<String> resourcesIter = uris.iterator(); resourcesIter.hasNext();) {
				allResources.append(CoreConstants.QOUTE_SINGLE_MARK).append(resourcesIter.next()).append(CoreConstants.QOUTE_SINGLE_MARK);
				if (resourcesIter.hasNext()) {
					allResources.append(CoreConstants.COMMA).append("\n");
				}
			}
			allResources.append("\n];\n");
		}
		
		if (allResources == null) {
			allResources = new StringBuilder();
		}
		for (String resource: uris) {
			allResources.append("\n").append(addedResources.get(resource));
		}
		concatinatedResourcesUri = uploadConcatinatedResources(allResources.toString(), fileType);
		if (StringUtil.isEmpty(concatinatedResourcesUri)) {
			return null;
		}
		getCache("idegaCoreConcatinatedRecources").put(cacheName.toString(), concatinatedResourcesUri);
		
		return concatinatedResourcesUri;
	}
	
	private String uploadConcatinatedResources(String content, String fileType) {
		if (StringUtil.isEmpty(content)) {
			return null;
		}
		
		String fileName = new StringBuilder().append("resources_").append(System.currentTimeMillis()).append(fileType).toString();
		String uriToResources = IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER)
																														.getVirtualPathWithFileNameString(fileName);
		File file = IWBundleResourceFilter.copyResourceFromJarOrCustomContentToWebapp(IWMainApplication.getDefaultIWMainApplication(), uriToResources, content);
		
		return (file == null || !file.exists()) ? null : uriToResources;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> getCache(String cacheName) {
		return IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication()).getCache(cacheName);
	}
	
	private String getResource(String cacheName, String resourceUri) {
		Map<String, String> cache = getCache(cacheName);
		String resourceContent = cache.get(resourceUri);
		if (!StringUtil.isEmpty(resourceContent)) {
			return resourceContent;
		}
		
		File resource = IWBundleResourceFilter.copyResourceFromJarToWebapp(IWMainApplication.getDefaultIWMainApplication(), resourceUri);
		if (resource == null) {
			//	TODO: manage 3rd party resources
		}
		String minifiedResource = getMinifiedResource(resource);
		if (StringUtil.isEmpty(minifiedResource)) {
			return null;
		}
		
		cache.put(resourceUri, minifiedResource);
		return minifiedResource;
	}
	
	private String getMinifiedResource(File resource) {
		if (resource == null || !resource.exists()) {
			return null;
		}
		
		List<String> fileContent = null;
		try {
			fileContent = FileUtil.getLinesFromFile(resource);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error getting content from file: " + resource.getName(), e);
		}
		if (ListUtil.isEmpty(fileContent)) {
			return null;
		}
		
		StringBuilder content = new StringBuilder();
		for (String line: fileContent) {
			content.append("\n").append(line);
		}
		
		//	TODO: minimize file content
		return content.toString();
	}
}
