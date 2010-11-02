/**
 * 
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = GroupRelationType.ENTITY_NAME)
@NamedQuery(name = "groupRelationType.findAll", query = "select t from GroupRelationType t")
public class GroupRelationType implements Serializable {

	private static final long serialVersionUID = -5974678086532632952L;

	public static final String ENTITY_NAME = "ic_group_relation_type";
	public static final String COLUMN_TYPE = "group_relation_type";
	private static final String COLUMN_DESCRIPTION = "group_relation_type_descr";

	@Id
	@Column(name = COLUMN_TYPE, length = 15)
	private String type;

	@Column(name = COLUMN_DESCRIPTION, length = 1000)
	private String description;

	public String getType() {
		return this.type;
	}

	public String getDescription() {
		return this.description;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}