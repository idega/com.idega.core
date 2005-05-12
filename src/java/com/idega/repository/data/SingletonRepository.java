/* 
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.repository.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


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
	
	public static synchronized SingletonRepository getRepository()	{
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
	
	
	public synchronized Object getInstanceUsingIdentifier(Class singletonClass, Instantiator instantiator, Object parameter, Object identfier) {
		Map instancesOfSingletons = null;
		Object singleton = null;
		String singletonName = singletonClass.getName();
		if (singletonMap.containsKey(singletonName)) {
			instancesOfSingletons = (Map) singletonMap.get(singletonName);
		}
		else {
			instancesOfSingletons = new HashMap();
		}
		if (instancesOfSingletons.containsKey(identfier)) {
			// existing singleton found
			singleton = instancesOfSingletons.get(identfier);
		}
		// create singleton 
		else {
			// create and store singleton
			singleton = instantiator.getInstance(parameter);
			instancesOfSingletons.put(identfier, singleton);
			singletonMap.put(singletonName, instancesOfSingletons);
			// store instantiator
			Map instantiators = null;
			if (instantiatorMap.containsKey(singletonName)) {
				instantiators = (Map) instantiatorMap.get(singletonName);
			}
			else {
				instantiators = new HashMap();
			}
			instantiators.put(identfier, instantiator);
			instantiatorMap.put(singletonName, instantiators);
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
			// can be a map or instantiator
			Object mapOrInstantiator = instantiatorMap.get(singletonName);
			unload(mapOrInstantiator);
			// remove the instance
			singletonMap.remove(singletonName);
		}
	}
	
	private synchronized void destroy() {
		Iterator iterator = instantiatorMap.values().iterator();
		while (iterator.hasNext()) {
			// can be a map or instantiator
			Object mapOrInstantiator = iterator.next();
			unload(mapOrInstantiator);
		}
	}
	
	private void unload(Object mapOrInstantiator) {
		if (mapOrInstantiator instanceof Instantiator) {
			((Instantiator) mapOrInstantiator).unload();
		}
		else {
			// it is a map, go further
			Iterator iterator = ((Map) mapOrInstantiator).values().iterator();
			while (iterator.hasNext()) {
				Object tempMapOrInstantiator = iterator.next();
				unload(tempMapOrInstantiator);
			}
		}
	}

}
	

	
