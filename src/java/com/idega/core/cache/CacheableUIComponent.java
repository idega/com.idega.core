/**
 * 
 */
package com.idega.core.cache;

import javax.faces.context.FacesContext;


/**
 * <p>
 * This interface should be implemented by UI components that want to implement the 
 * idegaWeb caching extensions for JSF.
 * extensions for JSF.
 * </p>
 *  Last modified: $Date: 2006/02/28 14:47:17 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface CacheableUIComponent {
	
	/**
	 * <p>
	 * Returns the Cacher for this component.
	 * </p>
	 * @param context
	 * @return
	 */
	public UIComponentCacher getCacher(FacesContext context);
	
	/**
	 * <p>
	 * Returns a unique identifier of the "view state" that the
	 * component is in, this could be e.g. 'view','edit' etc.
	 * This is then used by the default cacheing implementation to
	 * know which states should not be cached as the same state.
	 * </p>
	 * @param context
	 * @return
	 */
	public String getViewState(FacesContext context);
	
}
