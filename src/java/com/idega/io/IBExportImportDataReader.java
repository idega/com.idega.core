package com.idega.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.PageCacher;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.XMLConstants;
import com.idega.builder.data.IBExportImportData;
import com.idega.builder.data.IBReferences;
import com.idega.builder.data.StorableHolder;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.util.StringHandler;
import com.idega.util.datastructures.HashMatrix;
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
	
	private IWUserContext iwuc = null;
	private HashMatrix sourceNameHolder = null;
	private Map entryNameHolder = null;
	private Map pageIdHolder = null;
	private Map oldNewInstanceId = null;
	
	private int parentPageId = -1;
	private int parentTemplateId = -1;

	private boolean performValidation = true;
	private List missingModules = null;
	
	
	public IBExportImportDataReader(IWApplicationContext iwac) {
		super(iwac);
	}
	
	public IBExportImportDataReader(Storable storable, IWApplicationContext iwac) {
		super(storable, iwac);
	}
	
	public IBExportImportDataReader(Storable storable, boolean performValidation, IWApplicationContext iwac, IWUserContext iwuc) {
		super(storable, iwac);
		this.iwuc = iwuc;
		this.performValidation = performValidation;
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
		if (performValidation && ! ((IBExportImportData) storable).isValid()) {
			return;
		}
		// create external data first
		entryNameHolder = new HashMap();
		sourceNameHolder = new HashMatrix();
		IBReferences references = new IBReferences(iwac.getIWMainApplication());
		List nonPages = ((IBExportImportData) storable).getNonPageFileElements();
		Iterator nonPageIterator = nonPages.iterator();
		while (nonPageIterator.hasNext()) {
			XMLElement nonPageElement = (XMLElement) nonPageIterator.next();
			String zipEntryName = nonPageElement.getTextTrim(XMLConstants.FILE_USED_ID);
			// avoid errors if the metadata is corrupt
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
				String source = nonPageElement.getTextTrim(XMLConstants.FILE_SOURCE);
				String name = nonPageElement.getTextTrim(XMLConstants.FILE_NAME);
				sourceNameHolder.put(source, name, holder);				
			}
		}
		// create pages and templates
		List pageElements = ((IBExportImportData) storable).getSortedPageElements();
		createPages(pageElements, sourceFile);
		modifyPages(pageElements, sourceFile);
	}	
		
	private void createPages(List pageFileElements, File sourceFile) throws IOException {
		pageIdHolder = new HashMap(); 
		IBPageHelper pageHelper = IBPageHelper.getInstance();
		Map pageTree = PageTreeNode.getTree(iwac);
		Iterator pageIterator = pageFileElements.iterator();
		while (pageIterator.hasNext()) {
			XMLElement pageFileElement = (XMLElement) pageIterator.next();
			String zipEntryName = pageFileElement.getTextTrim(XMLConstants.FILE_USED_ID);
			// avoid errors if the metadata is corrupt
			if (! entryNameHolder.containsKey(zipEntryName)) {
				createPage(pageFileElement, zipEntryName, sourceFile, pageHelper, pageTree);
			}
		}
	}	
	
	private void modifyPages(List pageFileElements, File sourceFile) throws IOException {
		List localPages = new ArrayList();
		Iterator pageIterator = pageFileElements.iterator();
		while (pageIterator.hasNext()) {
			XMLElement pageFileElement = (XMLElement) pageIterator.next();
			String zipEntryName = pageFileElement.getTextTrim(XMLConstants.FILE_USED_ID);
			// avoid errors if the metadata is corrupt
			if (! localPages.contains(zipEntryName)) {
				localPages.add(zipEntryName);
				modifyPageContent(zipEntryName, sourceFile);
			}
		}
	}

	/**
	 * @param pageFileElement
	 * @param zipEntryName
	 * @param sourceFile
	 * @param pageHelper
	 * @param pageTree
	 * @throws IOException
	 * @throws RemoteException
	 */
	private void createPage(XMLElement pageFileElement, String zipEntryName, File sourceFile, IBPageHelper pageHelper, Map pageTree) throws IOException, RemoteException {
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
		
		XMLElement pageElement = pageData.getDocument().getRootElement().getChild(XMLConstants.PAGE_STRING);
		// set template --------------------------------
		String exportTemplate = pageElement.getAttributeValue(XMLConstants.TEMPLATE_STRING);
		String importTemplateValue = null;
		if (exportTemplate != null) {
			// find new id 
			StorableHolder templateHolder = (StorableHolder) pageIdHolder.get(exportTemplate);
			importTemplateValue = templateHolder.getValue();
		}
		String type = pageElement.getAttributeValue(XMLConstants.PAGE_TYPE);
		// set name ---------------------------------------
		String originalName = pageFileElement.getTextTrim(XMLConstants.FILE_ORIGINAL_NAME);
		// set parent and child ----------------------------
		String exportValue = pageFileElement.getTextTrim(XMLConstants.FILE_VALUE);
		String parentId = ((IBExportImportData) storable).getParentIdForPageId(exportValue);
		if (parentId == null && type != null) {
			if (XMLConstants.PAGE_TYPE_PAGE.equals(type) && parentPageId > -1) {
				parentId = Integer.toString(parentPageId);
			}
			else if (XMLConstants.PAGE_TYPE_TEMPLATE.equals(type) && parentTemplateId > -1) {
				parentId = Integer.toString(parentTemplateId);
			}
		}
		else {
			StorableHolder parentHolder = (StorableHolder) pageIdHolder.get(parentId);
			parentId = parentHolder.getValue();
		}
		// create the new page 			
		// you have to use page helper because page helper changes some settings for the builder application
		String pageHelperPageType = convertXMLTypeElement(type);
		int currentPageId = pageHelper.createNewPage(parentId, originalName, pageHelperPageType, importTemplateValue, pageTree, iwuc); 
		// get the just created page
		StorableHolder holder = getHolderForPage(currentPageId);
		entryNameHolder.put(zipEntryName, holder);
		pageIdHolder.put(exportValue, holder);
	}

	private void modifyPageContent(String zipEntryName, File sourceFile) throws IOException {
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
		
		XMLElement pageElement = pageData.getDocument().getRootElement().getChild(XMLConstants.PAGE_STRING);
		// set template --------------------------------
		String exportTemplate = pageElement.getAttributeValue(XMLConstants.TEMPLATE_STRING);
		String importTemplateValue = null;
		if (exportTemplate != null) {
			// find new id 
			StorableHolder templateHolder = (StorableHolder) pageIdHolder.get(exportTemplate);
			importTemplateValue = templateHolder.getValue();
			// set template entry in xml file
			pageElement.setAttribute(XMLConstants.TEMPLATE_STRING, importTemplateValue);
		}
		// get the current page
		StorableHolder holder = (StorableHolder) entryNameHolder.get(zipEntryName);
		ICPage currentPage = (ICPage) holder.getStorable();
		int currentPageId = Integer.parseInt(holder.getValue());
						
		// change module references -----------------------------------------------
		// change external references ----------------------------------------------
		Iterator iterator = pageElement.allChildrenIterator();
		while(iterator.hasNext()) {
			XMLElement element = (XMLElement) iterator.next();
			checkModuleEntries(element, currentPageId);
			checkPropertiesEntries(element);
		}
		// change region references
		iterator = pageElement.allChildrenIterator();
		while (iterator.hasNext()) {
			XMLElement element = (XMLElement) iterator.next();
			checkRegionEntries(element);
		}
		// store the file value to the current page
		ICFile currentFile = currentPage.getFile();
		pageData.setXmlFile(currentFile);
		pageData.store();
		// update page
		//TODO: thi: figure out why updatePage doesn't work 
		PageCacher.flagAllPagesInvalid();
		//builderLogic.updatePage(currentPageId);
	}	
	
	
	/** change the module element:
	 * <module id="11" ic_object_id="63" class="com.idega.block.calendar.presentation.Calendar" />
	 * id is changed (new ICObjectInstance instance is created)
	 * ic_object_id is changed (existing ICObject is looked up , the existing primary key is used) 
	 *  <module id="NEW-VALUE" ic_object_id="EXISTING-VALUE" class="com.idega.block.calendar.presentation.Calendar" />
	 * In order to update the references in the other pages keep the new module id in
	 * oldNewInstanceId 
	 * 
	 */
	private void checkModuleEntries(XMLElement element, int pageId) throws IOException {
		String nameOfElement = element.getName();
		// is it a module?
		if (XMLConstants.MODULE_STRING.equalsIgnoreCase(nameOfElement)) {
			// ask for the class
			String moduleClass = element.getAttributeValue(XMLConstants.CLASS_STRING);
			String importInstanceId = element.getAttributeValue(XMLConstants.ID_STRING); 
			// figure out what the current module class id is
			ICObject icObject = null;
			try {
				 icObject = findICObject(moduleClass);
			}
			catch (FinderException findEx) {
				throw new IOException("[IBExportImportDataReader] Couldn't find module class "+moduleClass);
			}
			catch (IDOLookupException lookUpEx) {
				throw new IOException("[IBExportImportDataReader] Couldn't look up home of ICObject");
			}
			// set id of ICObject 
			element.setAttribute(XMLConstants.IC_OBJECT_ID_STRING, icObject.getPrimaryKey().toString());
			// create new instance of ICObjectInstance
			String instanceId = null;
			try {
				instanceId = createNewObjectInstance(icObject, pageId);
			}
			catch (CreateException createEx) {
				throw new IOException("[IBExportImportDataReader] Couldn't create new ic object instance");
			}
			catch (IDOLookupException lookUpEx) {
				throw new IOException("[IBExportImportDataReader] Couldn't look up home of ICObjectInstance");
			}
			if (oldNewInstanceId == null) {
				oldNewInstanceId = new HashMap();
			}
			// set new id of ICObjectInstance 
			element.setAttribute(XMLConstants.ID_STRING, instanceId);
			oldNewInstanceId.put(importInstanceId, instanceId);			
		}
	}
	
	/**change the property element within the module element:
	 * <module id="9" ic_object_id="186" class="com.idega..presentation.Image" />
	 * 		<property>
	 * 		<name>image_id</name>
	 * 		<value>5</value>
	 * 		<type>java.lang.String</type>
	 * 		</property>
	 * </module>
	 * Only the content of the value element is replaced by  a new value, that is:
	 * 	<property>
	 * 		<name>image_id</name>
	 * 		<value>NEW-VALUE</value>
	 * 		<type>java.lang.String</type>
	 * 		</property>
	 * </module>
	 */
	private void checkPropertiesEntries(XMLElement element) {
		String nameOfElement = element.getName();
		// is it a property?
		if (XMLConstants.PROPERTY_STRING.equalsIgnoreCase(nameOfElement)) {
			// ask for the source of the property
			String propertySource = element.getParent().getAttributeValue(XMLConstants.CLASS_STRING);
			// ask for the name of the property
			String propertyName = element.getTextTrim(XMLConstants.NAME_STRING);
			// do we have a reference with that source and name?
			if (sourceNameHolder.containsKey(propertySource, propertyName)) {
				StorableHolder holder = (StorableHolder) sourceNameHolder.get(propertySource, propertyName);
				// set the value
				String value = holder.getValue();
				element.setContent(XMLConstants.VALUE_STRING, value);
			}
		}
	}

		/** change the region element:
	 * <region id="11" x="1" y="2" label="main" />
	 * or
	 * <region id="11.1.2" label="main" />
	 * The id is replaced by a new value, that is: 
	 * <region id="NEW_VALUE" x="1" y="2" label="main" />
	 * or
	 * <region id="NEW_VALUE.1.2" label="main" />
	 */
	private void checkRegionEntries(XMLElement element) throws IOException {
		String nameOfElement = element.getName();
		// is it a region?
		if (XMLConstants.REGION_STRING.equalsIgnoreCase(nameOfElement)) {
			// ask for the id 
			String regionId = element.getAttributeValue(XMLConstants.ID_STRING);
			// parse the id
			int index = regionId.indexOf(XMLConstants.DOT_REGION_STRING);
			boolean regionIsDotType = (index == -1) ? false : true;
			// There are two different types of regions: 
			// region type: <region id="12" x="1" y="2" label="main">
			// region type: <region id="12.1.2" label="main">     ---> regionIsDotType (that is the id is 12)
			String id = (regionIsDotType) ? regionId.substring(0, index) : regionId;
			// look up the new id
			if (! oldNewInstanceId.containsKey(id)) {
				throw new IOException("[IBExportImportDataReader] Id of object instance could not be found.");
			}
			String newId = (String) oldNewInstanceId.get(id);
			// set new id
			String newRegionId = (regionIsDotType) ? StringHandler.concat(newId, regionId.substring(index)) : newId;
			element.setAttribute(XMLConstants.ID_STRING, newRegionId);
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
	
	public List getMissingModules() {
		return missingModules;
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
  
  private ICObject findICObject(String className) throws IDOLookupException, FinderException  {
  	ICObjectHome home = (ICObjectHome) IDOLookup.getHome(ICObject.class);
  	return home.findByClassName(className);
  }
  
  private String createNewObjectInstance(ICObject object, int pageId) throws IDOLookupException, CreateException {
  	ICObjectInstanceHome home = (ICObjectInstanceHome) IDOLookup.getHome(ICObjectInstance.class);
  	ICObjectInstance instance = home.create();
  	instance.setICObject(object);
  	instance.setIBPageID(pageId);
  	instance.store();
  	return instance.getPrimaryKey().toString();
  }

	private StorableHolder getHolderForPage(int pageId) throws IOException {
		try {
			ICPageHome home = (ICPageHome) IDOLookup.getHome(ICPage.class);
			ICPage page = home.findByPrimaryKey(pageId);
			StorableHolder holder = new StorableHolder();
			holder.setStorable((Storable) page);
			holder.setValue(Integer.toString(pageId));
			return holder;
		}
		catch (IDOLookupException ex) {
			throw new IOException("[IBExportImportDataReader] Could not retrieve home of ICPage");
		}
		catch (FinderException findEx) {
			throw new IOException("[IBExportImportDataReader] Could not find page with id "+ Integer.toString(pageId));
		}


	}
	
	public String convertXMLTypeElement(String xmlTypeString) throws IOException {
		if (XMLConstants.PAGE_TYPE_PAGE.equalsIgnoreCase(xmlTypeString)) {
			return IBPageHelper.PAGE;
		}
		else if (XMLConstants.PAGE_TYPE_DRAFT.equalsIgnoreCase(xmlTypeString)) {
			return IBPageHelper.DRAFT;
		}
		else if (XMLConstants.PAGE_TYPE_TEMPLATE.equalsIgnoreCase(xmlTypeString)) {
			return IBPageHelper.TEMPLATE;
		}
		else if (XMLConstants.PAGE_TYPE_DPT_PAGE.equalsIgnoreCase(xmlTypeString)) {
				return IBPageHelper.DPT_PAGE;
		}
		else if (XMLConstants.PAGE_TYPE_DPT_TEMPLATE.equalsIgnoreCase(xmlTypeString)) {
			return IBPageHelper.DPT_TEMPLATE;
		}
		throw new IOException("[IBExportImportDataReader] Unknown page type "+ xmlTypeString);
	}

  	
}
