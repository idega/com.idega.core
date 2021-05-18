/**
 *
 */
package com.idega.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = Metadata.ENTITY_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = Metadata.QUERY_FIND_BY_GROUP_ID_AND_KEY, query = "select m.value from Group g inner join g.metadata m where g.groupID = :groupId and m.key = :key")
})
public class Metadata implements Serializable {

	private static final long serialVersionUID = -1350922579475674106L;

	public static final String	ENTITY_NAME = "ic_metadata",
								COLUMN_METADATA_ID = "ic_metadata_id",
								COLUMN_KEY = "metadata_name",
								COLUMN_VALUE = "metadata_value",
								COLUMN_TYPE = "meta_data_type",

								QUERY_FIND_BY_GROUP_ID_AND_KEY = "metadata.findByGroupIdAndKey";

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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Metadata other = (Metadata) obj;
		if (this.metadataID == null) {
			if (other.metadataID != null) {
				return false;
			}
		}
		else if (!this.metadataID.equals(other.metadataID)) {
			return false;
		}
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