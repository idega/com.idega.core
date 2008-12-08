package com.idega.presentation.text;

import javax.faces.component.UIComponent;

import com.idega.presentation.IWContext;

public interface InlineEditableComponent {

	public abstract void makeInlineEditable(IWContext iwc, UIComponent component);
	
}
