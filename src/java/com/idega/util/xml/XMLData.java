package com.idega.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.idega.core.data.ICFile;
import com.idega.core.data.ICFileHome;
import com.idega.data.IDOLookup;

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
  
  private XMLDocument document = null;
  private int xmlFileId = -1;
  
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
    
  
  public void initialize(int xmlFileId) {
    ICFile xmlFile = getXMLFile(xmlFileId);
    initialize(xmlFile);
  }
  
  public void inittialize(XMLFile xmlFile) {
    // force to initialize with an instance of XMLFile
    initialize(xmlFile);
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
    
  
  public void store() throws IOException  {
    // create or fetch existing file
    ICFile xmlFile = (xmlFileId < 0) ? getNewXMLFile() : getXMLFile(xmlFileId);
    xmlFile.setMimeType("text/xml");
    OutputStream output = xmlFile.getFileValueForWrite();
    XMLOutput xmlOutput = new XMLOutput("  ", true);
    xmlOutput.setLineSeparator(System.getProperty("line.separator"));
    xmlOutput.setTextNormalize(true);
    // do not use document directly use accessor method
    XMLDocument document = getDocument();
    xmlOutput.output(document, output);
    output.close();
    //FileWriter writer = new FileWriter("thomasTest.xml");
    xmlOutput.output(document, System.out);
    
    try {
      if (xmlFileId < 0) {
        xmlFile.store();
        xmlFileId = ((Integer) xmlFile.getPrimaryKey()).intValue();
      }
      else {
        xmlFile.update();
      }
    }
    catch (Exception ex)  {
    }
  }
 
  private void initialize(ICFile xmlFile) {
    XMLParser parser = new XMLParser();
    InputStream inputStream = null;
    try {
      inputStream = xmlFile.getFileValue();
      document = parser.parse(inputStream);
      inputStream.close();
      xmlFileId = ( (Integer) xmlFile.getPrimaryKey()).intValue();
    }
    catch (Exception ex)  {
      System.err.println("[QueryResult]: input stream could not be parsed. Message was: " + ex.getMessage());
      document = null;
      xmlFileId = -1;
      try { 
        inputStream.close();
      }
      catch (IOException ioEx)  {
        System.err.println("[QueryResult]: input stream could not be closed. Message was: "+ ex.getMessage());
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
      throw new RuntimeException("[XMLData]: Message was: " + ex.getMessage());
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

}
