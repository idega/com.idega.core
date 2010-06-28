/**
 * 
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = GroupDomainRelationType.ENTITY_NAME)
public class GroupDomainRelationType implements Serializable {

	private static final long serialVersionUID = -8706670995707213582L;

	public static final String RELATION_TYPE_TOP_NODE = "TOP_NODE";

	public static final String ENTITY_NAME = "ic_group_domain_rel_type";
	public static final String COLUMN_TYPE = "relation_type";
	private static final String COLUMN_DESCRIPTION = "description";

	@Id
	@Column(name = COLUMN_TYPE, length = 30)
	private String type;

	@Column(name = COLUMN_DESCRIPTION, length = 1000)
	private String description;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}