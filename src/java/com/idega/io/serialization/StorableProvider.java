package com.idega.io.serialization;

import java.io.IOException;

import com.idega.presentation.IWContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Apr 25, 2004
 */
public interface StorableProvider {
	
	public Storable getSource(String value, String className, IWContext iwc) throws IOException;
	
	public StorableHolder createSource(String value, String className, IWContext iwc) throws IOException;
	
}
