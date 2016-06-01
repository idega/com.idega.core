/**
 *
 */
package com.idega.core.file.data.bean;

import java.io.Serializable;

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

import com.idega.util.DBUtil;

@Entity
@Table(name = ICFileType.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "fileType.findAll", query = "select f from ICFileType f")
})
public class ICFileType implements Serializable {

  public static String IC_FILE_TYPE_APPLICATION = "ic_application";
  public static String IC_FILE_TYPE_AUDIO = "ic_audio";
  public static String IC_FILE_TYPE_DOCUMENT = "ic_document";
  public static String IC_FILE_TYPE_IMAGE = "ic_image";
  public static String IC_FILE_TYPE_VECTOR_GRAPHICS = "ic_vector";
  public static String IC_FILE_TYPE_VIDEO = "ic_video";
  public static String IC_FILE_TYPE_SYSTEM = "ic_system";//idegaWeb database file system (type)
  public static String IC_FILE_TYPE_ZIP = "ic_zip";

  private static final long serialVersionUID = 2349990182272588016L;

	public static final String ENTITY_NAME = "ic_file_type";
	public static final String COLUMN_FILE_TYPE_ID = "ic_file_type_id";
  private static final String COLUMN_FILE_TYPE_HANDLER_ID = "ic_file_type_handler_id";
  private static final String COLUMN_DESCRIPTION = "type_description";
  private static final String COLUMN_DISPLAY_NAME = "type_display_name";
  private static final String COLUMN_UNIQUE_NAME = "unique_name";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = COLUMN_FILE_TYPE_ID)
  private Integer fileTypeID;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = COLUMN_FILE_TYPE_HANDLER_ID)
  private ICFileTypeHandler handler;

  @Column(name = COLUMN_DESCRIPTION, length = 500)
  private String description;

  @Column(name = COLUMN_DISPLAY_NAME)
  private String displayName;

  @Column(name = COLUMN_UNIQUE_NAME, unique = true)
  private String uniqueName;

	/**
	 * @return the fileTypeHandlerID
	 */
	public Integer getId() {
		return this.fileTypeID;
	}

	/**
	 * @param fileTypeHandlerID the fileTypeHandlerID to set
	 */
	public void setId(Integer fileTypeID) {
		this.fileTypeID = fileTypeID;
	}

	/**
	 * @return the handler
	 */
	public ICFileTypeHandler getHandler() {
		handler = DBUtil.getInstance().lazyLoad(handler);
		return this.handler;
	}

	/**
	 * @param handler the handler to set
	 */
	public void setHandler(ICFileTypeHandler handler) {
		this.handler = handler;
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

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the uniqueName
	 */
	public String getUniqueName() {
		return this.uniqueName;
	}

	/**
	 * @param uniqueName the uniqueName to set
	 */
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
}