/*
 * $Id: XMLElement.java,v 1.16 2004/10/29 12:47:23 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.Text;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLElement {
  Element _element = null;

  public XMLElement(String name) {
    _element = new Element(name);
  }

  XMLElement(Element element) {
    _element = element;
    if (_element == null)
      System.out.println("Creating XMLElement with element == null");
  }

  Element getElement() {
    return(_element);
  }

  void setElement(Element element) {
    _element = element;
  }

  public void setAttribute(String name, String value) {
    if (_element != null)
      _element.setAttribute(name,value);
  }

  public XMLElement addContent(XMLElement element) {
    if (_element != null) {
      Element el = element.getElement();
      if (el != null) {
      		_element.addContent(el.detach());
      }
    }
    return this;
  }

  public XMLElement getChild(String name) {
    if (_element != null) {
      Element el = _element.getChild(name);
      if (el != null)
        return(new XMLElement(el));
    }

    return(null);
  }

  public boolean hasChildren() {
    if (_element != null)
      return(! _element.getChildren().isEmpty());

    return(false);
  }

  public List getChildren() {
    if (_element != null) {
      List li = _element.getChildren();
      Vector res = new Vector();
      Iterator it = li.iterator();

      while (it.hasNext()) {
        Element el = (Element)it.next();
        if (el != null) {
          XMLElement xmlel = new XMLElement(el);
          res.add(xmlel);
        }
      }

      return(res);
    }

    return(null);
  }

  public List getAttributes() {
    if (_element != null) {
      List li = _element.getAttributes();
      Vector res = new Vector();
      Iterator it = li.iterator();

      while (it.hasNext()) {
        Attribute at = (Attribute)it.next();
        XMLAttribute xmlat = new XMLAttribute(at);
        res.add(xmlat);
      }

      return(res);
    }

    return(null);
  }
  
  public String getAttributeValue(String name) {
  	XMLAttribute attribute = getAttribute(name);
  	if (attribute == null) {
  		return null;
  	}
  	return attribute.getValue();
  }

  public XMLAttribute getAttribute(String name) {
    if (_element != null) {
      Attribute at = _element.getAttribute(name);
      if (at != null)
        return(new XMLAttribute(at));
      else
        return(null);
    }

    return(null);
  }

  public String getName() {
    if (_element != null)
      return(_element.getName());

    return(null);
  }

  public String getTextTrim() {
    if (_element != null)
      return(_element.getTextTrim());
      
    return(null);
  }
  
  public String getTextTrim(String name) {
  	XMLElement element  = this.getChild(name);
  	return (element == null) ? null : element.getTextTrim();
  }
  
  public String getText(String name) {
  	XMLElement element = this.getChild(name);
  	return (element == null) ? null : element.getText();
  }

  public String getText() {
    if (_element != null)
      return(_element.getText());

    return(null);
  }

  public List getChildren(String name) {
    if (_element != null) {
      List li = _element.getChildren(name);
      Vector res = new Vector();
      Iterator it = li.iterator();

      while (it.hasNext()) {
        Element el = (Element)it.next();
        XMLElement xmlel = new XMLElement(el);
        res.add(xmlel);
      }

      return(res);
    }

    return(null);
  }

  public boolean removeContent(XMLElement element) {
    if (_element != null) {
      Element el = element.getElement();
      if (el != null)
        return(_element.removeContent(el));
    }

    return(false);
  }

  public XMLElement setText(String text) {
    if (_element != null)
      _element.setText(text);

    return this;
  }

  public XMLElement addContent(String text) {
    if (_element != null)
      _element.addContent(text);

    return this;
  }
  
  public XMLElement addContent(XMLCDATA data) {
  	if (_element != null)
  		_element.addContent(data.getContentData());
  		
  	return this;
  }
  
  /** sets the content of the first child or creates a child if no child exists
   * @author thomas
   */
  public XMLElement setContent(String name, String value) {
  	XMLElement child = getChild(name);
  	if (child == null) {
  		child = new XMLElement(name);
  	}
  	// do not use addContent!
  	child.setText(value);
  	return this;
  }
  		
  /** adds a child with the specified value 
   * @author Thomas
   */
  public XMLElement addContent(String name, String value) {
  	XMLElement element = new XMLElement(name);
  	element.addContent(value);
  	return addContent(element);
  }
  
  /**
   * A method that returns the first instance of CDATA that exists in this Element.
   * Return null if none is found. Should rather use getContent and check for
   * all CDATA content. 
   * 
   * @return The first CDATA instance in the content for this Element, null otherwise. 
   */
  public XMLCDATA getXMLCDATAContent() {
  	if (_element == null)
  		return null;

		List li = _element.getContent();
		Iterator it = li.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof CDATA) {
				return new XMLCDATA((CDATA)obj);	
			}
		}  	
		
		return null;
  }
  
  public List getContent() {
  	if (_element == null)
  		return null;
  		
		List ret = new Vector();
		List li = _element.getContent();
		Iterator it = li.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof Element) {
				XMLElement el = new XMLElement((Element)obj);
				ret.add(el);
			}
			else if (obj instanceof CDATA) {
				XMLCDATA data = new XMLCDATA((CDATA)obj);
				ret.add(data);
			}
			else if (obj instanceof Text) {
				String text = ((Text)obj).getText();
				ret.add(text);				
			}
		}
		
		return ret;
  }

  public XMLElement setAttribute(XMLAttribute attribute) {
    if (_element != null) {
      Attribute at = attribute.getAttribute();
      if (at != null)
        _element.setAttribute(at);
    }

    return this;
  }

  public boolean removeAttribute(String name) {
    if (_element != null)
      return(_element.removeAttribute(name));

    return(false);
  }

  public boolean removeChild(String name) {
    if (_element != null)
      return(_element.removeChild(name));

    return(false);
  }

  public boolean removeChildren() {
    if (_element != null) {
      return(_element.removeContent()) != null;
    }
    return(false);
  }
  
  public boolean removeParent() {
	if (_element != null) {
		Element parent = _element.getParentElement();
		if(parent!=null){
			return parent.removeChild(_element.getName());
		}
		return true;
	}
	return(false);
  }

  public XMLElement setChildren(List children) {
    if (_element != null) {
      if (children != null) {
        Iterator it = children.iterator();
        Vector res = new Vector();
        while (it.hasNext()) {
          XMLElement xmlel = (XMLElement)it.next();
          Element el = xmlel.getElement();
          if (el != null)
          res.add(el);
        }

        _element.setContent(res);
      }
    }

    return this;
  }

  public synchronized Object clone() {
    if (_element == null)
      return(null);

    Element el = (Element)_element.clone();
    XMLElement element = new XMLElement(el);
    return element;
  }
  
  /**
   * Returns parent, or null if this element is currently not attached to another element.
   * @author Thomas
   */
  public XMLElement getParent() {
  	if (_element == null) {
  		return null;
  	}
  	Element parent = _element.getParentElement();
  	return (parent == null) ? null : new XMLElement(parent);
  }
  
  /** Returns an iterator of all children (recursive) of this element.
   * 	 The order of the returned elements corresponds to the result of a breadth first search.
   * @author Thomas
   */
  public Iterator allChildrenBreadthFirstIterator() {
  	return new Iterator() {
  		
  		private Iterator iterator = null;
  		
  		public Object next() {
  			checkInitialization();
  			return iterator.next();
  		}
  		
  		public boolean hasNext() {
  			checkInitialization();
  			return iterator.hasNext();
  		}
  		
  		public void remove() {
  			checkInitialization();
  			iterator.remove();
  		}
 
			private void checkInitialization() {
  			 if (iterator == null) {
  			 	List allChildren = new ArrayList();
  			 	collectChildrenBreadthFirstMethod(XMLElement.this, allChildren);
  			 	iterator = allChildren.iterator();
  			 }
  		}
  			 	
				
			private void collectChildrenBreadthFirstMethod(XMLElement element, List allChildren) {
					// breadth first search
  				allChildren.add(element);
  				List localChildren = element.getChildren();
  				Iterator iterator = localChildren.iterator();
  				while (iterator.hasNext()) {
  					XMLElement elementItem = (XMLElement) iterator.next();
  					collectChildrenBreadthFirstMethod(elementItem, allChildren);
  				}
  			}
  	};
  }
  
  /**do not remove that method even if it is not used at the moment - it's so hard to find that method.
   * Use this method to get really rid of the parent of an element.
   * @author Thomas
   */
  public XMLElement detach()	{
  	if (_element != null)	{
  		return new XMLElement((Element)_element.detach());
  	}
  	return null;
  }
  
  public void setName(String newName) {
  	_element.setName(newName);
  }
		
}
