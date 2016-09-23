package com.idega.core.dao;

import java.util.List;

import com.idega.core.file.data.bean.ICFile;
import com.idega.core.persistence.GenericDao;

public interface ICFileDAO extends GenericDao {

	public static final String BEAN_NAME = "icFileDAO";
	
	public ICFile findById(Integer id);
	
	public List<ICFile> findAll();
	
	public boolean removeFile(Integer id);
	
}
