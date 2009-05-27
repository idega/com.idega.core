/* 
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.repository.data;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;


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

	private static volatile SingletonRepository singletonRepository = null;

	private static volatile boolean hasBeenStopped = false;
	
	//Singleton holder map implementation changed to ConcurrentHashMap to improve synchronization.
	private ConcurrentMap<String, SingletonInstantiatorBean> map = new ConcurrentHashMap<String, SingletonInstantiatorBean>();
	
	private SingletonRepository() {
		// empty
	}
	
  public static synchronized void start() {
		if (singletonRepository != null) {
			// nothing to do, start already called!
			Logger.getLogger(singletonRepository.getClass().getName()).finest("["+ SingletonRepository.class.getName()+"] Note: Tried to start Repository again but it is already running (this is usually not an  error or problem)");
			return;
		}
		singletonRepository = new SingletonRepository();
		hasBeenStopped=false;
		Logger.getLogger(singletonRepository.getClass().getName()).info("["+ SingletonRepository.class.getName()+"] Repository started");
	}
	
	public static synchronized void stop() {
		hasBeenStopped=true;
		if (singletonRepository == null) {
			// nothing to do
			return;
		}
		singletonRepository.destroy();
		Logger.getLogger(singletonRepository.getClass().getName()).info("["+ SingletonRepository.class.getName()+"] Repository stopped");
		singletonRepository = null;
	}
	
	/**
	 * <p>
	 * This method should be called only after the start() method has been invoked once, and not after the stop() method has been called.
	 * </p>
	 * @return An instance of the SingletonRepository if it is correctly initialized
	 * @throws RuntimeException if no instance is initialized.
	 */
	public static SingletonRepository getRepository() {
		if (singletonRepository == null) {
			synchronized (SingletonRepository.class) {
				if (hasBeenStopped) {
					throw new RuntimeException(
							"SingletonRepsitory has been stopped so no instance exists.");
				}
				start();
			}
		}
		return singletonRepository;
	}
	
	public Object getInstance(Class singletonClass, Instantiator instantiator) {
		String singletonName = singletonClass.getName();
		SingletonInstantiatorBean bean = map.get(singletonName);
		if (bean != null) {
			return bean.getSingleton();
		}
		bean = map.putIfAbsent(singletonName, 
				new SingletonInstantiatorBean(instantiator.getInstance(), instantiator));
		//if bean == null, then it's first time it is added, else - another thread put it.
		return bean != null ? bean.getSingleton() : map.get(singletonName).getSingleton(); 

	}
	
	
	public Object getInstanceUsingIdentifier(Class singletonClass, Instantiator instantiator, Object parameter, String identifier) {
		String singletonName = singletonClass.getName() + "_" + identifier;
		SingletonInstantiatorBean bean = map.get(singletonName);
		if (bean != null) {
			return bean.getSingleton();
		}
		bean = map.putIfAbsent(singletonName, 
				new SingletonInstantiatorBean(instantiator.getInstance(parameter), instantiator));
		return bean != null ? bean.getSingleton() : map.get(singletonName).getSingleton();
	}
	
	public Object getInstance(Class singletonClass, Instantiator instantiator, Object parameter) {
		String singletonName = singletonClass.getName();
		SingletonInstantiatorBean bean = map.get(singletonName);
		if (bean != null) {
			return bean.getSingleton();
		}
		bean = map.putIfAbsent(singletonName, 
				new SingletonInstantiatorBean(instantiator.getInstance(parameter), instantiator));
		return bean != null ? bean.getSingleton() : map.get(singletonName).getSingleton();
	}
	
	public Object getExistingInstanceOrNull(Class singletonClass) {
		SingletonInstantiatorBean singletonInstantiatorBean = map.get(singletonClass.getName());
		return singletonInstantiatorBean != null ? singletonInstantiatorBean.getSingleton() : null;
	}
	
	public void unloadInstance(Class singletonClass) {
		String singletonName = singletonClass.getName();
		SingletonInstantiatorBean bean = map.remove(singletonName);
		if (bean != null) {
			unload(bean.getInstantiator());
		}
	}
	
	public void unloadInstance(Class singletonClass, String identifier) {
		String singletonName = singletonClass.getName() + "_" + identifier;
		SingletonInstantiatorBean bean = map.remove(singletonName);
		if (bean != null) {
			unload(bean.getInstantiator());
		}
	}
	
	private void destroy() {
		Iterator<SingletonInstantiatorBean> iterator = this.map.values().iterator();
		while (iterator.hasNext()) {
			SingletonInstantiatorBean bean = iterator.next();
			unload(bean.getInstantiator());
		}
	}
	
	private void unload(Instantiator instantiator) {
		instantiator.unload();
	}
	
	
	/**
	 * Class for storing singleton/instantiator data.
	 *
	 */
	private static class SingletonInstantiatorBean {
		
		private Object singleton;
		
		private Instantiator instantiator;
		
		public SingletonInstantiatorBean(Object singleton, Instantiator instantiator) {
			this.singleton = singleton;
			this.instantiator = instantiator;
		}

		public Object getSingleton() {
			return singleton;
		}

		public Instantiator getInstantiator() {
			return instantiator;
		}
		
	}

}
	

	
