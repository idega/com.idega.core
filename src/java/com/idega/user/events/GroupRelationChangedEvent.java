package com.idega.user.events;

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
	private String groupType;

	private Integer relatedGroupId;
	private String relatedGroupType;

	private String status;

	public GroupRelationChangedEvent(EventType type) {
		super(type);

		this.type = type;
	}

	public GroupRelationChangedEvent(EventType type, Integer groupRelationId) {
		this(type);

		this.groupRelationId = groupRelationId;
	}

	public GroupRelationChangedEvent(EventType type, Integer groupRelationId, Integer groupId, String groupType, Integer relatedGroupId, String relatedGroupType, String status) {
		this(type, groupRelationId);

		this.groupId = groupId;
		this.groupType = groupType;

		this.relatedGroupId = relatedGroupId;
		this.relatedGroupType = relatedGroupType;

		this.status = status;
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
	public String toString() {
		return "Type: " + getType() + ", group relation ID: " + getGroupRelationId() + ", user ID: " + getUserId();
	}

}