/*
 * Created on 29.7.2003 by  tryggvil in project com.project
 */
package com.idega.event;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
/**
 * EventLogic //TODO: tryggvil Describe class
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class EventLogic
{
	public static final String IB_OBJECT_INSTANCE_COORDINATE = "ib_ob_inst";
	public static final String IB_OBJECT_INSTANCE_EVENT_SOURCE = "ib_ob_inst_ev_s";
	/**
	 *
	 */
	public static void setICObjectInstanceListeners(Link l, int[] ibPageId, int[] instanceId)
	{
		String prm = "";
		for (int i = 0; i < ibPageId.length; i++)
		{
			if (i != 0)
			{
				prm += ",";
			}
			prm += ibPageId[i] + "_" + instanceId[i];
		}
		l.addParameter(IB_OBJECT_INSTANCE_COORDINATE, prm);
	}
	/**
	 *
	 */
	public static void setICObjectInstanceListener(Link l, int ibPageId, int instanceId)
	{
		l.addParameter(IB_OBJECT_INSTANCE_COORDINATE, ibPageId + "_" + instanceId);
	}
	/**
	 *
	 */
	public static void setICObjectInstanceEventSource(Link l, int ibPageId, int instanceId)
	{
		l.addParameter(IB_OBJECT_INSTANCE_EVENT_SOURCE, ibPageId + "_" + instanceId);
	}
	/**
	 *
	 */
	public static PresentationObject[] getIWPOListeners(IWContext iwc)
	{
		String prm = iwc.getParameter(IB_OBJECT_INSTANCE_COORDINATE);
		String[] coordinates = null;
		if (prm != null)
		{
			StringTokenizer tokens = new StringTokenizer(prm, ",");
			coordinates = new String[tokens.countTokens()];
			int index = 0;
			while (tokens.hasMoreTokens())
			{
				coordinates[index++] = tokens.nextToken();
			}
		}
		if (coordinates != null && coordinates.length > 0)
		{
			List<PresentationObject> l = new ArrayList<PresentationObject>();
			for (int i = 0; i < coordinates.length; i++)
			{
				String crdnts = coordinates[i];
				int index = crdnts.indexOf('_');
				String page = crdnts.substring(0, index);
				String inst = crdnts.substring(index + 1, crdnts.length());
				if (!"".equals(page) && !"".equals(inst))
				{
					//Page parentPage = BuilderLogic.getInstance().getIBXMLPage(page).getPopulatedPage();
					//PresentationObject obj = parentPage.getContainedICObjectInstance(Integer.parseInt(inst));
					PresentationObject obj = getPopulatedObjectInstance(inst, iwc);
					if (obj != null && !obj.equals(PresentationObject.NULL_CLONE_OBJECT))
					{
						l.add(obj);
					}
				}
			}
			PresentationObject[] toReturn = l.toArray(new PresentationObject[0]);
			if (toReturn.length > 0)
			{
				/*
				System.err.println("BuilderLogic Listeners");
				for (int i = 0; i < toReturn.length; i++) {
				  System.err.println(" - "+toReturn[i].getParentPageID()+"_"+toReturn[i].getICObjectInstanceID());
				}*/
				return toReturn;
			}
			else
			{
				//System.err.println("BuilderLogic Listeners are null");
				return null;
			}
		}
		else
		{
			//System.err.println("BuilderLogic Listeners are null");
			return null;
		}
	}
	/**
	 *
	 */
	public static PresentationObject getIWPOEventSource(IWContext iwc)
	{
		String coordinates = iwc.getParameter(IB_OBJECT_INSTANCE_EVENT_SOURCE);
		if (coordinates != null)
		{
			String crdnts = coordinates;
			int index = crdnts.indexOf('_');
			String page = crdnts.substring(0, index);
			String inst = crdnts.substring(index + 1, crdnts.length());
			if (!"".equals(page) && !"".equals(inst))
			{
				/*Page parentPage = BuilderLogic.getInstance().getIBXMLPage(page).getPopulatedPage();
				PresentationObject obj = parentPage.getContainedICObjectInstance(Integer.parseInt(inst));
				return obj;*/
				return getPopulatedObjectInstance(inst, iwc);
			}
		}
		return null;
	}
	/**
	 *
	 */
	public static PresentationObject getPopulatedObjectInstance(int id, IWContext iwc)
	{
		return ObjectInstanceCacher.getObjectInstanceClone(id, iwc);
	}
	/**
	 *
	 */
	public static PresentationObject getPopulatedObjectInstance(String key, IWContext iwc)
	{
		return ObjectInstanceCacher.getObjectInstanceClone(key, iwc);
	}
	/**
	 *
	 */
	public static Map<String, PresentationObject> getCashedObjectInstancesForPage(int pageId)
	{
		return ObjectInstanceCacher.getObjectInstancesCachedForPage(pageId);
	}
	/**
	 *
	 */
	public static Map<String, PresentationObject> getCashedObjectInstancesForPage(String pageKey)
	{
		return ObjectInstanceCacher.getObjectInstancesCachedForPage(pageKey);
	}
	/**
	 *
	 */
	public static Set<String> getInstanceIdsOnPage(String pageKey)
	{
		Map<String, PresentationObject> m = ObjectInstanceCacher.getObjectInstancesCachedForPage(pageKey);
		if (m != null)
		{
			return m.keySet();
		}
		else
		{
			return null;
		}
	}
	/**
	 *
	 */
	public static Set<String> getInstanceIdsOnPage(int pageKey)
	{
		Map<String, PresentationObject> m = ObjectInstanceCacher.getObjectInstancesCachedForPage(pageKey);
		if (m != null)
		{
			return m.keySet();
		}
		else
		{
			return null;
		}
	}
}
