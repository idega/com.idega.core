/*
 * $Id: SearchPluginManager.java,v 1.3.2.1 2007/01/12 19:33:01 idegaweb Exp $ Created on Jan 18,
 * 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.search.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.FinderException;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * 
 * Last modified: $Date: 2007/01/12 19:33:01 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki </a>
 * @version $Revision: 1.3.2.1 $
 */
public class SearchPluginManager implements Singleton {

	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new SearchPluginManager();}};

	protected Map searchPlugins;

	/**
	 * This is a singleton class
	 */
	protected SearchPluginManager() {
		super();
		this.searchPlugins = new HashMap();
	}

	public static SearchPluginManager getInstance() {
		return (SearchPluginManager) SingletonRepository.getRepository().getInstance(SearchPluginManager.class,instantiator);
	}
	
	public Collection getAllSearchPluginsInitialized(IWMainApplication iwma) {
		try {
			ICObjectHome icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
			ICObject obj;
			Collection coll = icoHome.findAllByObjectType(ICObjectBMPBean.COMPONENT_TYPE_SEARCH_PLUGIN);
			if (coll != null && !coll.isEmpty()) {
				Iterator iter = coll.iterator();
				while (iter.hasNext()) {
					obj = (ICObject) iter.next();
					String className = obj.getClassName();
					if (!this.searchPlugins.containsKey(className)) {
						SearchPlugin searchPlugin;
						try {
							searchPlugin = (SearchPlugin) RefactorClassRegistry.forName(className).newInstance();
							boolean success = searchPlugin.initialize(iwma);
							if (success) {
								this.searchPlugins.put(className, searchPlugin);
							}
							else {
								//Todo use logger
								System.err.println("[SearchPluginManager] - Failed to initialize the search plugin: "
										+ className);
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							System.err.println("[SearchPluginManager] - Failed to initialize the search plugin: "
									+ className);
						}
					}
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			//no plugins
			//e.printStackTrace();
		}
		return this.searchPlugins.values();
	}

	/**
	 * It is better if you register your plugin as an iw.searchplugin in your
	 * bundle but you can also add it with this method.
	 * 
	 * @param plugin
	 */
	public void addSearchPlugin(SearchPlugin plugin, IWMainApplication iwma) {
		String className = plugin.getClass().getName();
		if (!this.searchPlugins.containsKey(className)) {
			boolean success = plugin.initialize(iwma);
			if (success) {
				this.searchPlugins.put(className,plugin);
			}
			else {
				//Todo use logger
				System.err.println("[SearchPluginManager] - Failed to initialize the search plugin: "
						+ className);
			}
			
		}
	}
}