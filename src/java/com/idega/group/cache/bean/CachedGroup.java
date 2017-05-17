package com.idega.group.cache.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedGroup implements Serializable {

	private static final long serialVersionUID = -6002584749728533881L;

	private Integer groupRelationId;

	private Integer id;

	private String name;
	private String type;

	private Map<Integer, CachedGroup> parents = new ConcurrentHashMap<>();
	private Map<Integer, CachedGroup> children = new ConcurrentHashMap<>();

	private List<CachedGroup> allParents;
	private Map<Integer, List<CachedGroup>> allChildren;

	private boolean active;

	public CachedGroup() {
		super();
	}

	public CachedGroup(Integer id, String name, String type) {
		this();

		this.id = id;
		this.name = name;
		this.type = type;
	}

	public CachedGroup(Integer groupRelationId, Integer id, String name, String type, boolean active) {
		this(id, name, type);

		this.groupRelationId = groupRelationId;
		this.active = active;
	}

	public Integer getGroupRelationId() {
		return groupRelationId;
	}

	public void setGroupRelationId(Integer groupRelationId) {
		this.groupRelationId = groupRelationId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<CachedGroup> getAllParents() {
		return allParents;
	}

	public void setAllParents(List<CachedGroup> allParents) {
		this.allParents = allParents;
	}

	public Map<Integer, List<CachedGroup>> getAllChildren() {
		return allChildren;
	}

	public void setAllChildren(Map<Integer, List<CachedGroup>> allChildren) {
		this.allChildren = allChildren;
	}

	public Map<Integer, CachedGroup> getParents() {
		return parents;
	}

	public void setParents(Map<Integer, CachedGroup> parents) {
		this.parents = parents;
	}

	public void addParent(CachedGroup parent) {
		addRelation(parents, parent);
	}

	private void addRelation(Map<Integer, CachedGroup> relations, CachedGroup relation) {
		if (relation == null) {
			return;
		}

		if (relations == null) {
			relations = new HashMap<>();
		}

		relations.put(relation.getId(), relation);
	}

	public Map<Integer, CachedGroup> getChildren() {
		return children;
	}

	public void setChildren(Map<Integer, CachedGroup> children) {
		this.children = children;
	}

	public void addChild(CachedGroup child) {
		addRelation(children, child);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CachedGroup) {
			CachedGroup cachedGroup = (CachedGroup) o;
			Integer relationId1 = getGroupRelationId();
			Integer relationId2 = cachedGroup.getGroupRelationId();
			Integer id1 = getId();
			Integer id2 = cachedGroup.getId();
			String name1 = getName();
			String name2 = cachedGroup.getName();
			String type1 = getType();
			String type2 = cachedGroup.getType();
			Map<Integer, CachedGroup> parents1 = getParents();
			Map<Integer, CachedGroup> parents2 = cachedGroup.getParents();
			if (
					relationId1 != null && relationId2 != null && relationId1.intValue() == relationId2.intValue() &&
					id1 != null && id2 != null && id1.intValue() == id2.intValue() &&
					name1 != null && name2 != null && name1.equals(name2) &&
					type1 != null && type2 != null && type1.equals(type2) &&
					parents1 != null && parents2 != null && parents1.equals(parents2)
			) {
				return true;
			}
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ID: " + getId() + ", type: " + getType() + ", parent IDs: " + getParents().keySet() + ", children IDs: " + getChildren().keySet();
	}

}