/*
 * Created on Aug 15, 2004
 */
package com.idega.core.idgenerator.business;


import com.idega.business.IBOHomeImpl;

/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public class UUIDBusinessHomeImpl extends IBOHomeImpl implements
		UUIDBusinessHome {
	protected Class getBeanInterfaceClass() {
		return UUIDBusiness.class;
	}

	public UUIDBusiness create() throws javax.ejb.CreateException {
		return (UUIDBusiness) super.createIBO();
	}

}
