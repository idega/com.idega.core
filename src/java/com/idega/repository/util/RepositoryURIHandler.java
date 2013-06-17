package com.idega.repository.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.jcr.RepositoryException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.util.FileInfo;
import com.idega.core.file.util.FileURIHandler;
import com.idega.repository.bean.RepositoryItem;
import com.idega.util.CoreConstants;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class RepositoryURIHandler extends DefaultSpringBean implements FileURIHandler {

	@Override
	public String getSupportedScheme() {
		return CoreConstants.REPOSITORY;
	}

	@Override
	public InputStream getFile(URI uri) throws FileNotFoundException {
		try {
			return getRepositoryService().getInputStreamAsRoot(uri.getSchemeSpecificPart());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public FileInfo getFileInfo(URI uri) {
		RepositoryItem item = null;
		try {
			item = getRepositoryService().getRepositoryItemAsRootUser(uri.getSchemeSpecificPart());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		if (item == null)
			return null;

		FileInfo fi = new FileInfo();

		String fileName = item.getName();

		//	Replace windows absolute path filename with just filename
		if (fileName.contains(CoreConstants.BACK_SLASH)) {
			int lastBackSlashIndex = fileName.lastIndexOf(CoreConstants.BACK_SLASH);
			fileName = fileName.substring(lastBackSlashIndex + 1);
		}

		fi.setFileName(fileName);
		fi.setContentLength(item.getLength());

		return fi;
	}

}
