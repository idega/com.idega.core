/*
 * Created on May 30, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.io;

import java.util.Map;
import java.util.Properties;
import org.xml.sax.Attributes;
import com.lowagie.text.*;
import com.lowagie.text.xml.XmlPeer;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */


/**
 * The <CODE>Tags</CODE>-class maps several XHTML-tags to iText-objects.
 */
public class SAXpdfHandler extends SAXiTextHandler {
    
/** This hashmap contains all the custom keys and peers. */
	protected Map myTags;
    
/**
 * Constructs a new SAXiTextHandler that will translate all the events
 * triggered by the parser to actions on the <CODE>Document</CODE>-object.
 *
 * @paramdocumentthis is the document on which events must be triggered
 */
    
	public SAXpdfHandler(DocListener document, Map myTags) {
		super(document);
		this.myTags = myTags;
	}
    
/**
 * This method gets called when a start tag is encountered.
 *
 * @paramnamethe name of the tag that is encountered
 * @paramattrsthe list of attributes
 */
    
	public void startElement(String uri, String lname, String name, Attributes attrs) {
		//System.err.println("Start: " + name);
		if (myTags.containsKey(name)) {
			XmlPeer peer = (XmlPeer) myTags.get(name);
			handleStartingTags(peer.getTag(), peer.getAttributes(attrs));
		}
		else {
			Properties attributes = new Properties();
			if (attrs != null) {
				for (int i = 0; i < attrs.getLength(); i++) {
					String attribute = attrs.getQName(i);
					attributes.setProperty(attribute, attrs.getValue(i));
				}
			}
			handleStartingTags(name, attributes);
		}
	}
    
/**
 * This method gets called when an end tag is encountered.
 *
 * @paramnamethe name of the tag that ends
 */
    
	public void endElement(String uri, String lname, String name) {
		//System.err.println("Stop: " + name);
		if (myTags.containsKey(name)) {
			XmlPeer peer = (XmlPeer) myTags.get(name);
			handleEndingTags(peer.getTag());
		}
		else {
			handleEndingTags(name);
		}
	}
}