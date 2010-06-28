/**
 * 
 */
package com.idega.core.file.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = ICMimeType.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "mimetype.findAll", query = "select m from ICMimeType m")
})
public class ICMimeType implements Serializable {

  public static String IC_MIME_TYPE_FOLDER = "application/vnd.iw-folder";
  public static String IC_MIME_TYPE_XML = "text/xml";//for ibxml this should be application/vnd.iw-ibxml"

  private static final long serialVersionUID = -8503656387275969919L;
	
  public static final String ENTITY_NAME = "ic_mime_type";
  private static final String COLUMN_MIME_TYPE = "mime_type";
  private static final String COLUMN_FILE_TYPE = "ic_file_type_id";
  private static final String COLUMN_DESCRIPTION = "description";
  
  @Id
  @Column(name = COLUMN_MIME_TYPE, length = 100, nullable = false)
  private String mimeType;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = COLUMN_FILE_TYPE)
  private ICFileType fileType;
  
  @Column(name = COLUMN_DESCRIPTION)
  private String description;

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @return the fileType
	 */
	public ICFileType getFileType() {
		return this.fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(ICFileType fileType) {
		this.fileType = fileType;
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
}