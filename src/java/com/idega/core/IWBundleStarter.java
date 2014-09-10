package com.idega.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.cache.PagesCacher;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.expression.ELUtil;

public class IWBundleStarter implements IWBundleStartable {

	private static final Logger LOGGER = Logger.getLogger(IWBundleStarter.class.getName());

	@Autowired
	private PagesCacher pagesCacher;

	private PagesCacher getPagesCacher() {
		if (pagesCacher == null) {
			ELUtil.getInstance().autowire(this);
		}
		return pagesCacher;
	}

	@Override
	public void start(IWBundle starterBundle) {
		IWMainApplicationSettings settings = starterBundle.getApplication().getSettings();
		doChangePermissionLength(settings);

		if (settings.getBoolean("iw_cache_pages", Boolean.FALSE)) {
			getPagesCacher().doCachePages();
		}
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


	@Override
	public void stop(IWBundle starterBundle) {
	}

}