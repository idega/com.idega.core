package com.idega.repository.data;

import com.idega.core.file.data.ICFile;
import com.idega.data.IDOEntity;

public interface RepositoryFile extends IDOEntity, ICFile {

	public String getExternalURL();
	
	public void setExternalURL(String url);
	
}