/**
 * 
 */
package com.idega.core.location.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = Street.ENTITY_NAME)
@NamedQuery(name = "street.findByPostalCodeAndName", query = "select s from Street s where s.postalCode = :postalCode and (s.name = :name or s.nameDativ = :nameDativ)")
public class Street implements Serializable {

	private static final long serialVersionUID = 3514012128676068798L;

	public static final String ENTITY_NAME = "ic_street";
	public static final String COLUMN_STREET_ID = "ic_street_id";
	private static final String COLUMN_NAME = "street_name";
	private static final String COLUMN_NAME_DATIV = "name_dativ";
	private static final String COLUMN_POSTAL_CODE = "postal_code_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_STREET_ID)
	private Integer streetID;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_NAME_DATIV)
	private String nameDativ;

	@ManyToOne
	@JoinColumn(name = COLUMN_POSTAL_CODE)
	private PostalCode postalCode;

	/**
	 * @return the streetID
	 */
	public Integer getId() {
		return this.streetID;
	}

	/**
	 * @param streetID
	 *          the streetID to set
	 */
	public void setId(Integer streetID) {
		this.streetID = streetID;
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
	 * @return the nameDativ
	 */
	public String getNameDativ() {
		return this.nameDativ;
	}

	/**
	 * @param nameDativ
	 *          the nameDativ to set
	 */
	public void setNameDativ(String nameDativ) {
		this.nameDativ = nameDativ;
	}

	/**
	 * @return the postalCode
	 */
	public PostalCode getPostalCode() {
		return this.postalCode;
	}

	/**
	 * @param postalCode
	 *          the postalCode to set
	 */
	public void setPostalCode(PostalCode postalCode) {
		this.postalCode = postalCode;
	}
}