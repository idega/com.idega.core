/*
 * $Id: XMLDocument.java,v 1.4 2003/08/05 10:05:43 gummi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLDocument {
  Document _doc = null;
  
  public XMLDocument(XMLElement element) {
    Element el = element.getElement();
    if (el != null)
      _doc = new Document(el);
  }

  XMLDocument(Document doc) {
    _doc = doc;
  }

  void setDocument(Document doc) {
    _doc = doc;
  }

  Document getDocument() {
    return(_doc);
  }

  public XMLElement getRootElement() {
    if (_doc != null)
      return(new XMLElement(_doc.getRootElement()));

    return(null);
  }

  public void setRootElement(XMLElement element) {
    if (_doc != null) {
      Element el = element.getElement();
      if (el != null)
        _doc.setRootElement(el);
    }
  }
  
  public DocType getDocType(){
	if (_doc != null)
		return _doc.getDocType();
		
	return(null);
  }
  
  public void setDocType(DocType docType){
  	if(_doc != null){
  		_doc.setDocType(docType);
  	}
  }
  
}
