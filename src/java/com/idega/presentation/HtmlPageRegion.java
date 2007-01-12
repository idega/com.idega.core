/*
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWUserContext;


/**
 * An instance of this class is a PresentationObject mapping of a region inside an HtmlPage based template.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 *
 */
public class HtmlPageRegion extends PresentationObjectContainer {
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#getLabel()
	 */
	public String getLabel() {
		// TODO Auto-generated method stub
		return super.getLabel();
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		// TODO Auto-generated method stub
		super.setLabel(label);
	}
	private String regionId;
	
	public HtmlPageRegion(){
		super();
	}
	
	/**
	 * This is overridden here to not get the IBObjectControl around this component in the Builder.
	 */
	public boolean getUseBuilderObjectControl(){
		return false;
	}
	
	/**
	 * @return Returns the regionId.
	 */
	public String getRegionId() {
		return this.regionId;
	}
	/**
	 * @param regionId The regionId to set.
	 */
	public void setRegionId(String regionId) {
		this.regionId = regionId;
		//This is done to handle the Builder Region handling
		setLabel(regionId);
	}
	/**
	 * Override this method to bypass the permission logic for HtmlPageRegions
	 * (because they dont have a direct instance id)
	 */
	public Object clonePermissionChecked(IWUserContext iwc,
			boolean askForPermission) {
		return clone(iwc, askForPermission);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#_main(com.idega.presentation.IWContext)
	 */
	public void _main(IWContext iwc) throws Exception {
		super._main(iwc);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
	}
	
	public boolean isTransient(){
		return false;
	}
	
	/**
	 * @see javax.faces.component.UIPanel#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = this.regionId;
		return values;
	}
	
	/**
	 * @see javax.faces.component.UIPanel#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(ctx, values[0]);
		this.regionId = (String)values[1];
	}
	
}
