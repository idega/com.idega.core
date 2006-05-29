/*
 * $Id: IWCache.java,v 1.1 2006/05/29 18:17:11 tryggvil Exp $
 * Created on 23.5.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.cache;

import net.sf.ehcache.Cache;


/**
 * <p>
 * Subclass of standard EHCache Cache object to distinguish it from the standard cache.
 * </p>
 *  Last modified: $Date: 2006/05/29 18:17:11 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class IWCache extends Cache {

	/**
	 * @param name
	 * @param maximumSize
	 * @param overflowToDisk
	 * @param eternal
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 */
	public IWCache(String name, int maximumSize, boolean overflowToDisk, boolean eternal, long timeToLiveSeconds,
			long timeToIdleSeconds) {
		super(name, maximumSize, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param maximumSize
	 * @param overflowToDisk
	 * @param eternal
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 * @param diskPersistent
	 * @param diskExpiryThreadIntervalSeconds
	 */
	public IWCache(String name, int maximumSize, boolean overflowToDisk, boolean eternal, long timeToLiveSeconds,
			long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds) {
		super(name, maximumSize, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds, diskPersistent,
				diskExpiryThreadIntervalSeconds);
		// TODO Auto-generated constructor stub
	}
}
