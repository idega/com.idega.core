/*
 * Created on Aug 16, 2004
 */
package com.idega.core.ldap.server.business;



import com.idega.business.IBOHomeImpl;

/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public class EmbeddedLDAPServerBusinessHomeImpl extends IBOHomeImpl implements
		EmbeddedLDAPServerBusinessHome {
	protected Class getBeanInterfaceClass() {
		return EmbeddedLDAPServerBusiness.class;
	}

	public EmbeddedLDAPServerBusiness create() throws javax.ejb.CreateException {
		return (EmbeddedLDAPServerBusiness) super.createIBO();
	}

}
