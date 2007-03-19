package com.idega.business;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version 1.0
 * 
 */
public abstract class IBOSessionBeanWrapper extends IBOSessionBean {
	
	private static final Logger logger = Logger.getLogger(IBOSessionBeanWrapper.class.getName());

	protected IBOSession getIBOSessionBean() {
		
		try {
			
			return IBOLookup.getSessionInstance(IWContext.getIWContext(FacesContext.getCurrentInstance()), getIBOSessionBeanInterfaceClass());
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception while looking up for IBO session bean by class: " + getIBOSessionBeanInterfaceClass().getName(), e);
			return null;
		}
		
	}
	
	protected abstract Class getIBOSessionBeanInterfaceClass();
}