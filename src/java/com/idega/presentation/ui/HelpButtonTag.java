/*
 * $Id: HelpButtonTag.java,v 1.1.2.1 2007/01/12 19:32:04 idegaweb Exp $
 * Created on 20.6.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import org.apache.myfaces.taglib.UIComponentTagBase;



/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:04 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1.2.1 $
 */
public class HelpButtonTag extends UIComponentTagBase {

	private String text = null;
	private String headline = null;
	private String imageUrl = null;
	
	/**
	 * 
	 */
	public HelpButtonTag() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		return "HelpButton";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		return null;
	}
	

	protected void setProperties(UIComponent component) {      
		super.setProperties(component);
		if (component != null) {
			HelpButton button = ((HelpButton)component);
			if(this.text!=null){
				if(isValueReference(this.text)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(this.text);
					button.setValueBinding("text", vb);
				} else {
					button.setText(this.text);
				}
			}
			
			if(this.headline!=null){
				if(isValueReference(this.headline)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(this.headline);
					button.setValueBinding("headline", vb);
				} else {
					button.setHeadline(this.headline);
				}
			}
			
			if(this.imageUrl!=null){
				if(isValueReference(this.imageUrl)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(this.imageUrl);
					button.setValueBinding("imageUrl", vb);
				} else {
					button.setImageUrl(this.imageUrl);
				}
			}
		}
	}
	
	public String getHeadline() {
		return this.headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getImageUrl() {
		return this.imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
