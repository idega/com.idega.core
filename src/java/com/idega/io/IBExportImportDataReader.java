package com.idega.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.FinderException;

import com.idega.builder.business.XMLConstants;
import com.idega.builder.data.IBExportImportData;
import com.idega.builder.data.IBReference;
import com.idega.builder.data.IBReferences;
import com.idega.builder.data.StorableHolder;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLElement;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 26, 2004
 */
public class IBExportImportDataReader extends ReaderFromFile implements ObjectReader {
	
	private Map entryNameHolder = null;
	private Map pageIdHolder = null;
	private int parentPageId = -1;
	private int parentTemplateId = -1;
	
	
	public IBExportImportDataReader(IWApplicationContext iwac) {
		super(iwac);
	}
	
	public IBExportImportDataReader(Storable storable, IWApplicationContext iwac) {
		super(storable, iwac);
	}

	public void openContainer(File file) throws IOException {
		try {
			readData(file);
		}
		catch (FileNotFoundException ex) {
			throw new IOException("[IBExportImportReader] Folder could not be found");
		}
	}
	
	private void readData(File sourceFile) throws IOException {
		readMetadata(sourceFile);
		// create external data first
		entryNameHolder = new HashMap();
		IBReferences references = new IBReferences(iwac.getIWMainApplication());
		List nonPages = ((IBExportImportData) storable).getNonPageFileElements();
		Iterator nonPageIterator = nonPages.iterator();
		while (nonPageIterator.hasNext()) {
			XMLElement nonPageElement = (XMLElement) nonPageIterator.next();
			String zipEntryName = nonPageElement.getTextTrim(XMLConstants.FILE_USED_ID);
			if (! entryNameHolder.containsKey(zipEntryName)) {
				StorableHolder holder = references.createSourceFromElement(nonPageElement);
				ZipInputStreamIgnoreClose inputStream = getZipInputStream(zipEntryName, sourceFile);
				ReaderFromFile currentReader = (ReaderFromFile) holder.getStorable().read(this);
				String originalName = nonPageElement.getTextTrim(XMLConstants.FILE_ORIGINAL_NAME);
				String mimeType = nonPageElement.getTextTrim(XMLConstants.FILE_MIME_TYPE);
				currentReader.setName(originalName);
				currentReader.setMimeType(mimeType);
				try {
					currentReader.readData(inputStream);
				}
				finally {
					closeEntry(inputStream);
					closeStream(inputStream);
				}
				entryNameHolder.put(zipEntryName, holder);
			}
		}
		// create pages and templates

		List pageElements = ((IBExportImportData) storable).getSortedPageElements();
		createPages(pageElements);
		modifyPages(pageElements, sourceFile);
		entryNameHolder.get("weser");	
	}	
		
	private ICPage getParentFromTarget(int parentPageId) throws  IOException {
		if (parentPageId == -1) {
			return null;
		}
		try {
			ICPageHome home = (ICPageHome) IDOLookup.getHome(ICPage.class);
			return home.findByPrimaryKey(parentPageId);
		}
		catch (FinderException findEx) {
			throw new IOException("[IBExportImportDataReader] Parent couldn't be found");
		}
		catch (IDOLookupException lookupEx) {
			throw new IOException("[IBExportImportDataReader] ICPageHome could not be retrieved");
		}
	}

	private void createPages(List pageFileElements) throws IOException {
		pageIdHolder = new HashMap(); 
		Iterator pageIterator = pageFileElements.iterator();
		while (pageIterator.hasNext()) {
			XMLElement pageFileElement = (XMLElement) pageIterator.next();
			String zipEntryName = pageFileElement.getTextTrim(XMLConstants.FILE_USED_ID);
			if (! entryNameHolder.containsKey(zipEntryName)) {
				String exportValue = pageFileElement.getTextTrim(XMLConstants.FILE_VALUE);
				StorableHolder holder = IBReference.createPage();
				entryNameHolder.put(zipEntryName, holder);
				pageIdHolder.put(exportValue, holder);
			}
		}
	}
	
	private void modifyPages(List pageElements, File sourceFile) throws IOException {
		Iterator pageIterator = pageElements.iterator();
		while (pageIterator.hasNext()) {
			XMLElement pageFileElement = (XMLElement) pageIterator.next();
			String zipEntryName = pageFileElement.getTextTrim(XMLConstants.FILE_USED_ID);
			StorableHolder holder = (StorableHolder) entryNameHolder.get(zipEntryName);
			ICPage currentPage = (ICPage) holder.getStorable();
			XMLData pageData = XMLData.getInstanceWithoutExistingFile();
			ZipInputStreamIgnoreClose zipInputStream = getZipInputStream(zipEntryName, sourceFile);
			ReaderFromFile reader = (ReaderFromFile) pageData.read(this);
			try {
				reader.readData(zipInputStream);
			}
			finally {
				closeEntry(zipInputStream);
				closeStream(zipInputStream);
			}
			// set type, set template, set name
			XMLElement pageElement = pageData.getDocument().getRootElement().getChild(XMLConstants.PAGE_STRING);
			
			// set template
			String exportTemplate = pageElement.getAttributeValue(XMLConstants.TEMPLATE_STRING);
			if (exportTemplate != null) {
				// find new id 
				StorableHolder templateHolder = (StorableHolder) pageIdHolder.get(exportTemplate);
				String importValue = templateHolder.getValue();
				currentPage.setTemplateId(Integer.parseInt(importValue));
			}
			
			// set type, keep type in mind
			boolean isPage = false;
			String type = pageElement.getAttributeValue(XMLConstants.PAGE_TYPE);
			if (type != null && XMLConstants.PAGE_TYPE_PAGE.equals(type)) {
				isPage = true;
				currentPage.setIsPage();
			}
			else {
				isPage = false;
				currentPage.setIsTemplate();
			}
			
			// set name
			String originalName = pageFileElement.getTextTrim(XMLConstants.FILE_ORIGINAL_NAME);
			currentPage.setName(originalName);
			
			// store 
			currentPage.store();
			
			// set parent and child
			String exportValue = pageFileElement.getTextTrim(XMLConstants.FILE_VALUE);
			ICPage parentPage = null;
			String parentId = ((IBExportImportData) storable).getParentIdForPageId(exportValue);
			if (parentId != null) {
				StorableHolder parentHolder = (StorableHolder) pageIdHolder.get(parentId);
				parentPage = (ICPage) parentHolder.getStorable();
			}
			else if (isPage && parentPageId > -1) {
					parentPage = getParentFromTarget(parentPageId);
			}
			else if (parentTemplateId > -1) {
				parentPage = getParentFromTarget(parentTemplateId);
			}
			if (parentPage != null) {
				try {
					parentPage.addChild((ICPage) holder.getStorable());
				}
				catch (SQLException ex) {
					throw new IOException("[IBExportImportDataReader] Couldn't  add child page");
				}					
			}
		}
	}	
	
	

	private void readMetadata(File file) throws FileNotFoundException, IOException {
		ZipInputStreamIgnoreClose zipInputStream = getZipInputStream(IBExportImportData.EXPORT_METADATA_FILE_NAME,file);
		XMLData metadata = XMLData.getInstanceWithoutExistingFileSetName(IBExportImportData.EXPORT_METADATA_NAME);
		ReaderFromFile currentReader = (ReaderFromFile) metadata.read(this);
		try {
			currentReader.readData(zipInputStream);
		}
		finally {
			closeEntry(zipInputStream);
			closeStream(zipInputStream);
		}
		((IBExportImportData) storable).setMetadataSummary(metadata);
	}
		
	

	
	private ZipInputStreamIgnoreClose getZipInputStream(String zipEntryName, File zipFile) throws IOException  {
		IBExportImportData exportImportData = null;
		ZipInputStreamIgnoreClose zipInputStream = new ZipInputStreamIgnoreClose(new BufferedInputStream(new FileInputStream(zipFile)));
		ZipEntry entry;
		while ((exportImportData == null && (entry = zipInputStream.getNextEntry()) != null)) {
			String name = entry.getName();
			if (name.equals(zipEntryName)) {
				return zipInputStream;
			}
		}
		return null;
	}

				
  public Object read(ICFile file) {
		return new ICFileReader((Storable) file, iwac);
	}


	/* (non-Javadoc)
	 * @see com.idega.io.ObjectReader#read(com.idega.util.xml.XMLData)
	 */
	public Object read(XMLData xmlData)  {
		return new XMLDataReader(xmlData, iwac);
	}


	/* (non-Javadoc)
	 * @see com.idega.io.ObjectReader#read(com.idega.builder.data.IBExportImportData)
	 */
	public Object read(IBExportImportData metadata) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see com.idega.io.ObjectReader#read(com.idega.core.builder.data.ICPage)
	 */
	public Object read(ICPage page) throws RemoteException {
		return null;
	}


/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setName(java.lang.String)
	 */
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setMimeType(java.lang.String)
	 */
	public void setMimeType(String mimeType) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#readData(java.io.InputStream)
	 */
	public InputStream readData(InputStream source) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
		
	
	public void setParentPageForImportedPages(int parentPageId) {
		this.parentPageId = parentPageId;
	}
	public void setParentTemplateForImportedTemplates(int parentTemplateId) {
		this.parentTemplateId = parentTemplateId;
	}
	
  protected void closeEntry(ZipInputStream input) {
  	try {
  		if (input != null) {
  			input.closeEntry();
  		}
  	}
  	// do not hide an existing exception
  	catch (IOException io) {
  	}
  }
  
  protected void closeStream(ZipInputStreamIgnoreClose input) {
  	try {
  		if (input != null) {
  			input.closeStream();
  		}
  	}
  	// do not hide an existing exception
  	catch (IOException io) {
  	}
  }
}
