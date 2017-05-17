package com.idega.user.events;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.context.ApplicationEvent;

public class GroupRelationChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1391585374701171810L;

	public enum EventType {

		START, GROUP_CHANGE, USER_UPDATE, USER_LOGIN, EMPTY;

	}

	private EventType type;

	private Integer groupRelationId, userId;

	private boolean executeInThread = true;

	private Integer groupId;
	private String groupName;
	private String groupType;

	private Integer relatedGroupId;
	private String relatedGroupName;
	private String relatedGroupType;

	private String status, source;

	private Timestamp initiationDate = null, terminationDate = null, initiationModificationDate = null, terminationModificationDate = null;

	public GroupRelationChangedEvent(EventType type) {
		super(type);

		this.type = type;
	}

	public GroupRelationChangedEvent(EventType type, Integer groupRelationId) {
		this(type);

		this.groupRelationId = groupRelationId;
	}

	public GroupRelationChangedEvent(
			EventType type,

			Integer groupRelationId,

			Integer groupId,
			String groupName,
			String groupType,

			Integer relatedGroupId,
			String relatedGroupName,
			String relatedGroupType,

			String status,
			Date initiationDate,
			Date terminationDate,
			Date initiationModificationDate,
			Date terminationModificationDate
	) {
		this(
				type,
				groupRelationId,
				groupId,
				groupName,
				groupType,
				relatedGroupId,
				relatedGroupName,
				relatedGroupType,
				status,
				initiationDate == null ? null : new Timestamp(initiationDate.getTime()),
				terminationDate == null ? null : new Timestamp(terminationDate.getTime()),
				initiationModificationDate == null ? null : new Timestamp(initiationModificationDate.getTime()),
				terminationModificationDate == null ? null : new Timestamp(terminationModificationDate.getTime())
		);
	}

	public GroupRelationChangedEvent(
			EventType type,

			Integer groupRelationId,

			Integer groupId,
			String groupName,
			String groupType,

			Integer relatedGroupId,
			String relatedGroupName,
			String relatedGroupType,

			String status,
			Timestamp initiationDate,
			Timestamp terminationDate,
			Timestamp initiationModificationDate,
			Timestamp terminationModificationDate
	) {
		this(type, groupRelationId);

		this.groupId = groupId;
		this.groupName = groupName;
		this.groupType = groupType;

		this.relatedGroupId = relatedGroupId;
		this.relatedGroupName = relatedGroupName;
		this.relatedGroupType = relatedGroupType;

		this.status = status;

		this.initiationDate = initiationDate;
		this.terminationDate = terminationDate;
		this.initiationModificationDate = initiationModificationDate;
		this.terminationModificationDate = terminationModificationDate;
	}

	public GroupRelationChangedEvent(EventType type, boolean executeInThread, Integer userId) {
		this(type);

		this.executeInThread = executeInThread;
		this.userId = userId;
	}

	public Integer getGroupRelationId() {
		return groupRelationId;
	}

	public Integer getUserId() {
		return userId;
	}

	public boolean isExecuteInThread() {
		return executeInThread;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public String getGroupType() {
		return groupType;
	}

	public Integer getRelatedGroupId() {
		return relatedGroupId;
	}

	public String getRelatedGroupType() {
		return relatedGroupType;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Timestamp getInitiationDate() {
		return initiationDate;
	}

	public Timestamp getTerminationDate() {
		return terminationDate;
	}

	public Timestamp getInitiationModificationDate() {
		return initiationModificationDate;
	}

	public Timestamp getTerminationModificationDate() {
		return terminationModificationDate;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getRelatedGroupName() {
		return relatedGroupName;
	}

	public void setRelatedGroupName(String relatedGroupName) {
		this.relatedGroupName = relatedGroupName;
	}

	@Override
	public String toString() {
		return "Type: " + getType() + ", group relation ID: " + getGroupRelationId() + ", user ID: " + getUserId();
	}

}