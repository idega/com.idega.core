/**
 * 
 */
package com.idega.core.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GenericType implements Serializable {

	private static final long serialVersionUID = -6190866377935925365L;

	private static final String COLUMN_DISPLAY_NAME = "type_display_name";
	private static final String COLUMN_UNIQUE_NAME = "unique_name";
	private static final String COLUMN_DESCRIPTION = "type_description";

	@Column(name = COLUMN_DISPLAY_NAME, length = 255)
	private String displayName;

	@Column(name = COLUMN_UNIQUE_NAME, length = 255)
	private String uniqueName;

	@Column(name = COLUMN_DESCRIPTION, length = 500)
	private String description;

	public abstract Integer getId();

	public String getName() {
		return getDisplayName();
	}

	public void setName(String name) {
		setDisplayName(name);
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUniqueName() {
		return this.uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
}