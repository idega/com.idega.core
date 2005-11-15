/*
 * $Id: CbpViewHandler.java,v 1.1 2005/11/15 23:57:59 tryggvil Exp $
 * Created on 21.6.2004 by  tryggvil
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.componentbased;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Locale;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.apache.myfaces.application.MyfacesStateManager;
import org.apache.myfaces.application.jsp.JspViewHandlerImpl;
import org.apache.myfaces.renderkit.html.HtmlLinkRendererBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.idega.presentation.IWContext;
import com.idega.repository.data.RefactorClassRegistry;

/**
 * <p>
 * This is a special implementation of a ViewHandler for "Component-based" Pages.<br/>
 * This ViewHandler is used for example when rendering out Builder pages of type IBXML and HTML.
 * <br>
 * </p>
 * Copyright (C) idega software 2004-2005<br>
 * 
 * Last modified: $Date: 2005/11/15 23:57:59 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class CbpViewHandler extends ViewHandler {

	private static Log log = LogFactory.getLog(CbpViewHandler.class);
	
//	private StateManager stateManager;
	
	/**
	 * Default constructor
	 */
	public CbpViewHandler() {
//		stateManager = new CbpStateManagerImpl();
	}
	
	/**
	 * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	public void renderView(FacesContext ctx, UIViewRoot viewRoot) throws IOException, FacesException {
		// Apparently not all versions of tomcat have the same
		// default content-type.
		// So we'll set it explicitly.
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		response.setContentType("text/html");
		
		// make sure to set the responsewriter
		initializeResponseWriter(ctx);		
		
		if(viewRoot == null) {
			throw new RuntimeException("CbpViewHandler: No component tree is available !");
		}
		String renderkitId = viewRoot.getRenderKitId();
		if (renderkitId == null) {
			renderkitId = calculateRenderKitId(ctx);
		}
		viewRoot.setRenderKitId(renderkitId);

		ResponseWriter out = ctx.getResponseWriter();
		//try {

			out.startDocument();
			renderComponent(ctx.getViewRoot(),ctx);
			out.endDocument();
			

			try {
				writeOutResponseAndClientState(ctx);

			}
			catch (JspException e) {
				//e.printStackTrace();
				throw new RuntimeException(e);
			}

		//} catch (RuntimeException e) {
			//throw new SmileRuntimeException(e.getMessage(),e);
		//}
	}

	
	
	public void writeOutResponseAndClientState(FacesContext facesContext) throws JspException
	    {
	        if (log.isTraceEnabled()) log.trace("entering ViewTag.doAfterBody");
	        try
	        {
	            //BodyContent bodyContent = getBodyContent();
	            //if (bodyContent != null)
	            //{
	                //FacesContext facesContext = FacesContext.getCurrentInstance();
	                StateManager stateManager = facesContext.getApplication().getStateManager();
	                StateManager.SerializedView serializedView = null;
	                try{
	                	serializedView = stateManager.saveSerializedView(facesContext);
	                }
	                catch(ArrayIndexOutOfBoundsException ar){
	                		System.err.println("StateManager.saveSerializedView caused ArrayIndexOutOfBoundsException:");
	                		ar.printStackTrace();
	                		throw new JspException(ar);
	                }
	                if (serializedView != null)
	                {
	                    //until now we have written to a buffer
	                    ResponseWriter bufferWriter = facesContext.getResponseWriter();
	                    bufferWriter.flush();
	                    //now we switch to real output
	                    ResponseWriter realWriter = bufferWriter.cloneWithWriter(getRealResponseWriter(facesContext));
	                    facesContext.setResponseWriter(realWriter);

	                    //String bodyStr = bodyContent.getString();
	                    String bodyStr = getOutputAsString(bufferWriter);
	                    int form_marker = bodyStr.indexOf(JspViewHandlerImpl.FORM_STATE_MARKER);
	                    int url_marker = bodyStr.indexOf(HtmlLinkRendererBase.URL_STATE_MARKER);
	                    int lastMarkerEnd = 0;
	                    while (form_marker != -1 || url_marker != -1)
	                    {
	                        if (url_marker == -1 || (form_marker != -1 && form_marker < url_marker))
	                        {
	                            //replace form_marker
	                            realWriter.write(bodyStr, lastMarkerEnd, form_marker - lastMarkerEnd);
	                            stateManager.writeState(facesContext, serializedView);
	                            lastMarkerEnd = form_marker + JspViewHandlerImpl.FORM_STATE_MARKER_LEN;
	                            form_marker = bodyStr.indexOf(JspViewHandlerImpl.FORM_STATE_MARKER, lastMarkerEnd);
	                        }
	                        else
	                        {
	                            //replace url_marker
	                            realWriter.write(bodyStr, lastMarkerEnd, url_marker - lastMarkerEnd);
	                            if (stateManager instanceof MyfacesStateManager)
	                            {
	                                ((MyfacesStateManager)stateManager).writeStateAsUrlParams(facesContext,
	                                                                                          serializedView);
	                            }
	                            else
	                            {
	                                log.error("Current StateManager is no MyfacesStateManager and does not support saving state in url parameters.");
	                            }
	                            lastMarkerEnd = url_marker + HtmlLinkRendererBase.URL_STATE_MARKER_LEN;
	                            url_marker = bodyStr.indexOf(HtmlLinkRendererBase.URL_STATE_MARKER, lastMarkerEnd);
	                        }
	                    }
	                    realWriter.write(bodyStr, lastMarkerEnd, bodyStr.length() - lastMarkerEnd);
	                //}
	               // else
	                //{
	                //    bodyContent.writeOut(getPreviousOut());
	                //}
	                    
	    				//Now we do endDocument on the real responseWriter.
	                 realWriter.endDocument();
	    				realWriter.flush();
	            }
	        }
	        catch (Exception e)
	        {
	            log.error("Error writing serialized page", e);
	            System.err.println("CbpViewHandler.writeOutResponseAndClientState(): "+e.getClass().getName()+" : "+e.getMessage());
	            //e.printStackTrace();
	            //throw new JspException(e);
                
                try {
                		ResponseWriter bufferWriter = facesContext.getResponseWriter();
					bufferWriter.flush();
	                //now we switch to real output
	                ResponseWriter realWriter = bufferWriter.cloneWithWriter(getRealResponseWriter(facesContext));
	                facesContext.setResponseWriter(realWriter);
	                String bodyStr = getOutputAsString(bufferWriter);
	                realWriter.write(bodyStr);
	                
				}
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            
	        }
	        if (log.isTraceEnabled()) log.trace("leaving ViewTag.doAfterBody");
	        //return super.doAfterBody();
	    }

	
	/**
	 * <p>
	 * Get the content that was buffered to the "fake" writer.
	 * </p>
	 * @param bufferWriter
	 * @return
	 */
	private String getOutputAsString(ResponseWriter bufferWriter) {
		HtmlStringBufferedResponseWriter responseWriter = (HtmlStringBufferedResponseWriter)bufferWriter;
		StringWriter writer = responseWriter.getStringWriter();
		//BlockCacheResponseWriter blockWriter = (BlockCacheResponseWriter)bufferWriter;
		return writer.getBuffer().toString();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getRealResponseWriter
	 * </p>
	 * @param facesContext
	 * @return
	 */
	private Writer getRealResponseWriter(FacesContext facesContext) {
		HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
		try {
			return response.getWriter();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//return null;
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot restoreView(FacesContext ctx, String viewId) {
//		if (getStateManager().isSavingStateInClient(ctx)) {
//			//get the state from the client
			return getStateManager().restoreView(ctx,viewId, calculateRenderKitId(ctx));
//		} 
//		UIViewRoot currentViewRoot = (UIViewRoot) ctx.getExternalContext().getSessionMap().get(SESSION_KEY_CURRENT_VIEW);
//		if (currentViewRoot == null) {
//			return null;
//		} else if (currentViewRoot.getViewId().equals(viewId)) {
//			return currentViewRoot;
//		}
//		return null;
	}

	/**
	 * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext ctx, String viewId) {

		UIViewRoot ret = new UIViewRoot();
		ret.setViewId(viewId);
		
		// TODO : Hack to allow unit tests to select empty views
		if(viewId.startsWith("unittesttree.")) {
			return ret;			
		}

		try {
			Class descriptorClazz = getDescriptorClassNameForViewId(viewId);
			if(descriptorClazz == null) { 
				// JSP page....
			} else {
				if(Page.class.isAssignableFrom(descriptorClazz)) {
					Page page = (Page) descriptorClazz.newInstance();
					page.init(ctx,ret);
				} else {
					throw new RuntimeException("CbpViewHandler: Descriptor Class for '" + viewId + "' must implement net.sourceforge.smile.context.Page !");
				}
			}
		} catch(IllegalAccessException e) {
			throw new RuntimeException("CbpViewHandler: Please make sure that the default constructor for descriptor class of <" + viewId + "> is public.",e);
		} catch(InstantiationException e) {
			throw new RuntimeException("CbpViewHandler: An exception was generated by the default constructor of the descriptor class of <" + viewId + ">.",e);
		} catch(Throwable t) {
			throw new RuntimeException("CbpViewHandler: Descriptor Class for '" + viewId + "' threw an exception during initialize() !",t);
		}

		//set the locale
		ret.setLocale(calculateLocale(ctx));

		//set the view on the session
		ctx.getExternalContext().getSessionMap().put(net.sourceforge.smile.application.CbpStateManagerImpl.SESSION_KEY_CURRENT_VIEW,ret);
		
		return ret;
	}

	/**
	 * @see javax.faces.application.ViewHandler#getStateManager()
	 */
	public StateManager getStateManager() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application application = facesContext.getApplication();
		return application.getStateManager();
	}

	/**
	 * @see javax.faces.application.ViewHandler#writeState(javax.faces.context.FacesContext)
	 */
	public void writeState(FacesContext ctx) throws IOException {
		StateManager.SerializedView serializedView = null;
		if (null != (serializedView = getStateManager().saveSerializedView(ctx))) {
			getStateManager().writeState(ctx,serializedView);
		}
	}

	/**
	 * @see javax.faces.application.ViewHandler#getViewIdPath(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getViewIdPath(FacesContext ctx, String viewId) {
		// TODO implement conversion
		if (viewId != null && viewId.startsWith("/")) {
			return viewId.substring(1);
		} else {
			return viewId;			
		}
	}

	/**
	 * @see javax.faces.application.ViewHandler#calculateLocale(javax.faces.context.FacesContext)
	 */
	public Locale calculateLocale(FacesContext ctx) {
		IWContext iwc = IWContext.getIWContext(ctx);
		return iwc.getCurrentLocale();
		
	}
	
	

	/**
	 * make sure a ResponseWriter instance is set on the component
	 */
	private void initializeResponseWriter(FacesContext ctx) throws FacesException {
		//check if running in httpservlet environment
		//boolean httpServletEnv = true;
		StringWriter bufferWriter = new StringWriter();
		if (!(ctx.getExternalContext().getRequest() instanceof HttpServletRequest)) {
			throw new RuntimeException("CbpViewHandler: idegaWeb currently does not support environments other than Http Servlet Environment.");
		}
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		//HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		String contextType = "text/html";
		String characterEncoding = request.getCharacterEncoding();
		//try {
			//This responsewriter is first constructed with a buffer that is later written out.
			ResponseWriter responseWriter = new HtmlStringBufferedResponseWriter(bufferWriter,contextType,characterEncoding);
//			ResponseWriter responseWriter = new ResponseWriterImplDecorated(response.getWriter(),contextType,characterEncoding);
			ctx.setResponseWriter(responseWriter);
		//} catch (IOException e) {
		//	throw new FacesException(e.getMessage(),e);
		//}		
	}
	
	/**
	 * Recursive operation to render a specific component in the view tree.
	 * 
	 * @param component
	 * @param context
	 */
	private void renderComponent(UIComponent component, FacesContext ctx) {
		try {
			component.encodeBegin(ctx);
			if(component.getRendersChildren()) {
				component.encodeChildren(ctx);
			} else {
				Iterator it;
				UIComponent currentChild;
				it = component.getChildren().iterator();
				while(it.hasNext()) {
					currentChild = (UIComponent) it.next();
					renderComponent(currentChild,ctx);
				}
			}		
			//if (component instanceof Screen) {
			//	writeState(ctx); 
			//}
			component.encodeEnd(ctx);
		} catch(IOException e) {
			log.error("Component <" + component.getId() + "> could not render ! Continuing rendering of view <" + ctx.getViewRoot().getViewId() + ">...");
		}
	}
	
	/**
	 * This function is responsible for finding the descripto class for a given
	 * viewId. The policy may change over time, like supporting multiple packages
	 * or mory flexible mapping of viewIds to descriptor classes,etc..
	 *  
	 * @param viewId
	 * @return the descriptor class for a given viewId, or null if no descriptor found.
	 */
	private Class getDescriptorClassNameForViewId(String viewId) {
		Class ret = null;
		String className;
		FacesContext ctx = FacesContext.getCurrentInstance();
				
		// TODO : We should implement a configurable scheme with more than one package.	
		if(viewId.endsWith(".jsf") || viewId.endsWith(".jsp")) {
			String shortClassName = viewId.substring(0,viewId.length()-4);
			if(shortClassName.startsWith("/")) {
				shortClassName = shortClassName.substring(1,shortClassName.length());
			}
			
			className = getDescriptorPackage(ctx) + shortClassName + getDescriptorPostfix(ctx);
			try {
				ret = RefactorClassRegistry.forName(className);
			} catch(ClassNotFoundException e) {
				ret = null;
			}
		}
		
		return ret;
	}

	/**
	 * Determines the package where the descriptor classes are located.
	 * @param context
	 * @return
	 */
	private String getDescriptorPackage(FacesContext context) {
		String ret = "";	// Default package.
		String temp;
		
		// Try to determine descriptor package...
		temp = context.getExternalContext().getInitParameter("net.sourceforge.smile.descriptor.package"); 
		if(temp != null) {
			ret = temp;
			if(!ret.endsWith(".")) {
				ret = ret + ".";
			}
		}
		
		return ret;
	}

	/**
	 * Determines the postfix for the descriptor class.
	 * @param context
	 * @return
	 */
	private String getDescriptorPostfix(FacesContext context) {
		String ret = "";	// Default.
		String temp;
		
		// Try to determine descriptor package...
		temp = context.getExternalContext().getInitParameter("net.sourceforge.smile.descriptor.postfix"); 
		if(temp != null) {
			ret = temp;
		}
		
		return ret;
	}

	/**
	 * @see javax.faces.application.ViewHandler#calculateRenderKitId(javax.faces.context.FacesContext)
	 */
	public String calculateRenderKitId(FacesContext ctx) {
		return RenderKitFactory.HTML_BASIC_RENDER_KIT;
	}

	/**
	 * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getActionURL(FacesContext ctx, String viewId) {
		// TODO Look into this:
		//return ctx.getExternalContext().encodeActionURL(viewId);
		// TODO implement conversion
		if (viewId != null && viewId.startsWith("/")) {
			return viewId.substring(1);
		} else {
			return viewId;			
		}
	}

	/**
	 * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getResourceURL(FacesContext ctx, String path) {
		// TODO Auto-generated method stub
		String resourceUrl = ctx.getExternalContext().encodeResourceURL(path);
		return resourceUrl;
	}
}
