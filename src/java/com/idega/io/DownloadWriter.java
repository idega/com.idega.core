/*
 * Created on 23.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;

/**
 * @author aron
 *
 * DownloadWriter To be used when files are downloaded to the user.
 * The response is set so that the client browser opens a save dialog.
 * Handles both files from database and absolute paths from filesystem if read permission is active
 */
public class DownloadWriter implements MediaWritable {
    
    public final static String PRM_ABSOLUTE_FILE_PATH = "abs_fpath";
    public final static String PRM_FILE_ID = "fileId";
    private File file = null;
    private ICFile icFile = null;

    /* (non-Javadoc)
     * @see com.idega.io.MediaWritable#getMimeType()
     */
    public String getMimeType() {
        return "application/octet-stream";
    }

    /* (non-Javadoc)
     * @see com.idega.io.MediaWritable#init(javax.servlet.http.HttpServletRequest, com.idega.presentation.IWContext)
     */
    public void init(HttpServletRequest req, IWContext iwc) {
        String fileId = iwc.getParameter(PRM_FILE_ID);
        String absPath = iwc.getParameter(PRM_ABSOLUTE_FILE_PATH);
        if(fileId!=null){
            try {
                icFile = ((ICFileHome)IDOLookup.getHome(ICFile.class)).findByPrimaryKey(Integer.valueOf(fileId));
                iwc.getResponse().setHeader("Content-Disposition", "attachment;filename=\"" + icFile.getName()+ "\"");
                iwc.getResponse().setContentLength(icFile.getFileSize().intValue());
            } catch (Exception e) {
                icFile=null;
            }
            
        }
        else if(absPath!=null){
            file = new File(absPath);
            if(file!=null && file.exists() && file.canRead()){
                iwc.getResponse().setHeader("Content-Disposition", "attachment;filename=\"" + file.getName()+ "\"");
            		iwc.getResponse().setContentLength((int) file.length());
            }

        }

    }

    /* (non-Javadoc)
     * @see com.idega.io.MediaWritable#writeTo(java.io.OutputStream)
     */
    public void writeTo(OutputStream out) throws IOException {
        if(icFile!=null){
            BufferedInputStream fis = new BufferedInputStream(icFile.getFileValue());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (fis.available() > 0) {
				baos.write(fis.read());
			}
			baos.writeTo(out);
			baos.flush();
            	baos.close();
            	fis.close();
        }
        else if(file!=null && file.exists() && file.canRead()){
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (fis.available() > 0) {
				baos.write(fis.read());
			}
			baos.writeTo(out);
			baos.flush();
        		baos.close();
        		fis.close();
        }
        else
    			throw new IOException("No file value");

    }

}
