package com.idega.core.cache;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.builder.dao.ICPageDAO;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.core.business.DefaultSpringBean;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.RepositoryStartedEvent;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class PagesCacher extends DefaultSpringBean implements ApplicationListener<RepositoryStartedEvent> {

	@Autowired
	private ICPageDAO icPageDAO;

	private ICPageDAO getICPageDAO() {
		if (icPageDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return icPageDAO;
	}

	@Override
	public void onApplicationEvent(RepositoryStartedEvent event) {
		if (event.getIWMA().getSettings().getBoolean("iw_cache_pages_on_startup", Boolean.TRUE)) {
			Thread cacher = new Thread(new Runnable() {

				@Override
				public void run() {
					doCachePages();
				}

			});
			cacher.start();
		}
	}

	public void doCachePages() {
		try {
			String query = "select p." + ICPageBMPBean.ENTITY_NAME + "_ID from " + ICPageBMPBean.ENTITY_NAME + " p where p.deleted is null or p.deleted = 'N'";
			List<Serializable[]> ids = SimpleQuerier.executeQuery(query, 1);
			if (ListUtil.isEmpty(ids)) {
				return;
			}

			ICPageDAO icPageDAO = getICPageDAO();
			for (Serializable[] data: ids) {
				if (ArrayUtil.isEmpty(data)) {
					continue;
				}

				Serializable id = data[0];
				if (id instanceof Number) {
					icPageDAO.isPagePublished(((Number) id).intValue());
				}
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching pages", e);
		}
	}

}