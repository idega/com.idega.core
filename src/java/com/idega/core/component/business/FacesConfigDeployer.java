/*
 * $Id: FacesConfigDeployer.java,v 1.1 2006/05/09 14:47:18 tryggvil Exp $
 * Created on 5.2.2006 in project org.apache.axis
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.idega.idegaweb.JarLoader;


/**
 * <p>
 * Implementation of JarLoader to automatically scan all faces-config.xml files in all
 * installed Jar files, parse them, and read into the componentRegistry.
 * </p>
 *  Last modified: $Date: 2006/05/09 14:47:18 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class FacesConfigDeployer implements JarLoader{

	private static Logger log = Logger.getLogger(FacesConfigDeployer.class.getName());
	private ComponentRegistry registry;
	
	
	/**
	 * @param registry 
	 * 
	 */
	public FacesConfigDeployer(ComponentRegistry registry) {
		this.registry=registry;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.JarLoader#loadJar(java.io.File, java.util.jar.JarFile, java.lang.String)
	 */
	public void loadJar(File bundleJarFile, JarFile jarFile, String jarPath) {
		Enumeration entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = (JarEntry) entries.nextElement();
			//JarFileEntry entryName = (JarFileEntry)entries.nextElement();
			String entryName = entry.getName();
			if(entryName.equals("META-INF/faces-config.xml")){
				try {
					//if(!entryName.endsWith("undeploy.wsdd")){
						log.info("Found JSF Description file: "+entryName);
						InputStream stream = jarFile.getInputStream(entry);
						processFacesConfig(stream);
					//}
					//}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	

	public void processFacesConfig(InputStream stream) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(stream);
        processDocument(document);
	}
	
	
	public void processDocument(Document document){
		
		Element rootElement = document.getDocumentElement();
		try {
			//processWSDD(null,engine,rootElement);
			NodeList childList = rootElement.getChildNodes();
			for (int i = 0; i < childList.getLength(); i++) {
				Node child = childList.item(i);
				if(child instanceof Element){
					Element elem = (Element)child;
					if(elem.getNodeName().equals("component")){
						processComponentElement(elem);
					}
				}
			}
			
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    protected void processComponentElement(Element element)
    throws Exception
	{
    		NodeList children = element.getChildNodes();
    		String componentClass = null;
    		String componentType=null;
    		int childrenCount = children.getLength();
		for (int i = 0; i < childrenCount; i++) {
			Node child = children.item(i);
			if(child instanceof Element){
				Element elem = (Element)child;
				if(elem.getNodeName().equals("component-class")){
					componentClass = getNodeTextValue(elem);
				}
				else	 if(elem.getNodeName().equals("component-type")){
					componentType = getNodeTextValue(elem);
				}
			}
		}
		if(componentClass!=null){
			ComponentInfo info = registry.getComponentByClassName(componentClass);
			if(info!=null){
				processProperties(element, info);
			}
		}
	}

    private String getNodeTextValue(Node node){
		String value = node.getNodeValue();
		if(value!=null){
			return value;
		}
		else{
			NodeList values = node.getChildNodes();
			Node child0 = values.item(0);
			if(child0!=null){
				value = child0.getNodeValue();
			}
		}
		return value;
    }
    
	/**
	 * <p>
	 * TODO tryggvil describe method processProperties
	 * </p>
	 * @param element
	 * @param info
	 */
	private void processProperties(Element componentElement, ComponentInfo info) {
		NodeList propertiesList = componentElement.getElementsByTagName("property");
		for (int i = 0; i < propertiesList.getLength(); i++) {
			Node nProperty = propertiesList.item(i);
			String propertyName=null;
			String propertyClass=null;
			String displayName=null;
			String description=null;
			String icon=null;
			String suggestedValue = null;
			if(nProperty instanceof Element){
				Element property = (Element)nProperty;
				NodeList lPropertyAttributes = property.getChildNodes();
				for (int j = 0; j < lPropertyAttributes.getLength(); j++) {
					Node nPropertyAttr = lPropertyAttributes.item(j);
					if(nPropertyAttr instanceof Element){
						Element elem = (Element)nPropertyAttr;
						if(elem.getNodeName().equals("property-name")){
							propertyName = getNodeTextValue(elem);
						}
						else	 if(elem.getNodeName().equals("property-class")){
							propertyClass =  getNodeTextValue(elem);
						}
						else	 if(elem.getNodeName().equals("display-name")){
							displayName =  getNodeTextValue(elem);
						}
						else	 if(elem.getNodeName().equals("description")){
							description =  getNodeTextValue(elem);
						}
						else	 if(elem.getNodeName().equals("icon")){
							icon =  getNodeTextValue(elem);
						}
						else	 if(elem.getNodeName().equals("suggested-value")){
							suggestedValue = getNodeTextValue(elem);
						}
					}
				}
				DefaultComponentProperty prop = new DefaultComponentProperty(info);
				prop.setName(propertyName);
				prop.setClassName(propertyClass);
				prop.setDisplayName(displayName);
				prop.setDescription(description);
				prop.setIcon(icon);
				prop.setSuggestedValue(suggestedValue);
				
				info.getProperties().add(prop);
				
			}
		}
	}

    
}
