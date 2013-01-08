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
		@NamedQuery(name = ICPermission.BY_CRITERIA, query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and i.permissionGroup = :group"),
		@NamedQuery(name = ICPermission.BY_VALUES, query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and i.permissionValue = :permissionValue"),
		@NamedQuery(name = ICPermission.BY_GROUP_AND_CONTEXT, query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue in (:contextValues) and i.permissionGroup = :group"),
		@NamedQuery(name = ICPermission.BY_CONTEXTS, query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue in (:contextValues)"),
		@NamedQuery(name = ICPermission.BY_CONTEXT_TYPE, query = "select i from ICPermission i where i.contextType = :contextType"),
		@NamedQuery(name = ICPermission.BY_CONTEXT, query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue"),
		@NamedQuery(name = ICPermission.BY_PERMISSION_GROUP_AND_PERMISSION_STRING, query = "select i from ICPermission i where i.contextType = :contextType and i.permissionString in (:permissionStrings) and i.permissionGroup = :group and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null) order by i.contextValue"),
		@NamedQuery(name = ICPermission.BY_CONTEXT_TYPE_AND_CONTEXT_VALUE, query = "select i from ICPermission i where i.contextType = :contextType and i.permissionString in (:permissionStrings) and i.permissionValue = 'Y' and i.permissionGroup = :group and i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null"),
		@NamedQuery(name = ICPermission.BY_CONTEXT_TYPE_AND_PERMISSION_GROUP, query = "select i from ICPermission i where i.contextType = :contextType and i.permissionGroup = :group and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null) order by i.contextValue"),
		@NamedQuery(name = ICPermission.BY_CONTEXT_TYPE_AND_PERMISSION_GROUPS, query = "select i from ICPermission i where i.contextType = :contextType and i.permissionGroup in (:groups) and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null) order by i.contextValue"),
		@NamedQuery(name = ICPermission.BY_CONTEXT_TYPE_AND_CONTEXT_VALUE_AND_PERMISSION, query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null)"),
		@NamedQuery(name = ICPermission.BY_PERMISSION_GROUPS, query = "select i from ICPermission i where i.contextType = :contextType and i.permissionString in (:permissionStrings) and i.permissionGroup in (:groups) and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null)"),
		@NamedQuery(name = ICPermission.BY_PERMISSION_GROUP, query = "select i from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString = :permissionString and i.permissionGroup = :group and (i.status = '" + ICPermission.STATUS_ACTIVE + "' or i.status is null)"),
		@NamedQuery(name = ICPermission.DELETE_BY_CRITERIA, query = "delete from ICPermission i where i.contextType = :contextType and i.contextValue = :contextValue and i.permissionString in (:permissionStrings) and i.permissionGroup in (:groups)")
})
public class ICPermission implements Serializable {

	private static final long serialVersionUID = -3406574119826904555L;

	public final static String	STATUS_ACTIVE = "ST_ACTIVE",
								STATUS_PASSIVE = "ST_PASSIVE",

								ENTITY_NAME = "ic_permission",
								COLUMN_PERMISSION_ID = "ic_permission_id",

								DELETE_BY_CRITERIA = "permission.deleteByCriteria",
								BY_PERMISSION_GROUP = "permission.findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue",
								BY_PERMISSION_GROUPS = "permission.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue",
								BY_CONTEXT_TYPE_AND_CONTEXT_VALUE_AND_PERMISSION = "permission.findAllPermissionsByContextTypeAndContextValueAndPermissionString",
								BY_CONTEXT_TYPE_AND_PERMISSION_GROUPS = "permission.findAllPermissionsByContextTypeAndPermissionGroupsOrderedByContextValue",
								BY_CONTEXT_TYPE_AND_PERMISSION_GROUP = "permission.findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue",
								BY_CONTEXT_TYPE_AND_CONTEXT_VALUE = "permission.findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup",
								BY_PERMISSION_GROUP_AND_PERMISSION_STRING = "permission.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue",
								BY_CONTEXTS = "permission.findByContexts",
								BY_CONTEXT = "permission.findByContext",
								BY_CONTEXT_TYPE = "permission.findByContextType",
								BY_GROUP_AND_CONTEXT = "permission.findByGroupAndContext",
								BY_VALUES = "permission.findByValues",
								BY_CRITERIA = "permission.findByCriteria",

								COLUMN_CONTEXT_VALUE = "permission_context_value";

	private static final String COLUMN_CONTEXT_TYPE = "permission_context_type",
								COLUMN_PERMISSION_STRING = "permission_string",
								COLUMN_PERMISSION_VALUE = "permission_value",
								COLUMN_GROUP = "group_id",
								COLUMN_INITIATION_DATE = "initiation_date",
								COLUMN_TERMINATION_DATE = "termination_date",
								COLUMN_PASSIVE_BY = "set_passive_by",
								COLUMN_STATUS = "status",
								COLUMN_INHERIT_TO_CHILDREN = "inherit";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_PERMISSION_ID)
	private Integer permissionID;

	@Column(name = COLUMN_CONTEXT_TYPE)
	private String contextType;

	@Column(name = COLUMN_CONTEXT_VALUE, length = ICRole.ROLE_KEY_MAX_LENGTH)
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