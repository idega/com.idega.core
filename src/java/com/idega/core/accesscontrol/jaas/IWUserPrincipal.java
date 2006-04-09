/*
 * $Id: IWUserPrincipal.java,v 1.2 2006/04/09 12:13:17 laddi Exp $
 * Created on 3.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.jaas;

import java.security.Principal;


/**
 * 
 *  Last modified: $Date: 2006/04/09 12:13:17 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
 */
public class IWUserPrincipal implements Principal {

    private String _name;
    
    public IWUserPrincipal(String name) {
        this._name = name;
    }
    
    public String getName() {
        return this._name;
    }
    
    public int hashCode() {
        return getName().hashCode();
    }
    
    public String toString() {
        return getName();
    }
    
    public boolean equals(Object principal) {
	    	if(principal instanceof IWUserPrincipal){
	        return ((IWUserPrincipal)principal).getName().equals(getName());
	    	} else {
	    		return false;
	    	}
    }
 }
