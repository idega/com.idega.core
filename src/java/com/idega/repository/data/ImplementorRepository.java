package com.idega.repository.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.idega.util.ArrayUtil;
import com.idega.util.datastructures.HashMatrix;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 14, 2004
 */
public class ImplementorRepository {
	
	private static final String GENERAL = "general";
	private static ImplementorRepository instance = null;
	
	static public ImplementorRepository getInstance() {
		if (instance == null) {
			instance = new ImplementorRepository();
		}
		return instance;
	}
	
	private HashMatrix interfaceCallerImplementor = null;
	
	private ImplementorRepository(){
		// should not be initialized by constructor
	} 
	
	public void  addImplementorForCaller(Class interfaceClass, Class callerClass, Class implementorClass) {
		if (interfaceCallerImplementor == null) {
			interfaceCallerImplementor = new HashMatrix();
		}
		String interfaceClassName = interfaceClass.getName();
		String callerClassName = (callerClass == null) ? GENERAL : callerClass.getName();
		String implementorClassName = implementorClass.getName();
		if (interfaceCallerImplementor.containsKey(interfaceClassName, callerClassName)) {
			List implementorNames = (List) interfaceCallerImplementor.get(interfaceClassName, callerClassName);
			if (implementorNames.contains(implementorClassName)) {
				// already added
				return;
			}
			// add the name to the existing list
			implementorNames.add(implementorClassName);
		}
		else{
			// new entry
			List implementorNames = new ArrayList(1);
			implementorNames.add(implementorClassName);
			interfaceCallerImplementor.put(interfaceClassName, callerClassName, implementorNames);
		}
	}
	
	public void addImplementor(Class interfaceClass, Class implementationClass) {
		addImplementorForCaller(interfaceClass, null, implementationClass);
	}
	
	public Object getImplementor(Class interfaceClass, Class callerClass) throws ClassNotFoundException {
		List implementors = getValidImplementorClasses(interfaceClass, callerClass);
		// get the first one
		if (implementors == null) {
			throw new ClassNotFoundException("[ImplementorRepository] ImImplementor for interface " + interfaceClass.getName() + "could not be found");
		}
		Class implementorClass = (Class) implementors.get(0);
		try {
			return implementorClass.newInstance();
		} 
		catch (InstantiationException e) {
			throw new ClassNotFoundException("[ImplementorRepository] Implementor for interface " + interfaceClass.getName() + "could not be created");		
		} 
		catch (IllegalAccessException e) {
			throw new ClassNotFoundException("[ImplementorRepository] None implementor for interface " + interfaceClass.getName() + "could not be created because of access problems");		
		}
	}
	
	public Object getImplementorOrNull(Class interfaceClass, Class implementationClass) {
		try {
			return getImplementor(interfaceClass, implementationClass);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param interfaceClass
	 * @param callerClass
	 * @return null or a non-empty list
	 */
	private List getImplementorNames(Class interfaceClass, Class callerClass) {
		if (interfaceCallerImplementor == null) {
			return null;
		}
		String interfaceClassName = interfaceClass.getName();
		String callerClassName = callerClass.getName();
		if (! interfaceCallerImplementor.containsKey(interfaceClassName, callerClassName)) {
			callerClassName = GENERAL;
		}
		List implementors = (List) interfaceCallerImplementor.get(interfaceClassName, callerClassName);
		if (implementors == null || implementors.isEmpty()) {
			return null;
		}
		return implementors;
	}
	
	private List getValidImplementorClasses(Class interfaceClass, Class callerClass) {
		List names = getImplementorNames(interfaceClass, callerClass);
		if (names == null) {
			return null;
		}
		List classes = new ArrayList(names.size());
		Iterator iterator = names.iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			try {
				Class implementorClass = Class.forName(name);
				classes.add(implementorClass);
			}
			catch (ClassNotFoundException e) {
				// do nothing, not very likely that a class is registered but doesn't exist
			}
		}
		if (classes.isEmpty()) {
			return null;
		}
		return classes;
	}
	

	/** 
	 * Checks if the caller class is considered to be of the specified type.
	 * Use that method only for interfaces that are used as flags like Clonable.
	 * If that method returns true it doesn't mean that you can cast an instance of
	 * the callerClass to the specified class.
	 * If you are going to perform a cast use the instanceOf check. 
	 * @param interfaceClass
	 * @param callerClass
	 * @return true if the callerClass is considered to be of the specified type. 
	 */
	public boolean isTypeOf(Class interfaceClass, Class callerClass) {
		// first part: 
		// does the same like "instanceOf"
		//
		// get all super classes and check for the interface
		List classes = new ArrayList(); 
		Class superClass = callerClass;
		while (superClass != null) {
			Class[] interfaces = superClass.getInterfaces();
			if (ArrayUtil.contains(interfaces,interfaceClass)) {
				// if the method is left at the place a cast could be done
				return true;
			}
			classes.add(superClass);
			superClass = superClass.getSuperclass();
		}
		// second part:
		// not found? check if there are some registered implementors that are considered as interface implementors
		// Important note: You can't cast the caller to that type! You can only check if that class is of that type.
		List implementorClasses = getValidImplementorClasses(interfaceClass, callerClass);
		if (implementorClasses == null) {
			return false;
		}
		Iterator iterator = implementorClasses.iterator();
		while (iterator.hasNext()) {
			Class implementor = (Class) iterator.next();
			if (classes.contains(implementor)) {
				return true;
			}
		}
		return false;		
	}
	
	
}
