package com.idega.idegaweb;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/04/01 17:47:29 $ by $Author: anton $
 *
 */
public class IWMainSlideStartedEvent extends ApplicationEvent  {

	private static final long serialVersionUID = -493360564216547575L;
	
	IWMainApplication iwma;

	public IWMainSlideStartedEvent(Object source) {
		super(source);
		
		iwma = (IWMainApplication)source;
	}
	
	public IWMainApplication getIWMA() {
		return iwma;
	}
}