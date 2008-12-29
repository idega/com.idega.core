package com.idega.util.resources;

import java.io.Serializable;
import java.util.List;

public interface ResourcesManager extends Serializable {

	public static final String SPRING_BEAN_IDENTIFIER = "ePlatformWebPageResourcesManager";
	
	public List<String> getJavaScriptResources();
	
	public List<String> getJavaScriptActions();
	
	public List<String> getCSSFiles();
	
	public String getConcatenatedResources(List<String> resources, String fileType, String serverName);
	
	public boolean isJavaScriptFile(String fileType);
}
