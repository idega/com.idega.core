/**
 * 
 */
package com.idega.core.location.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
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
@Cacheable
@Table(name = AddressType.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "addressType.findByUniqueName", query = "select at from AddressType at where at.uniqueName = :uniqueName")
})
public class AddressType extends GenericType implements Serializable {

	private static final long serialVersionUID = 2953309121346913457L;

  public final static String MAIN_ADDRESS_TYPE = "ic_user_address_1";
  public final static String CO_ADDRESS_TYPE = "ic_user_address_2";

	public static final String ENTITY_NAME = "ic_address_type";
	private static final String COLUMN_ADDRESS_TYPE_ID = "ic_address_type_id";

  @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ADDRESS_TYPE_ID)
  private Integer id;
  
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
}