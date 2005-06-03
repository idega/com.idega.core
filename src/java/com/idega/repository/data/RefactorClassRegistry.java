/*
 * Created on 11.8.2003 by  tryggvil in project com.project
 */
package com.idega.repository.data;

import java.util.HashMap;
import java.util.Map;
import com.idega.util.StringHandler;

/**
 * A class to hold a registry over classes that have been moved between packages or renamed (refactored)
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class RefactorClassRegistry implements Singleton {	
	
	private static Instantiator instantiator = new Instantiator() 
		{ 
			public Object getInstance() { 
				return new RefactorClassRegistry(); 
			}
		};
	
	//This constructor should not be called
	protected RefactorClassRegistry(){
		// default constructor
	}

	public static Class forName(String className) throws ClassNotFoundException {
		// first try to find the class
		try {
			return Class.forName(className);
		}
		catch (ClassNotFoundException ex) {
			return RefactorClassRegistry.getInstance().findClass(className, ex);
		}
	}
	
	public static RefactorClassRegistry getInstance(){
		return (RefactorClassRegistry) SingletonRepository.getRepository().getInstance(RefactorClassRegistry.class, instantiator);      
	}
		
	private Map refactoredClassNamesMap;
	private Map refactoredPackageNamesMap;
	
	/**
	 * @return
	 */
	public Map getRefactoredClassNames()
	{
		// TODO Auto-generated method stub
		if(refactoredClassNamesMap==null){
			refactoredClassNamesMap=new HashMap();
		}
		return refactoredClassNamesMap;
	}
	
	public Map getRefactoredPackageNames() {
		if (refactoredPackageNamesMap == null) {
			refactoredPackageNamesMap = new HashMap();
		}
		return refactoredPackageNamesMap;
	}
	
	/**
	 * @return
	 */
	public String getRefactoredClassName(String oldClassName)
	{
		String result = (String)getRefactoredClassNames().get(oldClassName);
		if (result == null) {
			String[] packageClass = StringHandler.splitOffPackageFromClassName(oldClassName);
			String newPackage = (String) getRefactoredPackageNames().get(packageClass[0]);
			if (newPackage != null) {
				StringBuffer buffer = new StringBuffer(newPackage);
				buffer.append(".").append(packageClass[1]);
				return buffer.toString();
			}
		}
		return result;
	}
	
	public void registerRefactoredPackage(String oldPackageName, Package validPackage) {
		registerRefactoredPackage(oldPackageName, validPackage.getName());
	}
	
	public void registerRefactoredPackage(String oldPackageName, String validPackageName) {
		getRefactoredPackageNames().put(oldPackageName, validPackageName);
	}
	
	public void registerRefactoredClass(String oldClassName, Class validClass) {
		registerRefactoredClass(oldClassName, validClass.getName());
	}
	
	public void registerRefactoredClass(String oldClassName,String newClassName){
		getRefactoredClassNames().put(oldClassName,newClassName);
	}
	
	public boolean isClassRefactored(String oldClassName){
		return getRefactoredClassName(oldClassName)!=null;
	}
	
	public Object newInstance(String className, Class callerClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class myClass = classForName(className);
		return newInstance(myClass, callerClass);
		
	}
	
	public Object newInstance(Class aClass, Class callerClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (aClass.isInterface()) {
			return ImplementorRepository.getInstance().newInstance(aClass, callerClass);
		}
		return aClass.newInstance();
	}
	
	public Class classForName(String className) throws ClassNotFoundException {
		return RefactorClassRegistry.forName(className);
	}
		
	private Class findClass(String className, ClassNotFoundException classNotFoundEx) throws ClassNotFoundException {
		// bad luck
		// is the class refactored?
		String refactoredClassName = getRefactoredClassName(className);
		if (refactoredClassName == null) {
			// nothing found, throw exception
			throw classNotFoundEx;
		}
		// something was found...but does the class exist?
		try {
			return Class.forName(refactoredClassName);
		}
		catch (ClassNotFoundException refactoredClassNotFoundEx) {
			// that is really bad luck (and strange)
			throw new ClassNotFoundException("[RefactoredClassName] Refactored class ( "+ refactoredClassName+" ) was not found. Original class name: "+className, classNotFoundEx);
		}
	}
}
