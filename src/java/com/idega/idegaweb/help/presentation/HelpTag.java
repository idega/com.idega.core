/*
 * $Id: HelpTag.java,v 1.1 2005/06/23 18:16:20 gummi Exp $
 * Created on 23.6.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.help.presentation;

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
public class HelpTag extends UIComponentTagBase {

	private String helpTextBundle = null;
	private String helpTextKey = null;
	private String image = null;
	
	/**
	 * 
	 */
	public HelpTag() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		return "Help";
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
			Help help = ((Help)component);
			if(helpTextBundle!=null){
				if(isValueReference(helpTextBundle)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(helpTextBundle);
					help.setValueBinding("helpTextBundle", vb);
				} else {
					help.setHelpTextBundle(helpTextBundle);
				}
			}
			
			if(helpTextKey!=null){
				if(isValueReference(helpTextKey)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(helpTextKey);
					help.setValueBinding("helpTextKey", vb);
				} else {
					help.setHelpTextKey(helpTextKey);
				}
			}
			
			if(image!=null){
				if(isValueReference(image)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(image);
					help.setValueBinding("image", vb);
				} else {
					help.setImage(image);
				}
			}
		}
	}
	
	public String getHelpTextBundle() {
		return helpTextBundle;
	}
	public void setHelpTextBundle(String helpTextBundle) {
		this.helpTextBundle = helpTextBundle;
	}
	public String getHelpTextKey() {
		return helpTextKey;
	}
	public void setHelpTextKey(String helpTextKey) {
		this.helpTextKey = helpTextKey;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
