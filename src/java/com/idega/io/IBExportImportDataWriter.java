package com.idega.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.idega.builder.data.IBExportImportData;
import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.xml.XMLData;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public class IBExportImportDataWriter extends WriterToFile implements ObjectWriter {
	
	private static final String ZIP_EXTENSION = "zip";
	private static final String ZIP_ELEMENT_DELIMITER = "_";
	
	public IBExportImportDataWriter(IWApplicationContext iwac) {
		super(iwac);
	}
	
	public IBExportImportDataWriter(Storable storable, IWApplicationContext iwac) {
		this(iwac);
		setSource(storable);
	}

	public String createContainer() throws IOException {
		String name = ((IBExportImportData) storable).getName();
		long folderIdentifier = System.currentTimeMillis();
 		String path = getRealPathToFile(name, ZIP_EXTENSION, folderIdentifier);
     File auxiliaryFile = null;
     OutputStream destination = null;
     OutputStream outputStreamWrapper = null;
     try {
       auxiliaryFile = new File(path);
       destination = new BufferedOutputStream((new FileOutputStream(auxiliaryFile)));
     }
     catch (FileNotFoundException ex)  {
//     	logError("[XMLData] problem creating file.");
//     	log(ex);
     	throw new IOException("xml file could not be stored");
     }
     try {
			outputStreamWrapper = writeData(destination);
		}
		finally {
			close(outputStreamWrapper);
		}
     return getURLToFile(name, ZIP_EXTENSION, folderIdentifier);
 	}
	
	public OutputStream writeData(OutputStream destination) throws IOException {
		ZipOutputStream zipOutputStream = new ZipOutputStream(destination);
		IBExportImportData metadata = (IBExportImportData) storable;
		List data = metadata.getData();
		List alreadyStoredElements = new ArrayList(data.size());
		// counter for the entries in metadata
		int entryNumber = 0;
		// counter for the prefix of the stored files
		int identifierNumber = 0;
 		Iterator iterator = data.iterator();
 		while (iterator.hasNext()) {
 			Storable element = (Storable) iterator.next();
 			// do not store the same elements twice into the zip file
 			int indexOfAlreadyStoredElement;
 			if ((indexOfAlreadyStoredElement = alreadyStoredElements.indexOf(element)) > - 1) {
 				metadata.modifyElementSetNameSetOriginalNameLikeElementAt(entryNumber++, indexOfAlreadyStoredElement);
 			}
 			else {
	 			alreadyStoredElements.add(element);
	 			WriterToFile currentWriter = (WriterToFile) element.write(this);
	 			String originalName = currentWriter.getName();
	 			String zipElementName = createZipElementName(originalName, identifierNumber++);
	 			ZipEntry zipEntry = new ZipEntry(zipElementName);
	 			String sourceClass = metadata.getSourceClassNameForElement(entryNumber);
	 			zipEntry.setComment(sourceClass);
	 			metadata.modifyElementSetNameSetOriginalName(entryNumber++, zipElementName, originalName);
	 			zipOutputStream.putNextEntry(zipEntry);
	 			currentWriter.writeData(zipOutputStream);
	 			zipOutputStream.closeEntry();
 			}
 		}
 		// add metadata itself to the zip file
 		XMLData metadataSummary = metadata.createMetadataSummary();
 		WriterToFile currentWriter = (WriterToFile) metadataSummary.write(this);
		String originalName = currentWriter.getName();
		ZipEntry zipEntry = new ZipEntry(originalName);
		zipOutputStream.putNextEntry(zipEntry);
		currentWriter.writeData(zipOutputStream);
		zipOutputStream.closeEntry();
		return zipOutputStream;
	}
	
  public String getName() {
  	return ((IBExportImportData) storable).getName();
  }

  public Object write(ICFile file) {
		return new ICFileWriter((Storable) file, iwac);
	}

  public Object write(XMLData xmlData) {
		return new XMLDataWriter(xmlData, iwac);
	}
  
  public Object write(IBExportImportData metadata) {
  	return new IBExportImportDataWriter(metadata, iwac);
  }

  private String createZipElementName(String originalName, int entryNumber) {
  	StringBuffer buffer = new StringBuffer();
  	buffer.append(entryNumber).append(ZIP_ELEMENT_DELIMITER).append(originalName);
  	return buffer.toString();
  }

	
     

 	}
 		
	
	

