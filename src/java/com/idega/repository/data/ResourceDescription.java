/*
 * $Id: ResourceDescription.java,v 1.1.2.1 2007/01/12 19:32:15 idegaweb Exp $
 * Created on Sep 29, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 * 
 */
package com.idega.repository.data;

/**
 *  A container that holds metadata for a resource.
 *  <p/>
 *  Last modified: $Date: 2007/01/12 19:32:15 $ by $Author: idegaweb $
 *   
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1.2.1 $
 * 
 * @see com.idega.repository.data.Resource
 */
public class ResourceDescription {
	
	private String source = null;
	private String provider = null;
	private boolean isEjb = false;
	
	public ResourceDescription(String source, String provider, boolean isEjb) {
		this.source = source;
		this.provider = provider;
		this.isEjb = isEjb;
	}

	
	public boolean isEjb() {
		return this.isEjb;
	}

	
	public String getProvider() {
		return this.provider;
	}

	
	public String getSource() {
		return this.source;
	}
}
