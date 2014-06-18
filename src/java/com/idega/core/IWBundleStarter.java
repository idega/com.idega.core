package com.idega.core;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.builder.dao.ICPageDAO;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

public class IWBundleStarter implements IWBundleStartable {

	private static final Logger LOGGER = Logger.getLogger(IWBundleStarter.class.getName());

	@Autowired
	private ICPageDAO icPageDAO;

	private ICPageDAO getICPageDAO() {
		if (icPageDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return icPageDAO;
	}

	@Override
	public void start(IWBundle starterBundle) {
		doChangePermissionLength(starterBundle.getApplication().getSettings());
		doCachePages();
	}

	private void doChangePermissionLength(IWMainApplicationSettings settings) {
		String key = "ic_permission_context_increased";
		if (!settings.getBoolean(key, false)) {
			String sql = null;
			try {
				sql = "alter table " + ICPermission.ENTITY_NAME + " modify (" + ICPermission.COLUMN_CONTEXT_VALUE + " varchar(" + ICRole.ROLE_KEY_MAX_LENGTH + "))";
				if (SimpleQuerier.execute(sql)) {
					settings.setProperty(key, Boolean.TRUE.toString());
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error executing: '" + sql + "'", e);
			}
		}
	}

	private void doCachePages() {
		//	Temporary solution to cache
		try {
			String query = "select p." + ICPageBMPBean.ENTITY_NAME + "_ID from " + ICPageBMPBean.ENTITY_NAME + " p";
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
			LOGGER.log(Level.WARNING, "Error caching pages", e);
		}
	}

	@Override
	public void stop(IWBundle starterBundle) {
	}

}