/*
 * $Id: XMLOutput.java,v 1.9 2005/06/06 23:49:50 palli Exp $
 * 
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLOutput {

	XMLOutputter _output = null;

	/**
	 * 
	 */
	public XMLOutput() {
		_output = new XMLOutputter();
	}

	/**
	 * 
	 */
	public XMLOutput(String indent, boolean newlines) {
		Format f = (newlines) ? Format.getPrettyFormat() : Format.getCompactFormat();
		f.setIndent(indent);
		_output = new XMLOutputter(f);
	}

	/**
	 * 
	 */
	public void setLineSeparator(String seperator) {
		if (_output != null) {
			Format f = _output.getFormat();
			if (f != null) {
				f.setLineSeparator(seperator);
				_output.setFormat(f);
			}
		}
	}

	/**
	 * 
	 */
	public void setTextNormalize(boolean normalize) {
		if (_output != null && normalize) {
			Format f = _output.getFormat();
			if (f != null) {
				f.setTextMode(Format.TextMode.NORMALIZE);
				_output.setFormat(f);
			}
		}
	}

	/**
	 * 
	 */
	public void output(XMLDocument document, OutputStream stream) throws IOException {
		if (_output != null)
			_output.output((Document) document.getDocument(), stream);
	}

	public String outputString(XMLElement element) {
		if (_output != null) {
			Element el = (Element) element.getElement();
			if (el != null)
				return (_output.outputString(el));
			else
				return (null);
		}
		else
			return (null);
	}

	public String outputString(XMLDocument document) throws IOException {
		StringWriter writer = new StringWriter();
		Document doc = (Document) document.getDocument();
		XMLOutputter outputter = new XMLOutputter();
		outputter.output(doc, writer);
		return writer.toString();
	}

	public void setEncoding(String encoding) {
		if (_output != null) {
			Format f = _output.getFormat();
			if (f != null) {
				f.setEncoding(encoding);
				_output.setFormat(f);
			}
		}
	}

	public void setSkipEncoding(boolean skip) {
		if (_output != null) {
			Format f = _output.getFormat();
			if (f != null) {
				f.setOmitEncoding(skip);
				_output.setFormat(f);
			}
		}
	}
}