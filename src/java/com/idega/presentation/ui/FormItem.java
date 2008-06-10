package com.idega.presentation.ui;

import com.idega.presentation.Layer;

/**
 * <p>
 * Convenient layer object to display an accessible form imput component
 * with an associated "label" contained together inside a Layer/Div.<br/>
 * This Div has by default the styleClass "formItem"
 * </p>
 *  Last modified: $Date: 2008/06/10 19:46:05 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class FormItem extends Layer {

	public static final String DEFAULT_STYLE_CLASS="formItem";
	
	public FormItem() {
		setStyleClass(DEFAULT_STYLE_CLASS);
	}

	public FormItem(String labelText,InterfaceObject inputComponent) {
		setStyleClass(DEFAULT_STYLE_CLASS);
		addLabel(labelText,inputComponent);
		add(inputComponent);
	}
	
	public void addLabel(String labelText,InterfaceObject inputComponent){
		Label label = new Label(labelText,inputComponent);
		add(label);
	}
}
