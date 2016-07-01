package com.idega.core.builder.dao;

import com.idega.core.persistence.GenericDao;

public interface IBPageNameDAO extends GenericDao {

	public String getNameByPageAndLocale(int pageId, int localeId);
	
}
