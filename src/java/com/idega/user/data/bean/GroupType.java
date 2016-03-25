/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.DBUtil;
import com.idega.util.ListUtil;

@Entity
@Table(name = GroupType.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "groupType.findAll", query = "select t from GroupType t"),
	@NamedQuery(name = "groupType.findByType", query = "select t from GroupType t where t.groupType = :groupType"),
	@NamedQuery(name = GroupType.QUERY_FIND_ALL_VISIBLE, query = "select t from GroupType t where t.isVisible != 'N' group by t.groupType")
})
@Cacheable
public class GroupType implements Serializable, ICTreeNode<GroupType> {

	private static final long serialVersionUID = 3574509217562528319L;

	public static final String	ENTITY_NAME = "ic_group_type",
								QUERY_FIND_ALL_VISIBLE = "groupType.findAllVisibleTypes";

	public static final String COLUMN_TYPE = "group_type";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_DEFAULT_GROUP_NAME = "default_group_name";
	private static final String COLUMN_IS_VISIBLE = "is_visible";
	private static final String COLUMN_MAX_INSTANCES = "max_instances";
	private static final String COLUMN_MAX_INSTANCES_PER_PARENT = "max_instances_per_parent";
	private static final String COLUMN_AUTO_CREATE = "auto_create";
	private static final String COLUMN_NUMBER_OF_INSTANCES_TO_AUTO_CREATE = "instances_auto_created";
	private static final String COLUMN_SUPPORTS_SAME_CHILD_TYPE = "same_child_type";
	private static final String COLUMN_SAME_CHILD_TYPE_ONLY = "same_child_type_only";

	@Id
	@Column(name = COLUMN_TYPE, length = 30)
	private String groupType;

	@Column(name = COLUMN_DESCRIPTION, length = 1000)
	private String description;

	@Column(name = COLUMN_DEFAULT_GROUP_NAME)
	private String defaultGroupName;

	@Column(name = COLUMN_IS_VISIBLE, length = 1)
	private Character isVisible;

	@Column(name = COLUMN_MAX_INSTANCES)
	private Integer maxInstances;

	@Column(name = COLUMN_MAX_INSTANCES_PER_PARENT)
	private Integer maxInstancesPerParent;

	@Column(name = COLUMN_AUTO_CREATE, length = 1)
	private Character autoCreate;

	@Column(name = COLUMN_NUMBER_OF_INSTANCES_TO_AUTO_CREATE)
	private Integer numberOfInstancesToAutoCreate;

	@Column(name = COLUMN_SUPPORTS_SAME_CHILD_TYPE, length = 1)
	private Character supportsSameChildType;

	@Column(name = COLUMN_SAME_CHILD_TYPE_ONLY, length = 1)
	private Character onlySupportsSameChildType;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = GroupType.class)
	@JoinTable(name = ENTITY_NAME + "_tree", joinColumns = { @JoinColumn(name = "child_" + COLUMN_TYPE, referencedColumnName = COLUMN_TYPE) }, inverseJoinColumns = { @JoinColumn(name = COLUMN_TYPE) })
	private GroupType parent;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = GroupType.class)
	@JoinTable(name = ENTITY_NAME + "_tree", joinColumns = { @JoinColumn(name = COLUMN_TYPE) }, inverseJoinColumns = { @JoinColumn(name = "child_" + COLUMN_TYPE, referencedColumnName = COLUMN_TYPE) })
	private List<GroupType> children;

	/**
	 * @return the groupType
	 */
	public String getGroupType() {
		return this.groupType;
	}

	/**
	 * @param groupType
	 *          the groupType to set
	 */
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *          the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the defaultGroupName
	 */
	public String getDefaultGroupName() {
		return this.defaultGroupName;
	}

	/**
	 * @param defaultGroupName
	 *          the defaultGroupName to set
	 */
	public void setDefaultGroupName(String defaultGroupName) {
		this.defaultGroupName = defaultGroupName;
	}

	/**
	 * @return the isVisible
	 */
	public boolean getIsVisible() {
		if (this.isVisible == null) {
			return true;
		}
		return this.isVisible == 'Y';
	}

	/**
	 * @param isVisible
	 *          the isVisible to set
	 */
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible ? 'Y' : 'N';
	}

	/**
	 * @return the maxInstances
	 */
	public int getMaxInstances() {
		return this.maxInstances != null ? this.maxInstances : -1;
	}

	/**
	 * @param maxInstances
	 *          the maxInstances to set
	 */
	public void setMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
	}

	/**
	 * @return the maxInstancesPerParent
	 */
	public int getMaxInstancesPerParent() {
		return this.maxInstancesPerParent != null ? this.maxInstancesPerParent : -1;
	}

	/**
	 * @param maxInstancesPerParent
	 *          the maxInstancesPerParent to set
	 */
	public void setMaxInstancesPerParent(int maxInstancesPerParent) {
		this.maxInstancesPerParent = maxInstancesPerParent;
	}

	/**
	 * @return the autoCreate
	 */
	public boolean getAutoCreate() {
		if (this.autoCreate == null) {
			return true;
		}
		return this.autoCreate == 'Y';
	}

	/**
	 * @param autoCreate
	 *          the autoCreate to set
	 */
	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate ? 'Y' : 'N';
	}

	/**
	 * @return the numberOfInstancesToAutoCreate
	 */
	public int getNumberOfInstancesToAutoCreate() {
		return this.numberOfInstancesToAutoCreate != null ? this.numberOfInstancesToAutoCreate : -1;
	}

	/**
	 * @param numberOfInstancesToAutoCreate
	 *          the numberOfInstancesToAutoCreate to set
	 */
	public void setNumberOfInstancesToAutoCreate(int numberOfInstancesToAutoCreate) {
		this.numberOfInstancesToAutoCreate = numberOfInstancesToAutoCreate;
	}

	/**
	 * @return the supportsSameChildType
	 */
	public boolean getSupportsSameChildType() {
		if (this.supportsSameChildType == null) {
			return true;
		}
		return this.supportsSameChildType == 'Y';
	}

	/**
	 * @param supportsSameChildType
	 *          the supportsSameChildType to set
	 */
	public void setSupportsSameChildType(boolean supportsSameChildType) {
		this.supportsSameChildType = supportsSameChildType ? 'Y' : 'N';
	}

	/**
	 * @return the onlySupportsSameChildType
	 */
	public boolean getOnlySupportsSameChildType() {
		if (this.onlySupportsSameChildType == null) {
			return false;
		}
		return this.onlySupportsSameChildType == 'Y';
	}

	/**
	 * @param onlySupportsSameChildType
	 *          the onlySupportsSameChildType to set
	 */
	public void setOnlySupportsSameChildType(boolean onlySupportsSameChildType) {
		this.onlySupportsSameChildType = onlySupportsSameChildType ? 'Y' : 'N';
	}

	/* ICTreeNode implementation */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public GroupType getChildAtIndex(int childIndex) {
		return getChildren().get(childIndex);
	}

	@Override
	public int getChildCount() {
		return getChildren().size();
	}

	@Override
	public List<GroupType> getChildren() {
		if (!DBUtil.getInstance().isInitialized(children)) {
			DBUtil.getInstance().lazyLoad(children);
		}
		return children;
	}

	@Override
	public Iterator<GroupType> getChildrenIterator() {
		return getChildren().iterator();
	}

	@Override
	public String getId() {
		return getGroupType();
	}

	@Override
	public int getIndex(GroupType node) {
		return Integer.parseInt(node.getId());
	}

	@Override
	public int getNodeID() {
		return -1;
	}

	@Override
	public String getNodeName() {
		return getGroupType();
	}

	@Override
	public String getNodeName(Locale locale) {
		return getNodeName();
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

	@Override
	public GroupType getParentNode() {
		parent = DBUtil.getInstance().lazyLoad(parent);
		return parent;
	}

	@Override
	public int getSiblingCount() {
		GroupType parent = getParentNode();
		if (parent != null) {
			return parent.getChildCount() - 1;
		}
		return 0;
	}

	@Override
	public boolean isLeaf() {
		return ListUtil.isEmpty(getChildren());
	}


	public void setParentNode(GroupType parent) {
		this.parent = parent;
	}

	public void setChildren(List<GroupType> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "Type: " + getGroupType();
	}

}