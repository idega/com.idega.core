/**
 * 
 */
package com.idega.core.component.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.data.UniqueIDCapable;

@Entity
@Table(name = ICObjectInstance.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "objectInstance.findByPageKey", query = "select o from ICObjectInstance o where o.page = :page"),
	@NamedQuery(name = "objectInstance.findByUniqueID", query = "select o from ICObjectInstance o where o.uniqueID = :uniqueID")
})
public class ICObjectInstance implements Serializable, UniqueIDCapable {

	private static final long serialVersionUID = -2961011967547708851L;

	public static final String ENTITY_NAME = "ic_object_instance";
	public static final String COLUMN_OBJECT_INSTANCE_ID = "ic_object_instance_id";
	private static final String COLUMN_OBJECT = "ic_object_id";
	private static final String COLUMN_PAGE = "ib_page_id";
	private static final String COLUMN_PARENT = "dpt_parent_id";
	private static final String COLUMN_UNIQUE_ID = "unique_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_OBJECT_INSTANCE_ID)
	private Integer objectInstanceID;

	@ManyToOne
	@JoinColumn(name = COLUMN_OBJECT)
	private ICObject object;

	@ManyToOne
	@JoinColumn(name = COLUMN_PAGE)
	private ICPage page;

	@ManyToOne
	@JoinColumn(name = COLUMN_PARENT)
	private ICObjectInstance parent;

	@Column(name = COLUMN_UNIQUE_ID, length = 36, nullable = false, unique = true)
	private String uniqueID;

	@PrePersist
	@PreUpdate
	public void setDefaultValues() {
		if (getUniqueId() == null) {
			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			setUniqueId(uidGenerator.generateId());
		}
	}

	/**
	 * @return the objectInstanceID
	 */
	public Integer getId() {
		return this.objectInstanceID;
	}

	/**
	 * @param objectInstanceID
	 *          the objectInstanceID to set
	 */
	public void setId(Integer objectInstanceID) {
		this.objectInstanceID = objectInstanceID;
	}

	/**
	 * @return the object
	 */
	public ICObject getObject() {
		return this.object;
	}

	/**
	 * @param object
	 *          the object to set
	 */
	public void setObject(ICObject object) {
		this.object = object;
	}

	/**
	 * @return the page
	 */
	public ICPage getPage() {
		return this.page;
	}

	/**
	 * @param page
	 *          the page to set
	 */
	public void setPage(ICPage page) {
		this.page = page;
	}

	/**
	 * @return the parent
	 */
	public ICObjectInstance getParent() {
		return this.parent;
	}

	/**
	 * @param parent
	 *          the parent to set
	 */
	public void setParent(ICObjectInstance parent) {
		this.parent = parent;
	}

	/**
	 * @return the uniqueID
	 */
	public String getUniqueId() {
		return this.uniqueID;
	}

	/**
	 * @param uniqueID
	 *          the uniqueID to set
	 */
	public void setUniqueId(String uniqueID) {
		this.uniqueID = uniqueID;
	}
}