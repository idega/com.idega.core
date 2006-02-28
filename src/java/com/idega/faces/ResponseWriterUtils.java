/**
 * 
 */
package com.idega.faces;

import java.io.StringWriter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import com.idega.core.cache.BufferResponseWriterManager;
import com.idega.faces.componentbased.HtmlStringBufferedResponseWriter;


/**
 * <p>
 * TODO tryggvil Describe Type ResponseWriterUtils
 * </p>
 *  Last modified: $Date: 2006/02/28 14:48:35 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class ResponseWriterUtils implements BufferResponseWriterManager {
	
	static BufferResponseWriterManager instance;
	
	public static BufferResponseWriterManager getInstance(){
		if(instance==null){
			instance = new ResponseWriterUtils();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.BufferResponseWriterManager#createBufferedResponseWriter(java.io.StringWriter, javax.faces.context.ResponseWriter)
	 */
	public ResponseWriter createBufferedResponseWriter(StringWriter buffer,ResponseWriter realResponseWriter){
		String contentType=null;
		String encoding=null;
		
		HtmlStringBufferedResponseWriter writer = new HtmlStringBufferedResponseWriter(buffer,contentType,encoding);
		writer.setRealResponseWriter(realResponseWriter);
		return writer;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.BufferResponseWriterManager#getBufferFromResponseWriter(javax.faces.context.ResponseWriter)
	 */
	public StringWriter getBufferFromResponseWriter(ResponseWriter writer){
		HtmlStringBufferedResponseWriter hWriter = (HtmlStringBufferedResponseWriter)writer;
		return hWriter.getStringWriter();
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.BufferResponseWriterManager#getRealResponseWriter(javax.faces.context.ResponseWriter)
	 */
	public ResponseWriter getRealResponseWriter(ResponseWriter bufferWriter){
		HtmlStringBufferedResponseWriter hWriter = (HtmlStringBufferedResponseWriter)bufferWriter;
		return hWriter.getRealResponseWriter();
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.BufferResponseWriterManager#switchContextToWriteToBuffer(javax.faces.context.FacesContext, java.io.StringWriter)
	 */
	public void switchContextToWriteToBuffer(FacesContext context,StringWriter buffer){
		ResponseWriter oldResponseWriter = context.getResponseWriter();
		ResponseWriter bufferResponseWriter = createBufferedResponseWriter(buffer,oldResponseWriter);
		context.setResponseWriter(bufferResponseWriter);
	}
	/* (non-Javadoc)
	 * @see com.idega.faces.BufferResponseWriterManager#resetRealResponseWriter(javax.faces.context.FacesContext)
	 */
	public StringWriter resetRealResponseWriter(FacesContext context){
		ResponseWriter responseWriter = context.getResponseWriter();
		StringWriter buffer = getBufferFromResponseWriter(responseWriter);
		ResponseWriter realResponseWriter = getRealResponseWriter(responseWriter);
		context.setResponseWriter(realResponseWriter);
		//ResponseWriter bufferResponseWriter = createBufferedResponseWriter(buffer);
		//context.setResponseWriter(bufferResponseWriter);
		return buffer;
	}
	
}
