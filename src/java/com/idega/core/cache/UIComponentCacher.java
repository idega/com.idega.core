/**
 * 
 */
package com.idega.core.cache;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * Implementation of a general cacher for UIComponents.	
 * </p>
 *  Last modified: $Date: 2006/03/08 09:15:22 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.4 $
 */
public class UIComponentCacher {
	
	private static UIComponentCacher instance = new UIComponentCacher();
	
	public static UIComponentCacher getDefaultCacher(FacesContext context){
		return instance;
	}
	
	public boolean existsInCache(UIComponent component,FacesContext context){
		if(!isCacheEnbled(component,context)){
			return false;
		}
		String cacheKey = getCacheKey(component,context);
		Map cacheMap = getCacheMap();
		if(cacheMap.containsKey(cacheKey)){
			return true;
		}
		return false;
	}

	public boolean isCacheEnbled(UIComponent component,FacesContext context){
		if(component.getId()==null){
			return false;
		}
		return false;
	}
	
	public void beginCache(UIComponent component,FacesContext context) throws IOException{
		/*String cacheKey =*/ getCacheKey(component,context);
		
		BufferResponseWriterManager manager = BufferResponseWriterManagerFactory.getInstance();
		StringWriter buffer = new StringWriter();
		
		manager.switchContextToWriteToBuffer(context,buffer);
	}
	
	public void endCache(UIComponent component,FacesContext context) throws IOException{
		BufferResponseWriterManager manager = BufferResponseWriterManagerFactory.getInstance();
		StringWriter buffer = manager.resetRealResponseWriter(context);
		StringBuffer sbBuffer = buffer.getBuffer();
		String sBuffer = sbBuffer.toString();
		String cacheKey = getCacheKey(component,context);
		Map cacheMap = getCacheMap();
		cacheMap.put(cacheKey,sBuffer);
		
		//finally print out the content as it has only been printed to buffer previously
		encodeCached(component,context);
	}
	
	/**
	 * Renders out an existing cached content.
	 * @throws IOException 
	 */
	public void encodeCached(UIComponent component, FacesContext context) throws IOException{
		String cachedContent = getCachedContent(component,context);
		if(cachedContent!=null){
			context.getResponseWriter().write(cachedContent);
		}
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCachedContent
	 * </p>
	 * @param component
	 * @param context
	 * @return
	 */
	protected String getCachedContent(UIComponent component, FacesContext context) {
		
		String cacheKey = getCacheKey(component,context);
		Map cacheMap = getCacheMap();
		
		return (String)cacheMap.get(cacheKey);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCacheMap
	 * </p>
	 * @return
	 */
	public Map getCacheMap() {
		
		IWCacheManager cm = IWCacheManager.getInstance(IWMainApplication.getDefaultIWMainApplication());
		return cm.getCacheMap();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCacheKey
	 * </p>
	 * @param component
	 * @param context
	 * @return
	 */
	protected String getCacheKey(UIComponent component, FacesContext context) {
		String id = component.getId();
		String sLocale = context.getViewRoot().getLocale().toString();
		if(sLocale!=null){
			id+="_";
			id+=sLocale;
		}
		if(component instanceof CacheableUIComponent){
			String state = ((CacheableUIComponent)component).getViewState(context);
			id+="_";
			id+=state;
		}
		
		return id;
	}
	
	
}
