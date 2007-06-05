/**
 * 
 */
package com.idega.core.cache;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * Implementation of a general cacher for UIComponents.	
 * </p>
 *  Last modified: $Date: 2007/06/05 16:57:54 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.8 $
 */
public class UIComponentCacher {
	
	public static final String UNDERSCORE = "_";
	private static final String DEFAULT_CACHE_NAME = "content";
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
			ResponseWriter writer = context.getResponseWriter();
			writer.write(cachedContent);
		}
		else{
			String cacheKey = getCacheKey(component,context);
			System.err.println("UIComponentCacher Error. Cached content is null, expected value for cacheKey: "+cacheKey);
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
		IWCacheManager2 iwcm = IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication());
		return iwcm.getCache(DEFAULT_CACHE_NAME);
	}

	public String getCacheKey(UIComponent component, FacesContext context) {
		return getCacheKeyStringBuffer(component, context).toString();
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method getCacheKey
	 * </p>
	 * @param component
	 * @param context
	 * @return
	 */
	protected StringBuffer getCacheKeyStringBuffer(UIComponent component, FacesContext context) {
		StringBuffer buf = new StringBuffer(component.getId());
		String sLocale = null;
		try {
			sLocale = context.getViewRoot().getLocale().toString();
		} catch (Exception e) {}
		if(sLocale!=null){
			buf.append(UNDERSCORE);
			buf.append(sLocale);
		}
		if(component instanceof CacheableUIComponent){
			String state = ((CacheableUIComponent)component).getViewState(context);
			buf.append(UNDERSCORE);
			buf.append(state);
		}
		return buf;
	}
	
	
}
