package com.idega.builder.bean;

import java.io.Serializable;

import com.idega.util.CoreConstants;

public class AdvancedProperty implements Serializable {

	private static final long serialVersionUID = 8520116287821698275L;

	private boolean selected;
	private String id, value, name;
	private Long externalId;

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

	public AdvancedProperty(String id, Long value) {
		this(id);
		this.value = String.valueOf(value);
	}

	public AdvancedProperty(String id, String value, String name) {
		this(id, value);

		this.name = name;
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

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getId() + CoreConstants.COLON + CoreConstants.SPACE + getValue() + ", name: " + getName();
	}
}