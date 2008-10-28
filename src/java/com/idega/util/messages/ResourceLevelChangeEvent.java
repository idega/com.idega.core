package com.idega.util.messages;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/10/28 09:53:39 $ by $Author: anton $
 *
 */
public class ResourceLevelChangeEvent extends ApplicationEvent  {

	private static final long serialVersionUID = 4469069084637205599L;
	
	public ResourceLevelChangeEvent(Object source) {
		super(source);
	}
}