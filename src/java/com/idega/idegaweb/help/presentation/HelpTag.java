/*
 * $Id: HelpTag.java,v 1.1.2.1 2007/01/12 19:31:57 idegaweb Exp $
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
 *  Last modified: $Date: 2007/01/12 19:31:57 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1.2.1 $
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
			if(this.helpTextBundle!=null){
				if(isValueReference(this.helpTextBundle)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(this.helpTextBundle);
					help.setValueBinding("helpTextBundle", vb);
				} else {
					help.setHelpTextBundle(this.helpTextBundle);
				}
			}
			
			if(this.helpTextKey!=null){
				if(isValueReference(this.helpTextKey)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(this.helpTextKey);
					help.setValueBinding("helpTextKey", vb);
				} else {
					help.setHelpTextKey(this.helpTextKey);
				}
			}
			
			if(this.image!=null){
				if(isValueReference(this.image)){
					ValueBinding vb = getFacesContext().getApplication().createValueBinding(this.image);
					help.setValueBinding("image", vb);
				} else {
					help.setImage(this.image);
				}
			}
		}
	}
	
	public String getHelpTextBundle() {
		return this.helpTextBundle;
	}
	public void setHelpTextBundle(String helpTextBundle) {
		this.helpTextBundle = helpTextBundle;
	}
	public String getHelpTextKey() {
		return this.helpTextKey;
	}
	public void setHelpTextKey(String helpTextKey) {
		this.helpTextKey = helpTextKey;
	}
	public String getImage() {
		return this.image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
