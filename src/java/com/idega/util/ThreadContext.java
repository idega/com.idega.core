//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.util;
import java.util.HashMap;
import java.util.Map;
import com.idega.repository.data.Singleton;
/**
*Class to store objects in context to a thread throughout its execution or some part of it
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class ThreadContext implements Singleton
{
	private Map threadsMap;
	private static ThreadContext instance;
	private ThreadContext()
	{
	}

	/**
	 * Return a static instance of this class since only one instance is needed in each JVM
	 * */
	public static synchronized ThreadContext getInstance()
	{
		if (instance == null)
		{
			instance = new ThreadContext();
		}
		return instance;
	}
	
	/**
	 * Unloads the ThreadContext
	 */
	public static void unload(){
		instance=null;
	}

	private Map getThreadsMap(){
		if (threadsMap == null)
		{
			threadsMap = new HashMap();
		}
		return threadsMap;
	}
	/**
	 * initializes a Map for the Thread thread to associate objects
	 * @param thread
	 */
	public void putThread(Thread thread)
	{
		getThreadsMap().put(thread, new HashMap());
	}
	/**
	 * releases all object mapped to the Thread thread
	 * @param thread
	 */
	public void releaseThread(Thread thread)
	{
		getThreadsMap().remove(thread);
	}
	/**
	 * Associates an object attribute to the Thread thread
	 * @param thread The Thread to associate an object to
	 * @param attributeName the key name
	 * @param attribute the Object
	 */
	public void setAttribute(Thread thread, String attributeName, Object attribute)
	{
		getThreadAttributes(thread).put(attributeName, attribute);
	}
	/**
	 * Associates an object attribute to the current running Thread
	 * @param attributeName the key name
	 * @param attribute the Object
	 */
	public void setAttribute(String attributeName, Object attribute)
	{
		getThreadAttributes(Thread.currentThread()).put(attributeName, attribute);
	}
	/**
	 * Removes an object attribute associated to the Thread thread
	 * @param thread The Thread to remove an associated object from
	 * @param attributeName the key name
	 */
	public void removeAttribute(Thread thread, String attributeName)
	{
		getThreadAttributes(thread).remove(attributeName);
	}
	/**
	 * Removes an object attribute associated to the current running Thread
	 * @param attributeName the key name
	 */
	public void removeAttribute(String attributeName)
	{
		getThreadAttributes(Thread.currentThread()).remove(attributeName);
	}
	private Map getThreadAttributes(Thread thread)
	{
		Map theReturn = (Map) getThreadsMap().get(thread);
		if (theReturn == null)
		{
			putThread(thread);
			theReturn = (Map) getThreadsMap().get(thread);
		}
		return theReturn;
	}
	/**
	 * Gets an object attribute associated to the Thread thread
	 * @param thread The Thread to get an associated object from
	 * @param attributeName the key name
	 */
	public Object getAttribute(Thread thread, String attributeName)
	{
		Map tempTable = (Map) getThreadsMap().get(thread);
		if (tempTable != null)
		{
			return tempTable.get(attributeName);
		}
		else
		{
			return null;
		}
	}
	/**
	 * Gets an object attribute associated to the currently running thread
	 * @param attributeName the key name
	 */	
	public Object getAttribute(String attributeName)
	{
		return getAttribute(Thread.currentThread(), attributeName);
	}
	/**
	 * Gets the number of threads with associated objects
	 * @return the thread count
	 */
	public int getSize()
	{
		return getThreadsMap().size();
	}
	
	public boolean reset(){
		try{
			getThreadsMap().clear();
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
