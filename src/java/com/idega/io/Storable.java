package com.idega.io;

import java.rmi.RemoteException;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description:
 * 	See {@link com.idega.io.ObjectWriter Writer}
 * </p>
 * 
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public interface Storable {
	
	public Object write(ObjectWriter writer) throws RemoteException;
	
	public Object read(ObjectReader reader) throws RemoteException;
	
}
