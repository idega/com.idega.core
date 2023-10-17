package com.idega.core;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.cache.PagesCacher;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileBMPBean;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.data.IDOLookup;
import com.idega.data.SimpleQuerier;
import com.idega.file.security.FileTokenChanger;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.timer.IWTaskScheduler;

public class IWBundleStarter implements IWBundleStartable {

	private static final Logger LOGGER = Logger.getLogger(IWBundleStarter.class.getName());

	@Autowired
	private PagesCacher pagesCacher;

	@Autowired
	private IWTaskScheduler scheduler;

	private PagesCacher getPagesCacher() {
		if (pagesCacher == null) {
			ELUtil.getInstance().autowire(this);
		}
		return pagesCacher;
	}

	private IWTaskScheduler getIWTaskScheduler() {
		if (scheduler == null) {
			ELUtil.getInstance().autowire(this);
		}
		return scheduler;
	}

	@Override
	public void start(IWBundle starterBundle) {
		setAttributesForFiles();

		IWMainApplicationSettings settings = starterBundle.getApplication().getSettings();
		doChangePermissionLength(settings);

		if (settings.getBoolean("iw_cache_pages", Boolean.FALSE)) {
			getPagesCacher().doCachePages();
		}
	}

	private void setAttributesForFiles() {
		try {
			ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);

			setUniqueIdsForFiles(fileHome);
			setTokensForFiles(fileHome);

			IWTaskScheduler scheduler = getIWTaskScheduler();
			scheduler.schedule(0, 3, new FileTokenChanger());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error setting attributes for files", e);
		}
	}

	private void setUniqueIdsForFiles(ICFileHome fileHome) {
		Collection<Integer> filesWithoutUniqueIDs = null;
		try {
			filesWithoutUniqueIDs = fileHome.getFilesWithoutUniqueIds();
			if (ListUtil.isEmpty(filesWithoutUniqueIDs)) {
				return;
			}

			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			int index = 1, allFiles = filesWithoutUniqueIDs.size();
			for (Integer fileId: filesWithoutUniqueIDs) {
				LOGGER.info("Proceeding file no. " + index + " out of " + allFiles);
				index++;

				ICFile file = null;
				try {
					file = fileHome.findByPrimaryKey(fileId);
					if (file == null) {
						continue;
					}

					if (StringUtil.isEmpty(file.getUniqueId())) {
						String uuid = uidGenerator.generateId();
						file.setUniqueId(uuid);
						file.store();
					}
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Error setting unique ID for file " + file, e);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error setting unique IDs for files " + filesWithoutUniqueIDs, e);
		}
	}

	private void setTokensForFiles(ICFileHome fileHome) {
		Collection<Integer> filesWithoutTokens = null;
		try {
			filesWithoutTokens = fileHome.getFilesWithoutTokens();
			if (ListUtil.isEmpty(filesWithoutTokens)) {
				return;
			}

			int index = 1, allFiles = filesWithoutTokens.size();
			for (Integer fileId: filesWithoutTokens) {
				LOGGER.info("Proceeding file no. " + index + " out of " + allFiles);
				index++;

				ICFile file = null;
				try {
					file = fileHome.findByPrimaryKey(fileId);
					if (file == null) {
						continue;
					}

					String token = null;
					while (fileHome.findByToken((token = StringHandler.getRandomString(ICFileBMPBean.TOKEN_MAX_LENGTH))) != null) {
						token = StringHandler.getRandomString(ICFileBMPBean.TOKEN_MAX_LENGTH);
					}
					file.setToken(token);
					file.store();
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Error setting token for file " + file, e);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error setting tokens for files " + filesWithoutTokens, e);
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