/**
 * 
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = GroupEvent.ENTITY_NAME)
public class GroupEvent implements Serializable {

	private static final long serialVersionUID = 5935195065979066119L;

	public static final String ENTITY_NAME = "ic_group_event";
	public static final String COLUMN_GROUP_EVENT_ID = "ic_group_event_id";
	private static final String COLUMN_GROUP = "ic_group_id";
	private static final String COLUMN_EVENT_TYPE = "event_type";
	private static final String COLUMN_DATE_OCCURED = "date_occured";
	private static final String COLUMN_DATE_REGISTERED = "date_registered";
	private static final String COLUMN_DESCRIPTION = "event_description";
	private static final String COLUMN_REGISTERED_BY_GROUP = "registered_by_group_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_GROUP_EVENT_ID)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_GROUP)
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_EVENT_TYPE)
	private GroupEventType eventType;

	@Temporal(TemporalType.DATE)
	@Column(name = COLUMN_DATE_OCCURED)
	private Date dateOccured;

	@Temporal(TemporalType.DATE)
	@Column(name = COLUMN_DATE_REGISTERED)
	private Date dateRegistered;

	@Column(name = COLUMN_DESCRIPTION, length = 1000)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_REGISTERED_BY_GROUP)
	private Group registrant;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public GroupEventType getEventType() {
		return this.eventType;
	}

	public void setEventType(GroupEventType eventType) {
		this.eventType = eventType;
	}

	public Date getDateOccured() {
		return this.dateOccured;
	}

	public void setDateOccured(Date dateOccured) {
		this.dateOccured = dateOccured;
	}

	public Date getDateRegistered() {
		return this.dateRegistered;
	}

	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Group getRegistrant() {
		return this.registrant;
	}

	public void setRegistrant(Group registrant) {
		this.registrant = registrant;
	}

}