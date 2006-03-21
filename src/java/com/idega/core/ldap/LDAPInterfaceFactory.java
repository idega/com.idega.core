/*
 * $Id: LDAPInterfaceFactory.java,v 1.1 2006/03/21 12:31:45 tryggvil Exp $
 * Created on 21.3.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.SingletonRepository;


/**
 * <p>
 * TODO tryggvil Describe Type LDAPHelperFactory
 * </p>
 *  Last modified: $Date: 2006/03/21 12:31:45 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class LDAPInterfaceFactory {
	
	private static Instantiator instantiator;
	
	/**
	 * <p>
	 * Gets the implementation of the LDAPInterface.
	 * </p>
	 * @param iwac
	 * @return
	 * @throws LDAPInterfaceException 
	 * @throws RuntimeException if an error occurs or failure in getting implementation
	 */
	public static LDAPInterface getInstance(IWApplicationContext iwac) throws LDAPInterfaceException{
		
		try {

			final Class implClass = Class.forName("com.idega.block.ldap.util.IWLDAPUtil");
			if(instantiator==null){
				instantiator = new Instantiator() { public Object getInstance() { try {
					return implClass.newInstance();
				}
				catch (InstantiationException e) {
					throw new RuntimeException(e);
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}};
			}
			return (LDAPInterface)SingletonRepository.getRepository().getInstance(implClass,instantiator);
		}
		catch (ClassNotFoundException e) {
			LDAPInterfaceException newE = new LDAPInterfaceException("Error while getting LDAP implementation",e);
			throw newE;
		}
	}
	
	/**
	 * 
	 */
	private LDAPInterfaceFactory() {
		super();
	}
}
