/**
 * 
 */
package com.idega.core.location.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.idega.user.data.bean.Group;

@Entity
@Cacheable
@Table(name = Commune.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "commune.findAll", query = "select c from Commune c order by c.name"),
	@NamedQuery(name = "commune.findByName", query = "select c from Commune c where c.commune = :commune"),
	@NamedQuery(name = "commune.findByCode", query = "select c from Commune c where c.code = :code"),
	@NamedQuery(name = "commune.findDefaultCommune", query = "select c from Commune c where c.isDefault = 'Y'")
})
public class Commune implements Serializable {

	private static final long serialVersionUID = 4464875032665460713L;

	public static final String ENTITY_NAME = "ic_commune";
	public static final String COLUMN_COMMUNE_ID = "ic_commune_id";
	private static final String COLUMN_NAME = "commune_name";
	private static final String COLUMN_COMMUNE = "commune";
	private static final String COLUMN_CODE = "commune_code";
	private static final String COLUMN_WEB_URL = "web_url";
	private static final String COLUMN_DEFAULT = "default_commune";
	private static final String COLUMN_VALID = "is_valid";
	private static final String COLUMN_PROVINCE_ID = "ic_province_id";
	private static final String COLUMN_GROUP_ID = "ic_group_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = Commune.COLUMN_COMMUNE_ID)
	private Integer communeID;

	@Column(name = Commune.COLUMN_NAME, length = 50)
	private String name;

	@Column(name = Commune.COLUMN_COMMUNE, length = 50)
	private String commune;

	@Column(name = Commune.COLUMN_CODE, length = 20)
	private String code;

	@Column(name = Commune.COLUMN_WEB_URL, length = 255)
	private String websiteURL;

	@Column(name = Commune.COLUMN_DEFAULT, length = 1, nullable = false)
	private Character isDefault;

	@Column(name = Commune.COLUMN_VALID, length = 1, nullable = false)
	private Character isValid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Commune.COLUMN_PROVINCE_ID)
	private Province province;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Commune.COLUMN_GROUP_ID)
	private Group group;

	public Commune() {
		setValid(true);
	}
	
	/**
	 * @return the communeID
	 */
	public Integer getId() {
		return this.communeID;
	}

	/**
	 * @param communeID
	 *          the communeID to set
	 */
	public void setId(Integer communeID) {
		this.communeID = communeID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the commune
	 */
	public String getCommune() {
		return this.commune;
	}

	/**
	 * @param commune
	 *          the commune to set
	 */
	public void setCommune(String commune) {
		this.commune = commune;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * @param code
	 *          the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the websiteURL
	 */
	public String getWebsiteURL() {
		return this.websiteURL;
	}

	/**
	 * @param websiteURL
	 *          the websiteURL to set
	 */
	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}

	/**
	 * @return the isDefault
	 */
	public boolean isDefault() {
		if (this.isDefault == null) {
			return false;
		}
		return this.isDefault == 'Y' ? true : false;
	}

	/**
	 * @param isDefault
	 *          the isDefault to set
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault ? 'Y' : 'N';
	}

	/**
	 * @return the isValid
	 */
	public boolean isValid() {
		if (this.isValid == null) {
			return false;
		}
		return this.isValid == 'Y' ? true : false;
	}

	/**
	 * @param isValid
	 *          the isValid to set
	 */
	public void setValid(boolean isValid) {
		this.isValid = isValid ? 'Y' : 'N';
	}

	/**
	 * @return the province
	 */
	public Province getProvince() {
		return this.province;
	}

	/**
	 * @param province
	 *          the province to set
	 */
	public void setProvince(Province province) {
		this.province = province;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group
	 *          the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
}