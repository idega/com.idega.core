/*
 * $Id: URIHandler.java,v 1.1 2005/01/18 17:11:31 tryggvil Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;

import javax.faces.component.UIComponent;


/**
 * 
 *  Last modified: $Date: 2005/01/18 17:11:31 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface URIHandler {
	
	public Object getDataObject(String uri);
	public UIComponent createLinkUIComponent(String uri);
	public UIComponent createLinkUIComponent(URIObject uri);
	
}
