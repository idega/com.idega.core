package com.idega.idegaweb;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/04/17 18:56:30 $ by $Author: arunas $
 *
 */
public class IWMainApplicationShutdownEvent extends ApplicationEvent  {

	private static final long serialVersionUID = -493360564216547571L;
	
	IWMainApplication iwma;

	public IWMainApplicationShutdownEvent(Object source) {
		super(source);
		
		iwma = ((IWMainApplicationStarter)source).getIWMainApplication();
	}
	
	public IWMainApplication getIWMA() {
		return iwma;
	}
}