/*
 * Created on 18.5.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces.smile;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.presentation.IWContext;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BuilderPageViewHandler extends CbpViewHandler {
	/**
	 * 
	 */
	public BuilderPageViewHandler() {
		super();
	}
	/**
	 * @param parentViewHandler
	 */
	public BuilderPageViewHandler(ViewHandler parentViewHandler) {
		this.setParentViewHandler(parentViewHandler);
	}
	
	

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext ctx, String viewId) {


		//UIComponent component = (UIComponent) Class.forName(realClassName).newInstance();
		//Page smilePage getSmilePagesWrapper(obj);
		UIViewRoot ret = new UIViewRoot();
		ret.setViewId(viewId);	

		try {
			UIComponent page = getPage();
			if(page == null) { 
				// JSP page....
			} else {
				if(page instanceof Page) {
					Page sPage = (Page) page;
					sPage.init(ctx,ret);
				} else {
					Page sPage = new PageWrapper(page);
					sPage.init(ctx,ret);
				}
			}
		} catch(Throwable t) {
			//throw new SmileException("Descriptor Class for '" + viewId + "' threw an exception during initialize() !",t);
			throw new RuntimeException("Descriptor Class for '" + viewId + "' threw an exception during initialize() !",t);
		}

		//set the locale
		ret.setLocale(calculateLocale(ctx));

		//set the view on the session
		//ctx.getExternalContext().getSessionMap().put(net.sourceforge.smile.application.CbpStateManagerImpl.SESSION_KEY_CURRENT_VIEW,ret);
		
		return ret;
	}
	
	protected UIComponent getPage() throws RemoteException{
		IWContext iwc = IWContext.getInstance();
	  	BuilderLogic blogic = BuilderLogic.getInstance();
	  	BuilderService bs = BuilderServiceFactory.getBuilderService(iwc);
	    

	    boolean builderview = false;
	    if (iwc.isParameterSet("view")) {
	      if(blogic.isBuilderApplicationRunning(iwc)){
	        String view = iwc.getParameter("view");
	        if(view.equals("builder"))
	          builderview=true;
	      }
	    }

	    int i_page_id = bs.getCurrentPageId(iwc);
	    return(blogic.getPage(i_page_id,builderview,iwc));
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	public void renderView(FacesContext ctx, UIViewRoot viewId)
			throws IOException, FacesException {
		super.renderView(ctx, viewId);
	}	
	
}
