/*
 * $Id: HtmlPageRegionFacetMap.java,v 1.1 2005/05/30 15:57:25 tryggvil Exp $
 * Created on 19.5.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.util.Iterator;
import javax.faces.component.UIComponent;


/**
 * <p>
 * A subclass of BuilderPageFacetMap specifically used by HtmlPage
 * </p>
 *  Last modified: $Date: 2005/05/30 15:57:25 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class HtmlPageRegionFacetMap extends BuilderPageFacetMap {

	/**
	 * @param component
	 */
	public HtmlPageRegionFacetMap(UIComponent component) {
		super(component);
	}
	
	/**
	 * @param key
	 * @return
	 */
	protected UIComponent findRegionComponent(String key) {
		/*Page page = (Page) this.getComponent();
		UIComponent component = page;
		component = findRegionComponentRecursive(component,key);
		return component;*/
		return super.findRegionComponent(key);
	}
	
	protected UIComponent findRegionComponentRecursive(UIComponent component, String key) {
		/*for (Iterator iter = component.getFacetsAndChildren(); iter.hasNext();) {
			UIComponent child = (UIComponent) iter.next();
			if(child instanceof HtmlPageRegion){
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
		return null;*/
		return super.findRegionComponentRecursive(component,key);
	}
}
