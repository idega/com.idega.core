package com.idega.io.serialization;

import java.rmi.RemoteException;

import com.idega.presentation.IWContext;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description:
 * 	See {@link com.idega.io.serialization.ObjectWriter Writer}
 * </p>
 * 
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public interface Storable {
	
	public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException;
	
	public Object read(ObjectReader reader, IWContext iwc) throws RemoteException;
	
}
