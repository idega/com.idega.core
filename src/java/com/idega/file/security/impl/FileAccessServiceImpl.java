package com.idega.file.security.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.data.ICFile;
import com.idega.file.security.FileAccessService;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.repository.access.RepositoryAccessManager;
import com.idega.user.data.User;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FileAccessServiceImpl extends DefaultSpringBean implements FileAccessService {

	@Autowired
	private RepositoryAccessManager repositoryAccessManager;

	@Override
	public boolean isAvailable(IWContext iwc, ICFile file, String fileUniqueId, String fileToken) throws Exception {
		if (file == null) {
			return false;
		}

		String token = file.getToken();
		if (StringUtil.isEmpty(fileToken) || StringUtil.isEmpty(token) || !token.equals(fileToken)) {
			getLogger().warning("Invalid token '" + fileToken + "' for file with unique ID '" + fileUniqueId + "'");
			return false;
		}

		if (!isPubliclyAvailable(iwc, file)) {
			getLogger().warning("File with unique ID " + fileUniqueId + " is not publicly available - user must be logged in");
			return false;
		}

		if (file.isPublic()) {
			return true;
		}

		Set<String> paths = new HashSet<>();
		String name = file.getName();
		if (!StringUtil.isEmpty(name)) {
			paths.add(name);
		}
		String uri = file.getFileUri();
		if (!StringUtil.isEmpty(uri)) {
			paths.add(uri);
		}

		User user = iwc.isLoggedOn() ? iwc.getCurrentUser() : null;
		if (hasPermission(iwc, user, paths)) {
			return true;
		}

		String error =	"Private file (unique ID " + fileUniqueId + ", " + paths + ") is not available for " +
						(user == null ? "unauthorized access - user must be logged in" : user + " - not enough access rights");
		getLogger().warning(error);
		CoreUtil.sendExceptionNotification(error, null);

		return false;
	}

	@Override
	public boolean hasPermission(IWContext iwc, String path) throws Exception {
		return repositoryAccessManager.hasPermission(iwc, path);
	}

	@Override
	public boolean hasPermission(IWContext iwc, User user, Collection<String> paths) throws Exception {
		user = user == null ?
				iwc != null && iwc.isLoggedOn() ? iwc.getCurrentUser() : null :
				user;
		if (repositoryAccessManager.hasPermission(iwc, paths)) {
			getLogger().info(
					"Resource (" + paths + ") is available for " + user +
					" (ID: " + user.getId() + ", personal ID: " + user.getPersonalID() + ")"
			);
			return true;
		} else {
			getLogger().warning("Resource (" + paths + ") is not available for " + user +
					" (ID: " + user.getId() + ", personal ID: " + user.getPersonalID() + ")");
		}

		return false;
	}

	private boolean isPubliclyAvailable(IWContext iwc, ICFile file) {
		IWMainApplicationSettings settings = iwc == null ? getSettings() : iwc.getApplicationSettings();
		if (!settings.getBoolean("file.check_publicly_available", true)) {
			return true;
		}

		if (file == null) {
			return false;
		}

		if (!file.isPublic() && (iwc == null || !iwc.isLoggedOn())) {
			return false;
		}

		return true;
	}

}