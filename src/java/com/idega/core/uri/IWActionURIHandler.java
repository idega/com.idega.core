/*
 * $Id: IWActionURIHandler.java,v 1.3 2005/03/08 18:29:43 gummi Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;



/**
 * 
 *  Last modified: $Date: 2005/03/08 18:29:43 $ by $Author: gummi $
 * 
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public interface IWActionURIHandler {
	
	/**
	 * 
	 * @param requestURI
	 * @return return the real URI for an action URI or null if the handler is not for this requestURI
	 */
//	public String getRedirectURI(String requestURI);
	//removed by eiki...don't think it is needed
	
	/**
	 * 
	 * @param uri
	 * @return return the real URI for an action URI or null if the handler is not for this IWActionURI
	 */
	public String getRedirectURI(IWActionURI uri);	
	
	/**
	 * 
	 * @param requestURI
	 * @return null if the default IWActionURI "parser" class should be used or your own object
	 */
	public IWActionURI getIWActionURI(String requestURI);
	
	/**
	 * 
	 * @param uri This will be of the type com.idega.core.uri.IWActionURI
	 * @return true if this handler supports this action uri otherwise false
	 */
	public boolean canHandleIWActionURI(IWActionURI uri);
	
	/**
	 * 
	 * @return returns identifier for the current implementation of this interface
	 */
	public String getHandlerIdentifier();
	
}
