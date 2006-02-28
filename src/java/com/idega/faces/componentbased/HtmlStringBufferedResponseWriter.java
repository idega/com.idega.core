/*
 * $Id: HtmlStringBufferedResponseWriter.java,v 1.2 2006/02/28 14:48:35 tryggvil Exp $
 * Created on 24.10.2005 in project com.idega.faces
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.componentbased;

import java.io.StringWriter;
import javax.faces.FacesException;
import javax.faces.context.ResponseWriter;
import org.apache.myfaces.renderkit.html.HtmlResponseWriterImpl;


/**
 * <p>
 * Class used by the CbpViewHandler to buffer out the written content to a string.<br/>
 * This is done so the state management parrameters can be rewritten when the page has rendered.
 * </p>
 *  Last modified: $Date: 2006/02/28 14:48:35 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class HtmlStringBufferedResponseWriter extends HtmlResponseWriterImpl {

	private StringWriter stringWriter;
	private ResponseWriter realResponseWriter;
	
	/**
	 * @param writer
	 * @param contentType
	 * @param characterEncoding
	 * @throws FacesException
	 */
	public HtmlStringBufferedResponseWriter(StringWriter writer, String contentType, String characterEncoding)
			throws FacesException {
		super(writer, contentType, characterEncoding);
		setStringWriter(writer);
		// TODO Auto-generated constructor stub
	}
	
	public StringWriter getStringWriter(){
		return stringWriter;
	}
	
	public void setStringWriter(StringWriter writer){
		this.stringWriter=writer;
	}

	
	/**
	 * @return Returns the realResponseWriter.
	 */
	public ResponseWriter getRealResponseWriter() {
		return realResponseWriter;
	}

	
	/**
	 * @param realResponseWriter The realResponseWriter to set.
	 */
	public void setRealResponseWriter(ResponseWriter realResponseWriter) {
		this.realResponseWriter = realResponseWriter;
	}
}
