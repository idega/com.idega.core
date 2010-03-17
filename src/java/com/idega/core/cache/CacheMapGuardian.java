package com.idega.core.cache;

import java.io.Serializable;

/**
 * <p>
 * Guardian that can be attached to a CacheMap for guarding against improper actions when objects are just before to bet put, get, removed and cleared.
 * </p>
 *  Last modified: $Date: 2010/03/17 17:02:00 $ by $Author: valdas $
 * 
 * @author <a href="mailto:valdas@idega.com">valdas</a>
 * @version $Revision: 1.1 $
 */
public interface CacheMapGuardian <K extends Serializable, V> {
	
	public boolean beforeRemove(K key, V object);
	
	public boolean beforePut(K key, V object);
	
	public boolean beforeGet(K key);

	public boolean beforeClear();

}