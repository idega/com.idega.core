/*
 * $Id: XMLDocument.java,v 1.8 2006/04/09 12:13:14 laddi Exp $
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
    if (el != null) {
			this._doc = new Document(el);
		}
  }

  public XMLDocument(XMLElement element, XMLDocType type) {
	    Element el = (Element)element.getElement();
	    DocType ty = (DocType) type.getDocType();
	    if (el != null & ty != null) {
				this._doc = new Document(el, ty);
			}
	  }

  
  /**
   * This object really only accepts a org.jdom.Document type but is declared Object becaluse of jdom dependency issues.
   * @param oDocument a Document instance
   * @throws ClassCastException if object not of correct type
   */
  public XMLDocument(Object oDocument) {
  	Document doc = (Document)oDocument;
    this._doc = doc;
  }

  /**
   * This object really only accepts a org.jdom.Document type but is declared Object becaluse of jdom dependency issues.
   * @param oDocument the Document instance to set
   * @throws ClassCastException if object not of correct type
   */  
  public void setDocument(Object oDocument) {
  	Document doc = (Document)oDocument;
    this._doc = doc;
  }
  /**
   * This object really only returns a org.jdom.Document type but is declared Object becaluse of jdom dependency issues.
   * @return the set Document instance
   */
  public Object getDocument() {
    return(this._doc);
  }

  public XMLElement getRootElement() {
    if (this._doc != null) {
			return(new XMLElement(this._doc.getRootElement()));
		}

    return(null);
  }

  public void setRootElement(XMLElement element) {
    if (this._doc != null) {
      Element el = (Element)element.getElement();
      if (el != null) {
				this._doc.setRootElement(el);
			}
    }
  }
  
  public DocType getDocType(){
	if (this._doc != null) {
		return this._doc.getDocType();
	}
		
	return(null);
  }
  
  public void setDocType(DocType docType){
  	if(this._doc != null){
  		this._doc.setDocType(docType);
  	}
  }
  
}
