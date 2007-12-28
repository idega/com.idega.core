/*
 * $Id: BuilderPageFacetMap.java,v 1.2 2007/12/28 13:23:04 valdas Exp $
 * Created on 16.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.faces.component.UIComponent;

/**
 * <p>
 * This is special implementation of the standard JSF Facet Map to be used to implement the <code>region</code> tag inside Builder pages.<br/>
 * This class is used to override the getFacets() method in BuilderPage and HtmlPage.
 * <p>
 * Last modified: $Date: 2007/12/28 13:23:04 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class BuilderPageFacetMap extends PresentationObjectComponentFacetMap {

	private static final long serialVersionUID = 3225846483768163503L;
	
	public static final String PREFIX="builderRegion";
	
	/**
	 * @param component
	 */
	public BuilderPageFacetMap(UIComponent component) {
		super(component);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		super.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return super.clone();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return super.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return super.containsValue(value);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, UIComponent>> entrySet() {
		return super.entrySet();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public UIComponent get(Object key) {
		String facetKey=(String)key;
		
		if(facetKey.startsWith(PREFIX)){
			String regionKey = facetKey.substring(PREFIX.length(),facetKey.length());
			UIComponent region = findRegionComponent(regionKey);
			if(region!=null){
				if(region.getChildren().size()>0){
					return (UIComponent) region.getChildren().get(0);
				}
			}
			/*if(doesComponentContainChild(region,component)){
				//
			}else{
				region.getChildren().add(value);
			}*/
			return null;
		}
		else{
			return super.get(key);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectComponentFacetMap#getComponent()
	 */
	protected UIComponent getComponent() {
		return super.getComponent();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return super.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet() {
		return super.keySet();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public UIComponent put(String key, UIComponent value) {
		//return super.put(key, value);
		String facetKey = (String)key;
		if(facetKey.startsWith(PREFIX)){
			String regionKey = facetKey.substring(PREFIX.length(),facetKey.length());
			UIComponent region = findRegionComponent(regionKey);
			UIComponent component = value;
			if(region!=null){
				if(doesComponentContainChild(region, component)){
					//
				}else{
					region.getChildren().add(value);
				}
			}
			
			return null;
		}
		else{
			return super.put(key,value);
		}
	}
	
	/**
	 * Returns true if component already contains child
	 * @param component
	 * @param child
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean doesComponentContainChild(UIComponent component, UIComponent child){
		if (component != null) {
			for (Iterator<UIComponent> iter = component.getChildren().iterator(); iter.hasNext();) {
				UIComponent element = iter.next();
				if(element.getId().equals(child.getId())){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * @param key
	 * @return
	 */
	protected UIComponent findRegionComponent(String key) {
		Page page = (Page) this.getComponent();

		UIComponent component = page;
			
		component = findRegionComponentRecursive(component,key);
		
		
		return component;
	}
	
	@SuppressWarnings("unchecked")
	protected UIComponent findRegionComponentRecursive(UIComponent component, String key) {
		for (Iterator<UIComponent> iter = component.getFacetsAndChildren(); iter.hasNext();) {
			UIComponent child = iter.next();
			if(child instanceof PresentationObjectContainer){
				PresentationObjectContainer poc = (PresentationObjectContainer)child;
				String label = poc.getLabel();
				if(key.equals(label)){
					return poc;
				}
			}
			UIComponent obj = findRegionComponentRecursive(child,key);
			if(obj!=null){
				return obj;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends UIComponent> t) {
		super.putAll(t);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public UIComponent remove(Object key) {
		String facetKey = (String)key;
		if(facetKey.startsWith(PREFIX)){
			String regionKey = facetKey.substring(PREFIX.length(),facetKey.length());
			UIComponent region = findRegionComponent(regionKey);
			if(region!=null){
				region.getChildren().clear();
				region.getFacets().clear();
			}
			//region.getChildren().add(value);
			return null;
		}
		else{
			return super.remove(key);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectComponentFacetMap#setComponent(javax.faces.component.UIComponent)
	 */
	protected void setComponent(UIComponent _component) {
		super.setComponent(_component);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		return super.size();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<UIComponent> values() {
		return super.values();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
