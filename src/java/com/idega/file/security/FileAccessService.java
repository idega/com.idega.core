package com.idega.file.security;

import java.util.Collection;

import com.idega.core.file.data.ICFile;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;

public interface FileAccessService {

	public boolean isAvailable(IWContext iwc, ICFile file, String fileUniqueId, String fileToken) throws Exception;

	public boolean hasPermission(IWContext iwc, String path) throws Exception;
	public boolean hasPermission(IWContext iwc, User user, Collection<String> paths) throws Exception;

}