package com.idega.idegaweb;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/04/02 18:31:12 $ by $Author: anton $
 *
 */
public class RepositoryStartedEvent extends ApplicationEvent  {

	private static final long serialVersionUID = 4469069084637205599L;
	
	IWMainApplication iwma;

	public RepositoryStartedEvent(Object source) {
		super(source);
		
		iwma = (IWMainApplication)source;
	}
	
	public IWMainApplication getIWMA() {
		return iwma;
	}
}