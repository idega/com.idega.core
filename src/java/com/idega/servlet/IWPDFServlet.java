/*
 * Created on May 13, 2003
 */
package com.idega.servlet;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.builder.servlet.IBMainServlet;
import com.idega.idegaweb.IWConstants;
import com.idega.presentation.IWContext;
import com.idega.servlet.util.StringBufferWriter;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.XmlParser;

/**
 * A servlet that takes in a idegaWeb page id as a parameter and makes all elements output<br>
 * in iText compatible xml and then uses it to generate pdf's. iText is brilliant piece of software<br>
 * that can be used for many things but mainly for doing JAVA to PDF. http://www.lowagie.com/iText/
 * @author <a href="mailto:eiki@idega.is>Eirikur Hrafnsson</a>
 */
public class IWPDFServlet extends IBMainServlet {
	
	
	protected void __initializeIWC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		super.__initializeIWC( request,  response);
		IWContext iwc = getIWContext();
		
		iwc.setLanguage(IWConstants.MARKUP_LANGUAGE_PDF_XML);
		

		StringBufferWriter writer;

		writer = new StringBufferWriter(iwc.getResponse().getOutputStream());
		iwc.setWriter(writer);

		
		storeObject(IW_CONTEXT_KEY,iwc);
		
		
	}

	
	protected void finished(IWContext iwc){
		try {
			// step 1: creation of a document-object
			StringBufferWriter writer = (StringBufferWriter)iwc.getWriter();
			//Document document = new Document();
			Document document = new Document(PageSize.A4);//AND MARGINS , 80, 50, 30, 65);
			
			// step 2:
			// we create a writer that listens to the document
			// and directs a XML-stream to the servlet output
			OutputStream out = writer.getOutputStream();
			
			//PdfWriter.getInstance(document, new FileOutputStream(iwc.getApplication().getApplicationRealPath()+"iText-test.pdf"));
			PdfWriter.getInstance(document, out);
			
			//HtmlWriter.getInstance(document, new FileOutputStream("Chap0702.html"));
            
			// step 3: we create a parser and set the document handler
      InputStream inputstream = writer.getInputStream();

			com.idega.util.FileUtil.streamToFile(inputstream, iwc.getApplication().getApplicationRealPath(),"PDF-XML.xml");
     
			// step 4: we parse the document
			XmlParser.parse(document,"file://"+iwc.getApplication().getApplicationRealPath()+"PDF-XML.xml");
         
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}


			
}
