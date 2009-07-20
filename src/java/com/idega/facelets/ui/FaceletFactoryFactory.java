package com.idega.facelets.ui;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.ResourceResolver;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2007/11/22 16:41:39 $ by $Author: civilis $
 *
 */
public class FaceletFactoryFactory {
	
	private Compiler compiler;
	private ResourceResolver resourceResolver;
	private FaceletFactory faceletFactory;
	
	/**
	 * Method should be called only, when current FacesContext is available
	 * @return FaceletFactory
	 */
	public synchronized FaceletFactory createFaceletFactory() {

		if(faceletFactory == null) {
			
	        long refreshPeriod = FaceletViewHandler.DEFAULT_REFRESH_PERIOD;
	        faceletFactory = new DefaultFaceletFactory(getCompiler(), getResourceResolver(), refreshPeriod);
		}
		
		return faceletFactory;
    }

	public Compiler getCompiler() {
		return compiler;
	}

	public void setCompiler(Compiler compiler) {
		this.compiler = compiler;
	}

	public ResourceResolver getResourceResolver() {
		return resourceResolver;
	}

	public void setResourceResolver(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}
}