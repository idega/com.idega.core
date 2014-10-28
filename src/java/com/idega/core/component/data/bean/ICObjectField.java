/**
 *
 */
package com.idega.core.component.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = ICObjectField.ENTITY_NAME)
@Cacheable
public class ICObjectField implements Serializable {

	private static final long serialVersionUID = -1801113599183243196L;

	public static final String ENTITY_NAME = "ic_object_field";
	public static final String COLUMN_OBJECT_FIELD_ID = "ic_object_field_id";
	private static final String COLUMN_OBJECT_ID = "ic_object_id";
	private static final String COLUMN_NAME = "field_name";
	private static final String COLUMN_IDENTIFIER = "field_identifier";
	private static final String COLUMN_METHOD = "field_method";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_OBJECT_FIELD_ID)
	private Integer objectFieldID;

	@ManyToOne
	@JoinColumn(name = COLUMN_OBJECT_ID)
	private ICObject object;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_IDENTIFIER)
	private String identifier;

	@Column(name = COLUMN_METHOD)
	private String method;

	/**
	 * @return the objectFieldID
	 */
	public Integer getId() {
		return this.objectFieldID;
	}

	/**
	 * @param objectFieldID
	 *          the objectFieldID to set
	 */
	public void setId(Integer objectFieldID) {
		this.objectFieldID = objectFieldID;
	}

	/**
	 * @return the object
	 */
	public ICObject getObject() {
		return this.object;
	}

	/**
	 * @param object
	 *          the object to set
	 */
	public void setObject(ICObject object) {
		this.object = object;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * @param identifier
	 *          the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * @param method
	 *          the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
}