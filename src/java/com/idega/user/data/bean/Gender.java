/**
 * 
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name=Gender.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name="gender.findAll", query="select g from Gender g"),
	@NamedQuery(name="gender.findByName", query="select g from Gender g where name like :name")
})
public class Gender implements Serializable {

  public static final String NAME_MALE = "male";
  public static final String NAME_FEMALE = "female";

  private static final long serialVersionUID = 7539272096080771536L;

	public static final String ENTITY_NAME = "ic_gender";
	public static final String COLUMN_GENDER_ID = "ic_gender_id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_DESCRIPTION = "description";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = Gender.COLUMN_GENDER_ID)
	private Integer genderID;

	@Column(name = Gender.COLUMN_NAME)
	private String name;
	
	@Column(name = Gender.COLUMN_DESCRIPTION, length = 1000)
	private String description;
	
	/**
	 * @return the genderID
	 */
	public Integer getId() {
		return this.genderID;
	}

	/**
	 * @param genderID the genderID to set
	 */
	public void setId(Integer genderID) {
		this.genderID = genderID;
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
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isFemaleGender() {
		return getName().equals(NAME_FEMALE);
	}
	
	public boolean isMaleGender() {
		return getName().equals(NAME_MALE);
	}
}