/*
 * Created on 17.3.2003
 *
 **
 */
package com.idega.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.idega.core.data.ICFile;
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
	
	public MemoryFileBuffer writeToBuffer(HashMap tagmap, String xmlTemplateFileURL)throws DocumentException,IOException,SAXException,ParserConfigurationException{
		return writeToBuffer(getDocument(PageSize.A4), tagmap, xmlTemplateFileURL);
	}
	
	public MemoryFileBuffer writeToBuffer(Document document,HashMap tagmap, String xmlTemplateFileURL)throws DocumentException,IOException,SAXException,ParserConfigurationException{
		MemoryFileBuffer bout = new MemoryFileBuffer();
		OutputStream OS = new MemoryOutputStream(bout);
		InputStream IS = new MemoryInputStream(bout);
		PdfWriter.getInstance(document, OS);
		document.open();
		
		parseTagMap(document,tagmap, xmlTemplateFileURL);
		
		document.close();
		OS.close();
		IS.close();
		return bout;		
	}
	
	public void parseTagMap(Document document, HashMap tagMap, String xmlTemplateFileURL)throws SAXException,ParserConfigurationException,IOException{
		javax.xml.parsers.SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		SAXmyHandler handler = new SAXmyHandler(document,tagMap);
		handler.setControlOpenClose(false); 
		parser.parse(xmlTemplateFileURL,handler);
	}
	
	public int writeToDatabase(HashMap tagmap, String xmlTemplateFileURL) {
		try {
			MemoryFileBuffer bout = writeToBuffer(tagmap, xmlTemplateFileURL);
			InputStream is = new MemoryInputStream(bout);
			
			ICFile pdfFile = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();
			pdfFile.setFileValue(is);
			pdfFile.setMimeType("application/pdf");
			pdfFile.setName("document.pdf");
			pdfFile.setFileSize(bout.length());
			pdfFile.insert();
			return pdfFile.getID();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
		catch (DocumentException e) {
			e.printStackTrace();
			return -1;
		}
		catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		catch (SAXException e) {
			e.printStackTrace();
			return -1;
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public Document getDocument(Rectangle size){
		return new Document(size);
	}
	
	public static  float getPointsFromMM(float millimeters) {
		float pointPerMM = 72 / 25.4f;
		return millimeters * pointPerMM;
	}
}