package com.idega.util.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

public class ResourcesAdder extends DefaultAddResource {
	
	private static final Logger LOGGER = Logger.getLogger(ResourcesAdder.class.getName());
	
	private static final String WEB_PAGE_RESOURCES = "idegaCoreWebPageResources";
	private static final String CONCATENATED_RESOURCES = "idegaCoreConcatenatedRecources";
	public static final String OPTIMIZIED_RESOURCES = "optimiziedResources_";
	
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
			addResources(facesContext, getJavaScriptResources(), ".js", serverName);
		}
		else {
			for (String uri: getJavaScriptResources()) {
				super.addJavaScriptAtPosition(facesContext, AddResource.HEADER_BEGIN, uri);
			}
		}
		
		//	CSS
		if (useOptimizer) {
			addResources(facesContext, getCSSFiles(), ".css", serverName);
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
		
		javaScriptActions.clear();
		javaScriptResources.clear();
		cssFiles.clear();
	}
	
	private void addResources(FacesContext facesContext, List<String> resources, String fileType, String serverName) {
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
		
		String concatenatedResourcesUri = getConcatenatedResources(resources, fileType, serverName);
		if (!ListUtil.isEmpty(resources)) {
			//	Restoring original resources
			for (String uri: resources) {
				super.addJavaScriptAtPosition(facesContext, ResourcesAdder.HEADER_BEGIN, uri);
			}
		}
		if (!StringUtil.isEmpty(concatenatedResourcesUri)) {
			//	Adding concatenated file to page
			super.addJavaScriptAtPosition(facesContext, ResourcesAdder.HEADER_BEGIN, concatenatedResourcesUri);
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
	
	private String getCachedConcatenatedResources(String cacheName, String fileType) {
		String concatenatedResourcesUri = getCachedResource(CONCATENATED_RESOURCES, cacheName);
		if (StringUtil.isEmpty(concatenatedResourcesUri)) {
			return null;
		}
		
		String cachedContent = getCachedResource(CONCATENATED_RESOURCES, new StringBuilder(cacheName.toString()).append("_content").toString());
		if (StringUtil.isEmpty(cachedContent)) {
			return null;
		}
		
		concatenatedResourcesUri = copyConcatenatedResourcesToWebApp(cachedContent, concatenatedResourcesUri, fileType);
		
		return StringUtil.isEmpty(concatenatedResourcesUri) ? null : concatenatedResourcesUri;
	}
	
	private String getUriToConcatenatedResourcesFromResources(List<String> resources, String fileType) {
		if (ListUtil.isEmpty(resources) || StringUtil.isEmpty(fileType)) {
			return null;
		}
		
		//	Will try to get big file URI from requested resources
		StringBuilder cacheName = new StringBuilder();
		for (String resourceUri: resources) {
			cacheName.append(resourceUri);
		}
		
		String uriToConcatenatedResources = getCachedConcatenatedResources(cacheName.toString(), fileType);
		if (!StringUtil.isEmpty(uriToConcatenatedResources)) {
			LOGGER.info(new StringBuilder("Found uri ('").append(uriToConcatenatedResources).append("') for concatenated files: ").append(cacheName.toString())
					.toString());
			
			resources.clear();
		}
		return uriToConcatenatedResources;
	}
	
	private String getConcatenatedResources(List<String> resources, String fileType, String serverName) {
		if (ListUtil.isEmpty(resources)) {
			return null;
		}
		
		//	Checking if ALL resources were concatenated already
		String uriToAllConcatenatedResources = getUriToConcatenatedResourcesFromResources(resources, fileType);
		if (!StringUtil.isEmpty(uriToAllConcatenatedResources)) {
			return uriToAllConcatenatedResources;
		}
		
		//	Didn't find concatenated file for ALL resources, will check every resource separately if it's available to be minified
		String resourceContent = null;
		List<String> uris = new ArrayList<String>();
		Map<String, String> addedResources = new HashMap<String, String>();
		for (String resourceUri: resources) {
			resourceContent = getResource(WEB_PAGE_RESOURCES, resourceUri, serverName);
			if (StringUtil.isEmpty(resourceContent)) {
				LOGGER.warning(new StringBuilder("Impossible to concatenate file: ").append(resourceUri).toString());
			}
			else {
				//	Resource is available to be concatenated
				uris.add(resourceUri);
				addedResources.put(resourceUri, resourceContent);
			}
		}
		
		if (ListUtil.isEmpty(addedResources.values())) {
			return null;	//	Nothing to concatenate
		}
		
		//	Removing AVAILABLE resources from request. Available resources will be concatenated to one big file
		resources.removeAll(uris);
		
		//	Checking if there is concatenated resource from ONLY AVAILABLE resources
		String concatenatedResourcesUri = getUriToConcatenatedResourcesFromResources(resources, fileType);
		if (!StringUtil.isEmpty(concatenatedResourcesUri)) {
			return concatenatedResourcesUri;
		}
		
		//	Nothing found in cache, creating big file
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
		StringBuilder cacheName = new StringBuilder();
		for (String resource: uris) {
			allResources.append("\n").append(addedResources.get(resource));
			
			cacheName.append(resource);
		}
		concatenatedResourcesUri = copyConcatenatedResourcesToWebApp(allResources.toString(), null, fileType);
		if (StringUtil.isEmpty(concatenatedResourcesUri)) {
			return null;
		}

		//	Putting in cache
		setCachedResource(CONCATENATED_RESOURCES, cacheName.toString(), concatenatedResourcesUri);
		setCachedResource(CONCATENATED_RESOURCES, cacheName.append("_content").toString(), allResources.toString());
		
		return concatenatedResourcesUri;
	}
	
	private String copyConcatenatedResourcesToWebApp(String content, String uriToResources, String fileType) {
		if (StringUtil.isEmpty(content)) {
			return null;
		}
		
		if (StringUtil.isEmpty(uriToResources)) {
			String fileName = new StringBuilder().append(OPTIMIZIED_RESOURCES).append(System.currentTimeMillis()).append(fileType).toString();
			uriToResources = IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER)
																			.getVirtualPathWithFileNameString(fileName);
		}
		File file = IWBundleResourceFilter.copyResourceFromJarOrCustomContentToWebapp(IWMainApplication.getDefaultIWMainApplication(), uriToResources, content);
		
		return (file == null || !file.exists()) ? null : uriToResources;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> getCache(String cacheName) {
		try {
			return IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication()).getCache(cacheName);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting cache!", e);
		}
		return null;
	}
	
	private String getCachedResource(String cacheName, String resourceName) {
		try {
			return getCache(cacheName).get(resourceName);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error putting resource to cache: " + cacheName, e);
		}
		return null;
	}
	
	private boolean setCachedResource(String cacheName, String resourceName, String content) {
		try {
			getCache(cacheName).put(resourceName, content);
			return true;
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error putting resource to cache: " + cacheName, e);
		}
		return false;
	}
	
	private String getResource(String cacheName, String resourceUri, String serverName) {
		String minifiedResource = getCachedResource(cacheName, resourceUri);
		if (!StringUtil.isEmpty(minifiedResource)) {
			return minifiedResource;
		}
		
		File resource = IWBundleResourceFilter.copyResourceFromJarToWebapp(IWMainApplication.getDefaultIWMainApplication(), resourceUri);
		minifiedResource = resource == null ? getMinifiedResource(serverName, resourceUri) : getMinifiedResource(resource);
		if (StringUtil.isEmpty(minifiedResource)) {
			return null;
		}
		
		setCachedResource(cacheName, resourceUri, minifiedResource);
		return minifiedResource;
	}
	
	private String getMinifiedResource(String serverURL, String resourceURI) {
		if (resourceURI.startsWith(CoreConstants.SLASH)) {
			resourceURI = resourceURI.replaceFirst(CoreConstants.SLASH, CoreConstants.EMPTY);
		}
		String fullLink = new StringBuilder(serverURL).append(resourceURI).toString();
		URL url = null;
		try {
			url = new URL(fullLink);
		} catch (MalformedURLException e) {
			LOGGER.log(Level.WARNING, "Error getting resource from: " + fullLink, e);
		}
		if (url == null) {
			return null;
		}
		
		InputStream input = null;
		try {
			input = url.openStream();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Error getting resource from: " + fullLink, e);
		}
		if (input == null) {
			return null;
		}
		
		return getMinifiedResource(input);
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
		
		try {
			return getMinifiedResource(StringHandler.getStreamFromString(content.toString()));
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while minifying resource: " + resource.getName(), e);
		}
		
		return content.toString();
	}
		
	private String getMinifiedResource(InputStream input) {
		OutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			ResourceMinifier minifier = new ResourceMinifier(input, output);
			minifier.minify();
		} catch(Exception e) {
			output = null;
			LOGGER.log(Level.WARNING, "Error while minifying resource", e);
		} finally {
			IOUtil.closeInputStream(input);
			IOUtil.closeOutputStream(output);
		}
		
		return output == null ? null : output.toString();
	}
}
