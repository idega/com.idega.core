package com.idega.builder.bean;

public class AdvancedProperty {
	
	private boolean selected = false;
	private String id;
	private String value;
	
	public AdvancedProperty() {}
	
	public AdvancedProperty(String id) {
		this();
		this.id = id;
	}
	
	public AdvancedProperty(String id, String value) {
		this();
		this.id = id;
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
}