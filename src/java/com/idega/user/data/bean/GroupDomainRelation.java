/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.RemoveException;
import javax.persistence.Cacheable;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.core.builder.data.bean.ICDomain;
import com.idega.util.DBUtil;
import com.idega.util.IWTimestamp;

@Entity
@Table(name = GroupDomainRelation.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "groupDomainRelation.findAllByDomain", query = "select gdr from GroupDomainRelation gdr where gdr.domain = :domain and gdr.status is null"),
	@NamedQuery(name = "groupDomainRelation.findAllByDomainAndType", query = "select gdr from GroupDomainRelation gdr where gdr.domain = :domain and gdr.relationship = :type and gdr.status is null"),
	@NamedQuery(name = "groupDomainRelation.findAllByGroup", query = "select gdr from GroupDomainRelation gdr where gdr.relatedGroup = :group and gdr.status is null"),
	@NamedQuery(name = "groupDomainRelation.findAllByGroupAndType", query = "select gdr from GroupDomainRelation gdr where gdr.relatedGroup = :group and gdr.relationship = :type and gdr.status is null"),
	@NamedQuery(name = "groupDomainRelation.findAllByDomainAndGroup", query = "select gdr from GroupDomainRelation gdr where gdr.domain = :domain and gdr.relatedGroup = :group and gdr.status is null")
})
@Cacheable
public class GroupDomainRelation implements Serializable {

	private static final long serialVersionUID = 5391461126198416175L;

	public static final String ENTITY_NAME = "ic_group_domain_relation";
	public static final String COLUMN_GROUP_DOMAIN_RELATION_ID = "ic_group_domain_relation_id";
	private static final String COLUMN_DOMAIN = "ib_domain_id";
	private static final String COLUMN_RELATED_GROUP = "related_ic_group_id";
	private static final String COLUMN_RELATIONSHIP_TYPE = "relationship_type";
	private static final String COLUMN_STATUS = "group_relation_status";
	private static final String COLUMN_INITIATION_DATE = "initiation_date";
	private static final String COLUMN_TERMINATION_DATE = "termination_date";
	private static final String COLUMN_PASSIVE_BY = "set_passive_by";

	private static final String STATUS_PASSIVE = "ST_PASSIVE";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_GROUP_DOMAIN_RELATION_ID)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_DOMAIN)
	private ICDomain domain;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_RELATED_GROUP)
	private Group relatedGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_RELATIONSHIP_TYPE)
	private GroupDomainRelationType relationship;

	@Column(name = COLUMN_STATUS)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_INITIATION_DATE)
	private Date initiationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_TERMINATION_DATE)
	private Date terminationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_PASSIVE_BY)
	private User passiveBy;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ICDomain getDomain() {
		domain = DBUtil.getInstance().lazyLoad(domain);
		return this.domain;
	}

	public void setDomain(ICDomain domain) {
		this.domain = domain;
	}

	public Group getRelatedGroup() {
		relatedGroup = DBUtil.getInstance().lazyLoad(relatedGroup);
		return this.relatedGroup;
	}

	public void setRelatedGroup(Group relatedGroup) {
		this.relatedGroup = relatedGroup;
	}

	public GroupDomainRelationType getRelationship() {
		relationship = DBUtil.getInstance().lazyLoad(relationship);
		return this.relationship;
	}

	public void setRelationship(GroupDomainRelationType relationship) {
		this.relationship = relationship;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getInitiationDate() {
		return this.initiationDate;
	}

	public void setInitiationDate(Date initiationDate) {
		this.initiationDate = initiationDate;
	}

	public Date getTerminationDate() {
		return this.terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public User getPassiveBy() {
		passiveBy = DBUtil.getInstance().lazyLoad(passiveBy);
		return this.passiveBy;
	}

	public void setPassiveBy(User passiveBy) {
		this.passiveBy = passiveBy;
	}

	public void removeBy(User user) throws RemoveException {
		setStatus(STATUS_PASSIVE);
		setTerminationDate(IWTimestamp.getTimestampRightNow());
		setPassiveBy(user);
	}
}