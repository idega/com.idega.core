package com.idega.event;
/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import java.util.Map;
import java.util.HashMap;
public class ObjectInstanceCacher
{
	private static Map _objectInstanceCache = new HashMap();
	private static Map _objectInstanceCacheForPage = new HashMap();
	private ObjectInstanceCacher()
	{
	}
	public static PresentationObject getObjectInstanceCached(String key)
	{
		return (PresentationObject) getObjectInstanceCacheMap().get(key);
	}
	public static PresentationObject getObjectInstanceClone(String key, IWContext iwc)
	{
		PresentationObject obj = getObjectInstanceCached(key);
		if (obj != null)
		{
			return (PresentationObject) obj.clonePermissionChecked(iwc);
		}
		else
		{
			return null;
		}
	}
	public static Map getObjectInstancesCachedForPage(String pageKey)
	{
		Map map = getObjectInstanceCacheMapForPage();
		if (map != null)
		{
			return (Map) map.get(pageKey);
		}
		else
		{
			return null;
		}
	}
	public static Map getObjectInstancesCachedForPage(int pageKey)
	{
		return getObjectInstancesCachedForPage(Integer.toString(pageKey));
	}
	public static PresentationObject getObjectInstanceCached(int key)
	{
		return getObjectInstanceCached(Integer.toString(key));
	}
	public static PresentationObject getObjectInstanceClone(int key, IWContext iwc)
	{
		return getObjectInstanceClone(Integer.toString(key), iwc);
	}
	public static void changeObjectInstanceID(
		Page page,
		String oldInstanceKey,
		String newInstanceKey,
		PresentationObject newObjectInstance)
	{
		//    System.out.println("ObjectInstanceCasher.changeObjectInstanceID(....)");
		if (newInstanceKey != null)
		{
			putObjectIntanceInCache(newInstanceKey, newObjectInstance);
		}
		//System.err.println("Cashing objectInstance: "+instanceKey);
		String templateKey = page.getTemplateId();
		copyInstancesFromPageToPage(templateKey, Integer.toString(page.getPageID()));
		//System.err.println("Cashing objectInstance: "+instanceKey+" on page "+ ibxml.getKey()+" extending: "+ibxml.getTemplateId());
		if (oldInstanceKey != null)
		{
			getObjectInstancesCachedForPage(page.getPageID()).remove(oldInstanceKey);
		}
		if (newInstanceKey != null)
		{
			getObjectInstancesCachedForPage(page.getPageID()).put(newInstanceKey, newObjectInstance);
		}
		//Map tmp = getObjectInstancesCachedForPage(page.getPageID());
		/*
		    System.out.println("pageID = "+page.getPageID());
		
		    System.out.println("oldInstanceKey: "+ oldInstanceKey);
		    System.out.println("newInstanceKey: "+ newInstanceKey);
		
		    System.out.println("temp.contains(oldInstanceKey): "+ tmp.containsKey(oldInstanceKey) );
		    System.out.println("temp.contains(newInstanceKey): "+ tmp.containsKey(newInstanceKey));
		*/
	}
	/**
	 * Copies the cacheMap from page with key oldPageKey to page with 
	 * newPageKey if it hasen't already been done.
	 * @param oldPageKey
	 * @param newPageKey
	 */
	public static void copyInstancesFromPageToPage(String oldPageKey, String newPageKey)
	{
		Map map = getObjectInstancesCachedForPage(newPageKey);
		if (map == null)
		{
			Map templateMap = getObjectInstancesCachedForPage(oldPageKey);
			if (templateMap != null)
			{
				//System.err.println("geting template Map");
				map = (Map) ((HashMap) templateMap).clone();
			}
			else
			{
				//System.err.println("creating new Map");
				map = new HashMap();
			}
			getObjectInstanceCacheMapForPage().put(newPageKey, map);
		}
	}
	public static void putObjectIntanceInCache(String instanceKey, PresentationObject objectInstance)
	{
		getObjectInstanceCacheMap().put(instanceKey, objectInstance);
	}
	private static Map getObjectInstanceCacheMap()
	{
		return _objectInstanceCache;
	}
	private static Map getObjectInstanceCacheMapForPage()
	{
		return _objectInstanceCacheForPage;
	}
}
