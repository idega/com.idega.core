package com.idega.io;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description:
 * 	See also {@link com.idega.io.Writer Writer}
 * </p>
 * 
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public interface Storable {
	
	public Object write(Writer writer);
	
}
