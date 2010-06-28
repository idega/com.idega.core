/**
 * 
 */
package com.idega.core.file.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = ICFileTypeHandler.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "fileTypeHandler.findAll", query = "select f from ICFileTypeHandler f")
})
public class ICFileTypeHandler implements Serializable {

	private static final long serialVersionUID = -5329234969659856300L;

	public static final String ENTITY_NAME = "ic_file_type_handler";
	public static final String COLUMN_FILE_TYPE_HANDLER_ID = "ic_file_type_handler_id";
	private static final String COLUMN_TYPE_HANDLER_CLASS = "type_handler_class";
	private static final String COLUMN_TYPE_HANDLER_NAME = "type_handler_name";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_FILE_TYPE_HANDLER_ID)
	private Integer fileTypeHandlerID;
	
	@Column(name = COLUMN_TYPE_HANDLER_NAME)
	private String name;
	
	@Column(name = COLUMN_TYPE_HANDLER_CLASS, length = 500)
	private String handlerClass;
	
	/**
	 * @return the fileTypeHandlerID
	 */
	public Integer getId() {
		return this.fileTypeHandlerID;
	}
	
	/**
	 * @param fileTypeHandlerID the fileTypeHandlerID to set
	 */
	public void setId(Integer fileTypeHandlerID) {
		this.fileTypeHandlerID = fileTypeHandlerID;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the handlerClass
	 */
	public String getHandlerClass() {
		return this.handlerClass;
	}
	
	/**
	 * @param handlerClass the handlerClass to set
	 */
	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
	}

	/**
	 * @param theClass
	 */
	public void setHandlerClass(Class theClass) {
		setHandlerClass(theClass.getName());
	}

	/**
	 * @param name
	 * @param classString
	 */
	public void setNameAndHandlerClass(String name, String classString) {
		setName(name);
		setHandlerClass(classString);
	}
	
	/**
	 * @param name
	 * @param theClass
	 */
	public void setNameAndHandlerClass(String name, Class theClass) {
		setName(name);
		setHandlerClass(theClass);
	}
}