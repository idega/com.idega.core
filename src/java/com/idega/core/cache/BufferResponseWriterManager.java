/**
 * 
 */
package com.idega.core.cache;

import java.io.StringWriter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * <p>
 * Interface for creating and managing JSF ResponseWriters when switching to writing
 * to a buffer instead of directly to the http response.
 * </p>
 *  Last modified: $Date: 2006/02/28 14:47:17 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
public interface BufferResponseWriterManager {

	public abstract ResponseWriter createBufferedResponseWriter(StringWriter buffer, ResponseWriter realResponseWriter);

	/**
	 * Gets the buffer for the writer if it is a buffered response writer.
	 */
	public abstract StringWriter getBufferFromResponseWriter(ResponseWriter writer);

	/**
	 * Gets the buffer for the writer if it is a buffered response writer.
	 */
	public abstract ResponseWriter getRealResponseWriter(ResponseWriter bufferWriter);

	/**
	 * <p>
	 * Switches the FacesContext to write out to a buffer instead to the default
	 * (HttpServletResponse) output
	 * </p>
	 * @param context
	 * @param buffer
	 */
	public abstract void switchContextToWriteToBuffer(FacesContext context, StringWriter buffer);

	/**
	 * <p>
	 * Resets or switches the FacesContext to write again the real (HttpServletResponse) output
	 * insteada of a buffer.
	 * @return Returns the StringWriter buffer that was written to.
	 * </p>
	 */
	public abstract StringWriter resetRealResponseWriter(FacesContext context);
}