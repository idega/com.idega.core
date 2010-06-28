/**
 * 
 */
package com.idega.core.contact.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.idega.core.data.bean.GenericType;

@Entity
@Table(name = PhoneType.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "phoneType.findAll", query = "select t from PhoneType t")
})
public class PhoneType extends GenericType implements Serializable {

	public static final String UNIQUE_NAME_HOME_PHONE = "home_phone";
	public static final String UNIQUE_NAME_WORK_PHONE = "work_phone";
	public static final String UNIQUE_NAME_MOBILE_PHONE = "mobile_phone";
	public static final String UNIQUE_NAME_FAX_NUMBER = "fax_number";

	public static final int HOME_PHONE_ID = 1;
	public static final int WORK_PHONE_ID = 2;
	public static final int MOBILE_PHONE_ID = 3;
	public static final int FAX_NUMBER_ID = 4;

	private static final long serialVersionUID = -2932315377563353027L;

	public static final String ENTITY_NAME = "ic_phone_type";
	private static final String COLUMN_PHONE_TYPE_ID = "ic_phone_type_id";

  @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_PHONE_TYPE_ID)
  private Integer id;
  
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
}