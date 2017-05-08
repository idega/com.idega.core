package com.idega.user.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class GroupRelationBean implements Serializable {

	private static final long serialVersionUID = 8705614539739380352L;

	private Integer id;

	private Integer groupId;
	private String groupType;

	private boolean active;

	private Integer relatedGroupId;

	private String relatedGroupType;

	private Timestamp initiationDate, terminationDate, initiationModificationDate, terminationModificationDate;

	private Map<Integer, GroupRelationBean> children;

	private Integer parentEntityId;
	private String parentEntityType;

	public GroupRelationBean() {
		super();
	}

	public GroupRelationBean(
			Integer id,
			Integer groupId,
			String groupType,
			Integer relatedGroupId,
			String relatedGroupType,
			boolean active,
			Timestamp initiationDate,
			Timestamp terminationDate,
			Timestamp initiationModificationDate,
			Timestamp terminationModificationDate
	) {
		this.id = id;

		this.groupId = groupId;
		this.groupType = groupType;

		this.relatedGroupId = relatedGroupId;
		this.relatedGroupType = relatedGroupType;

		this.initiationDate = initiationDate;
		this.terminationDate = terminationDate;
		this.initiationModificationDate = initiationModificationDate;
		this.terminationModificationDate = terminationModificationDate;

		this.active = active;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public Integer getRelatedGroupId() {
		return relatedGroupId;
	}

	public void setRelatedGroupId(Integer relatedGroupId) {
		this.relatedGroupId = relatedGroupId;
	}

	public String getRelatedGroupType() {
		return relatedGroupType;
	}

	public void setRelatedGroupType(String relatedGroupType) {
		this.relatedGroupType = relatedGroupType;
	}

	public Timestamp getInitiationDate() {
		return initiationDate;
	}

	public void setInitiationDate(Timestamp initiationDate) {
		this.initiationDate = initiationDate;
	}

	public Timestamp getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Timestamp terminationDate) {
		this.terminationDate = terminationDate;
	}

	public Timestamp getInitiationModificationDate() {
		return initiationModificationDate;
	}

	public void setInitiationModificationDate(Timestamp initiationModificationDate) {
		this.initiationModificationDate = initiationModificationDate;
	}

	public Timestamp getTerminationModificationDate() {
		return terminationModificationDate;
	}

	public void setTerminationModificationDate(Timestamp terminationModificationDate) {
		this.terminationModificationDate = terminationModificationDate;
	}

	public Map<Integer, GroupRelationBean> getChildren() {
		return children;
	}

	public void setChildren(Map<Integer, GroupRelationBean> children) {
		this.children = children;
	}

	public void addChild(GroupRelationBean child) {
		if (child == null) {
			return;
		}

		if (children == null) {
			children = new HashMap<>();
		}

		children.put(child.getGroupId(), child);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getParentEntityId() {
		return parentEntityId;
	}

	public void setParentEntityId(Integer parentEntityId) {
		this.parentEntityId = parentEntityId;
	}

	public String getParentEntityType() {
		return parentEntityType;
	}

	public void setParentEntityType(String parentEntityType) {
		this.parentEntityType = parentEntityType;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GroupRelationBean) {
			GroupRelationBean gr = (GroupRelationBean) o;
			Integer id1 = getId();
			Integer id2 = gr.getId();

			Integer groupId1 = getGroupId();
			Integer groupId2 = gr.getGroupId();
			String type1 = getGroupType();
			String type2 = gr.getGroupType();

			String relatedGroupType1 = getRelatedGroupType();
			String relatedGroupType2 = gr.getRelatedGroupType();
			Integer relatedGroupId1 = getRelatedGroupId();
			Integer relatedGroupId2 = gr.getRelatedGroupId();
			if (
					id1 != null && id2 != null && id1.intValue() == id2.intValue() &&

					groupId1 != null && groupId2 != null && groupId1.intValue() == groupId2.intValue() &&
					type1 != null && type2 != null && type1.equals(type2) &&

					relatedGroupId1 != null && relatedGroupId2 != null && relatedGroupId1.intValue() == relatedGroupId2.intValue() &&
					relatedGroupType1 != null && relatedGroupType2 != null && relatedGroupType1.equals(relatedGroupType2)
			) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "Group ID: " + getGroupId() + ", group type: " + getGroupType() + ", related group ID: " + getRelatedGroupId() + ", related group type: " + getRelatedGroupType();
	}

}