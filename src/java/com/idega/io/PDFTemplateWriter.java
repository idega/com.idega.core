/*
 * Created on 17.3.2003
 *
 **
 */
package com.idega.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.SAXmyHandler;

/**
 * @author aron
 */
public class PDFTemplateWriter {
	
	public MemoryFileBuffer writeToBuffer(Document document,HashMap tagmap, String xmlTemplateFileUrl)throws DocumentException,IOException,SAXException,ParserConfigurationException{
	
		MemoryFileBuffer bout = new MemoryFileBuffer();
		OutputStream OS = new MemoryOutputStream(bout);
		InputStream IS = new MemoryInputStream(bout);
		PdfWriter writer = PdfWriter.getInstance(document, OS);
		document.open();
		
		parseTagMap(document,tagmap, xmlTemplateFileUrl);
		
		document.close();
		OS.close();
		IS.close();
		return bout;		
	}
	
	public void parseTagMap(Document document, HashMap tagMap, String templateFileUrl)throws SAXException,ParserConfigurationException,IOException{
		javax.xml.parsers.SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		SAXmyHandler handler = new SAXmyHandler(document,tagMap);
		handler.setControlOpenClose(false); 
		parser.parse(templateFileUrl,handler);
	}
	
	public Document getDocument(Rectangle size){
		return new Document(size);
	}
	
	public static  float getPointsFromMM(float millimeters) {
		float pointPerMM = 72 / 25.4f;
		return millimeters * pointPerMM;
	}
}
