package com.idega.util.resources;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.idega.idegaweb.include.ExternalLink;
import com.idega.idegaweb.include.JavaScriptLink;
import com.idega.idegaweb.include.RSSLink;
import com.idega.idegaweb.include.StyleSheetLink;

public interface ResourcesManager extends Serializable {

	public static final String SPRING_BEAN_IDENTIFIER = "ePlatformWebPageResourcesManager";
	
	public List<JavaScriptLink> getJavaScriptResources();
	
	public List<JavaScriptLink> getJavaScriptActions();
	
	public List<StyleSheetLink> getCSSFiles();
	
	public List<RSSLink> getFeedLinks();
	
	public String getConcatenatedResources(List<? extends ExternalLink> resources, String fileType, String serverName);
	
	public boolean isJavaScriptFile(String fileType);
	
	public Map<String, String> getMediaMap();
}