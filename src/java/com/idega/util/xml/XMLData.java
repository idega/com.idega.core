package com.idega.util.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.zip.ZipInputStream;
import org.jdom.IllegalDataException;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.io.serialization.ObjectReader;
import com.idega.io.serialization.ObjectWriter;
import com.idega.io.serialization.Storable;

import com.idega.presentation.IWContext;
import com.idega.util.FileUtil;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on May 22, 2003
 */
public class XMLData implements Storable {
  
  private final String DEFAULT_ROOT = "default_root";
  
  public final String DEFAULT_NAME = "xml_data";
  
  public final String AUXILIARY_FOLDER = "auxiliaryXMLDataFolder";
  public final String AUXILIARY_FILE = "auxililary_xml_data_file_";
  public final String XML_EXTENSION = ".xml";
  
  private XMLDocument document = null;
 
  // xmlFileId and xmlFile are synchronized
  private int xmlFileId = -1;
  private ICFile xmlFile = null;
  
  private String name = null;
  private String rootName = null;
  
  public static XMLData getInstanceForInputStream(InputStream inputStream) throws IOException {
  	XMLData data = new XMLData();
    data.initialize(inputStream);
    return data;
  }
  
  public static XMLData getInstanceForInputStream(ZipInputStream zipInputStream) throws IOException {
  	XMLData data = new XMLData();
    data.initialize(zipInputStream);
    return data;
  }
  
  public static XMLData getInstanceForFile(int xmlFileId) throws IOException {
    XMLData data = new XMLData();
    data.initialize(xmlFileId);
    return data;
  }
  
  public static XMLData getInstanceForFile(ICFile file) throws IOException {
    XMLData data = new XMLData();
    data.initialize(file);
    return data;
  }
  
  public static XMLData getInstanceForFile(String path) throws IOException {
  	XMLData data = new XMLData();
  	data.initialize(path);
  	return data;
  }
  
  public static XMLData getInstanceForFile(File file) throws IOException {
  	XMLData data = new XMLData();
  	data.initialize(file);
  	return data;
  }
  
  public static XMLData getInstanceWithoutExistingFile()  {
    XMLData data = new XMLData();
    return data;
  } 
  
  public static XMLData getInstanceWithoutExistingFileSetName(String name) {
    XMLData data = XMLData.getInstanceWithoutExistingFile();
    data.setName(name);
    return data;
  }
  
  public static XMLData getInstanceWithoutExistingFileSetNameSetRootName(String name, String rootName) {
  	XMLData data = getInstanceWithoutExistingFileSetName(name);
  	data.rootName = rootName;
  	return data;
  }
 
  public void initialize(int fileId) throws IOException {
    ICFile tempXmlFile = getXMLFile(fileId);
    initialize(tempXmlFile);
  }
  
  public String getName()  {
    // name is set
    if (name != null && name.length() > 0) {
      return name;
    }
    // name is not set, file id is set
    if (xmlFileId > -1) { 
      StringBuffer buffer = new StringBuffer(DEFAULT_NAME);
      buffer.append('_').append(xmlFileId);
      return buffer.toString();
    }
    // neither name nor file id is set
    return DEFAULT_NAME;
  }   

  
  public void setName(String name)  {
    this.name = name;
  }
   
  public XMLDocument getDocument()  {
    if (document == null)  {
      // create an empty document
    	String tempRootName = (rootName == null) ? DEFAULT_ROOT : rootName;
      document = new XMLDocument(new XMLElement(tempRootName));
    }  
    return document;
  }
  
  public void setDocument(XMLDocument document) {
    this.document = document;
  }        
  
  public ICFile store() throws IOException {
    // create or fetch existing ICFile
    ICFile tempXmlFile = (xmlFileId < 0) ? getNewXMLFile() : getXMLFile(xmlFileId);
    tempXmlFile.setMimeType("text/xml");
    tempXmlFile.setName(getName());
    try {
      tempXmlFile.store();
    }
    catch (IDOStoreException ex)  {
      System.err.println("[XMLData] problem storing ICFile Message is: "+ex.getMessage());
      ex.printStackTrace(System.err);
      throw new IOException("xml file could not be stored");
    }
    if (xmlFileId < 0) {
      xmlFileId = ((Integer)tempXmlFile.getPrimaryKey()).intValue();
      // the default name uses the id, therefore set again and store again
      if (name == null) {
        tempXmlFile.setName(getName());
      }
    }

    // To avoid problems with databases (e.g. MySQL) 
    // we do not write directly to the ICFile object but
    // create an auxiliary file on the hard disk and write the xml file to that file.
    // After that we read the file on the hard disk an write it to the ICFile object.
    // Finally we delete the auxiliary file.
    
    // write the output first to a file object  
    // get the output stream      
    String separator = FileUtil.getFileSeparator();
    IWMainApplication mainApp = IWContext.getInstance().getIWMainApplication();
    StringBuffer path = new StringBuffer(mainApp.getApplicationRealPath());
           
    path.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY)
      .append(separator)
      .append(AUXILIARY_FOLDER);
    // check if the folder exists create it if necessary
    // usually the folder should be already be there.
    // the folder is never deleted by this class
    FileUtil.createFolder(path.toString());
    // set name of auxiliary file
    path.append(separator).append(AUXILIARY_FILE).append(xmlFileId).append(XML_EXTENSION);
    BufferedOutputStream outputStream = null;
    File auxiliaryFile = null;
    try {
      auxiliaryFile = new File(path.toString());
      outputStream = new BufferedOutputStream(new FileOutputStream(auxiliaryFile));
    }
    catch (FileNotFoundException ex)  {
      System.err.println("[XMLData] problem creating file. Message is: "+ex.getMessage());
      ex.printStackTrace(System.err);
      throw new IOException("xml file could not be stored");
    }
    // now we have an output stream of the auxiliary file
    // write to the xml file
    XMLOutput xmlOutput = new XMLOutput("  ", true);
    xmlOutput.setLineSeparator(System.getProperty("line.separator"));
    xmlOutput.setTextNormalize(true);
    
    //xmlOutput.setEncoding("iso-8859-1");
    xmlOutput.setEncoding("UTF-8");
    
    // do not use document directly use accessor method
    XMLDocument myDocument = getDocument();
    try {
      xmlOutput.output(myDocument, outputStream);
    }
   finally {
   	close(outputStream);
   }
   //writing finished
    // get size of the file
    int size = (int) auxiliaryFile.length();
    // get the input stream of the auxiliary file
    BufferedInputStream inputStream = null;
    try {
      inputStream = new BufferedInputStream(new FileInputStream(auxiliaryFile));
        }
    catch (FileNotFoundException ex)  {
      System.err.println("[XMLData] problem reading file. Message is: "+ex.getMessage());
      ex.printStackTrace(System.err);
      throw new IOException("xml file could not be stored");
    }
    // now we have an input stream of the auxiliary file
    
    // write to the ICFile object
    tempXmlFile.setFileSize(size);
    try {
    	tempXmlFile.setFileValue(inputStream);
    	tempXmlFile.store();
    }
    finally {
    	close(inputStream);
    }
//    try {
      //xmlFile.update();

//    }
//    catch (SQLException ex)  {
//      System.err.println("[XMLData] problem storing ICFile Message is: "+ex.getMessage());
//      ex.printStackTrace(System.err);
//      throw new IOException("xml file could not be stored");
//    }
    // reading finished
    // delete file
    auxiliaryFile.delete();
    return tempXmlFile;
  }
  
  

  private void initialize(ICFile aXmlFile) throws IOException {
  	 name = aXmlFile.getName();
  	 setXmlFile(aXmlFile);
  	 InputStream inputStream = aXmlFile.getFileValue();
  	 initialize(inputStream);
  }
  	 
  private void initialize(ZipInputStream inputStream) throws IOException {
  	// do not close zip input streams
  	try {
  		XMLParser parser = new XMLParser();
  		document = parser.parse(inputStream);
  	}
    catch (IllegalDataException illEx) {
    	document = null;
    	xmlFileId = -1;
    	throw new IOException("[XMLData] input stream could not be parsed because of some illegal data within the data. Message is: " + illEx.getMessage());
    }
  	catch (XMLException ex)  {
      document = null;
      xmlFileId = -1;
      throw new IOException("[XMLData] input stream could not be parsed. Message is: " + ex.getMessage());
  	}
  }
  
  public void initialize(InputStream inputStream) throws IOException {
    try {
      XMLParser parser = new XMLParser();
      document = parser.parse(inputStream);
    }
    catch (IllegalDataException illEx) {
    	document = null;
    	xmlFileId = -1;
    	throw new IOException("[XMLData] input stream could not be parsed because of some illegal data within the data. Message is: " + illEx.getMessage());
    }
    catch (XMLException ex)  {
      document = null;
      xmlFileId = -1;
      throw new IOException("[XMLData] input stream could not be parsed. Message is: " + ex.getMessage());
    }
     finally { 
        close(inputStream);
     }
  } 
    
  private void initialize(String path) throws IOException {
  	File file = new File(path);
  	if (! (file.exists() && file.canRead() && file.isFile())) {
  		throw new IOException("[XMLData] File could not be opened");
  	}
  	initialize(file);
  }
  	
  	
  private void initialize(File file) throws IOException {
  	InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
  	initialize(inputStream);
  }
  	
    
  private ICFile getXMLFile(int fileId)  {
  	if (xmlFile != null) {
  		return xmlFile;
  	}
    try {
      ICFileHome home = (ICFileHome) IDOLookup.getHome(ICFile.class);
      ICFile tempXmlFile = home.findByPrimaryKey(new Integer(fileId));
      return tempXmlFile;
    }
    // FinderException, RemoteException
    catch(Exception ex){
      System.err.println("[XMLData]: Can't restrieve file with id "+ fileId + "Message is:" + ex.getMessage());
      ex.printStackTrace(System.err); 
      return null;
    }
  } 
  
  private ICFile getNewXMLFile()  {
    try {
      ICFileHome home = (ICFileHome) IDOLookup.getHome(ICFile.class);
      ICFile tempXmlFile = home.create();
      return tempXmlFile;
    }
    // FinderException, RemoteException
    catch (Exception ex)  {
      throw new RuntimeException("[XMLData]: Message was: " + ex.getMessage());
    }
    
  }
  
  private void close(InputStream input) {
  	try {
			if (input != null) {
				input.close();
			}
		}
		catch (IOException io) {
			// do not hide an existing exception
		}
  }		
  
  private void close(OutputStream output) {
  	try {
  		if (output != null) {
  			output.close();
  		}
  	}
  	catch (IOException io) {
		// do not hide an existing exception
  	}
  }

  /**
  * @return
  */
  public int getXmlFileId() {
   return xmlFileId;
  }

  /**
  * @param i
  */
  public void setXmlFileId(int i) {
   xmlFileId = i;
   // check existing file 
   if ((xmlFile != null) && (((Integer)xmlFile.getPrimaryKey()).intValue() != i)) {
   	// different file is set therefore set existing xmlFile to null
   	xmlFile = null;
   }
  }

  public void setXmlFile(ICFile file) {
  	this.xmlFile = file;
  	xmlFileId = ((Integer)xmlFile.getPrimaryKey()).intValue();
  }
  
  
  public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException {
  	return writer.write(this, iwc);
  }

  public Object read(ObjectReader reader, IWContext iwc) throws RemoteException {
  	return reader.read(this, iwc);
  }
  
  public void add(XMLData data) {
  	XMLElement dataRootElement = data.getDocument().getRootElement();
  	dataRootElement = dataRootElement.detach();
  	getDocument().getRootElement().addContent(dataRootElement);  	
  }
}
