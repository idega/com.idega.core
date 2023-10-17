/*
 * Created on 24.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.presentation.text;

import java.io.IOException;
import java.util.logging.Level;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.io.DownloadWriter;
import com.idega.io.MediaWritable;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Span;
import com.idega.util.StringUtil;

/**
 * @author aron
 *
 * DownloadLink used to download files with
 */
public class DownloadLink extends Link {
	public static final String COMPONENT_TYPE = "DownloadLink";

	public static final String DOWNLOAD_WRITER_PROPERTY = "downloadWriter";
	public static final String LINK_TEXT = "value";
	public static final String STYLE_CLASS = "styleClass";
	public static final String TITLE = "title";

	private boolean encryptClasses = true;

    private Class<? extends MediaWritable> writerClass = null;

    public boolean isEncryptClasses() {
		return encryptClasses;
	}

	public void setEncryptClasses(boolean encryptClasses) {
		this.encryptClasses = encryptClasses;
	}

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
    }

    /**
     * @param text
     */
    public DownloadLink(Text text,String absolutepath) {
        super(text);
        addParameter(DownloadWriter.PRM_ABSOLUTE_FILE_PATH,absolutepath);
    }

    @Override
	public void encodeBegin(FacesContext context) throws IOException {
    	Class<? extends MediaWritable> mediaWriterClass = getMediaWriter(context);
    	String text = getValue(context);
    	String title = getTitle(context);
    	String styleClass = getStyleClass(context);

    	if(mediaWriterClass != null)
    		setMediaWriterClass(mediaWriterClass);

    	if (!StringUtil.isEmpty(text)) {
    		setPresentationObject(new Span(new Text(text)));
    	}

    	if (!StringUtil.isEmpty(title)) {
    		setTitle(title);
    	}

    	if (!StringUtil.isEmpty(styleClass)) {
    		setStyleClass(styleClass);
    	}

    	super.encodeBegin(context);
    }

    @Override
	public void setFile(ICFile file) {
    	if (file == null) {
    		return;
    	}

        addParameter(DownloadWriter.PRM_FILE_UNIQUE_ID, file.getUniqueId());
        String token = file.getToken();
		if (!StringUtil.isEmpty(token)) {
			addParameter(DownloadWriter.PRM_FILE_TOKEN, token);
		}
    }

    @Override
	public void setFile(int fileId) {
    	if (fileId <= 0) {
    		return;
    	}

    	ICFile file = null;
    	try {
    		ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
    		file = fileHome.findByPrimaryKey(fileId);
    	} catch (Exception e) {
    		getLogger().log(Level.WARNING, "Error setting file by ID " + fileId, e);
    	}
    	setFile(file);
    }

    public void setMediaWriterClass(Class<? extends MediaWritable> writerClass){
        this.writerClass = writerClass;
    }

    @SuppressWarnings("unchecked")
	public Class<? extends MediaWritable> getMediaWriter(FacesContext context) {
    	ValueExpression ve = getValueExpression(DOWNLOAD_WRITER_PROPERTY);

    	if (ve == null) {
    		return null;
    	}

    	Object o = ve.getValue(context.getELContext());
    	if (o instanceof Class) {
    		return (Class<? extends MediaWritable>) o;
    	}
    	return null;
	}

    private String getValue(FacesContext context) {
    	ValueExpression textExpression = getValueExpression(LINK_TEXT);

    	String text = textExpression == null ? (String) context.getExternalContext().getRequestParameterMap().get(LINK_TEXT) :
    		textExpression.getValue(context.getELContext()).toString();

		return text;
	}

    private String getTitle(FacesContext context) {
    	ValueExpression textExpression = getValueExpression(TITLE);

    	String text = textExpression == null ? (String) context.getExternalContext().getRequestParameterMap().get(TITLE) :
    		textExpression.getValue(context.getELContext()).toString();

		return text;
	}

    private String getStyleClass(FacesContext context) {
    	ValueExpression textExpression = getValueExpression(STYLE_CLASS);

    	String text = textExpression == null ? (String) context.getExternalContext().getRequestParameterMap().get(STYLE_CLASS) :
    		textExpression.getValue(context.getELContext()).toString();

		return text;
	}

    @Override
	public void main(IWContext iwc)throws Exception{
        super.main(iwc);
        setURL(iwc.getIWMainApplication().getMediaServletURI());
        if(this.writerClass!=null){
            addParameter(MediaWritable.PRM_WRITABLE_CLASS, isEncryptClasses() ?
            		IWMainApplication.getEncryptedClassName(this.writerClass) :
            		this.writerClass.getName()
            );
        }
    }


    public void setRelativeFilePath(String relativeFilePath){
    		addParameter(DownloadWriter.PRM_RELATIVE_FILE_PATH,relativeFilePath);
    }

    public void setAlternativeFileName(String name){
		addParameter(DownloadWriter.PRM_FILE_NAME,name);
    }

    public void setAbsoluteFilePath(String absoluteFilePath){
		addParameter(DownloadWriter.PRM_ABSOLUTE_FILE_PATH,absoluteFilePath);
    }

    @Override
    public void print(IWContext iwc) throws Exception {
		setURL(iwc.getIWMainApplication().getMediaServletURI());
		if (this.writerClass!=null) {
			addParameter(MediaWritable.PRM_WRITABLE_CLASS, IWMainApplication.getEncryptedClassName(this.writerClass));
		}

		super.print(iwc);
		this._parameterString = new StringBuffer();	//	Because parameters are not updated if there are more than 1 link used in JSP/facelet
    }
}
