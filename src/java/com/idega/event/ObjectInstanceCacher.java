package com.idega.event;
/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */
import java.util.HashMap;
import java.util.Map;

import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.util.datastructures.map.MapUtil;
public class ObjectInstanceCacher {

	private static Map<String, PresentationObject> _objectInstanceCache = new HashMap<String, PresentationObject>();
	private static Map<String, Map<String, PresentationObject>> _objectInstanceCacheForPage = new HashMap<String, Map<String, PresentationObject>>();

	private ObjectInstanceCacher() {}

	public static PresentationObject getObjectInstanceCached(String key) {
		return getObjectInstanceCacheMap().get(key);
	}

	public static PresentationObject getObjectInstanceClone(String key, IWContext iwc) {
		PresentationObject obj = getObjectInstanceCached(key);
		if (obj != null) {
			return (PresentationObject) obj.clonePermissionChecked(iwc);
		} else {
			return null;
		}
	}

	public static Map<String, PresentationObject> getObjectInstancesCachedForPage(String pageKey) {
		Map<String, Map<String, PresentationObject>> map = getObjectInstanceCacheMapForPage();
		if (map != null) {
			return map.get(pageKey);
		} else {
			return null;
		}
	}

	public static Map<String, PresentationObject> getObjectInstancesCachedForPage(int pageKey) {
		return getObjectInstancesCachedForPage(Integer.toString(pageKey));
	}

	public static PresentationObject getObjectInstanceCached(int key) {
		return getObjectInstanceCached(Integer.toString(key));
	}

	public static PresentationObject getObjectInstanceClone(int key, IWContext iwc) {
		return getObjectInstanceClone(Integer.toString(key), iwc);
	}

	public static void changeObjectInstanceID(
		Page page,
		String oldInstanceKey,
		String newInstanceKey,
		PresentationObject newObjectInstance
	) {
		if (newInstanceKey != null) {
			putObjectIntanceInCache(newInstanceKey, newObjectInstance);
		}

		String templateKey = page.getTemplateId();
		copyInstancesFromPageToPage(templateKey, Integer.toString(page.getPageID()));

		if (oldInstanceKey != null) {
			getObjectInstancesCachedForPage(page.getPageID()).remove(oldInstanceKey);
		}
		if (newInstanceKey != null) {
			getObjectInstancesCachedForPage(page.getPageID()).put(newInstanceKey, newObjectInstance);
		}
	}

	/**
	 * Copies the cacheMap from page with key oldPageKey to page with
	 * newPageKey if it hasen't already been done.
	 * @param oldPageKey
	 * @param newPageKey
	 */
	public static void copyInstancesFromPageToPage(String oldPageKey, String newPageKey) {
		Map<String, PresentationObject> map = getObjectInstancesCachedForPage(newPageKey);
		if (map == null) {
			Map<String, PresentationObject> templateMap = getObjectInstancesCachedForPage(oldPageKey);
			if (templateMap != null) {
				map = MapUtil.deepCopy(templateMap);
			} else {
				map = new HashMap<String, PresentationObject>();
			}
			getObjectInstanceCacheMapForPage().put(newPageKey, map);
		}
	}

	public static void putObjectIntanceInCache(String instanceKey, PresentationObject objectInstance) {
		getObjectInstanceCacheMap().put(instanceKey, objectInstance);
	}

	private static Map<String, PresentationObject> getObjectInstanceCacheMap() {
		return _objectInstanceCache;
	}

	private static Map<String, Map<String, PresentationObject>> getObjectInstanceCacheMapForPage() {
		return _objectInstanceCacheForPage;
	}
}
