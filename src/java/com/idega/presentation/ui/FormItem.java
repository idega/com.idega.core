package com.idega.presentation.ui;

import com.idega.presentation.Layer;

/**
 * <p>
 * Convenient layer object to display an accessible form imput component
 * with an associated "label" contained together inside a Layer/Div.<br/>
 * This Div has by default the styleClass "formItem"
 * </p>
 *  Last modified: $Date: 2007/06/27 11:06:58 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class FormItem extends Layer {

	public static final String DEFAULT_STYLE_CLASS="formItem";
	
	public FormItem() {
		setStyleClass(DEFAULT_STYLE_CLASS);
	}

	public FormItem(String labelText,InterfaceObject inputComponent) {
		addLabel(labelText,inputComponent);
		add(inputComponent);
	}
	
	public void addLabel(String labelText,InterfaceObject inputComponent){
		Label label = new Label(labelText,inputComponent);
		add(label);
	}
}
