/*
 * Created on 17.3.2003
 *
 **
 */
package com.idega.io;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.SAXException;

import com.idega.core.data.ICFile;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;


/**
 * @author aron
 */
/**
  * @deprecated use com.idega.block.ITextXMLHandler instead
  */
public class PDFTemplateWriter {
	
	/**
	 * @param tagmap
	 * @param xmlTemplateFileStream
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public MemoryFileBuffer writeToBuffer(Map tagmap, InputStream xmlTemplateFileStream)throws DocumentException,IOException,SAXException,ParserConfigurationException{
		return writeToBuffer(getDocument(PageSize.A4), tagmap, xmlTemplateFileStream);
	}
	
	/**
	 * @param document
	 * @param tagmap
	 * @param xmlTemplateFileURL
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public MemoryFileBuffer writeToBuffer(Document document,Map tagmap, String  xmlTemplateFileURL)throws DocumentException,IOException,SAXException,ParserConfigurationException{
		if (xmlTemplateFileURL == null) {
			throw new IllegalArgumentException("uri cannot be null");
		}
        
        
		FileInputStream input = new FileInputStream(xmlTemplateFileURL);
		return writeToBuffer(document,tagmap,input);
	}
	
	
	
	/**
	 * @param document
	 * @param tagmap
	 * @param xmlTemplateFileInputSource
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public MemoryFileBuffer writeToBuffer(Document document,Map tagmap, InputStream xmlTemplateFileInputSource)throws DocumentException,IOException,SAXException,ParserConfigurationException{
		MemoryFileBuffer bout = new MemoryFileBuffer();
		OutputStream OS = new MemoryOutputStream(bout);
		
		PdfWriter.getInstance(document, OS);
		document.open();
		
		parseTagMap(document,tagmap, xmlTemplateFileInputSource);
		
		document.close();
		OS.close();
		
		return bout;		
	}
	
	
	public void parseTagMap(Document document, Map tagMap, InputStream xmlTemplateFileStream)throws SAXException,ParserConfigurationException,IOException{
		javax.xml.parsers.SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		SAXpdfHandler handler = new SAXpdfHandler(document,tagMap);
		handler.setControlOpenClose(false); 
		parser.parse(xmlTemplateFileStream,handler);
	}
	
	
	
	public int writeToDatabase(Map tagmap, String xmlTemplateFileURL)  {
		
		try {
			return writeToDatabase(tagmap,new FileInputStream(xmlTemplateFileURL));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * @param buffer
	 * @param fileName
	 * @param mimeType
	 * @return
	 */
	
	
	
	/**
	 * @param tagmap
	 * @param xmlTemplateFile
	 * @return
	 */
	public int writeToDatabase(Map tagmap, InputStream xmlTemplateFile) {
		try {
			MemoryFileBuffer bout = writeToBuffer(tagmap, xmlTemplateFile);
			InputStream is = new MemoryInputStream(bout);
			
			ICFile pdfFile = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();
			pdfFile.setFileValue(is);
			pdfFile.setMimeType("application/pdf");
			pdfFile.setName("document.pdf");
			pdfFile.setFileSize(bout.length());
			pdfFile.insert();
			return pdfFile.getID();
		}
		catch (java.sql.SQLException ex) {
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
	
	/**
	 * Creates a Document object with a specified pagesize
	 * @param size as a <CODE>Rectangle</CODE>
	 * @return <CODE>Document</CODE>
	 */
	public Document getDocument(Rectangle size){
		return new Document(size);
	}
	
	/**
	 * Converts points from millimeters
	 * 
	 * @param millimeters to be converted
	 * @return <CODE>float</CODE>
	 */
	public static  float getPointsFromMM(float millimeters) {
		float pointPerMM = 72 / 25.4f;
		return millimeters * pointPerMM;
	}
}