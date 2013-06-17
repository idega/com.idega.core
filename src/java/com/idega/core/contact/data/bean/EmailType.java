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
@Table(name = EmailType.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "emailType.findAll", query = "select t from EmailType t"),
	@NamedQuery(name = "emailType.findByUniqueType", query = "select t from EmailType t where t.uniqueName = :uniqueName")
})
public class EmailType extends GenericType implements Serializable {

	public static final String MAIN_EMAIL = "main";

	private static final long serialVersionUID = -2932315377563353027L;

	public static final String ENTITY_NAME = "ic_email_type";
	private static final String COLUMN_EMAIL_TYPE_ID = "ic_email_type_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_EMAIL_TYPE_ID)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}