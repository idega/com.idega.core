package com.idega.util.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.sql.SQLException;

import com.idega.core.data.ICFile;
import com.idega.core.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;

import com.idega.util.FileUtil;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
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
public class XMLData {
  
  private final String DEFAULT_ROOT = "default_root";
  
  public final String DEFAULT_NAME = "xml_data";
  
  public final String AUXILIARY_FOLDER = "auxiliaryXMLDataFolder";
  public final String AUXILIARY_FILE = "auxililary_xml_data_file_";
  public final String XML_EXTENSION = ".xml";
  
  private XMLDocument document = null;
  private int xmlFileId = -1;
  private String name = null;
  
  public static XMLData getInstanceForFile(int xmlFileId) {
    XMLData data = new XMLData();
    data.initialize(xmlFileId);
    return data;
  }
  
  public static XMLData getInstanceForFile(XMLFile xmlFile) {
    XMLData data = new XMLData();
    data.initialize(xmlFile);
    return data;
  }
  
  public static XMLData getInstanceWithoutExistingFile()  {
    XMLData data = new XMLData();
    return data;
  } 
  
  public static XMLData getInstanceWithoutExistingFile(String name) {
    XMLData data = XMLData.getInstanceWithoutExistingFile();
    data.setName(name);
    return data;
  }
 
  public void initialize(int xmlFileId) {
    ICFile xmlFile = getXMLFile(xmlFileId);
    initialize(xmlFile);
  }
  
  public void inittialize(XMLFile xmlFile) {
    // force to initialize with an instance of XMLFile
    initialize(xmlFile);
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
      document = new XMLDocument(new XMLElement(DEFAULT_ROOT));
    }  
    return document;
  }
  
  public void setDocument(XMLDocument document) {
    this.document = document;
  }        
  
  public ICFile store(IWApplicationContext iwac) throws IOException {
    // create or fetch existing ICFile
    ICFile xmlFile = (xmlFileId < 0) ? getNewXMLFile() : getXMLFile(xmlFileId);
    xmlFile.setMimeType("text/xml");
    xmlFile.setName(getName());
    try {
      xmlFile.store();
    }
    catch (IDOStoreException ex)  {
      System.err.println("[XMLData] problem storing ICFile Message is: "+ex.getMessage());
      ex.printStackTrace(System.err);
      throw new IOException("xml file could not be stored");
    }
    if (xmlFileId < 0) {
      xmlFileId = ((Integer)xmlFile.getPrimaryKey()).intValue();
      // the default name uses the id, therefore set again and store again
      if (name == null) {
        xmlFile.setName(getName());
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
    IWMainApplication mainApp = iwac.getApplication();
    StringBuffer path = new StringBuffer(mainApp.getApplicationRealPath());
           
    path.append(mainApp.getIWCacheManager().IW_ROOT_CACHE_DIRECTORY)
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
    xmlOutput.setEncoding("iso-8859-1");
    // do not use document directly use accessor method
    XMLDocument document = getDocument();
    try {
      xmlOutput.output(document, outputStream);
    }
    catch (IOException ex) {
      System.err.println("[XMLData] problem writing to file. Message is: "+ex.getMessage());
      ex.printStackTrace(System.err);
      outputStream.close();
      throw new IOException("xml file could not be stored");
    }
    outputStream.close();
    // writing finished
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
    xmlFile.setFileSize(size);
    xmlFile.setFileValue(inputStream);
//    try {
      //xmlFile.update();
	  xmlFile.store();
//    }
//    catch (SQLException ex)  {
//      System.err.println("[XMLData] problem storing ICFile Message is: "+ex.getMessage());
//      ex.printStackTrace(System.err);
//      throw new IOException("xml file could not be stored");
//    }
    inputStream.close();
    // reading finished
    // delete file
    auxiliaryFile.delete();
    return xmlFile;
  }
 
  private void initialize(ICFile xmlFile) {
    InputStream inputStream = null;
    try {
      name = xmlFile.getName();
      XMLParser parser = new XMLParser();
      inputStream = xmlFile.getFileValue();
      document = parser.parse(inputStream);
      inputStream.close();
      xmlFileId = ( (Integer) xmlFile.getPrimaryKey()).intValue();
    }
    catch (Exception ex)  {
      System.err.println("[QueryResult]: input stream could not be parsed. Message was: " + ex.getMessage());
      ex.printStackTrace(System.err);
      document = null;
      xmlFileId = -1;
      try { 
        inputStream.close();
      }
      catch (IOException ioEx)  {
        System.err.println("[QueryResult]: input stream could not be closed. Message was: "+ ex.getMessage());
        ioEx.printStackTrace(System.err);
      }
    }      
  } 
    
    
  private ICFile getXMLFile(int fileId)  {
    try {
      ICFileHome home = (ICFileHome) IDOLookup.getHome(ICFile.class);
      ICFile xmlFile = (ICFile) home.findByPrimaryKey(new Integer(fileId));
      return xmlFile;
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
      ICFile xmlFile = (ICFile) home.create();
      return xmlFile;
    }
    // FinderException, RemoteException
    catch (Exception ex)  {
      throw new RuntimeException("[XMLData]: Message was: " + ex.getMessage());
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
  }

}
