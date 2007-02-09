/**
 * 
 */
package com.idega.core.cache;


/**
 * <p>
 * Listener that can be attached to a CacheMap for listening to when
 * objects are put, get, removed and cleared.
 * </p>
 *  Last modified: $Date: 2007/02/09 01:55:01 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public interface CacheMapListener {
	
	public void removedObject(Object key);
	
	public void putObject(Object key,Object object);
	
	public void gotObject(Object key,Object object);
	
	/**
	 * <p>
	 * Called when clear() is called on the CacheMap
	 * </p>
	 */
	public void cleared();
}
