package com.idega.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLOutput;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public class XMLDataWriter extends ICFileWriter {
	
	private static final String XML_EXTENSION = "xml";
	
	public XMLDataWriter(Storable storable, IWApplicationContext iwac) {
		super(storable, iwac);
	}
	
	public  String createContainer() throws IOException {
			// get path
		long folderIdentifier = System.currentTimeMillis();
		String name = ((XMLData) storable).getName();
		String path = getRealPathToFile(name, XML_EXTENSION, folderIdentifier);
    OutputStream destination = null;
    File auxiliaryFile = null;
    try {
      auxiliaryFile = new File(path);
      destination = new BufferedOutputStream(new FileOutputStream(auxiliaryFile));
    }
    catch (FileNotFoundException ex)  {
//     logError("[XMLData] problem creating file.");
//     log(ex);
      throw new IOException("xml file could not be stored");
    }
    // now we have an output stream of the auxiliary file
    // write to the xml file
    try {
    	writeData(destination);
    }
    finally {
   		close(destination);
    }
    return getURLToFile(name, XML_EXTENSION, folderIdentifier);
	}
	
	public String getName() {
		return ((XMLData) storable).getName();
	}
	
	public void writeData(OutputStream destination) throws IOException {  
    XMLOutput xmlOutput = new XMLOutput("  ", true);
    xmlOutput.setLineSeparator(System.getProperty("line.separator"));
    xmlOutput.setTextNormalize(true);
    xmlOutput.setEncoding("iso-8859-1");
    // do not use document directly use accessor method
    XMLDocument myDocument = ((XMLData) storable).getDocument();
    xmlOutput.output(myDocument, destination);
	}



}
