/*
 * Created on 11.8.2003 by  tryggvil in project com.project
 */
package com.idega.repository.data;

import java.util.HashMap;
import java.util.Map;

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
	/**
	 * @return
	 */
	public String getRefactoredClassName(String oldClassName)
	{
		return (String)getRefactoredClassNames().get(oldClassName);
	}
	
	public void registerRefactoredClass(String oldClassName,String newClassName){
		getRefactoredClassNames().put(oldClassName,newClassName);
	}
	
	public boolean isClassRefactored(String oldClassName){
		return getRefactoredClassName(oldClassName)!=null;
	}
}
