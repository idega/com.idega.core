package com.idega.builder.bean;

public class AdvancedProperty {
	
	private String id = null;
	private String value = null;
	
	public AdvancedProperty() {}
	
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

}
