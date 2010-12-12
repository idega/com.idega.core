package com.idega.presentation;

import java.util.logging.Level;

import javax.el.ValueExpression;
import javax.faces.webapp.UIComponentTag;

@SuppressWarnings("deprecation")
public abstract class ComponentTag extends UIComponentTag {

	private Object id;

	protected String getValue(Object value) {
		String realValue = getTypedValue(value);

		if (realValue == null && value != null) {
			log.warning("Unable to convert " + value + " to String, using toString method");
			return value.toString();
		}

		return realValue;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getTypedValue(Object value) {
		if (value == null) {
			return null;
		}

		try {
			if (value instanceof ValueExpression) {
				Object realValue = ((ValueExpression) value).getValue(getELContext());
				return (T) realValue;
			} else {
				return (T) value;
			}
		} catch (Throwable e) {
			log.log(Level.SEVERE, "Error converting " + value + " (" + value.getClass() + ") to requested type!", e);
		}

		return null;
	}

	@Override
	public String getId() {
		if (id == null) {
			return super.getId();
		}

		return getValue(id);
	}

	public void setId(Object id) {
		this.id = id;
	}

}