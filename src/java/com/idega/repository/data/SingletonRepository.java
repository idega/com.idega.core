/*
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.repository.data;

import java.util.HashMap;
import java.util.Iterator;


/**
 * @author thomas
 *
 * A container for singletons. 
 * 
 * Must be switched on and off by using start() and stop() methods.
 * 
 * Call getInstance for getting a singleton using an Instantiator.
 * 
 * When shutting down call the class method stop.
 * 
 */

public class SingletonRepository {

	private static SingletonRepository singletonRepository = null;
	
	// key: classname value: instance
	private HashMap singletonMap = new HashMap();
	// key: classname value: instantiator
	private HashMap instantiatorMap = new HashMap();


	
	private SingletonRepository() {
		// empty
	}
	
	public static synchronized void start() {
		singletonRepository = new SingletonRepository();
		System.out.println("["+ SingletonRepository.class.getName()+"] Repository started");
	}
	
	public static synchronized void stop() {
		if (singletonRepository == null) {
			// nothing to do
			return;
		}
		singletonRepository.destroy();
		singletonRepository = null;
		System.out.println("["+ SingletonRepository.class.getName()+"] Repository stopped");
	}
	
	public static SingletonRepository getRepository()	{
		return singletonRepository;
	}
	
	public synchronized Object getInstance(Class singletonClass, Instantiator instantiator) {
		Object singleton = null;
		String singletonName = singletonClass.getName();
		if (singletonMap.containsKey(singletonName)) {
			singleton = singletonMap.get(singletonName);
		}
		else {
			singleton = instantiator.getInstance();
			singletonMap.put(singletonName, singleton);
			instantiatorMap.put(singletonName, instantiator);
		}
		return singleton;
	}
	
	public synchronized Object getInstance(Class singletonClass, Instantiator instantiator, Object parameter) {
		Object singleton = null;
		String singletonName = singletonClass.getName();
		if (singletonMap.containsKey(singletonName)) {
			singleton = singletonMap.get(singletonName);
		}
		else {
			singleton = instantiator.getInstance(parameter);
			singletonMap.put(singletonName, singleton);
			instantiatorMap.put(singletonName, instantiator);
		}
		return singleton;
	}
	
	public synchronized Object getExistingInstanceOrNull(Class singletonClass) {
		return singletonMap.get(singletonClass.getName());
	}
	
	public synchronized void unloadInstance(Class singletonClass) {
		String singletonName = singletonClass.getName();
		if (singletonMap.containsKey(singletonName)) {
			Instantiator instantiator = (Instantiator)  instantiatorMap.get(singletonName);
			instantiator.unload();
			// remove the instance
			singletonMap.remove(singletonName);
		}
	}
	
	private synchronized void destroy() {
		Iterator iterator = instantiatorMap.values().iterator();
		while (iterator.hasNext()) {
			Instantiator instantiator = (Instantiator) iterator.next();
			instantiator.unload();
		}
	}

}
	

	
