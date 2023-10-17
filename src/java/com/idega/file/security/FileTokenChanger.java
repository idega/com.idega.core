package com.idega.file.security;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileBMPBean;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;

public class FileTokenChanger implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(FileTokenChanger.class.getName());

	@Override
	public void run() {
		if (!IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("file.change_tokens_daily", true)) {
			return;
		}

		doChangeTokens();
	}

	private void doChangeTokens() {
		LOGGER.info("Starting to change tokens");
		Collection<Integer> ids = null;
		try {
			ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
			ids = fileHome.getIdsOfAllFiles();
			if (ListUtil.isEmpty(ids)) {
				return;
			}

			int index = 1, total = ids.size();
			for (Integer id: ids) {
				LOGGER.info("Iterating via file no. " + index + " out of " + total);
				index++;

				ICFile file = null;
				String newToken = null;
				try {
					file = fileHome.findByPrimaryKey(id);
					if (file == null) {
						continue;
					}

					newToken = StringHandler.getRandomString(ICFileBMPBean.TOKEN_MAX_LENGTH);
					while (fileHome.findByToken(newToken) != null) {
						newToken = StringHandler.getRandomString(ICFileBMPBean.TOKEN_MAX_LENGTH);
					}

					file.setToken(newToken);
					file.store();
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Error changing token for file " + file + ". New token: " + newToken, e);
				}
			}

			LOGGER.info("Finished changing tokens");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error changing tokens for " + ids, e);
		}
	}

}