/**
 *
 */
package com.idega.core.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = ApplicationBinding.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "applicationBinding.findAll", query = "select a from ApplicationBinding a"),
	@NamedQuery(name = "applicationBinding.findAllByType", query = "select a from ApplicationBinding a where a.type = :type")
})
@Cacheable
public class ApplicationBinding implements Serializable {

	private static final long serialVersionUID = -8927507522282551946L;

	public static final String ENTITY_NAME = "ic_application_binding";
	public final static String COLUMN_KEY = "BINDING_KEY";
	private final static String COLUMN_VALUE = "BINDING_VALUE";
	private final static String COLUMN_TYPE = "BINDING_TYPE";

	public final static int MAX_KEY_LENGTH = 30;

	@Id
	@Column(name = COLUMN_KEY, length = MAX_KEY_LENGTH)
	private String key;

	@Column(name = COLUMN_VALUE)
	private String value;

	@Column(name = COLUMN_TYPE)
	private String type;

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
