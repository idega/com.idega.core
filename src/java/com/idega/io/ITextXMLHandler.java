/*
 * Created on May 30, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
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
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
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
	
	private int fileType = 1;
	
	/** Construcst a handler for given type flags, 
	 *  types are provided by using bitwise and ( & ) on this objects types ( PDF, TXT, HTML )
	 * @param type  
	 */
	public ITextXMLHandler(int type){
		this.fileType = type;
	}
	
	/** Gives the types this handler is handling
	 * @return type
	 */
	public int type(){
		return fileType;
	}
	
	/** Constructs memorybuffers for each type, provided a tagmap 
	 *  and inputstream to a xml template. Document size is set to A4
	 * @param tagMap
	 * @param xmlIS
	 * @return MemoryBuffer[]
	 */
	public MemoryFileBuffer[] writeToBuffers(Map tagMap, InputStream xmlIS ){
		return  writeToBuffers(getDocument(PageSize.A4), tagMap,  xmlIS );
	}
	
	/**Constructs memorybuffers for each type, provided a tagmap 
	 *  and URI to a xml template. Document size is set to A4
	 * @param tagMap
	 * @param xmlURL
	 * @return
	 */
	public MemoryFileBuffer[] writeToBuffers(Map tagMap, String xmlURI ){
			try {
				return  writeToBuffers(getDocument(PageSize.A4), tagMap,  new FileInputStream(xmlURI) );
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
	}
	
	
	/** Constructs memorybuffers for each type, provided a tagmap 
	 *  and inputstream to a xml template.
	 * @param document
	 * @param tagMap
	 * @param xmlIS
	 * @return
	 */
	public MemoryFileBuffer[] writeToBuffers(Document document,Map tagMap, InputStream xmlIS ){
		
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
	
	/**
	 * @param document
	 * @param tagMap
	 * @param xmlTemplateFileStream
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public void parseTagMap(Document document, Map tagMap, InputStream xmlTemplateFileStream)throws SAXException,ParserConfigurationException,IOException{
		javax.xml.parsers.SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		SAXpdfHandler handler = new SAXpdfHandler(document,tagMap);
		handler.setControlOpenClose(false); 
		parser.parse(xmlTemplateFileStream,handler);
	}
	
	/** Stores a memory buffer to database with, provided the name and mimetype
	 * @param buffer
	 * @param fileName
	 * @param mimeType
	 * @return ICFIle
	 */
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
		
		public String bufferToString(MemoryFileBuffer buffer)throws IOException{
			MemoryInputStream in = new MemoryInputStream(buffer);
			
			StringWriter out =  new StringWriter(buffer.length());
			int c;
	   		while ((c = in.read()) != -1)
		  		out.write(c);
	   		in.close();
	   		out.close();
			return out.toString();
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
		
		public String getPDFMimeType(){
			return "application/pdf";
		}
}
