/*
 * $Id: IWPropertyList.java,v 1.9 2001/08/16 09:53:08 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import com.idega.util.FileUtil;
import com.idega.util.ListUtil;

/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 0.8 - Under development
 */
public class IWPropertyList {
  private Document xmlDocument;
  private File xmlFile;
  private Element parentElement;

  private Element mapElement;
  private static String rootElementTag = "pxml";
   static String dictTag = "dict";
   static String mapTag = "map";
   static String nameTag = "name";
   static String arrayTag = "array";
   static String keyTag = "key";
   static String valueTag = "value";
   static String typeTag = "type";
   static String stringTag = "string";
   static String stringString = "java.lang.String";

  IWPropertyList(){
  }

  IWPropertyList(Element parentElement){
    this.parentElement=parentElement;
  }

  public IWPropertyList(String fileNameWithPath) {
    load(fileNameWithPath);
  }

  /**
   * Creates the file and superfolders if createFileAndFolder is true
   */
  public IWPropertyList(String path,String fileNameWithoutFullPath,boolean createFileAndFolder) {
    File file=null;
    if(createFileAndFolder){
      try{
        file = new File(path,fileNameWithoutFullPath);
        if(!file.exists()){
          file = FileUtil.getFileAndCreateIfNotExists(path,fileNameWithoutFullPath);
          FileOutputStream stream = new FileOutputStream(file);
          char[] array = ((String)"<"+rootElementTag+"></"+rootElementTag+">").toCharArray();
          for (int i = 0; i < array.length; i++) {
            stream.write((int)array[i]);
          }
          stream.flush();
          stream.close();
        }


      }
      catch(IOException ex){
        ex.printStackTrace();
      }
    }
    else{
        file = new File(path+FileUtil.getFileSeparator()+fileNameWithoutFullPath);
    }
    load(file);
  }

  public IWPropertyList(File file){
    load(file);
  }

  Element getParentElement(){
    return parentElement;
  }

  Element getMapElement(){
    if(mapElement==null){
      mapElement = parentElement.getChild(mapTag);
      if(mapElement==null){
        Element dictElement = parentElement.getChild(dictTag);
        if(dictElement!=null){
          mapElement = new Element(mapTag);
          mapElement.setChildren(dictElement.getChildren());
          parentElement.removeContent(dictElement);
          parentElement.addContent(mapElement);
        }
      }
      if(mapElement==null){
        mapElement = new Element(mapTag);
        parentElement.addContent(mapElement);
      }
    }
    return mapElement;
  }

  public void setProperty(String key, Object value) {
    setProperty(key,value.toString(),value.getClass().getName());
  }

  public void setProperty(String key, int value) {
    setProperty(key,new Integer(value));
  }

  public void setProperty(String key, boolean value) {
    setProperty(key,new Boolean(value));
  }

  public void setProperty(String key, String value) {
    setProperty(key,value,stringString);
  }

  public void setProperty(String key,Object[] value){
    Element keyElement = findKeyElement(key);
    IWProperty.setProperty(keyElement,key,value,this);
  }

  public IWProperty getNewProperty(){
    return new IWProperty(this);
  }


  /**
   * Returns null if there is no IWProperty associated with the specific key
   */
  public IWProperty getIWProperty(String key){
    Element el = this.findKeyElement(key);
    if(el!=null){
      return new IWProperty(el,this);
    }
    return null;
  }

  /**
   * Returns null if there is no IWPropertyList associated with the specific key
   */
  public IWPropertyList getPropertyList(String key){
    Element keyElement = this.findKeyElement(key);
    if(keyElement!=null){
      return IWProperty.getPropertyList(keyElement);
    }
    return null;
  }

  /**
   * Creates a new IWPropertyList associated with the specific key
   */
  public IWPropertyList getNewPropertyList(String key){
    Element keyElement = this.findKeyElement(key);
    if(keyElement==null){
      keyElement = IWProperty.createKeyElement(this,key);
    }
    return IWProperty.getNewPropertyList(keyElement,this);
  }

  /**
   * Use to set an array property with only one "String" value to begin with
   */
  public void setArrayProperty(String key, Object value){
      setProperty(key,value,arrayTag);
  }


  void setProperty(String key, Object value,String type) {
    Element keyElement = findKeyElement(key);
    IWProperty.setProperty(keyElement,key,value,type,this);
  }

  private void addProperty(String key, String value) {
    addProperty(key,value,stringString);
  }

  private void addProperty(String key, Object value) {
    addProperty(key,value.toString(),value.getClass().getName());
  }

  private void addProperty(String key, int value) {
    addProperty(key,new Integer(value));
  }



  private void addProperty(String key, Object value,String type) {
    IWProperty.addProperty(key,value,type,this);
  }


  private void addNewProperty(Element key, String keyName,Object value,String type) {
    IWProperty.addNewProperty(key,keyName,value,type);
  }


  public String getPropertyType(String key) {
    return IWProperty.getPropertyType(this.findKeyElement(key));
  }

  /**
   * Returns null if key not found
   */
  public String getProperty(String key) {
    try {
      return findKeyElement(key).getChild(valueTag).getText();
    }
    catch(NullPointerException ex) {
      return null;
    }
  }

  /**
   * @return null if no match
   */
  static Element findKeyElement(Element startElement,String key) {
    List list = startElement.getChildren();
    Iterator iter = list.iterator();
    while(iter.hasNext()) {
      Element keyElement = (Element)iter.next();
      Element nameElement = keyElement.getChild(nameTag);
      if (nameElement.getText().equalsIgnoreCase(key)) {
        return keyElement;
      }
    }
    return null;
  }

  /**
   * @return null if no match
   */
  private Element findKeyElement(String key) {
    return  findKeyElement(getMapElement(),key);
  }

// added by Eirikur Hrafnsson eiki@idega.is
  protected List getKeys() {
    Element mapElement = getMapElement();
    if(mapElement!=null){
      List list = mapElement.getChildren();
      Iterator iter = list.iterator();
      List keys = new Vector();

      while(iter.hasNext()) {
        Element keyElement = (Element)iter.next();
        Element nameElement = keyElement.getChild(nameTag);
        keys.add( nameElement.getText() );
      }
      return keys;
    }
    else{
      return ListUtil.getEmptyList();
    }
  }

  public void load(String path) {
    File file =new File(path);
    load(file);
  }

  public void load(File file){
    SAXBuilder builder = new SAXBuilder(false);
    xmlFile = file;
    try{
      xmlDocument = builder.build(xmlFile);
      parentElement = xmlDocument.getRootElement();
      mapElement = getMapElement();

    }
    catch(JDOMException e) {
      e.printStackTrace();
    }
    catch(Throwable e) {
      e.printStackTrace();
    }
  }

  public void removeProperty(String key){
    Element element = this.findKeyElement(key);
    if(element!=null){
      if(mapElement!=null){
        mapElement.removeContent(element);
      }
    }
  }



  /**
   * Returns null if no match
   */

  private Element getArrayValueElement(Element arrayElement,Object value){
    List arrayList = arrayElement.getChildren();
    Iterator iter = arrayList.iterator();
    while (iter.hasNext()) {
      Element item = (Element)iter.next();
      if (IWProperty.valueContains(item,value)){
        return item;
      }
    }
    return null;
  }

  public Object getValueObject(Element valueElement){
    return getValueString(valueElement);
  }

  public String getValueString(Element valueElement){
    return valueElement.getText();
  }

  public Iterator iterator(){
    return getIWPropertyListIterator();
  }

  public IWPropertyListIterator getIWPropertyListIterator(){
    return new IWPropertyListIterator(this.getKeys().iterator(),this);
  }

  public void removeProperty(String key, Object value){
    Element element = this.findKeyElement(key);
    if(element!=null){
      Element typeElement = element.getChild(typeTag);
      Element valueElement = element.getChild(valueTag);
      /**
       * if it is an array
       */
      if(typeElement.getText().equals(arrayTag)){
        Element arrayElement = valueElement.getChild(arrayTag);
        Element newValueElement = this.getArrayValueElement(arrayElement,value);
        if(newValueElement!=null){
          arrayElement.removeContent(newValueElement);
        }
      }
      else{
        if(valueElement.getText().equals(value.toString())){
          if(mapElement!=null){
            mapElement.removeContent(element);
          }
        }
      }
    }
  }

  public void store() {
    try {
      store(new FileOutputStream(xmlFile));
    }
    catch(FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void store(OutputStream stream) {
    if(xmlDocument!=null){
      try {
        XMLOutputter outputter = new XMLOutputter("  ",true);
        outputter.setLineSeparator(System.getProperty("line.separator"));
        outputter.setTrimText(true);
        outputter.output(xmlDocument,stream);
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
  }
}
