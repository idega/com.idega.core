/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = Status.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = Status.QUERY_FIND_ALL, query = "select s from Status s"),
	@NamedQuery(name = "status.findByStatusKey", query = "select s from Status s where s.statusKey = :statusKey"),
	@NamedQuery(name = Status.QUERY_FIND_BY_STATUS_KEYS, query = "select s from Status s where s.statusKey in (:statusKey)")
})
@Cacheable
public class Status implements Serializable {

	private static final long serialVersionUID = 8886349570227448043L;

	public static final String	ENTITY_NAME = "ic_user_status",
								COLUMN_STATUS_ID = "ic_user_status_id",
								QUERY_FIND_ALL = "status.findAll",
								QUERY_FIND_BY_STATUS_KEYS = "status.findByStatusKeys";

	private static final String COLUMN_STATUS_KEY = "status_key";
	private static final String COLUMN_STATUS_ORDER = "status_order";
	private static final String COLUMN_PARENT = "parent_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_STATUS_ID)
	private Integer statusID;

	@Column(name = COLUMN_STATUS_KEY)
	private String statusKey;

	@Column(name = COLUMN_STATUS_ORDER)
	private Integer statusOrder;

	@ManyToOne
	@JoinColumn(name = COLUMN_PARENT)
	private Status parent;


	public Integer getId() {
		return this.statusID;
	}


	public void setId(Integer statusID) {
		this.statusID = statusID;
	}


	public String getStatusKey() {
		return this.statusKey;
	}


	public void setStatusKey(String statusKey) {
		this.statusKey = statusKey;
	}


	public Integer getStatusOrder() {
		return this.statusOrder;
	}


	public void setStatusOrder(Integer statusOrder) {
		this.statusOrder = statusOrder;
	}


	public Status getParent() {
		return this.parent;
	}


	public void setParent(Status parent) {
		this.parent = parent;
	}
}