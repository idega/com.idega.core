/*
 * Created on 24.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.presentation.text;

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
    }
    
    /**
     * 
     * @param absolutepath
     */
    public DownloadLink(String text) {
        super(text);
    }
    
    /**
     * 
     * @param absolutepath
     */
    public DownloadLink(String text,String absolutepath) {
        this(text);
        setMediaWriterClass(DownloadWriter.class);
        addParameter(DownloadWriter.PRM_ABSOLUTE_FILE_PATH,absolutepath);
    }
  
    /**
     * @param icFileId
     */
    public DownloadLink(int icFileId) {
        this();
        setMediaWriterClass(DownloadWriter.class);
        addParameter(DownloadWriter.PRM_FILE_ID,icFileId);
    }
    
    /**
     * @param mo
     */
    public DownloadLink(PresentationObject mo) {
        super(mo);
    }
   
    /**
     * @param text
     */
    public DownloadLink(Text text) {
        super(text);
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
}
