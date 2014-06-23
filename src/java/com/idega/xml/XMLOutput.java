/*
 * $Id: XMLOutput.java,v 1.12 2006/04/09 12:13:14 laddi Exp $
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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.xml.XmlUtil;

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
		this._output = new XMLOutputter();
	}

	/**
	 *
	 */
	public XMLOutput(String indent, boolean newlines) {
		Format f = (newlines) ? XmlUtil.getPrettyFormat(IWMainApplication.getDefaultIWMainApplication().getSettings()) : Format.getCompactFormat();
		f.setIndent(indent);
		this._output = new XMLOutputter(f);
	}

	/**
	 *
	 */
	public void setLineSeparator(String seperator) {
		if (this._output != null) {
			Format f = this._output.getFormat();
			if (f != null) {
				f.setLineSeparator(seperator);
				this._output.setFormat(f);
			}
		}
	}

	/**
	 *
	 */
	public void setTextNormalize(boolean normalize) {
		if (this._output != null && normalize) {
			Format f = this._output.getFormat();
			if (f != null) {
				f.setTextMode(Format.TextMode.NORMALIZE);
				this._output.setFormat(f);
			}
		}
	}

	/**
	 *
	 */
	public void output(XMLDocument document, OutputStream stream) throws IOException {
		if (this._output != null) {
			this._output.output((Document) document.getDocument(), stream);
		}
	}

	public String outputString(XMLElement element) {
		if (this._output != null) {
			Element el = (Element) element.getElement();
			if (el != null){
				this._output.getFormat().setOmitDeclaration(true);
				return (this._output.outputString(el));
			}
			else {
				return (null);
			}
		}
		else {
			return (null);
		}
	}

	public String outputString(XMLDocument document) throws IOException {
		StringWriter writer = new StringWriter();
		Document doc = (Document) document.getDocument();
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(this._output.getFormat());
		outputter.output(doc, writer);
		return writer.toString();
	}

	public void setEncoding(String encoding) {
		if (this._output != null) {
			Format f = this._output.getFormat();
			if (f != null) {
				f.setEncoding(encoding);
				this._output.setFormat(f);
			}
		}
	}

	public void setSkipEncoding(boolean skip) {
		if (this._output != null) {
			Format f = this._output.getFormat();
			if (f != null) {
				f.setOmitEncoding(skip);
				this._output.setFormat(f);
			}
		}
	}
}