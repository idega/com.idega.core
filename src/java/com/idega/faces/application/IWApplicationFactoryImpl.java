/*
 * Created on 12.3.2004 by  tryggvil in project smile
 */
package com.idega.faces.application;

import java.util.logging.Logger;

import javax.faces.application.Application;
import org.apache.myfaces.application.ApplicationFactoryImpl;

//import org.apache.log4j.Logger;


/**
 * IWApplicationFactoryImpl //TODO: tryggvil Describe class
 * Copyright (C) idega software 2004
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWApplicationFactoryImpl extends ApplicationFactoryImpl {

	private Application application = new IWApplicationImpl();
	private static Logger log = Logger.getLogger(IWApplicationFactoryImpl.class.getName());
	
	public  IWApplicationFactoryImpl(){
		log.info("Loading IWApplicationFactoryImpl");
	}
	
	/**
	 * @see javax.faces.application.ApplicationFactory#getApplication()
	 */
	public synchronized Application getApplication() {
		return application;
	}

	/**
	 * @see javax.faces.application.ApplicationFactory#setApplication(javax.faces.application.Application)
	 */
	public synchronized void setApplication(Application application) {
		this.application = application;
	}
}
