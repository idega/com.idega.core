package com.idega.core.file.tmp;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;
import com.idega.util.EventTimer;
import com.idega.util.FileUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.3 $
 *
 *          Last modified: $Date: 2009/02/20 14:26:23 $ by $Author: civilis $
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class TmpFilesManager {

	static String UPLOADS_PATH;
	private Collection<TmpFileResolver> resResolvers;

	public void init(Collection<TmpFileResolver> resResolvers) {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		String realPath = iwma.getApplicationRealPath();

		UPLOADS_PATH = realPath + "tmpFiles/";
		FileUtil.createFolder(UPLOADS_PATH);

		if (resResolvers != null) {
			for (TmpFileResolver resResolver : resResolvers) {
				String ctxFolder = resResolver.getContextPath();
				if (ctxFolder.startsWith(CoreConstants.SLASH))
					ctxFolder = ctxFolder.substring(1);

				String custFolder = UPLOADS_PATH + ctxFolder;
				FileUtil.createFolder(custFolder);
				resResolver.setRealBasePath(custFolder);
			}
		}

		this.resResolvers = resResolvers;
	}

	public void cleanup(String identifier, Object resource, TmpFileResolver resolver) {
		Collection<File> filesToClean = resolver.getFilesToCleanUp(identifier, resource);
		for (File f : filesToClean) {
			if (f.exists()) {
				if (f.isDirectory()) {
					FileUtil.deleteNotEmptyDirectory(f);
				} else {
					FileUtil.delete(f);
				}
			}
		}
	}

	public void doPeriodicalCleanup() {
		if (resResolvers != null) {
			for (TmpFileResolver resResolver : resResolvers) {
				FileUtil.deleteAllFilesAndFolderInFolderOlderThan(resResolver.getRealBasePath(), EventTimer.THREAD_SLEEP_24_HOURS);
			}
		}
	}

	/**
	 *
	 * @param identifier
	 *            - might be some specific identifier, that you want to get
	 *            files for, e.g., variable in xforms submission data. Highly
	 *            resolver dependent.
	 * @param resource
	 *            - the same as with identifier
	 * @param resolver
	 * @return files uris. Might be local files, or stored in repository etc.
	 */
	public Collection<URI> getFilesUris(String identifier, Object resource, TmpFileResolver resolver) {
		return resolver.resolveFilesUris(identifier, resource);
	}
}