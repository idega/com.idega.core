/*
 * $Id: CLFileEncodingConverter.java,v 1.1 2005/02/15 19:04:47 eiki Exp $
 * Created on Feb 15, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.encoding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;


/**
 * 
 *  Last modified: $Date: 2005/02/15 19:04:47 $ by $Author: eiki $
 * Accepts 4 inputs: original encoding, desired encoding, filename, newfilename
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class CLFileEncodingConverter {

	/**
	 * 
	 */
	public CLFileEncodingConverter() {
		super();
	}

	public static void main(String[] args) {
		try {
			String fromEncoding = args[0];
			String toEncoding = args[1];
			String filePath = args[2];
			String newFilePath = args[3];
			
			
			 File inputFile = new File(filePath);
			 File outputFile = new File(newFilePath);
			 
			    Reader in = new InputStreamReader(new FileInputStream(inputFile),fromEncoding);
			    Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),toEncoding));
			    
			    int c;
			    while ((c = in.read()) != -1){
			       out.write(c);
			    }

			    in.close();
			    out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(-1);

	}
}
