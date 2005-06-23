/*
 * $Id: HelpButtonTag.java,v 1.1 2005/06/23 18:16:20 gummi Exp $
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
 *  Last modified: $Date: 2005/06/23 18:16:20 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
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
			if(text!=null){
				if(isValueReference(text)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(text);
					button.setValueBinding("text", vb);
				} else {
					button.setText(text);
				}
			}
			
			if(headline!=null){
				if(isValueReference(headline)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(headline);
					button.setValueBinding("headline", vb);
				} else {
					button.setHeadline(headline);
				}
			}
			
			if(imageUrl!=null){
				if(isValueReference(imageUrl)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(imageUrl);
					button.setValueBinding("imageUrl", vb);
				} else {
					button.setImageUrl(imageUrl);
				}
			}
		}
	}
	
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
