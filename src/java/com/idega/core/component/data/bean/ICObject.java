/**
 *
 */
package com.idega.core.component.data.bean;

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

import com.idega.core.file.data.bean.ICFile;

@Entity
@Table(name = ICObject.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "object.findAll", query = "select o from ICObject o"),
	@NamedQuery(name = "object.findAllByBundle", query = "select o from ICObject o where o.bundleName = :bundle"),
	@NamedQuery(name = "object.findAllByType", query = "select o from ICObject o where o.type = :type"),
	@NamedQuery(name = "object.findAllByBundleAndType", query = "select o from ICObject o where o.bundleName = :bundle and o.type = :type"),
	@NamedQuery(name = "object.findAllByClass", query = "select o from ICObject o where o.className = :className")
})
@Cacheable
public class ICObject implements Serializable {

	private static final long serialVersionUID = -896325302059689975L;

	public static final String COMPONENT_TYPE_ELEMENT = "iw.element";
	public static final String COMPONENT_TYPE_BLOCK = "iw.block";
	public static final String COMPONENT_TYPE_APPLICATION = "iw.application";
	public static final String COMPONENT_TYPE_APPLICATION_COMPONENT = "iw.application.component";
	public static final String COMPONENT_TYPE_DATA = "iw.data";
	public static final String COMPONENT_TYPE_HOME = "iw.home";
	public static final String COMPONENT_TYPE_PROPERTYHANDLER = "iw.propertyhandler";
	public static final String COMPONENT_TYPE_INPUTHANDLER = "iw.inputhandler";
	public static final String COMPONENT_TYPE_SEARCH_PLUGIN = "iw.searchplugin";
	public static final String COMPONENT_TYPE_JSFUICOMPONENT = "jsf.uicomponent";
	public static final String COMPONENT_TYPE_USER_PLUGIN = "iw.plugin.user";

	public static final String ENTITY_NAME = "ic_object";
	public static final String COLUMN_OBJECT_ID = "ic_object_id";
	private static final String COLUMN_OBJECT_TYPE = "object_type";
	private static final String COLUMN_CLASS_NAME = "class_name";
	private final static String COLUMN_CLASS_VALUE = "class_value";
	private final static String COLUMN_BUNDLE_NAME = "bundle";
	private final static String COLUMN_ICON_FILE = "icon_file";
	private final static String COLUMN_ICON_URI = "icon_uri";
	private static final String COLUMN_OBJECT_NAME = "object_name";
	private static final String COLUMN_WIDGET = "widget";
	private static final String COLUMN_BLOCK = "block";
	private static final String COLUMN_DESCRIPTION = "description";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_OBJECT_ID)
	private Integer objectID;

	@Column(name = COLUMN_OBJECT_NAME)
	private String name;

	@ManyToOne
	@JoinColumn(name = COLUMN_OBJECT_TYPE)
	private ICObjectType type;

	@Column(name = COLUMN_CLASS_NAME)
	private String className;

	@ManyToOne
	@JoinColumn(name = COLUMN_CLASS_VALUE)
	private ICFile classValue;

	@Column(name = COLUMN_BUNDLE_NAME, length = 1000)
	private String bundleName;

	@ManyToOne
	@JoinColumn(name = COLUMN_ICON_FILE)
	private ICFile iconFile;

	@Column(name = COLUMN_ICON_URI)
	private String iconURI;

	@Column(name = COLUMN_WIDGET, length = 1)
	private Character isWidget;

	@Column(name = COLUMN_BLOCK, length = 1)
	private Character isBlock;

	@Column(name = COLUMN_DESCRIPTION, length = 4000)
	private String description;

	/**
	 * @return the objectID
	 */
	public Integer getId() {
		return this.objectID;
	}

	/**
	 * @param objectID
	 *          the objectID to set
	 */
	public void setId(Integer objectID) {
		this.objectID = objectID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public ICObjectType getType() {
		return this.type;
	}

	/**
	 * @param type
	 *          the type to set
	 */
	public void setType(ICObjectType type) {
		this.type = type;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * @param className
	 *          the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the classValue
	 */
	public ICFile getClassValue() {
		return this.classValue;
	}

	/**
	 * @param classValue
	 *          the classValue to set
	 */
	public void setClassValue(ICFile classValue) {
		this.classValue = classValue;
	}

	/**
	 * @return the bundleName
	 */
	public String getBundleName() {
		return this.bundleName;
	}

	/**
	 * @param bundleName
	 *          the bundleName to set
	 */
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	/**
	 * @return the iconFile
	 */
	public ICFile getIconFile() {
		return this.iconFile;
	}

	/**
	 * @param iconFile
	 *          the iconFile to set
	 */
	public void setIconFile(ICFile iconFile) {
		this.iconFile = iconFile;
	}

	/**
	 * @return the iconURI
	 */
	public String getIconURI() {
		return this.iconURI;
	}

	/**
	 * @param iconURI
	 *          the iconURI to set
	 */
	public void setIconURI(String iconURI) {
		this.iconURI = iconURI;
	}

	/**
	 * @return the isWidget
	 */
	public boolean isWidget() {
		if (this.isWidget == null) {
			return false;
		}
		return this.isWidget == 'Y';
	}

	/**
	 * @param isWidget
	 *          the isWidget to set
	 */
	public void setWidget(boolean isWidget) {
		this.isWidget = isWidget ? 'Y' : 'N';
	}

	/**
	 * @return the isBlock
	 */
	public boolean isBlock() {
		if (this.isBlock == null) {
			return false;
		}
		return this.isBlock == 'Y';
	}

	/**
	 * @param isBlock
	 *          the isBlock to set
	 */
	public void setBlock(boolean isBlock) {
		this.isBlock = isBlock ? 'Y' : 'N';
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *          the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}