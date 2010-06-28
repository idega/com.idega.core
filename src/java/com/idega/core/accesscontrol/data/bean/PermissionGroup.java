/**
 * 
 */
package com.idega.core.accesscontrol.data.bean;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import com.idega.user.data.bean.Group;

@Entity
@DiscriminatorValue(PermissionGroup.GROUP_TYPE_PERMISSION)
@NamedQuery(name = "permissionGroup.findByName", query = "select p from PermissionGroup p where p.name = :name")
public class PermissionGroup extends Group implements Serializable {

	private static final long serialVersionUID = -5063609730732907543L;
	public static final String GROUP_TYPE_PERMISSION = "permission";

}