/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.data.MetaDataCapable;
import com.idega.data.bean.Metadata;
import com.idega.user.events.GroupRelationChangedEvent;
import com.idega.user.events.GroupRelationChangedEvent.EventType;
import com.idega.util.CoreUtil;
import com.idega.util.DBUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

@Entity
@Table(name = GroupRelation.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = GroupRelation.QUERY_FIND_ALL, query = "select r from GroupRelation r"),
	@NamedQuery(name = GroupRelation.QUERY_FIND_BY_ID, query = "select r from GroupRelation r where r.groupRelationID = :" + GroupRelation.PARAM_GROUP_RELATION_ID),
	@NamedQuery(name = GroupRelation.QUERY_FIND_BY_RELATED_GROUP, query = "select distinct r.group from GroupRelation r where r.relatedGroup = :relatedGroup and r.status = '" + GroupRelation.STATUS_ACTIVE + "' and r.groupRelationType = '" + GroupRelation.RELATION_TYPE_GROUP_PARENT + "'"),
	@NamedQuery(name = GroupRelation.QUERY_FIND_BY_RELATED_GROUP_AND_TYPE, query = "select distinct r.group from GroupRelation r join r.group g where r.relatedGroup = :relatedGroup and g.groupType in (:groupTypes) and r.status = '" + GroupRelation.STATUS_ACTIVE + "' and r.groupRelationType = '" + GroupRelation.RELATION_TYPE_GROUP_PARENT + "'"),
	@NamedQuery(name = GroupRelation.QUERY_FIND_BY_RELATED_GROUP_ID_AND_TYPE, query = "select distinct r.group from GroupRelation r join r.group g where r.relatedGroup.groupID = :relatedGroupId and g.groupType.groupType in (:groupTypes) and r.status = '" + GroupRelation.STATUS_ACTIVE + "' and r.groupRelationType = '" + GroupRelation.RELATION_TYPE_GROUP_PARENT + "'"),
	@NamedQuery(name = GroupRelation.QUERY_FIND_BY_RELATED_GROUPS_IDS_AND_TYPES, query = "select distinct r.group from GroupRelation r join r.group g where r.relatedGroup.groupID in (:relatedGroupsIds) and g.groupType.groupType in (:groupTypes) and r.status = '" + GroupRelation.STATUS_ACTIVE + "' and r.groupRelationType = '" + GroupRelation.RELATION_TYPE_GROUP_PARENT + "'"),
	@NamedQuery(name = GroupRelation.QUERY_GET_HISTORY, query = "select r from GroupRelation r join r.group g where r.relatedGroup.groupID = :"+GroupRelation.PARAM_RELATED_GROUP_ID+" and r.groupRelationType = '" + GroupRelation.RELATION_TYPE_GROUP_PARENT + "'"),
	@NamedQuery(name = "groupRelation.findBiDirectionalRelation", query = "select r from GroupRelation r where (r.group = :group and r.relatedGroup = :relatedGroup) or (r.relatedGroup = :group and r.group = :relatedGroup) and r.status = '" + GroupRelation.STATUS_ACTIVE + "'"),
	@NamedQuery(name = GroupRelation.QUERY_COUNT_BY_RELATED_GROUP_TYPE, query = "select count(r) from GroupRelation r where r.relatedGroupType.groupType = :" + GroupRelation.PARAM_RELATED_GROUP_TYPE),
	@NamedQuery(
			name = GroupRelation.QUERY_FIND_PARENT_IDS,
			query = "SELECT DISTINCT gr.group.id FROM GroupRelation gr "
					+ "WHERE gr.relatedGroup.id in (:ids) "
					+ "AND (gr.groupRelationType.type='GROUP_PARENT' OR gr.groupRelationType.type IS NULL) "
					+ "AND (gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "')"),
	@NamedQuery(
			name = GroupRelation.QUERY_FIND_DIRECT_GROUP_IDS_FOR_USER,
			query = "SELECT DISTINCT gr.group.id FROM GroupRelation gr WHERE gr.relatedGroup.id = :userId AND (gr.groupRelationType.type='GROUP_PARENT' OR gr.groupRelationType.type IS NULL) " +
			"AND (gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "')"
	),
	@NamedQuery(
			name = GroupRelation.QUERY_FIND_DIRECT_GROUPS_FOR_USER_BY_TYPE,
			query = "SELECT DISTINCT gr.group FROM GroupRelation gr WHERE gr.relatedGroup.id = :userId AND gr.group.groupType.groupType in (:groupTypes) AND (gr.groupRelationType.type='GROUP_PARENT' OR gr.groupRelationType.type IS NULL) " +
			"AND (gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "')"
	),
	@NamedQuery(
			name = GroupRelation.QUERY_FIND_GROUPS_IDS_ACTIVE_FROM,
			query = "select gr.relatedGroup.id from GroupRelation gr where gr.relatedGroup.id in (:ids) and (gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" +
					GroupRelation.STATUS_PASSIVE_PENDING + "') and gr.initiationDate >= :date and gr.groupRelationType.type = '" + GroupRelation.RELATION_TYPE_GROUP_PARENT + "'"
	),
	@NamedQuery(
			name = GroupRelation.QUERY_FIND_GROUPS_ACTIVE_FROM,
			query = "select gr.relatedGroup from GroupRelation gr where gr.relatedGroup.id in (:ids) and (gr.status = '" + GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" +
					GroupRelation.STATUS_PASSIVE_PENDING + "') and gr.initiationDate >= :date and gr.groupRelationType.type = '" + GroupRelation.RELATION_TYPE_GROUP_PARENT + "'"
	),
	@NamedQuery(
			name = GroupRelation.QUERY_FIND_GROUPS_IDS_BY_STATUSES,
			query = "SELECT DISTINCT gr.relatedGroup.id FROM GroupRelation gr WHERE gr.relatedGroup.id in (:ids) AND (gr.groupRelationType.type='" + GroupRelation.RELATION_TYPE_GROUP_PARENT +
					"' OR gr.groupRelationType.type IS NULL) AND gr.status in (:statuses)")
})
@Cacheable
public class GroupRelation implements Serializable, MetaDataCapable {

	private static final long serialVersionUID = 5850270896539731950L;

	public static final String	QUERY_FIND_ALL = "groupRelation.findAll",
								QUERY_FIND_BY_RELATED_GROUP = "groupRelation.findByRelatedGroup",
								QUERY_FIND_BY_ID = "groupRelation.findById",
								QUERY_FIND_BY_RELATED_GROUP_AND_TYPE = "groupRelation.findByRelatedGroupAndType",
								QUERY_GET_HISTORY = "groupRelation.getHistory",
								QUERY_FIND_PARENT_IDS = "groupRelation.findParentIds",
								QUERY_COUNT_BY_RELATED_GROUP_TYPE = "groupRelation.countByRelatedGroupType",
								QUERY_FIND_DIRECT_GROUP_IDS_FOR_USER = "groupRelation.findDirectGroupIdsForUser",
								QUERY_FIND_DIRECT_GROUPS_FOR_USER_BY_TYPE = "groupRelation.findDirectGroupsForUserByType",
								QUERY_FIND_BY_RELATED_GROUP_ID_AND_TYPE = "groupRelation.findByRelatedGroupIdAndType",
								QUERY_FIND_BY_RELATED_GROUPS_IDS_AND_TYPES = "groupRelation.findByRelatedGroupsIdAndTypes",
								QUERY_FIND_GROUPS_IDS_ACTIVE_FROM = "groupRelation.findGroupsIdsActiveFrom",
								QUERY_FIND_GROUPS_ACTIVE_FROM = "groupRelation.findGroupsActiveFrom",
								QUERY_FIND_GROUPS_IDS_BY_STATUSES = "groupRelation.findGroupsIdsByStatuses";

	public static final String PARAM_GROUP_RELATION_ID = "groupRelationId";
	public static final String PARAM_RELATED_GROUP_ID = "relatedGroupId";
	public static final String PARAM_RELATED_GROUP_TYPE = "relatedGroupType";
	public static final String PARAM_RELATED_GROUP_IDS = "relatedGroupIds";
	public static final String PARAM_GROUP_TYPE = "groupType";
	public static final String PARAM_GROUP_TYPES = "groupTypes";
	public static final String PARAM_DATE_FROM = "dateFrom";
	public static final String PARAM_DATE_TO = "dateTo";
	public static final String PARAM_GROUP_ID = "groupId";

	public final static String STATUS_ACTIVE = "ST_ACTIVE";
	public final static String STATUS_PASSIVE = "ST_PASSIVE";
	public final static String STATUS_PASSIVE_PENDING = "PASS_PEND";
	public final static String STATUS_ACTIVE_PENDING = "ACT_PEND";

	public static final String RELATION_TYPE_GROUP_PARENT = "GROUP_PARENT";

	public static final String ENTITY_NAME = "ic_group_relation";
	public static final String COLUMN_GROUP_RELATION_ID = "ic_group_relation_id";
	public static final String COLUMN_GROUP = "ic_group_id";
	public static final String COLUMN_RELATED_GROUP = "related_ic_group_id";

	private static final String COLUMN_RELATIONSHIP_TYPE = "relationship_type";
	private static final String COLUMN_STATUS = "group_relation_status";
	private static final String COLUMN_INITIATION_DATE = "initiation_date";
	private static final String COLUMN_TERMINATION_DATE = "termination_date";
	private static final String COLUMN_PASSIVE_BY = "set_passive_by";
	private static final String COLUMN_CREATED_BY = "created_by";
	private static final String COLUMN_RELATED_GROUP_TYPE = "related_group_type";
	private static final String COLUMN_INITIATION_MODIFICATION_DATE = "init_modification_date";
	private static final String COLUMN_TERMINATION_MODIFICATION_DATE = "term_modification_date";

	@PrePersist
	public void setDefaultValues() {
		if (getStatus() == null) {
			setStatus(STATUS_ACTIVE);
		}
		if (getInitiationDate() == null) {
			setInitiationDate(IWTimestamp.getTimestampRightNow());
		}

		onBeforeUpdate();
	}

	@PreUpdate
	@PreRemove
	public void onBeforeUpdate() {
		final Integer id = getId();
		if (id != null) {
			//	Editing
			String status = getStatus();
			GroupRelationType groupRelationType = getGroupRelationType();
			String relationshipType = groupRelationType == null ? null : groupRelationType.getType();
			GroupType relatedGroupTypeEntity = getRelatedGroupType();
			String relatedGroupType = relatedGroupTypeEntity == null ? null : relatedGroupTypeEntity.getGroupType();
			if (StringUtil.isEmpty(status) || StringUtil.isEmpty(relationshipType) || StringUtil.isEmpty(relatedGroupType)) {
				String message = "Insufficient data for " + getClass().getName() + ", ID: " + id + ". Status: " + status + ", relationship type : " + relationshipType + ", related group type: " + relatedGroupType;
				RuntimeException e = new RuntimeException(message);
				CoreUtil.sendExceptionNotification(message, e);
				throw e;
			}
		}
	}

	@PostPersist
	@PostUpdate
	@PostRemove
	public void onChange() {
		Integer id = getId();

		try {
			Group group = getGroup();
			Group relatedGroup = getRelatedGroup();
			String status = getStatus();
			ELUtil.getInstance().publishEvent(
					new GroupRelationChangedEvent(
							EventType.GROUP_CHANGE,
							id,
							group == null ? null : group.getID(),
							group == null ? null : group.getName(),
							group == null ? null : group.getType(),
							relatedGroup == null ? null : relatedGroup.getID(),
							relatedGroup == null ? null : relatedGroup.getName(),
							relatedGroup == null ? null : relatedGroup.getType(),
							status,
							getInitiationDate(),
							getTerminationDate(),
							getInitiationModificationDate(),
							getTerminationModificationDate()
					)
			);
		} catch (Exception e) {
			String message = "Error posting event about updated " + getClass().getName() + " with ID: " + id;
			Logger.getLogger(getClass().getName()).log(Level.WARNING, message, e);
			CoreUtil.sendExceptionNotification(message, e);
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_GROUP_RELATION_ID)
	private Integer groupRelationID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_GROUP)
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_RELATED_GROUP)
	private Group relatedGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_RELATIONSHIP_TYPE)
	private GroupRelationType groupRelationType;

	@Column(name = COLUMN_STATUS, length = 30)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_INITIATION_DATE)
	private Date initiationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_TERMINATION_DATE)
	private Date terminationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_PASSIVE_BY)
	private User passiveBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_CREATED_BY)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_RELATED_GROUP_TYPE)
	private GroupType relatedGroupType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_INITIATION_MODIFICATION_DATE)
	private Date initiationModificationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_TERMINATION_MODIFICATION_DATE)
	private Date terminationModificationDate;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Metadata.class)
	@JoinTable(name = "ic_group_relation_ic_metadata", joinColumns = { @JoinColumn(name = COLUMN_GROUP_RELATION_ID) }, inverseJoinColumns = { @JoinColumn(name = Metadata.COLUMN_METADATA_ID) })
	private Set<Metadata> metadata;

	public Integer getId() {
		return this.groupRelationID;
	}

	public Group getGroup() {
		group = DBUtil.getInstance().lazyLoad(group);
		return this.group;
	}

	public Group getRelatedGroup() {
		relatedGroup = DBUtil.getInstance().lazyLoad(relatedGroup);
		return this.relatedGroup;
	}

	public GroupRelationType getGroupRelationType() {
		groupRelationType = DBUtil.getInstance().lazyLoad(groupRelationType);
		return this.groupRelationType;
	}

	public String getStatus() {
		return this.status;
	}

	public Date getInitiationDate() {
		return this.initiationDate;
	}

	public Date getTerminationDate() {
		return this.terminationDate;
	}

	public User getPassiveBy() {
		passiveBy = DBUtil.getInstance().lazyLoad(passiveBy);
		return this.passiveBy;
	}

	public User getCreatedBy() {
		createdBy = DBUtil.getInstance().lazyLoad(createdBy);
		return this.createdBy;
	}

	public GroupType getRelatedGroupType() {
		relatedGroupType = DBUtil.getInstance().lazyLoad(relatedGroupType);
		return this.relatedGroupType;
	}

	public Date getInitiationModificationDate() {
		return this.initiationModificationDate;
	}

	public Date getTerminationModificationDate() {
		return this.terminationModificationDate;
	}

	public void setId(Integer groupRelationID) {
		this.groupRelationID = groupRelationID;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setRelatedGroup(Group relatedGroup) {
		this.relatedGroup = relatedGroup;
	}

	public void setGroupRelationType(GroupRelationType groupRelationType) {
		this.groupRelationType = groupRelationType;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setInitiationDate(Date initiationDate) {
		this.initiationDate = initiationDate;
		this.initiationModificationDate = IWTimestamp.getTimestampRightNow();
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
		this.terminationModificationDate = IWTimestamp.getTimestampRightNow();
	}

	public void setPassiveBy(User passiveBy) {
		this.passiveBy = passiveBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public void setRelatedGroupType(GroupType relatedGroupType) {
		this.relatedGroupType = relatedGroupType;
	}

	/**
	 * @return the metadata
	 */
	public Set<Metadata> getMetadata() {
		metadata = DBUtil.getInstance().lazyLoad(metadata);
		return this.metadata;
	}

	/**
	 * @param metadata
	 *          the metadata to set
	 */
	public void setMetadata(Set<Metadata> metadata) {
		this.metadata = metadata;
	}

	private Metadata getMetadata(String key) {
		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			if (metaData.getKey().equals(key)) {
				return metaData;
			}
		}

		return null;
	}

	@Override
	public String getMetaData(String metaDataKey) {
		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			if (metaData.getKey().equals(metaDataKey)) {
				return metaData.getValue();
			}
		}

		return null;
	}

	@Override
	public Map<String, String> getMetaDataAttributes() {
		Map<String, String> map = new HashMap<String, String>();

		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			map.put(metaData.getKey(), metaData.getValue());
		}

		return map;
	}

	@Override
	public Map<String, String> getMetaDataTypes() {
		Map<String, String> map = new HashMap<String, String>();

		Set<Metadata> list = getMetadata();
		for (Metadata metaData : list) {
			map.put(metaData.getKey(), metaData.getType());
		}

		return map;
	}

	@Override
	public boolean removeMetaData(String metaDataKey) {
		Metadata metadata = getMetadata(metaDataKey);
		if (metadata != null) {
			getMetadata().remove(metadata);
		}

		return false;
	}

	@Override
	public void renameMetaData(String oldKeyName, String newKeyName, String value) {
		Metadata metadata = getMetadata(oldKeyName);
		if (metadata != null) {
			metadata.setKey(newKeyName);
			if (value != null) {
				metadata.setValue(value);
			}
		}
	}

	@Override
	public void renameMetaData(String oldKeyName, String newKeyName) {
		renameMetaData(oldKeyName, newKeyName, null);
	}

	@Override
	public void setMetaData(String metaDataKey, String value, String type) {
		Metadata metadata = getMetadata(metaDataKey);
		if (metadata == null) {
			metadata = new Metadata();
			metadata.setKey(metaDataKey);
		}
		metadata.setValue(value);
		if (type != null) {
			metadata.setType(type);
		}

		getMetadata().add(metadata);

	}

	@Override
	public void setMetaData(String metaDataKey, String value) {
		setMetaData(metaDataKey, value, null);
	}

	@Override
	public void setMetaDataAttributes(Map<String, String> map) {
		for (String key : map.keySet()) {
			String value = map.get(key);

			Metadata metadata = getMetadata(key);
			if (metadata == null) {
				metadata = new Metadata();
				metadata.setKey(key);
			}
			metadata.setValue(value);

			getMetadata().add(metadata);
		}
	}

	@Override
	public void updateMetaData() throws SQLException {
		//Does nothing...
	}
}