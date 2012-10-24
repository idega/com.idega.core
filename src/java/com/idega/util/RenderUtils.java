/*
 * $Id: RenderUtils.java,v 1.9 2009/01/12 05:52:57 valdas Exp $
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
import javax.faces.context.ResponseWriter;


/**
 * Utility class for rendering logic in JSF.
 *
 *  Last modified: $Date: 2009/01/12 05:52:57 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.9 $
 */
public class RenderUtils {

	public static final String DIV_TAG="div";
	public static final String STYLE_CLASS_ATTRIBUTE="class";
	public static final String ID_ATTRIBUTE="id";


	/**
	 * Renders a child component for the current component. This operation is handy when implementing
	 * renderes that perform child rendering themselves (eg. a layout renderer/grid renderer/ etc..).
	 * Passes on any IOExceptions thrown by the child/child renderer.
	 *
	 * @param context the current FacesContext
	 * @param child which child to render
	 */
	public static void renderChild(FacesContext context, UIComponent child) throws IOException {
		if (context == null || child == null)
			return;

		if (!child.isRendered())
			return;	//	No need to render

		CoreUtil.doEnsureScopeIsSet(context);

		child.encodeBegin(context);
		if (child.getRendersChildren()) {
			child.encodeChildren(context);
		} else {
			//	Special case for forms:
			Collection<UIComponent> fChildren = child.getChildren();
			for (Iterator<UIComponent> iter = fChildren.iterator(); iter.hasNext();) {
				renderChild(context, iter.next());
			}
		}
		child.encodeEnd(context);
	}

	/**
	 * Render the specified facet.
	 */
	public static void renderFacet(FacesContext context, UIComponent component, String facetName) throws IOException {
		UIComponent facet = (component.getFacets().get(facetName));
		if (facet != null) {
			facet.encodeBegin(context);
			if(facet.getRendersChildren()){
				facet.encodeChildren(context);
			}
			facet.encodeEnd(context);
		}
	}

	public static void renderDividerBegin(ResponseWriter responseWriter) throws IOException{
		renderDividerBegin(responseWriter,null,null);
	}

	public static void renderDividerBegin(ResponseWriter responseWriter,String styleClassName) throws IOException{
		renderDividerBegin(responseWriter,styleClassName,null);
	}

	public static void renderDividerBegin(ResponseWriter responseWriter,String styleClassName,String id) throws IOException{
		responseWriter.startElement(DIV_TAG,null);
		if(styleClassName!=null){
			responseWriter.writeAttribute(STYLE_CLASS_ATTRIBUTE,styleClassName, null);
		}
		if(id!=null){
			responseWriter.writeAttribute(ID_ATTRIBUTE,id,null);
		}
	}

	public static void renderDividerEnd(ResponseWriter responseWriter,String className,String id) throws IOException{
		responseWriter.endElement(DIV_TAG);
	}

}
