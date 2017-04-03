package com.idega.group.cache.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GroupRelation implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer groupId;
	private String groupType;

	private Integer relatedGroupId;

	private String relatedGroupType;

	private boolean active;

	private Map<Integer, GroupRelation> children;

	public GroupRelation() {
		super();
	}

	public GroupRelation(Integer id, Integer groupId, String groupType, Integer relatedGroupId, String relatedGroupType, boolean active) {
		this.id = id;

		this.groupId = groupId;
		this.groupType = groupType;

		this.relatedGroupId = relatedGroupId;
		this.relatedGroupType = relatedGroupType;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Map<Integer, GroupRelation> getChildren() {
		return children;
	}

	public void setChildren(Map<Integer, GroupRelation> children) {
		this.children = children;
	}

	public void addChild(GroupRelation child) {
		if (child == null) {
			return;
		}

		if (children == null) {
			children = new HashMap<Integer, GroupRelation>();
		}

		children.put(child.getGroupId(), child);
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof GroupRelation) {
			GroupRelation gr = (GroupRelation) o;
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
		return "Group ID: " + getGroupId() + ", related group ID: " + getRelatedGroupId() + ", related group type: " + getRelatedGroupType();
	}

}