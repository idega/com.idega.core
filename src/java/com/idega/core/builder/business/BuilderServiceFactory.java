/*
 * Created on 8.7.2003 by  tryggvil in project com.project
 */
package com.idega.core.builder.business;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWApplicationContext;

/**
 * BuilderServiceFactory: This is a helper class to use to fetch an instance of BuilderService
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public final class BuilderServiceFactory
{

	/**
	 * This class should not be instantiated
	 */
	private BuilderServiceFactory()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This method fetches the instance of BuilderService for the current working application
	 * @param iwac The ApplicationContext for the working application
	 * @return the BuilderService instance
	 */
	public static BuilderService getBuilderService(IWApplicationContext iwac) throws RemoteException{
		/*Class serviceClass=null;
		try
		{
			//TODO: Remove hardcoding of serviceclass:
			serviceClass = Class.forName("com.idega.builder.business.IBMainService");
		}
		catch (ClassNotFoundException e)
		{
			throw new RemoteException("BuilderServiceFactory.getBuilderService:"+e.getClass()+":"+e.getMessage());
		}*/
		return (BuilderService)IBOLookup.getServiceInstance(iwac,BuilderService.class);
	}
	
	public static BuilderPageWriterService getBuilderPageWriterService(IWApplicationContext iwac) throws IBOLookupException {
		return (BuilderPageWriterService)IBOLookup.getServiceInstance(iwac,BuilderPageWriterService.class);
	}
	
}