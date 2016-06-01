/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.Table;

import com.idega.core.component.data.bean.ICObject;
import com.idega.util.DBUtil;

@Entity
@Table(name = UserGroupPlugin.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "userGroupPlugin.findAll", query = "select ugp from UserGroupPlugin ugp"),
	@NamedQuery(name = "userGroupPlugin.findAllByGroupType", query = "select ugp from UserGroupPlugin ugp join ugp.groupTypes gt where gt.groupType = :groupType")
})
@Cacheable
public class UserGroupPlugin implements Serializable {

	private static final long serialVersionUID = 278160785639317656L;

	public static final String ENTITY_NAME = "ic_user_plugin";
	public static final String COLUMN_USER_PLUGIN_ID = "ic_user_plugin_id";
	private static final String COLUMN_NAME = "plug_in_name";
	private static final String COLUMN_DESCRIPTION = "plug_in_desc";
	private static final String COLUMN_TYPE = "plug_in_type";
	private static final String COLUMN_BUSINESS_OBJECT = "business_ic_object";
	private static final String COLUMN_PRESENTATION_OBJECT = "presentation_ic_object";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_USER_PLUGIN_ID)
	private Integer id;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_DESCRIPTION)
	private String description;

	@Column(name = COLUMN_TYPE)
	private String type;

	@ManyToOne
	@JoinColumn(name = COLUMN_BUSINESS_OBJECT)
	private ICObject businessObject;

	@ManyToOne
	@JoinColumn(name = COLUMN_PRESENTATION_OBJECT)
	private ICObject presentationObject;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = GroupType.class)
	@JoinTable(name = "ic_group_type_ic_user_plugin", joinColumns = { @JoinColumn(name = COLUMN_USER_PLUGIN_ID) }, inverseJoinColumns = { @JoinColumn(name = GroupType.COLUMN_TYPE) })
	private List<GroupType> groupTypes;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ICObject getBusinessObject() {
		return this.businessObject;
	}

	public void setBusinessObject(ICObject businessObject) {
		this.businessObject = businessObject;
	}

	public ICObject getPresentationObject() {
		return this.presentationObject;
	}

	public void setPresentationObject(ICObject presentationObject) {
		this.presentationObject = presentationObject;
	}

	public List<GroupType> getGroupTypes() {
		groupTypes = DBUtil.getInstance().lazyLoad(groupTypes);
		return this.groupTypes;
	}

	public void setGroupTypes(List<GroupType> groupTypes) {
		this.groupTypes = groupTypes;
	}
}