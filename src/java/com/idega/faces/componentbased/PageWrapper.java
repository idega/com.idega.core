/*
 * Created on 16.6.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces.componentbased;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;



public class PageWrapper implements Page{

	private UIComponent child;
	
	public PageWrapper(UIComponent child){
		if(child instanceof com.idega.presentation.Page){
			this.child=child;
		}
		else{
			this.child = new com.idega.presentation.Page();
			this.child.getChildren().add(child);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.cbp.Page#init(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
	 */
	public void init(FacesContext ctx, UIComponent root) {
		root.getChildren().add(this.child);
	}
	
	
	
}