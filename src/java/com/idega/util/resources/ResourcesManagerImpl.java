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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

@Scope("request")
@Service(ResourcesManager.SPRING_BEAN_IDENTIFIER)
public class ResourcesManagerImpl implements ResourcesManager {

	private static final long serialVersionUID = -3876318831841387443L;
	private static final Logger LOGGER = Logger.getLogger(ResourcesManagerImpl.class.getName());
	
	private static final String WEB_PAGE_RESOURCES = "idegaCoreWebPageResources";
	private static final String CONCATENATED_RESOURCES = "idegaCoreConcatenatedRecources";

	@Autowired
	private ResourceScanner resourceScanner;

	private List<String> javaScriptActions;
	private List<String> javaScriptResources;
	private List<String> cssFiles;
	
	public List<String> getJavaScriptResources() {
		if (javaScriptResources == null) {
			javaScriptResources = new ArrayList<String>();
		}
		return javaScriptResources;
	}
	
	public List<String> getJavaScriptActions() {
		if (javaScriptActions == null) {
			javaScriptActions = new ArrayList<String>();
		}
		return javaScriptActions;
	}
	
	public List<String> getCSSFiles() {
		if (cssFiles == null) {
			cssFiles = new ArrayList<String>();
		}
		return cssFiles;
	}
	
	
	public ResourceScanner getResourceScanner() {
		return resourceScanner;
	}

	public void setResourceScanner(ResourceScanner resourceScanner) {
		this.resourceScanner = resourceScanner;
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
	
	private String getUriToConcatenatedResourcesFromCache(List<String> resources, String fileType) {
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
		
		if (!StringUtil.isEmpty(uriToConcatenatedResources)) {
			addNotifierAboutLoadedCSSFiles(resources, fileType);
		}
		
		return uriToConcatenatedResources;
	}
	
	public String getConcatenatedResources(List<String> resources, String fileType, String serverName) {
		if (ListUtil.isEmpty(resources)) {
			return null;
		}
		
		//	Checking if ALL resources were concatenated already
		String uriToAllConcatenatedResources = getUriToConcatenatedResourcesFromCache(resources, fileType);
		if (!StringUtil.isEmpty(uriToAllConcatenatedResources)) {
			return uriToAllConcatenatedResources;
		}
		
		//	Didn't find concatenated file for ALL resources, will check every resource separately if it's available to be minified
		boolean cssFiles = !isJavaScriptFile(fileType);
		String resourceContent = null;
		List<String> resourcesToLoad = new ArrayList<String>();
		Map<String, String> addedResources = new HashMap<String, String>();
		for (String resourceUri: resources) {
			resourceContent = getResource(WEB_PAGE_RESOURCES, resourceUri, serverName);
			if (StringUtil.isEmpty(resourceContent)) {
				LOGGER.warning(new StringBuilder("Impossible to concatenate file: ").append(resourceUri).toString());
				if (cssFiles) {
					resourceContent = new StringBuilder("/* ").append(resourceUri).append(" not available */").toString();
				}
			}
			
			if (!StringUtil.isEmpty(resourceContent)) {
				//	Resource is available to be concatenated
				resourcesToLoad.add(resourceUri);
				addedResources.put(resourceUri, resourceContent);
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
			allResources.append("\n];\n");
		}
		else {
			addNotifierAboutLoadedCSSFiles(resourcesToLoad, fileType);
		}
		
		if (allResources == null) {
			allResources = new StringBuilder();
		}
		StringBuilder cacheName = new StringBuilder();
		for (String resource: resourcesToLoad) {
			allResources/*.append("\n")*/.append(addedResources.get(resource));
			
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
	
	private void addNotifierAboutLoadedCSSFiles(List<String> resources, String fileType) {
		if (isJavaScriptFile(fileType)) {
			return;
		}
		
		//	Notifying about CSS files
		String addedCSSFilesNotifier = getJavaScriptActionForLoadedCSSFiles(resources);
		if (StringUtil.isEmpty(addedCSSFilesNotifier)) {
			return;
		}
		
		if (!getJavaScriptActions().contains(addedCSSFilesNotifier)) {
			getJavaScriptActions().add(addedCSSFilesNotifier);
		}
	}
	
	public boolean isJavaScriptFile(String fileType) {
		return ResourcesAdder.FILE_TYPE_JAVA_SCRIPT.equals(fileType);
	}
	
	private String getJavaScriptActionForLoadedCSSFiles(List<String> cssFiles) {
		if (ListUtil.isEmpty(cssFiles)) {
			return null;
		}
		
		StringBuilder addedCSSFilesNotifier = new StringBuilder("IWCORE.addLoadedResources(");
		addedCSSFilesNotifier = addResourcesToList(addedCSSFilesNotifier, cssFiles);
		addedCSSFilesNotifier.append(");");
		return addedCSSFilesNotifier.toString();
	}
	
	private StringBuilder addResourcesToList(StringBuilder content, List<String> resources) {
		for (Iterator<String> resourcesIter = resources.iterator(); resourcesIter.hasNext();) {
			content.append(CoreConstants.QOUTE_SINGLE_MARK).append(resourcesIter.next()).append(CoreConstants.QOUTE_SINGLE_MARK);
			if (resourcesIter.hasNext()) {
				content.append(CoreConstants.COMMA).append("\n");
			}
		}
		return content;
	}
	
	private String copyConcatenatedResourcesToWebApp(String content, String uriToResources, String fileType) {
		if (StringUtil.isEmpty(content)) {
			return null;
		}
		
		if (StringUtil.isEmpty(uriToResources)) {
			String fileName = new StringBuilder().append(getCachePrefix()).append(ResourcesAdder.OPTIMIZIED_RESOURCES).append(System.currentTimeMillis())
													.append(fileType).toString();
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
	
	private String getResource(String cacheName, String resourceUri, String serverName) {
		String minifiedResource = getCachedResource(cacheName, resourceUri);
		if (!StringUtil.isEmpty(minifiedResource)) {
			return minifiedResource;
		}
		
		File resource = IWBundleResourceFilter.copyResourceFromJarToWebapp(IWMainApplication.getDefaultIWMainApplication(), resourceUri);
		minifiedResource = resource == null ? getMinifiedResource(serverName, resourceUri) : getMinifiedResource(resource, resourceUri);
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
		
		return getMinifiedResource(input);
	}
	
	private String getMinifiedResource(File resource, String resourceUri) {
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
		
		boolean javaScript = true;
		String resourceContent = null;
		if (resourceUri.endsWith(ResourcesAdder.FILE_TYPE_JAVA_SCRIPT)) {
			StringBuilder content = new StringBuilder();
			for (String line: fileContent) {
				content.append("\n").append(line);
			}
			resourceContent = content.toString();
		}
		else {
			javaScript = false;
			resourceContent = getResourceScanner().getParsedContent(fileContent, resourceUri.substring(0, resourceUri.lastIndexOf(CoreConstants.SLASH) + 1));
		}
		
		if (StringUtil.isEmpty(resourceContent)) {
			return null;
		}
		
		if (javaScript) {
			try {
				return getMinifiedResource(StringHandler.getStreamFromString(resourceContent));
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error while minifying resource: " + resource.getName(), e);
			}
		} else {
			return getMinifiedResource(resourceContent);
		}
		
		return resourceContent;
	}
	
	private String getMinifiedResource(String content) {
		CSSMinifier cssMinifier = new CSSMinifier();
		return cssMinifier.minifyCSS(new StringBuffer(content)).toString();
	}
	
	private String getMinifiedResource(InputStream input) {
		OutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			JavaScriptMinifier minifier = new JavaScriptMinifier(input, output);
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
