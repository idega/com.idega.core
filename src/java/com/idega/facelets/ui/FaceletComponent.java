package com.idega.facelets.ui;

import java.io.IOException;

import javax.faces.context.FacesContext;

import com.idega.presentation.IWBaseComponent;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.CoreConstants;
import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/02/14 21:29:08 $ by $Author: civilis $
 *
 */
public class FaceletComponent extends IWBaseComponent {
	
	private String faceletURI;
	
	private static final String faceletFactoryFactoryBeanId = "faceletFactoryFactory";
	public static final String COMPONENT_TYPE = "FaceletComponent";
	
	
	public FaceletComponent() { }
	
	public FaceletComponent(String faceletURI) {
		setFaceletURI(faceletURI);
	}
	
	@Override
	protected void initializeComponent(FacesContext context) {

		super.initializeComponent(context);
		
		String resourceURI = getFaceletURI();
		
		if(resourceURI == null || CoreConstants.EMPTY.equals(resourceURI))
			return;
		
		IWBundleResourceFilter.checkCopyOfResourceToWebapp(context, resourceURI);
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);
		
		try {
			String resourceURI = getFaceletURI();
			
			if(resourceURI == null || CoreConstants.EMPTY.equals(resourceURI))
				return;

			FaceletFactory faceletFactory = ((FaceletFactoryFactory)getBeanInstance(faceletFactoryFactoryBeanId)).createFaceletFactory();
			
			Facelet facelet;
			
			synchronized (faceletFactory) {
//			TODO: perhaps facelet factory is thread safe?
				facelet = faceletFactory.getFacelet(resourceURI);
			}
			
			facelet.apply(context, this);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object saveState(FacesContext ctx) {
		
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = faceletURI;
		return values;
	}
	
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		faceletURI = (String) values[1];
	}

	public String getFaceletURI() {
		return faceletURI;
	}

	public void setFaceletURI(String faceletURI) {
		
		if(this.faceletURI != null)
			throw new UnsupportedOperationException("Facelet URI already set. Create new component. URI: "+this.faceletURI);
		
		this.faceletURI = faceletURI;
	}
}