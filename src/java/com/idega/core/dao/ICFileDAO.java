package com.idega.core.dao;

import java.util.Date;
import java.util.List;

import com.idega.core.file.data.bean.ICFile;
import com.idega.core.file.data.bean.ICMimeType;
import com.idega.core.persistence.GenericDao;

public interface ICFileDAO extends GenericDao {

	public static final String BEAN_NAME = "icFileDAO";

	/**
	 * 
	 * <p>Updates or creates file</p>
	 * @param file to update/create, not <code>null</code>
	 * @return {@link ICFile} or <code>null</code> on failure;
	 */
	public ICFile update(ICFile file);

	/**
	 * 
	 * <p>Updates or creates file</p>
	 * @param id is {@link ICFile#getId()}
	 * @param name is {@link ICFile#getName()}
	 * @param description is {@link ICFile#getDescription()}
	 * @param fileValue is {@link ICFile#getFileValue()}
	 * @param creationDate is {@link ICFile#getCreationDate()}
	 * @param modificationDate is {@link ICFile#getModificationDate()}
	 * @param fileSize is {@link ICFile#getFileSize()}
	 * @param type TODO
	 * @return entity or <code>null</code> on failure;
	 */
	public ICFile update(
			Integer id,
			String name, 
			String description, 
			byte[] fileValue, 
			Date creationDate, 
			Date modificationDate, 
			Integer fileSize, 
			ICMimeType type);
	
	public ICFile findById(Integer id);
	
	public List<ICFile> findAll();
	
	public boolean removeFile(Integer id);
	
}
