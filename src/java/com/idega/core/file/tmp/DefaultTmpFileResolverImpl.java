package com.idega.core.file.tmp;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;

/**
 *
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 *          Last modified: $Date: 2009/02/20 14:26:23 $ by $Author: civilis $
 *
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@TmpFileResolverType("defaultResolver")
@Service("defUplResResolver")
public class DefaultTmpFileResolverImpl implements TmpFileResolver {

	public static String UPLOADS_PATH;

	@Override
	public Collection<URI> resolveFilesUris(String identifier, Object resource) {

		String realBasePath = getRealBasePath();

		String filesFolder = concatFolderWithFolder(realBasePath, identifier);

		File[] files = FileUtil.getAllFilesInDirectory(filesFolder);

		if (files != null) {

			ArrayList<URI> uris = new ArrayList<URI>(files.length);

			for (File file : files) {

				URI uri = file.toURI();
				uris.add(uri);
			}

			return uris;
		}

		return new ArrayList<URI>(0);
	}

	@Override
	public String getRealBasePath() {

		return UPLOADS_PATH;
	}

	@Override
	public void setRealBasePath(String basePath) {

		UPLOADS_PATH = basePath;
	}

	@Override
	public String getContextPath() {

		return "defaultFiles/";
	}

	private String concatFolderWithFolder(String fol1, String fol2) {

		if (fol2.startsWith(CoreConstants.SLASH))
			fol2 = fol2.substring(1);

		if (!fol1.endsWith(CoreConstants.SLASH))
			fol1 += CoreConstants.SLASH;

		return fol1 + fol2;
	}

	@Override
	public Collection<File> getFilesToCleanUp(String identifier, Object resource) {

		String realBasePath = getRealBasePath();

		String filesFolder = concatFolderWithFolder(realBasePath, identifier);

		ArrayList<File> fs = new ArrayList<File>(1);
		fs.add(new File(filesFolder));

		return fs;
	}

	public Collection<URI> resolveAllFilesUris(Object resource) {

		throw new UnsupportedOperationException("Not supported yet");
	}

	@Override
	public void replaceAllFiles(Object resource,
			TmpFilesModifyStrategy replaceStrategy) {

		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void uploadToTmpLocation(String pathDirRelativeToBase, String fileName, InputStream inputStream) {
		uploadToTmpLocation(pathDirRelativeToBase, fileName, inputStream, Boolean.TRUE);
	}

	@Override
	public void uploadToTmpLocation(String pathDirRelativeToBase, String fileName, InputStream inputStream, boolean closeStream) {
		if (StringUtil.isEmpty(fileName)) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Tried to upload file to tmp dir, but no fileName provided");
			return;
		}

		String basePath = getRealBasePath();

		if (!basePath.endsWith(CoreConstants.SLASH))
			basePath = basePath + CoreConstants.SLASH;

		if (pathDirRelativeToBase != null) {

			if (pathDirRelativeToBase.startsWith(CoreConstants.SLASH))
				pathDirRelativeToBase = pathDirRelativeToBase.substring(1);

			basePath += pathDirRelativeToBase;
		}

		FileUtil.streamToFile(inputStream, basePath, fileName, closeStream);
	}

	@Override
	public File getFile(String fullUploadFolderPath, String fileName) {

		if (!fullUploadFolderPath.endsWith(CoreConstants.SLASH)) {
			fullUploadFolderPath += CoreConstants.SLASH;
		}

		File file = new File(fullUploadFolderPath + fileName);
		return file;
	}

	@Override
	public String getTmpUploadDir(String pathDirRelativeToBase) {

		String basePath = getRealBasePath();

		if (!basePath.endsWith(CoreConstants.SLASH))
			basePath = basePath + CoreConstants.SLASH;

		if (pathDirRelativeToBase != null) {

			if (pathDirRelativeToBase.startsWith(CoreConstants.SLASH))
				pathDirRelativeToBase = pathDirRelativeToBase.substring(1);

			basePath += pathDirRelativeToBase;
		}

		if (!pathDirRelativeToBase.endsWith(CoreConstants.SLASH)) {
			pathDirRelativeToBase += CoreConstants.SLASH;
		}

		FileUtil.createFolder(basePath);

		return basePath;
	}
}