package com.idega.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.idega.builder.data.IBExportImportData;
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
	
	public IBExportImportData getData(File file) throws IOException  {
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
		ZipEntry entry;
		XMLData metadata = null;
		while ((metadata == null && (entry = zipInputStream.getNextEntry()) != null)) {
			String name = entry.getName();
			if (IBExportImportData.EXPORT_METADATA_FILE_NAME.equals(name)) {
				metadata = XMLData.getInstanceForInputStream(zipInputStream);
			}
		}
		String name = metadata.getName();
		name.length();
		return null;
	}
			
				
			
			
			
}
