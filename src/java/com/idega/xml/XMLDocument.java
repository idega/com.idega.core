/*
 * $Id: XMLDocument.java,v 1.6 2005/12/16 17:00:41 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import java.io.Serializable;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLDocument implements Serializable {
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -7635890602981794914L;
Document _doc = null;
  
  public XMLDocument(XMLElement element) {
    Element el = (Element)element.getElement();
    if (el != null)
      _doc = new Document(el);
  }
  /**
   * This object really only accepts a org.jdom.Document type but is declared Object becaluse of jdom dependency issues.
   * @param oDocument a Document instance
   * @throws ClassCastException if object not of correct type
   */
  public XMLDocument(Object oDocument) {
  	Document doc = (Document)oDocument;
    _doc = doc;
  }

  /**
   * This object really only accepts a org.jdom.Document type but is declared Object becaluse of jdom dependency issues.
   * @param oDocument the Document instance to set
   * @throws ClassCastException if object not of correct type
   */  
  public void setDocument(Object oDocument) {
  	Document doc = (Document)oDocument;
    _doc = doc;
  }
  /**
   * This object really only returns a org.jdom.Document type but is declared Object becaluse of jdom dependency issues.
   * @return the set Document instance
   */
  public Object getDocument() {
    return(_doc);
  }

  public XMLElement getRootElement() {
    if (_doc != null)
      return(new XMLElement(_doc.getRootElement()));

    return(null);
  }

  public void setRootElement(XMLElement element) {
    if (_doc != null) {
      Element el = (Element)element.getElement();
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
