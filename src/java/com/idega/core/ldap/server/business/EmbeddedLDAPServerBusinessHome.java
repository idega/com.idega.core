/*
 * Created on Aug 16, 2004
 */
package com.idega.core.ldap.server.business;



import com.idega.business.IBOHome;

/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface EmbeddedLDAPServerBusinessHome extends IBOHome {
	public EmbeddedLDAPServerBusiness create()
			throws javax.ejb.CreateException, java.rmi.RemoteException;

}
