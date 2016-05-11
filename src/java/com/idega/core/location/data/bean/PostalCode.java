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
import javax.persistence.Table;

@Entity
@Cacheable
@Table(name = PostalCode.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "postalCode.findAll", query = "select p from PostalCode p order by p.postalCode"),
	@NamedQuery(name = "postalCode.findAllByCountry", query = "select p from PostalCode p where p.country = :country order by p.postalCode"),
	@NamedQuery(name = "postalCode.findByPostalCode", query = "select p from PostalCode p where p.postalCode = :postalCode"),
	@NamedQuery(name = PostalCode.QUERY_FIND_BY_ADDRESS, 
		query =		"SELECT DISTINCT p FROM PostalCode p "
				+	"JOIN p.addresses a ON a.id = :id")
})
public class PostalCode implements Serializable {

	private static final long serialVersionUID = -7559508364224794919L;

	public static final String ENTITY_NAME = "ic_postal_code";
	public static final String COLUMN_POSTAL_CODE_ID = "ic_postal_code_id";
	private static final String COLUMN_POSTAL_CODE = "postal_code";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_POSTAL_ADDRESS = "postal_address";
	private static final String COLUMN_COUNTRY_ID = "ic_country_id";
	private static final String COLUMN_COMMUNE_ID = "ic_commune_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_POSTAL_CODE_ID)
	private Integer postalCodeID;

	@Column(name = COLUMN_POSTAL_CODE, length = 50)
	private String postalCode;

	@Column(name = COLUMN_NAME, length = 50)
	private String name;

	@Column(name = COLUMN_POSTAL_ADDRESS, length = 50)
	private String postalAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_COUNTRY_ID)
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_COMMUNE_ID)
	private Commune commune;

	public static final String QUERY_FIND_BY_ADDRESS = "postalCode.findByAddress";
	public static final String addressesProp = "addresses";
	@OneToMany(
			mappedBy = "postalCode", 
			fetch = FetchType.LAZY,
			orphanRemoval = false,
			cascade = {
					CascadeType.PERSIST, 
					CascadeType.MERGE,
					CascadeType.DETACH,
					CascadeType.REFRESH})
	private List<Address> addresses;

	/**
	 * @return the postalCodeID
	 */
	public Integer getId() {
		return this.postalCodeID;
	}

	/**
	 * @param postalCodeID
	 *          the postalCodeID to set
	 */
	public void setId(Integer postalCodeID) {
		this.postalCodeID = postalCodeID;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return this.postalCode;
	}

	/**
	 * @param postalCode
	 *          the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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
	 * @return the postalAddress
	 */
	public String getPostalAddress() {
		return this.postalAddress;
	}

	/**
	 * @param postalAddress
	 *          the postalAddress to set
	 */
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return this.country;
	}

	/**
	 * @param country
	 *          the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the commune
	 */
	public Commune getCommune() {
		return this.commune;
	}

	/**
	 * @param commune
	 *          the commune to set
	 */
	public void setCommune(Commune commune) {
		this.commune = commune;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
}