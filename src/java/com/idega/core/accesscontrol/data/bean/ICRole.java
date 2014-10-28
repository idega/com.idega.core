/**
 *
 */
package com.idega.core.accesscontrol.data.bean;

import java.io.Serializable;
import java.util.List;

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

@Entity
@Table(name = ICRole.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "role.findAll", query = "select r from ICRole r")
})
@Cacheable
public class ICRole implements Serializable {

	private static final long serialVersionUID = -6349961116652729409L;

	public static final String ENTITY_NAME = "ic_perm_role";
	public static final String COLUMN_ROLE_KEY = "role_key";
	private static final String COLUMN_ROLE_DESCRIPTION_LOCALIZABLE_KEY = "desc_loc_key";
	private static final String COLUMN_ROLE_NAME_LOCALIZABLE_KEY = "name_loc_key";

	private static final String TREE_TABLE_NAME = ENTITY_NAME + "_tree";

	public static final int ROLE_KEY_MAX_LENGTH = 50;

	@Id
	@Column(name = COLUMN_ROLE_KEY, length = ROLE_KEY_MAX_LENGTH)
	private String roleKey;

	@Column(name = COLUMN_ROLE_DESCRIPTION_LOCALIZABLE_KEY)
	private String roleDescriptionLocalizableKey;

	@Column(name = COLUMN_ROLE_NAME_LOCALIZABLE_KEY)
	private String roleNameLocalizableKey;

	@ManyToOne(optional = true)
	@JoinTable(name = TREE_TABLE_NAME, joinColumns = { @JoinColumn(name = "child_" + COLUMN_ROLE_KEY, referencedColumnName = COLUMN_ROLE_KEY) }, inverseJoinColumns = { @JoinColumn(name = COLUMN_ROLE_KEY) })
	private ICRole parent;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ICRole.class)
	@JoinTable(name = TREE_TABLE_NAME, joinColumns = { @JoinColumn(name = COLUMN_ROLE_KEY) }, inverseJoinColumns = { @JoinColumn(name = "child_" + COLUMN_ROLE_KEY, referencedColumnName = COLUMN_ROLE_KEY) })
	private List<ICRole> children;

	/**
	 * @return the roleKey
	 */
	public String getRoleKey() {
		return this.roleKey;
	}

	/**
	 * @param roleKey
	 *          the roleKey to set
	 */
	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}

	/**
	 * @return the roleDescriptionLocalizableKey
	 */
	public String getRoleDescriptionLocalizableKey() {
		return this.roleDescriptionLocalizableKey;
	}

	/**
	 * @param roleDescriptionLocalizableKey
	 *          the roleDescriptionLocalizableKey to set
	 */
	public void setRoleDescriptionLocalizableKey(String roleDescriptionLocalizableKey) {
		this.roleDescriptionLocalizableKey = roleDescriptionLocalizableKey;
	}

	/**
	 * @return the roleNameLocalizableKey
	 */
	public String getRoleNameLocalizableKey() {
		return this.roleNameLocalizableKey;
	}

	/**
	 * @param roleNameLocalizableKey
	 *          the roleNameLocalizableKey to set
	 */
	public void setRoleNameLocalizableKey(String roleNameLocalizableKey) {
		this.roleNameLocalizableKey = roleNameLocalizableKey;
	}

	public List<ICRole> getChildren() {
		return this.children;
	}

	public void setChildren(List<ICRole> children) {
		this.children = children;
	}

	public ICRole getParent() {
		return this.parent;
	}

	public void setParent(ICRole parent) {
		this.parent = parent;
	}
}