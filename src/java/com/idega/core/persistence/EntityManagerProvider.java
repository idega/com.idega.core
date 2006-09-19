/**
 * $Id: EntityManagerProvider.java,v 1.1 2006/09/19 23:53:39 tryggvil Exp $
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
 * Plug-in interface to provide an implementation of EntityManager
 * </p>
 *  Last modified: $Date: 2006/09/19 23:53:39 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface EntityManagerProvider {
	
	public EntityManagerFactory getEntityManagerFactory();
	
}
