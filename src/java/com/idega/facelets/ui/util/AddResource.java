package com.idega.facelets.ui.util;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2009/03/08 13:32:22 $ by $Author: valdas $
 *
 */
public class AddResource extends IWBaseComponent {

	public static final String resourcePositionHeader = "header";
	public static final String javascriptFileExt = ".js";
	public static final String cssFileExt = ".css";
	
	private static final String resourcePathProperty= "resourcePath";
	private String resourcePath;
	private String resourceLocation = resourcePositionHeader;
	
	public String getResourcePath() {
		return resourcePath;
	}
	
	public String getResourcePath(FacesContext context) {
		
		String resourcePath = getResourcePath();
		
		if(resourcePath == null) {
			ValueExpression ve = getValueExpression(resourcePathProperty);
			resourcePath = ve == null ? null : ve.getValue(context.getELContext()).toString();
		}

		setResourcePath(resourcePath);
		
		return resourcePath;
	}
	
	public void setResourcePath(String resourcePath) {
		
		if(resourcePath != null)
			this.resourcePath = resourcePath;
	}

	public String getResourceLocation() {
		return resourceLocation;
	}

	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);
		
		String resourcePath = getResourcePath(context);
		
		if(resourcePath != null && !CoreConstants.EMPTY.equals(resourcePath)) {
			
			String resourceLocation = getResourceLocation();
			
			if(resourceLocation == null || CoreConstants.EMPTY.equals(resourceLocation))
				resourceLocation = resourcePositionHeader;
			
			IWContext iwc = IWContext.getIWContext(context);
			if(resourcePath.endsWith(javascriptFileExt))
				PresentationUtil.addJavaScriptSourceLineToHeader(iwc, resourcePath);
			else if(resourcePath.endsWith(cssFileExt))
				PresentationUtil.addStyleSheetToHeader(iwc, resourcePath);
		}
	}
	
	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = this.resourcePath;
		return values;
	}
	
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.resourcePath = ((String) values[1]);
	}
}