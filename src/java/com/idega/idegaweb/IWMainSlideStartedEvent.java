package com.idega.idegaweb;

import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/03/31 16:06:56 $ by $Author: anton $
 *
 */
public class IWMainSlideStartedEvent extends ApplicationEvent  {

	private static final long serialVersionUID = -493360564216547575L;
	
	//IWMainApplication iwma;

	public IWMainSlideStartedEvent(Object source) {
		super(source);
		
		//iwma = IWMainApplication.getIWMainApplication();
	}
	
//	public IWMainApplication getIWMA() {
//		return iwma;
//	}
}