package com.idega.test;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class MockViewHandler extends ViewHandler {

	@Override
	public Locale calculateLocale(FacesContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String calculateRenderKitId(FacesContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UIViewRoot createView(FacesContext arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActionURL(FacesContext arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceURL(FacesContext arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void renderView(FacesContext arg0, UIViewRoot arg1)
			throws IOException, FacesException {
		// TODO Auto-generated method stub

	}

	@Override
	public UIViewRoot restoreView(FacesContext arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeState(FacesContext arg0) throws IOException {
		// TODO Auto-generated method stub

	}

}
