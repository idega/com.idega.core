//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.core;


import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;
import com.idega.xml.XMLParser;
import com.idega.xml.XMLOutput;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.8 - Under development
*/
public class ICProperty{



  private XMLDocument xmlDocument;
  private File xmlFile;
  private XMLElement rootElement;
  //private static String rootElementTag = "plist";
  private static String propertyString = "property";
  private static String keyString = "key";
  private static String valueString = "value";
  private static String typeString = "type";
  private static String stringString = "java.lang.String";

  public ICProperty(){
  }

   public ICProperty(ICPropertyList list){
//     load(path);
   }

   public void setProperty(String key, Object value){
      setProperty(key,value.toString(),value.getClass().getName());
   }

   public void setProperty(String key, String value){
      setProperty(key,value,stringString);
   }

   public void setProperty(String key, int value){
      setProperty(key, new Integer(value));
   }

   public void setProperty(String key, String value,String type){
      XMLElement property = findPropertyElement(key);
      if(property==null){
        addProperty(key,value,type);
      }
      else{
        property.removeChild(valueString);
        property.removeChild(typeString);
        setProperty(property,value,type);
      }
   }

   public void addProperty(String key, String value){
      addProperty(key,value,stringString);
   }

   public void addProperty(String key, Object value){
      addProperty(key,value.toString(),value.getClass().getName());
   }

  public void addProperty(String key, int value){
      addProperty(key,new Integer(value));
  }

   public void addProperty(String key, String value,String type){
      XMLElement property = new XMLElement(propertyString);
      XMLElement keyElement = new XMLElement(keyString);
      keyElement.addContent(key);
      property.addContent(keyElement);
      setProperty(property,value,type);
      rootElement.addContent(property);
   }

   private void setProperty(XMLElement property, String value,String type){
      XMLElement valueElement = new XMLElement(valueString);
      valueElement.addContent(value);
      XMLElement typeElement = new XMLElement(typeString);
      typeElement.addContent(type);
      property.addContent(valueElement);
      property.addContent(typeElement);
   }

   public String getPropertyType(String key){
      return findPropertyElement(key).getChild(typeString).getText();
   }

  /**
   *
   */
  public String getProperty(String key) {
    try {
      return(findPropertyElement(key).getChild(valueString).getText());
    }
    catch(NullPointerException ex) {
      return(null);
    }
  }

  /**
   * Returns null if no match
   */
  private XMLElement findPropertyElement(String key){
    List list = rootElement.getChildren();
    Iterator iter = list.iterator();

    while(iter.hasNext()){
      XMLElement property = (XMLElement)iter.next();
      XMLElement keyElement = property.getChild(keyString);
      if(keyElement.getText().equalsIgnoreCase(key)){
        return(property);
      }
    }

    return(null);
   }




}
