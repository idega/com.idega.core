//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.util;


import java.util.*;


/**
*Class to store objects in context to a thread throughout its execution or some part of it
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class ThreadContext{

private Hashtable theThreads;
private static ThreadContext instance;

	private ThreadContext(){
		initHashTable();
	}

	/*public ThreadContext(Object obj){
		this(obj.currentThread());
	}

	public ThreadContext(Thread thread){

	}*/


	/**

	 * Return a static instance of this class since only one instance is needed in each JVM

	 */
	public static ThreadContext getInstance(){
		if (instance == null){
			instance = new ThreadContext();
		}
		return instance;
	}

	private void initHashTable(){
		if (theThreads == null){
			theThreads = new Hashtable();
		}
	}

	public void putThread(Thread thread){
		theThreads.put(thread,new Hashtable());
	}


	public void releaseThread(Thread thread){
		theThreads.remove(thread);
	}

	public void setAttribute(Thread thread,String attributeName,Object attribute){
		getThreadAttributes(thread).put(attributeName,attribute);
	}

	public void setAttribute(String attributeName,Object attribute){
		getThreadAttributes(Thread.currentThread()).put(attributeName,attribute);
	}


        public void removeAttribute(Thread thread,String attributeName){
		getThreadAttributes(thread).remove(attributeName);
	}

        public void removeAttribute(String attributeName){
		getThreadAttributes(Thread.currentThread()).remove(attributeName);
	}


        private Hashtable getThreadAttributes(){
          return getThreadAttributes(Thread.currentThread());
        }


        private Hashtable getThreadAttributes(Thread thread){
          Hashtable theReturn = (Hashtable)theThreads.get(thread);
          if (theReturn == null){
            putThread(thread);
            theReturn = (Hashtable)theThreads.get(thread);
          }
          return theReturn;
        }

	public Object getAttribute(Thread thread,String attributeName){
                Hashtable tempTable = (Hashtable) theThreads.get(thread);
                if (tempTable != null){
                        return tempTable.get(attributeName);
                }
                else{
                        return null;
                }
	}

        public Object getAttribute(String attributeName){
            return getAttribute(Thread.currentThread(),attributeName);
	}


        public int getSize(){
          return theThreads.size();
        }


        public Enumeration getThreadElements(){
          return theThreads.keys();
        }


}
