/*
 * Created on 16.6.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces.componentbased;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Page {

	public void init(FacesContext ctx, UIComponent root);
}
