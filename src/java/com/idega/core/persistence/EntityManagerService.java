/**
 * $Id: EntityManagerService.java,v 1.1 2006/09/19 23:53:39 tryggvil Exp $
 * Created in 2006 by tryggvil
 *
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.persistence;

import javax.persistence.EntityManagerFactory;


/**
 * <p>
 * Class to get a reference to the default EJB3 Persistence manager
 * </p>
 *  Last modified: $Date: 2006/09/19 23:53:39 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class EntityManagerService {
	
	private EntityManagerProvider defaultProvider;


	public EntityManagerFactory getEntityManagerFactory(){
		return getEntityManagerProvider().getEntityManagerFactory();
	}
	
	
	public EntityManagerProvider getEntityManagerProvider(){
		if(defaultProvider==null){
			try {
				defaultProvider = (EntityManagerProvider) Class.forName("com.idega.hibernate.EntityManagerUtil").newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return defaultProvider;
		
	}
}
