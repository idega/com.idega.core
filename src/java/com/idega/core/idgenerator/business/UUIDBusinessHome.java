/*
 * Created on Aug 15, 2004
 */
package com.idega.core.idgenerator.business;


import com.idega.business.IBOHome;

/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface UUIDBusinessHome extends IBOHome {
	public UUIDBusiness create() throws javax.ejb.CreateException,
			java.rmi.RemoteException;

}
