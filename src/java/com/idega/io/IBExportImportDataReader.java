package com.idega.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;

import com.idega.builder.business.FileBusiness;
import com.idega.builder.data.IBExportImportData;
import com.idega.core.file.data.ICFile;
import com.idega.util.xml.XMLData;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 26, 2004
 */
public class IBExportImportDataReader {
	
	public IBExportImportData getData(File zipFile, FileBusiness fileBusiness) throws IOException  {
		IBExportImportData exportImportData = readMetadata(zipFile);
		// metadata has been read
		// iterate again through the zip file and fill map with names as key and files as values
		ZipInputStreamIgnoreClose zipInputStream;
		ZipEntry entry;
		Map nameFile = new HashMap();
		zipInputStream = new ZipInputStreamIgnoreClose(new FileInputStream(zipFile));
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String name = entry.getName();
			if (IBExportImportData.EXPORT_METADATA_FILE_NAME.equals(name)) {
				// do nothing
			}
			else if (exportImportData.isFileEntryAPage(name)) {
				// is a page!
				XMLData data = XMLData.getInstanceForInputStream(zipInputStream);
				nameFile.put(name, data);
			}
			else {
				String mimeType = exportImportData.getMimeType(name);
				ICFile file = fileBusiness.createFileFromInputStream(zipInputStream, name, mimeType);
				nameFile.put(name, file);
			}
			zipInputStream.closeEntry();
		}
		zipInputStream.closeStream();
		
		// now the 
		return exportImportData;
	}

  /**
	 * @param zipFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private IBExportImportData readMetadata(File zipFile) throws FileNotFoundException, IOException {
		IBExportImportData exportImportData = null;
		ZipInputStreamIgnoreClose zipInputStream = new ZipInputStreamIgnoreClose(new FileInputStream(zipFile));
		ZipEntry entry;
		while ((exportImportData == null && (entry = zipInputStream.getNextEntry()) != null)) {
			String name = entry.getName();
			// very fist check, is the entry the metadata?
			if (IBExportImportData.EXPORT_METADATA_FILE_NAME.equals(name)) {
				XMLData metadata = XMLData.getInstanceForInputStream(zipInputStream);
				exportImportData = IBExportImportData.getInstance(metadata);
			}
			zipInputStream.closeEntry();
		}
		closeStream(zipInputStream);
		return exportImportData;
	}

	protected void closeStream(ZipInputStreamIgnoreClose inputStream) {
  	try {
			if (inputStream != null) {
				inputStream.closeStream();
			}
		}
		// do not hide an existing exception
		catch (IOException io) {
		}
  }		
				
			
			
			
}
