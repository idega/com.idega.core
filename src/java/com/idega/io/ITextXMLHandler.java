/*
 * Created on May 30, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.idega.core.data.ICFile;
import com.idega.core.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class ITextXMLHandler {
	
	public final static int PDF = 1;
	public final static int TXT = 2;
	public final static int HTML = 4;
	//public final static int RTF = 8 ;
	
	public MemoryFileBuffer[] writeToBuffers(Document document,Map tagMap, InputStream xmlIS,int fileType ){
		
			try {
				Vector buffers = new Vector();
				Vector oses = new Vector();
				if((fileType & PDF) == 1){
					MemoryFileBuffer bout = new MemoryFileBuffer();
					OutputStream OS = new MemoryOutputStream(bout);
					PdfWriter.getInstance(document, OS);
					buffers.add(OS);
					oses.add(OS);
				}
				if((fileType & TXT)==1){
					MemoryFileBuffer bout = new MemoryFileBuffer();
					OutputStream OS = new MemoryOutputStream(bout);
					TxtWriter.getInstance(document, OS);
					buffers.add(OS);
					oses.add(OS);
				}
				if((fileType & TXT)==1){
						MemoryFileBuffer bout = new MemoryFileBuffer();
						OutputStream OS = new MemoryOutputStream(bout);
						HtmlWriter.getInstance(document, OS);
						buffers.add(OS);
						oses.add(OS);
				}
				
				
				document.open();
				parseTagMap(document,tagMap, xmlIS);
				document.close();
				
				Iterator iter = oses.iterator();
				while (iter.hasNext()) {
					OutputStream os = (OutputStream) iter.next();
					os.close();			
				}
				return (MemoryFileBuffer[])buffers.toArray();
			}
			catch (DocumentException e) {
				
				e.printStackTrace();
			}
			catch (SAXException e) {
			
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) {
				
				e.printStackTrace();
			}
			catch (IOException e) {
				
				e.printStackTrace();
			}
			return null;
		
	}
	
	public void parseTagMap(Document document, Map tagMap, InputStream xmlTemplateFileStream)throws SAXException,ParserConfigurationException,IOException{
		javax.xml.parsers.SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		SAXpdfHandler handler = new SAXpdfHandler(document,tagMap);
		handler.setControlOpenClose(false); 
		parser.parse(xmlTemplateFileStream,handler);
	}
	
	public ICFile writeToDatabase(MemoryFileBuffer buffer,String fileName,String mimeType){
			try {
				InputStream is = new MemoryInputStream(buffer);
				ICFile icFile = ((ICFileHome)IDOLookup.getHome(ICFile.class)).create();
				icFile.setFileValue(is);
				icFile.setMimeType(mimeType);
				icFile.setName(fileName);
				icFile.setFileSize(buffer.length());
				icFile.store();
				return icFile;
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch (IDOStoreException e) {
				e.printStackTrace();
			}
			catch (CreateException e) {
				e.printStackTrace();
			}
			return null;
		}
}
