/**
 *
 */
package com.idega.core.accesscontrol.data.bean;

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

import com.idega.core.component.data.bean.ICObject;

@Entity
@Table(name = ICObjectPermission.ENTITY_NAME)
@Cacheable
public class ICObjectPermission implements Serializable {

	private static final long serialVersionUID = -8440268993662302436L;

	public static final String ENTITY_NAME = "ic_object_permission";
	public static final String COLUMN_OBJECT_PERMISSION_ID = "ic_object_permission_id";
	private static final String COLUMN_OBJECT = "ic_object_id";
	private static final String COLUMN_PERMISSION_TYPE = "permission_type";
	private static final String COLUMN_DESCRIPTION = "description";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_OBJECT_PERMISSION_ID)
	private Integer objectPermissionID;

	@ManyToOne
	@JoinColumn(name = COLUMN_OBJECT)
	private ICObject object;

	@Column(name = COLUMN_PERMISSION_TYPE)
	private String permissionType;

	@Column(name = COLUMN_DESCRIPTION)
	private String description;

	public Integer getId() {
		return this.objectPermissionID;
	}

	public void setId(Integer objectPermissionID) {
		this.objectPermissionID = objectPermissionID;
	}

	public ICObject getObject() {
		return this.object;
	}

	public void setObject(ICObject object) {
		this.object = object;
	}

	public String getPermissionType() {
		return this.permissionType;
	}

	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static String getCOLUMN_DESCRIPTION() {
		return COLUMN_DESCRIPTION;
	}
}