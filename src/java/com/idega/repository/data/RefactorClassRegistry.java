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
public class RefactorClassRegistry
{	
	private static RefactorClassRegistry instance;
	
	//This constructor should not be called
	private RefactorClassRegistry(){
	}

	
	public static RefactorClassRegistry getInstance(){
		if(instance==null){
			instance = new RefactorClassRegistry();
		}
		return instance;
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
}
