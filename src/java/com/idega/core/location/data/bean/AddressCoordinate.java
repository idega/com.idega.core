/**
 * 
 */
package com.idega.core.location.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = AddressCoordinate.ENTITY_NAME)
@NamedQuery(name = "addressCoordinate.findByCoordinate", query = "select a from AddressCoordinate a where a.coordinate = :coordinate")
public class AddressCoordinate implements Serializable {

	private static final long serialVersionUID = -5499335400643504803L;

	public static final String ENTITY_NAME = "ic_address_coordinate";
	public static final String COLUMN_ADDRESS_COORDINATE_ID = "ic_address_coordinate_id";
	private static final String COLUMN_COMMUNE = "ic_commune_id";
	private static final String COLUMN_COORDINATE = "coordinate";
	private static final String COLUMN_CODE = "coordinate_code";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ADDRESS_COORDINATE_ID)
	private Integer addressCoordinateID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_COMMUNE)
	private Commune commune;

	@Column(name = COLUMN_COORDINATE, length = 50)
	private String coordinate;

	@Column(name = COLUMN_CODE, length = 10)
	private String code;

	/**
	 * @return the addressCoordinateID
	 */
	public Integer getId() {
		return this.addressCoordinateID;
	}

	/**
	 * @param addressCoordinateID
	 *          the addressCoordinateID to set
	 */
	public void setId(Integer addressCoordinateID) {
		this.addressCoordinateID = addressCoordinateID;
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

	/**
	 * @return the coordinate
	 */
	public String getCoordinate() {
		return this.coordinate;
	}

	/**
	 * @param coordinate
	 *          the coordinate to set
	 */
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
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
}