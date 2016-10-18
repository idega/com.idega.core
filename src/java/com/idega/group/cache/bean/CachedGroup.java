package com.idega.group.cache.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CachedGroup implements Serializable {

	private static final long serialVersionUID = -6002584749728533881L;

	private Integer id;

	private String type;

	private List<CachedGroup> parents;

	private Map<Integer, List<CachedGroup>> children;

	public CachedGroup() {
		super();
	}

	public CachedGroup(Integer id, String type) {
		this();

		this.id = id;
		this.type = type;

		if ("alias".equals(type)) {
			System.out.println("Alias in " + this);
		}
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

	public List<CachedGroup> getParents() {
		return parents;
	}

	public void setParents(List<CachedGroup> parents) {
		this.parents = parents;
	}

	public Map<Integer, List<CachedGroup>> getChildren() {
		return children;
	}

	public void setChildren(Map<Integer, List<CachedGroup>> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "ID: " + getId() + ", type: " + getType() + ", children: " + getChildren() + ", parents: " + getParents();
	}

}