package com.idega.io.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.idega.presentation.IWContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 31, 2004
 */
public abstract class ReaderFromFile {
	
	protected IWContext iwc = null;
	protected Storable storable = null;
	
	public ReaderFromFile() {
		// default constructor
	}

	public ReaderFromFile(IWContext iwc) {
		this.iwc = iwc;
	}
	
	public ReaderFromFile(Storable storable, IWContext iwc) {
		this(iwc);
		setTarget(storable);
	}
	
	public abstract void openContainer(File file) throws IOException;
	
	public abstract void setName(String name);
	
	public abstract void setMimeType(String mimeType);
	
	public abstract InputStream readData(InputStream source) throws IOException;
	
	public void setTarget(Storable storable) {
		this.storable = storable;
	}
	
	protected void writeFromStreamToStream(InputStream source, OutputStream destination) throws IOException { 
		// parts of this method  were copied from "Java in a nutshell" by David Flanagan
    byte[] buffer = new byte[4096];  // A buffer to hold file contents
    int bytesRead;                       
    while((bytesRead = source.read(buffer)) != -1)  {  // Read bytes until EOF
      destination.write(buffer, 0, bytesRead);    
    }
	}
	
  protected void close(InputStream input) {
  	try {
			if (input != null) {
				input.close();
			}
		}
		catch (IOException io) {
			// do not hide an existing exception
		}
  }		
  
  protected void close(OutputStream output) {
  	try {
  		if (output != null) {
  			output.close();
  		}
  	}
  	catch (IOException io) {
		// do not hide an existing exception
  	}
  }


}
