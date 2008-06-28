package com.idega.core.file.tmp;

import java.net.URI;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/06/28 19:03:12 $ by $Author: civilis $
 *
 */
public interface TmpFilesModifyStrategy {

	/**
	 * executes moving by given uri to some implicit uri. Might optionally modify given resource, e.g., submission instance node, to reflect changes 
	 * @param uri - uri to take file from
	 * @param resource - optional
	 * @return - new file uri
	 */
	public abstract URI executeMove(URI uri, Object resource);
}