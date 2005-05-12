package com.idega.core.builder.business;

import java.rmi.RemoteException;
import com.idega.business.IBOSession;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 29, 2004
 */
public interface ICDynamicPageTriggerCopySession extends IBOSession {
	
	Object getNewValue(Class dataClassKey, Object oldValue) throws RemoteException;
	
	void setNewValue(Class dataClassKey, Object oldValue, Object newValue) throws RemoteException;
	
	boolean hasRootPage() throws RemoteException;

}
