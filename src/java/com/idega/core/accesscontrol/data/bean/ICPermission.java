/**
 *
 */
package com.idega.core.accesscontrol.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

@Entity
@Table(name = ICPermission.ENTITY_NAME)
@NamedQueries({
		@NamedQuery(name = "permission.findByCriteria", query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and i.permissionGroup = :group"),
		@NamedQuery(name = "permission.findByValues", query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and i.permissionValue = :permissionValue"),
		@NamedQuery(name = "permission.findByGroupAndContext", query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue in (:contextValues) and i.permissionGroup = :group"),
		@NamedQuery(name = "findByContextType", query = "select i from ICPermission i where i.contextType = :contextType"),
		@NamedQuery(name = "permission.findByContext", query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue"),
		@NamedQuery(name = "permission.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue", query = "select i from ICPermission i where i.contextType = :contextType and i.permissionString in (:permissionStrings) and i.permissionGroup = :group and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null) order by i.contextValue"),
		@NamedQuery(name = "permission.findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup", query = "select i from ICPermission i where i.contextType = :contextType and i.permissionString in (:permissionStrings) and i.permissionValue = 'Y' and i.permissionGroup = :group and i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null"),
		@NamedQuery(name = "permission.findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue", query = "select i from ICPermission i where i.contextType = :contextType and i.permissionGroup = :group and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null) order by i.contextValue"),
		@NamedQuery(name = "permission.findAllPermissionsByContextTypeAndPermissionGroupsOrderedByContextValue", query = "select i from ICPermission i where i.contextType = :contextType and i.permissionGroup in (:groups) and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null) order by i.contextValue"),
		@NamedQuery(name = "permission.findAllPermissionsByContextTypeAndContextValueAndPermissionString", query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null)"),
		@NamedQuery(name = "permission.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue", query = "select i from ICPermission i where i.contextType = :contextType and i.permissionString in (:permissionStrings) and i.permissionGroup in (:groups) and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null)"),
		@NamedQuery(name = "permission.findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue", query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and i.permissionGroup = :group and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null)"),
		@NamedQuery(name = "permission.deleteByCriteria", query = "delete from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString in (:permissionStrings) and i.permissionGroup in (:groups)")
})
public class ICPermission implements Serializable {

	private static final long serialVersionUID = -3406574119826904555L;

	public final static String STATUS_ACTIVE = "ST_ACTIVE";
	public final static String STATUS_PASSIVE = "ST_PASSIVE";

	public static final String ENTITY_NAME = "ic_permission";
	public static final String COLUMN_PERMISSION_ID = "ic_permission_id";
	private static final String COLUMN_CONTEXT_TYPE = "permission_context_type";
	private static final String COLUMN_CONTEXT_VALUE = "permission_context_value";
	private static final String COLUMN_PERMISSION_STRING = "permission_string";
	private static final String COLUMN_PERMISSION_VALUE = "permission_value";
	private static final String COLUMN_GROUP = "group_id";
	private static final String COLUMN_INITIATION_DATE = "initiation_date";
	private static final String COLUMN_TERMINATION_DATE = "termination_date";
	private static final String COLUMN_PASSIVE_BY = "set_passive_by";
	private static final String COLUMN_STATUS = "status";
	private final static String COLUMN_INHERIT_TO_CHILDREN = "inherit";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_PERMISSION_ID)
	private Integer permissionID;

	@Column(name = COLUMN_CONTEXT_TYPE)
	private String contextType;

	@Column(name = COLUMN_CONTEXT_VALUE, length = 30)
	private String contextValue;

	@Column(name = COLUMN_PERMISSION_STRING)
	private String permissionString;

	@Column(name = COLUMN_PERMISSION_VALUE, length = 1)
	private Character permissionValue;

	@ManyToOne
	@JoinColumn(name = COLUMN_GROUP)
	private Group permissionGroup;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_INITIATION_DATE)
	private Date initiationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_TERMINATION_DATE)
	private Date terminationDate;

	@ManyToOne
	@JoinColumn(name = COLUMN_PASSIVE_BY)
	private User passiveBy;

	@Column(name = COLUMN_STATUS, length = 10)
	private String status;

	@Column(name = COLUMN_INHERIT_TO_CHILDREN, length = 1)
	private Character inheritToChildren;

	public Integer getId() {
		return this.permissionID;
	}

	public void setId(Integer permissionID) {
		this.permissionID = permissionID;
	}

	public String getContextType() {
		return this.contextType;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

	public String getContextValue() {
		return this.contextValue;
	}

	public void setContextValue(String contextValue) {
		this.contextValue = contextValue;
	}

	public String getPermissionString() {
		return this.permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}

	public boolean getPermissionValue() {
		if (this.permissionValue == null) {
			return false;
		}
		return this.permissionValue == 'Y';
	}

	public void setPermissionValue(boolean permissionValue) {
		this.permissionValue = permissionValue ? 'Y' : 'N';
	}

	public Group getPermissionGroup() {
		return this.permissionGroup;
	}

	public void setPermissionGroup(Group permissionGroup) {
		this.permissionGroup = permissionGroup;
	}

	public Date getInitiationDate() {
		return this.initiationDate;
	}

	public void setInitiationDate(Date initiationDate) {
		this.initiationDate = initiationDate;
	}

	public Date getTerminationDate() {
		return this.terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public User getPassiveBy() {
		return this.passiveBy;
	}

	public void setPassiveBy(User passiveBy) {
		this.passiveBy = passiveBy;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean getInheritToChildren() {
		if (this.inheritToChildren == null) {
			return false;
		}
		return this.inheritToChildren == 'Y';
	}

	public void setInheritToChildren(boolean inheritToChildren) {
		this.inheritToChildren = inheritToChildren ? 'Y' : 'N';
	}

	public void setActive(){
		this.setStatus(STATUS_ACTIVE);
	}

	public void setPassive(){
		this.setStatus(STATUS_PASSIVE);
	}

	public boolean isActive(){
		String status = this.getStatus();
		if (status != null && status.equals(STATUS_ACTIVE)){
			return true;
		}
		return false;
	}

	public boolean isPassive(){
		String status = this.getStatus();
		if (status != null && status.equals(STATUS_PASSIVE)){
			return true;
		}
		return false;
	}
}