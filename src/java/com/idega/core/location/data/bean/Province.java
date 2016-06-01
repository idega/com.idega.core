/**
 *
 */
package com.idega.core.location.data.bean;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.idega.util.DBUtil;

@Entity
@Cacheable
@Table(name=Province.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name="province.findAll", query="select p from Province p"),
	@NamedQuery(name="province.findAllByCountry", query="select p from Province p where p.country = :country"),
	@NamedQuery(name="province.findByNameAndCountry", query="select p from Province p where p.name = :name and p.country = :country")
})
public class Province implements Serializable {

	private static final long serialVersionUID = -3785768771474518686L;

	public static final String ENTITY_NAME = "ic_province";
	public static final String COLUMN_PROVINCE_ID = "ic_province_id";
	private static final String COLUMN_NAME = "province_name";
	private static final String COLUMN_COUNTRY_ID = "ic_country_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name=Province.COLUMN_PROVINCE_ID)
	private Integer provinceID;

	@Column(name=Province.COLUMN_NAME, length = 50)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = Province.COLUMN_COUNTRY_ID)
	private Country country;

	@OneToMany(mappedBy="province", cascade = { CascadeType.REMOVE })
	@OrderBy("name")
	private List<Commune> communes;

	/**
	 * @return the provinceID
	 */
	public Integer getId() {
		return this.provinceID;
	}

	/**
	 * @param provinceID the provinceID to set
	 */
	public void setId(Integer provinceID) {
		this.provinceID = provinceID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		country = DBUtil.getInstance().lazyLoad(country);
		return this.country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the communes
	 */
	public List<Commune> getCommunes() {
		return this.communes;
	}
}