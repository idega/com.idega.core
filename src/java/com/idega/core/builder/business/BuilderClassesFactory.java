package com.idega.core.builder.business;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: 
 * Returns an instance of the implementation of the BuilderClassesFactory.
 * This instance depends on the set implementation class. 
 * The implementation class should be set by the current application during startup.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 10, 2004
 */
public class BuilderClassesFactory {
	
	static Class IMPLEMENTATIONCLASS = null;
	
	static public BuilderClassesImplFactory newInstance() {
		try {
			return (BuilderClassesImplFactory) IMPLEMENTATIONCLASS.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("[BuilderClassesFactory] BuilderClassesImplFactory could not be instanciated. (propably class of BuilderClassesImplFactory is null that is it was not set");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("[BuilderClassesFactory] BuilderClassesImplFactory could not be instanciated because of access problems. (propably default constructor doesn't exist or is private");
		}
	}
		
	static public void setBuilderClassesFactory(Class implementationClass)	{
		IMPLEMENTATIONCLASS = implementationClass;
	}
	
}
