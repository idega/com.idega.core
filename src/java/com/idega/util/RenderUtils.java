/*
 * $Id: RenderUtils.java,v 1.3 2004/11/14 23:31:31 tryggvil Exp $
 * Created on 25.8.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 * 
 *  Last modified: $Date: 2004/11/14 23:31:31 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class RenderUtils {
	
	/**
	 * Renders a child component for the current component. This operation is handy when implementing
	 * renderes that perform child rendering themselves (eg. a layout renderer/grid renderer/ etc..).
	 * Passes on any IOExceptions thrown by the child/child renderer.
	 * 
	 * @param context the current FacesContext
	 * @param child which child to render
	 */
	public static void renderChild(FacesContext context, UIComponent child) throws IOException {
		//child.encodeBegin(context);
		//child.encodeChildren(context);
		//child.encodeEnd(context);
		if(child!=null){
			if(child.isRendered()){
				child.encodeBegin(context);
				if(child.getRendersChildren()){
					child.encodeChildren(context);
				}
				else
				//if(child instanceof UIForm)
				{
					//Special case for forms:
					Collection fChildren = child.getChildren();
					for (Iterator iter = fChildren.iterator(); iter.hasNext();) {
						UIComponent fChild = (UIComponent) iter.next();
						renderChild(context,fChild);
					}
				}
				child.encodeEnd(context);
			}
		}
	
	
	}

	/**
	 * Render the specified facet.
	 */
	public static void renderFacet(FacesContext context, UIComponent component, String facetName) throws IOException {
		UIComponent facet = (UIComponent) (component.getFacets().get(facetName));
		if (facet != null) {
			facet.encodeBegin(context);
			facet.encodeChildren(context);
			facet.encodeEnd(context);
		}
	}	
	
}
