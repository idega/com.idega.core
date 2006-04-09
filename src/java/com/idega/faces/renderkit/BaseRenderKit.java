/*
 * Created on 3.8.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.renderkit;

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import org.apache.myfaces.renderkit.html.HtmlRenderKitImpl;


/**
 * This is a simple base class for new RenderKits
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version 1.0
 */
public class BaseRenderKit extends RenderKit {

	private static String DOT=".";
	
	//This class delegates all method calls to the real implementation from MyFaces
	private RenderKit backingRenderKit = new HtmlRenderKitImpl();
	
	public BaseRenderKit(RenderKit kit){
		this.backingRenderKit=kit;
	}
	
	private String key(String family,String rendererType){
		return family+DOT+rendererType;
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public void addRenderer(String arg0, String arg1, Renderer arg2) {
		//impl.addRenderer(arg0, arg1, arg2);
		getRendererMap().put(key(arg0,arg1),arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public ResponseWriter createResponseWriter(Writer arg0, String arg1, String arg2) {
		return this.backingRenderKit.createResponseWriter(arg0, arg1, arg2);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	//public boolean equals(Object arg0) {
	//	return backingRenderKit.equals(arg0);
	//}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public Renderer getRenderer(String family,String rendererType) {
		//Try first to get the renderer from the backingRenderKit
		Renderer renderer = this.backingRenderKit.getRenderer(family,rendererType);
		if(renderer==null){
			return (Renderer)getRendererMap().get(key(family,rendererType));
		}
		else{
			return renderer;
		}
	}
	/**
	 * @return
	 */
	public ResponseStateManager getResponseStateManager() {
		return this.backingRenderKit.getResponseStateManager();
	}

	
	private Map renderers;
	private Map getRendererMap(){
		if(this.renderers==null){
			this.renderers = new HashMap();
		}
		return this.renderers;
	}

	/* (non-Javadoc)
	 * @see javax.faces.render.RenderKit#createResponseStream(java.io.OutputStream)
	 */
	public ResponseStream createResponseStream(OutputStream out) {
		return this.backingRenderKit.createResponseStream(out);
	}
	
	
}
