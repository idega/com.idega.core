/*
 * Created on Dec 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.event;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWException;
import com.idega.presentation.IWContext;

/**
 * @author thomas
 *
 * A helper class to connect the old event system with the new one
 */
public class OldEventSystemHelperBridge implements IWPageEventListener {

	/* (non-Javadoc)
	 * @see com.idega.event.IWPageEventListener#actionPerformed(com.idega.presentation.IWContext)
	 */
	public boolean actionPerformed(IWContext iwc) throws IWException {
		try {
			IWEventMachine machine = (IWEventMachine) IBOLookup.getSessionInstance(iwc, IWEventMachine.class);
			machine.processEvent(null,  iwc);
			return true;
		}
		catch (IBOLookupException ex) {
			throw new IWException("[OldEventSystemHelperBridge] Could not look up IWEventMachine");
		} catch (RemoteException e) {
			throw new IWException("[OldEventSystemHelperBridge] Remote Exception during look up of IWEventMachine");
		}
	}

}
