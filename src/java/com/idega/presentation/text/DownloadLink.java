/*
 * Created on 24.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.presentation.text;

import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWMainApplication;
import com.idega.io.DownloadWriter;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author aron
 *
 * DownloadLink used to download files with
 */
public class DownloadLink extends Link {
    
    private Class writerClass = null;
    
    /**
     * 
     */
    public DownloadLink() {
        super();
        setMediaWriterClass(DownloadWriter.class);
    }
    
    /**
     * 
     * @param absolutepath
     */
    public DownloadLink(String text) {
        super(text);
        setMediaWriterClass(DownloadWriter.class);
    }
    
//THIS CAN CONFUSE PEOPLE BECAUSE THERE IS NO CONSTRUCTOR FOR RELATIVE PATH. IT'S BETTER TO FORCE PEOPLE TO USE THE CORRECT SET METHODS
//    public DownloadLink(String text,String absolutepath) {
//        this(text);
//        setMediaWriterClass(DownloadWriter.class);
//        setAbsoluteFilePath(absolutepath);
//    }
  
    /**
     * @param icFileId
     */
    public DownloadLink(int icFileId) {
        this();
        setMediaWriterClass(DownloadWriter.class);
        setFile(icFileId);
    }
    
    /**
     * @param mo
     */
    public DownloadLink(PresentationObject mo) {
        super(mo);
        setMediaWriterClass(DownloadWriter.class);
    }
   
    /**
     * @param text
     */
    public DownloadLink(Text text) {
        super(text);
        setMediaWriterClass(DownloadWriter.class);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param text
     */
    public DownloadLink(Text text,String absolutepath) {
        super(text);
        addParameter(DownloadWriter.PRM_ABSOLUTE_FILE_PATH,absolutepath);
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
     * @see com.idega.presentation.text.Link#setFile(com.idega.core.file.data.ICFile)
     */
    public void setFile(ICFile file) {
        addParameter(DownloadWriter.PRM_FILE_ID,((Integer)file.getPrimaryKey()).intValue());
    }
    
    /* (non-Javadoc)
     * @see com.idega.presentation.text.Link#setFile(int)
     */
    public void setFile(int fileId) {
        addParameter(DownloadWriter.PRM_FILE_ID,fileId);
    }
    
    
    public void setMediaWriterClass(Class writerClass){
        this.writerClass = writerClass;
    }
    
    public void main(IWContext iwc)throws Exception{
        super.main(iwc);
        setURL(iwc.getIWMainApplication().getMediaServletURI());
        if(writerClass!=null){
            addParameter(DownloadWriter.PRM_WRITABLE_CLASS, IWMainApplication.getEncryptedClassName(writerClass));
        }
    }
    
    
    public void setRelativeFilePath(String relativeFilePath){
    		addParameter(DownloadWriter.PRM_RELATIVE_FILE_PATH,relativeFilePath);
    }
    
    public void setAbsoluteFilePath(String absoluteFilePath){
		addParameter(DownloadWriter.PRM_ABSOLUTE_FILE_PATH,absoluteFilePath);
}
}
