package com.idega.repository.data;

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
		interfaceCallerImplementor.put(interfaceClassName, callerClassName, implementorClassName);
	}
	
	public void addImplementor(Class interfaceClass, Class implementationClass) {
		addImplementorForCaller(interfaceClass, null, implementationClass);
	}
	
	public Object getImplementor(Class interfaceClass, Class callerClass) throws ClassNotFoundException {
		String interfaceClassName = interfaceClass.getName();
		String callerClassName = callerClass.getName();
		if (! interfaceCallerImplementor.containsKey(interfaceClassName, callerClassName)) {
			callerClassName = GENERAL;
		}
		String implementorClassName = (String) interfaceCallerImplementor.get(interfaceClassName, callerClassName);
		if (implementorClassName == null) {
			return new ClassNotFoundException("[ImplementorRepository] None implementor for interface " + interfaceClassName + " was found");
		}
		Class implementorClass = Class.forName(implementorClassName);
		Object implementor = null;
		try {
			implementor = implementorClass.newInstance();
		} 
		catch (InstantiationException e) {
			return new ClassNotFoundException("[ImplementorRepository] Implementor for interface " + interfaceClassName + "could not be created");		
		} 
		catch (IllegalAccessException e) {
			return new ClassNotFoundException("[ImplementorRepository] None implementor for interface " + interfaceClassName + "could not be created because of access problems");		
		}
		return implementor;
		
		
		
	}
}
