package com.idega.core.location.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data structure to capture coordinates: latitude (Double), longitude (Double), radius (Double)
 *
 * @author valdas
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Cacheable
@Table(name = Location.TABLE_NAME)
public class Location implements Serializable {

	private static final long serialVersionUID = 9117437952488671244L;

	public static final String TABLE_NAME = "ic_location";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "latitude")
	private Double lat;

	@Column(name = "longitude")
	private Double lon;

	@Column(name = "radius")
	private Double radius;

	public Location() {
		super();
	}

	public Location(Double lat, Double lon) {
		this();

		this.lat = lat;
		this.lon = lon;
	}

	public Location(Double latitude, Double longitude, Double radius) {
		this(latitude, longitude);

		this.radius = radius;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	public Double getLatitude() {
		return lat == null ? -91 : lat;
	}

	public void setLatitude(Double latitude) {
		this.lat = latitude;
	}

	public Double getLongitude() {
		return lon == null ? -181 : lon;
	}

	public void setLongitude(Double longitude) {
		this.lon = longitude;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Latitude: " + getLatitude() + ", longitude: " + getLongitude() + ", radius: " + getRadius();
	}
}
