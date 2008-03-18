package com.idega.idegaweb;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/03/18 15:03:53 $ by $Author: civilis $
 *
 */
public class IWMainApplicationStartedEvent extends ApplicationEvent  {

	private static final long serialVersionUID = -493360564216547575L;
	
	IWMainApplication iwma;

	public IWMainApplicationStartedEvent(Object source) {
		super(source);
		
		iwma = ((IWMainApplicationStarter)source).getIWMainApplication();
	}
	
	public IWMainApplication getIWMA() {
		return iwma;
	}
}