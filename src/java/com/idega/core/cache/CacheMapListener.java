/**
 * 
 */
package com.idega.core.cache;


/**
 * <p>
 * Listener that can be attached to a CacheMap for listening to when
 * objects are put, get, removed and cleared.
 * </p>
 *  Last modified: $Date: 2006/02/28 14:47:17 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface CacheMapListener {
	
	public void removedObject(String key);
	
	public void putObject(String key,Object object);
	
	public void gotObject(String key,Object object);
	
	/**
	 * <p>
	 * Called when clear() is called on the CacheMap
	 * </p>
	 */
	public void cleared();
}
