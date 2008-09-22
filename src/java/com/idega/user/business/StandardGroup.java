package com.idega.user.business;

import com.idega.user.data.Group;

/**
 * Represents the standard group in the system (if it's egov, it's usually Commune Accepted Citizens)
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 * 
 *          Last modified: $Date: 2008/09/22 12:03:18 $ by $Author: civilis $
 */
public interface StandardGroup {

	public abstract Group getGroup();
}