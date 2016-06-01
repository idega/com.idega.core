/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GroupEventType.ENTITY_NAME)
@Cacheable
public class GroupEventType implements Serializable {

	private static final long serialVersionUID = -6248390005127333195L;

	public static final String ENTITY_NAME = "ic_group_event_type";
	public static final String COLUMN_GROUP_EVENT_TYPE = "ic_group_event_type_id";
	private static final String COLUMN_TYPE = "event_type";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_GROUP_EVENT_TYPE)
	private Integer id;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = COLUMN_TYPE)
	private String type;
}