/*
 * $Id: DefaultURIHandler.java,v 1.1 2005/01/18 17:11:31 tryggvil Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputLink;


/**
 * 
 *  Last modified: $Date: 2005/01/18 17:11:31 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class DefaultURIHandler implements URIHandler {

	/* (non-Javadoc)
	 * @see com.idega.core.uri.URIHandler#getDataObject()
	 */
	public Object getDataObject(String uri) {
		return null;
	}
	
	public String getName(String uri) {
		Object dataObject = getDataObject(uri);
		if(dataObject!=null){
			return dataObject.toString();
		}
		return null;
	}
	

	/* (non-Javadoc)
	 * @see com.idega.core.uri.URIHandler#createLinkUIComponent(java.lang.String)
	 */
	public UIComponent createLinkUIComponent(URIObject uri) {
		
		String uriString = uri.getURI();
		return createLinkUIComponent(uriString);
	
	}
	

	/* (non-Javadoc)
	 * @see com.idega.core.uri.URIHandler#createLinkUIComponent(java.lang.String)
	 */
	public UIComponent createLinkUIComponent(String uri) {
		
		HtmlOutputLink link = new HtmlOutputLink();
		link.setValue(uri);
		
		Object data = getDataObject(uri);
		if(data!=null){
		
		}
		
		return link;
	}

}
