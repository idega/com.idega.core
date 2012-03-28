package com.idega.builder.bean;

import java.io.Serializable;

import com.idega.util.CoreConstants;

public class AdvancedProperty implements Serializable {

	private static final long serialVersionUID = 8520116287821698275L;

	private boolean selected;
	private String id, value;

	public AdvancedProperty() {
		super();
	}

	public AdvancedProperty(String id) {
		this();

		this.id = id;
	}

	public AdvancedProperty(String id, String value) {
		this(id);

		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return new StringBuilder(id).append(CoreConstants.COLON).append(CoreConstants.SPACE).append(value).toString();
	}
}