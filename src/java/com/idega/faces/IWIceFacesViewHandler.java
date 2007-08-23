package com.idega.faces;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.icesoft.faces.application.D2DViewHandlerDelegating;
import com.icesoft.faces.context.BridgeFacesContext;

public class IWIceFacesViewHandler extends D2DViewHandlerDelegating {

	public IWIceFacesViewHandler(ViewHandler viewHandler) {
		super(viewHandler);
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		if (context instanceof BridgeFacesContext) {
			BridgeFacesContext iceFacesContext = (BridgeFacesContext) context;
			try {
				iceFacesContext.createAndSetResponseWriter();
			} catch (IOException e) {
				throw new FacesException(e.getMessage(),e);
			}
		}
		super.renderView(context, viewToRender);
	}
	
}
