/**
 * 
 */
package com.idega.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Metadata.ENTITY_NAME)
public class Metadata implements Serializable {

	private static final long serialVersionUID = -1350922579475674106L;

	public static final String ENTITY_NAME = "ic_metadata";
	public static final String COLUMN_METADATA_ID = "ic_metadata_id";
	private static final String COLUMN_KEY = "metadata_name";
	private static final String COLUMN_VALUE = "metadata_value";
	private static final String COLUMN_TYPE = "meta_data_type";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_METADATA_ID)
	private Integer metadataID;

	@Column(name = COLUMN_KEY)
	private String key;

	@Column(name = COLUMN_VALUE, length = 2000)
	private String value;

	@Column(name = COLUMN_TYPE)
	private String type;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.metadataID == null) ? 0 : this.metadataID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Metadata other = (Metadata) obj;
		if (this.metadataID == null) {
			if (other.metadataID != null)
				return false;
		}
		else if (!this.metadataID.equals(other.metadataID))
			return false;
		return true;
	}

	/**
	 * @return the metadataID
	 */
	public Integer getId() {
		return this.metadataID;
	}

	/**
	 * @param metadataID
	 *          the metadataID to set
	 */
	public void setId(Integer metadataID) {
		this.metadataID = metadataID;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key
	 *          the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value
	 *          the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *          the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}