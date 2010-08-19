package com.idega.presentation;

import javax.el.ValueExpression;
import javax.faces.webapp.UIComponentTag;

@SuppressWarnings("deprecation")
public abstract class ComponentTag extends UIComponentTag {

	protected String getValue(Object value) {
		if (value instanceof ValueExpression) {
			return (String) ((ValueExpression) value).getValue(getELContext());
		} else if (value instanceof String) {
			return (String) value;
		} else if (value != null) {
			return value.toString();
		}
		return null;
	}

}