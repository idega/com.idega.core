package com.idega.xml;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
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
  
  XMLDocument document = null;
  
  public void initialize(XMLFile xmlFile) {
    XMLParser parser = new XMLParser();
    try {
      BufferedInputStream inputStream = new BufferedInputStream(xmlFile.getFileValue());
      document = parser.parse(inputStream);
      inputStream.close();
    }
    catch (Exception ex)  {
      System.err.println("[QueryResult]: input stream could not be parsed. Message was: " + ex.getMessage());
      document = null;
    }      
  }
    
  public Collection getFields() {
    XMLElement element = document.getRootElement();
    Collection coll = element.getChildren();
    Collection coll1 = element.getChildren("name");
    Collection coll2 = element.getChildren("field");
    Collection coll3 = element.getChildren("eee");
    return coll;
  }

}
