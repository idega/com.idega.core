package com.idega.util.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.include.ExternalLink;
import com.idega.idegaweb.include.JavaScriptLink;
import com.idega.idegaweb.include.StyleSheetLink;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

@Scope("request")
@Service(ResourcesManager.SPRING_BEAN_IDENTIFIER)
public class ResourcesManagerImpl implements ResourcesManager {

	private static final long serialVersionUID = -3876318831841387443L;
	private static final Logger LOGGER = Logger.getLogger(ResourcesManagerImpl.class.getName());
	
	private static final String WEB_PAGE_RESOURCES = "idegaCoreWebPageResources";
	private static final String CONCATENATED_RESOURCES = "idegaCoreConcatenatedRecources";

	private List<JavaScriptLink> javaScriptActions;
	private List<JavaScriptLink> javaScriptResources;
	private List<StyleSheetLink> cssFiles;
	
	private Map<String, String> mediaMap;
	
	public List<JavaScriptLink> getJavaScriptResources() {
		if (javaScriptResources == null) {
			javaScriptResources = new ArrayList<JavaScriptLink>();
		}
		return javaScriptResources;
	}
	
	public List<JavaScriptLink> getJavaScriptActions() {
		if (javaScriptActions == null) {
			javaScriptActions = new ArrayList<JavaScriptLink>();
		}
		return javaScriptActions;
	}
	
	public List<StyleSheetLink> getCSSFiles() {
		if (cssFiles == null) {
			cssFiles = new ArrayList<StyleSheetLink>();
		}
		return cssFiles;
	}
	
	public Map<String, String> getMediaMap() {
		if (mediaMap == null) {
			mediaMap = new HashMap<String, String>();
		}
		return mediaMap;
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
		
		return StringUtil.isEmpty(concatenatedResourcesUri) ? null : concatenatedResourcesUri;
	}
	
	private String getUriToConcatenatedResourcesFromCache(List<? extends ExternalLink> resources, String fileType) {
		if (ListUtil.isEmpty(resources) || StringUtil.isEmpty(fileType)) {
			return null;
		}
		
		//	Will try to get big file URI from requested resources
		StringBuilder cacheName = new StringBuilder();
		for (ExternalLink resource: resources) {
			cacheName.append(resource.getUrl());
		}
		
		String uriToConcatenatedResources = getCachedConcatenatedResources(cacheName.toString(), fileType);
		if (!StringUtil.isEmpty(uriToConcatenatedResources)) {
			resources.clear();
		}
		
		if (!StringUtil.isEmpty(uriToConcatenatedResources)) {
			addNotifierAboutLoadedCSSFiles(resources, fileType);
		}
		
		return uriToConcatenatedResources;
	}
	
	public String getConcatenatedResources(List<? extends ExternalLink> resources, String fileType, String serverName) {
		if (ListUtil.isEmpty(resources)) {
			return null;
		}
		
		//	Checking if ALL resources were concatenated already
		String uriToAllConcatenatedResources = getUriToConcatenatedResourcesFromCache(resources, fileType);
		if (!StringUtil.isEmpty(uriToAllConcatenatedResources)) {
			return uriToAllConcatenatedResources;
		}
		
		//	Didn't find concatenated file for ALL resources, will check every resource separately if it's available to be minified
		String resourceContent = null;
		List<ExternalLink> resourcesToLoad = new ArrayList<ExternalLink>();
		Map<String, String> addedResources = new HashMap<String, String>();
		for (ExternalLink resource: resources) {
			resourceContent = getResource(WEB_PAGE_RESOURCES, resource, serverName);
			if (StringUtil.isEmpty(resourceContent)) {
				if (resourceContent == null) {
					LOGGER.warning(new StringBuilder("Impossible to concatenate file: ").append(resource.getUrl()).toString());
				} else {
					resourceContent = new StringBuilder("/* ").append(resource.getUrl()).append(resourceContent == null ? " not available */" : " is empty */")
						.toString();
				}
			}
			
			if (!StringUtil.isEmpty(resourceContent)) {
				//	Resource is available to be concatenated
				resourcesToLoad.add(resource);
				addedResources.put(resource.getUrl(), resourceContent);
			}
		}
		
		if (ListUtil.isEmpty(addedResources.values())) {
			return null;	//	Nothing to concatenate
		}
		
		//	Removing AVAILABLE resources from request. Available resources will be concatenated to one big file
		resources.removeAll(resourcesToLoad);
		
		//	Checking if there is concatenated resource from ONLY AVAILABLE resources
		String concatenatedResourcesUri = getUriToConcatenatedResourcesFromCache(resources, fileType);
		if (!StringUtil.isEmpty(concatenatedResourcesUri)) {
			return concatenatedResourcesUri;
		}
		
		//	Nothing found in cache, creating big file
		StringBuilder allResources = null;
		if (isJavaScriptFile(fileType)) {
			allResources = new StringBuilder("var IdegaResourcesHandler = [");
			allResources = addResourcesToList(allResources, resourcesToLoad);
			allResources.append("];\n");
		}
		else {
			addNotifierAboutLoadedCSSFiles(resourcesToLoad, fileType);
		}
		
		if (allResources == null) {
			allResources = new StringBuilder();
		}
		StringBuilder cacheName = new StringBuilder();
		for (ExternalLink resource: resourcesToLoad) {
			allResources.append(addedResources.get(resource.getUrl()));
			
			cacheName.append(resource);
		}
		concatenatedResourcesUri = copyConcatenatedResourcesToWebApp(allResources.toString(), fileType);
		if (StringUtil.isEmpty(concatenatedResourcesUri)) {
			return null;
		}

		//	Putting in cache
		setCachedResource(CONCATENATED_RESOURCES, cacheName.toString(), concatenatedResourcesUri);
		setCachedResource(CONCATENATED_RESOURCES, cacheName.append("_content").toString(), allResources.toString());
		
		return concatenatedResourcesUri;
	}
	
	private void addNotifierAboutLoadedCSSFiles(List<? extends ExternalLink> resources, String fileType) {
		if (isJavaScriptFile(fileType)) {
			return;
		}
		
		//	Notifying about CSS files
		String addedCSSFilesNotifier = getJavaScriptActionForLoadedCSSFiles(resources);
		if (StringUtil.isEmpty(addedCSSFilesNotifier)) {
			return;
		}
		
		JavaScriptLink loadedCSSFilesAction = new JavaScriptLink();
		loadedCSSFilesAction.addAction(addedCSSFilesNotifier);
		if (!getJavaScriptActions().contains(loadedCSSFilesAction)) {
			getJavaScriptActions().add(loadedCSSFilesAction);
		}
	}
	
	public boolean isJavaScriptFile(String fileType) {
		return ResourcesAdder.FILE_TYPE_JAVA_SCRIPT.equals(fileType);
	}
	
	private String getJavaScriptActionForLoadedCSSFiles(List<? extends ExternalLink> cssFiles) {
		if (ListUtil.isEmpty(cssFiles)) {
			return null;
		}
		
		StringBuilder addedCSSFilesNotifier = new StringBuilder("IWCORE.addLoadedResources(");
		addedCSSFilesNotifier = addResourcesToList(addedCSSFilesNotifier, cssFiles);
		addedCSSFilesNotifier.append(");");
		return addedCSSFilesNotifier.toString();
	}
	
	@SuppressWarnings("unchecked")
	private StringBuilder addResourcesToList(StringBuilder content, List<? extends ExternalLink> resources) {
		for (Iterator<ExternalLink> resourcesIter = (Iterator<ExternalLink>) resources.iterator(); resourcesIter.hasNext();) {
			content.append(CoreConstants.QOUTE_SINGLE_MARK).append(resourcesIter.next().getUrl()).append(CoreConstants.QOUTE_SINGLE_MARK);
			if (resourcesIter.hasNext()) {
				content.append(CoreConstants.COMMA);
			}
		}
		return content;
	}
	
	private String copyConcatenatedResourcesToWebApp(String content, String fileType) {
		if (StringUtil.isEmpty(content)) {
			return null;
		}
		
		String fileName = new StringBuilder().append(getCachePrefix()).append(ResourcesAdder.OPTIMIZIED_RESOURCES).append(System.currentTimeMillis())
											.append(fileType).toString();
		String uriToResources = IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER)
																			.getVirtualPathWithFileNameString(fileName);
		
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
		String prefix = getCachePrefix();
		try {
			return getCache(new StringBuilder().append(prefix).append(cacheName).toString()).get(resourceName);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error putting resource to cache: " + cacheName, e);
		}
		return null;
	}
	
	private boolean setCachedResource(String cacheName, String resourceName, String content) {
		String prefix = getCachePrefix();
		try {
			getCache(new StringBuilder().append(prefix).append(cacheName).toString()).put(resourceName, content);
			return true;
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error putting resource to cache: " + cacheName, e);
		}
		return false;
	}
	
	private String getCachePrefix() {
		try {
			return IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("idega_core.resources_prefix", CoreConstants.EMPTY);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting application property", e);
		}
		return CoreConstants.EMPTY;
	}
	
	private String getResource(String cacheName, ExternalLink resource, String serverName) {
		String minifiedResource = getCachedResource(cacheName, resource.getUrl());
		if (!StringUtil.isEmpty(minifiedResource)) {
			return minifiedResource;
		}
		
		File resourceInWorkspace = IWBundleResourceFilter.copyResourceFromJarToWebapp(IWMainApplication.getDefaultIWMainApplication(), resource.getUrl());
		minifiedResource = (resourceInWorkspace == null || !resourceInWorkspace.exists()) ?
				getMinifiedResourceFromApplication(serverName, resource) :
				getMinifiedResourceFromWorkspace(resourceInWorkspace, resource);
		if (minifiedResource == null) {
			return null;
		}
		
		setCachedResource(cacheName, resource.getUrl(), minifiedResource);
		return minifiedResource;
	}
	
	private String getMinifiedResourceFromApplication(String serverURL, ExternalLink resource) {
		String resourceURI = resource.getUrl();
		if (resourceURI.startsWith(CoreConstants.SLASH)) {
			resourceURI = resourceURI.replaceFirst(CoreConstants.SLASH, CoreConstants.EMPTY);
		}
		String fullLink = new StringBuilder(serverURL).append(resourceURI).toString();
		URL url = null;
		try {
			url = new URL(fullLink);
		} catch (MalformedURLException e) {
			LOGGER.warning("Error getting resource from: " + fullLink);
		}
		if (url == null) {
			return null;
		}
		
		InputStream input = null;
		try {
			input = url.openStream();
		} catch (IOException e) {
			LOGGER.warning("Error getting resource from: " + fullLink);
		}
		if (input == null) {
			return null;
		}
		
		resource.setContentStream(input);

		String minified = null;
		try {
			minified = getMinifiedResource(resource);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting resource ('"+fullLink+"') from stream!", e);
		} finally {
			IOUtil.closeInputStream(input);
		}
		
		return minified;
	}
	
	private String getMinifiedResourceFromWorkspace(File resource, ExternalLink resourceLink) {
		if (resource == null || !resource.exists()) {
			return null;
		}
		
		String fileContent = null;
		try {
			fileContent = FileUtil.getStringFromFile(resource);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error getting content from file: " + resource.getName(), e);
		}
		if (StringUtil.isEmpty(fileContent)) {
			return null;
		}
		
		resourceLink.setContent(fileContent);
		
		String minified = null;
		try {
			minified = getMinifiedResource(resourceLink);
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error while minifying resource: " + resource.getName(), e);
			return fileContent;
		}
		if (StringUtil.isEmpty(minified)) {
			LOGGER.log(Level.WARNING, "Error while minifying resource: " + resource.getName());
			return fileContent;
		}
		
		return minified;
	}
	
	private String getMinifiedResource(ExternalLink resource) {
		AbstractMinifier minifier = resource instanceof StyleSheetLink ? new CSSMinifier() : new  JavaScriptMinifier();
		return minifier.getMinifiedResource(resource);
	}

}
