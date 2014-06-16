/*
 * $Id: ICPageDAOImpl.java 1.1 Sep 22, 2009 laddi Exp $
 * Created on Sep 22, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.dao.impl;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.builder.dao.ICPageDAO;
import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.idegaweb.IWMainApplication;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("icPageDAO")
@Transactional(readOnly = true)
public class ICPageDAOImpl extends GenericDaoImpl implements ICPageDAO {

	@Override
	public ICPage findPage(Integer pageID) {
		return find(ICPage.class, pageID);
	}

	@Override
	public boolean isPagePublished(Integer pageId) {
		if (pageId == null) {
			return false;
		}

		IWCacheManager2 cacheManager = IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication());
		Map<Integer, Boolean> cache = cacheManager.getCache("ic_page_is_published_cache", 1000000, true, false, 604800, 604800);
		if (cache.containsKey(pageId)) {
			return cache.get(pageId);
		}

		ICPage page = findPage(pageId);
		Boolean published = page != null && page.isPublished() ? Boolean.TRUE : Boolean.FALSE;
		cache.put(pageId, published);
		return published;
	}

}