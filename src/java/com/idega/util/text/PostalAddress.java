/*
 * $Id: PostalAddress.java,v 1.1.2.1 2007/01/12 19:32:37 idegaweb Exp $
 * Created on 20.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.text;

/**
 *  Formats postal addresses
 * 
 *  Last modified: $Date: 2007/01/12 19:32:37 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1.2.1 $
 */
public class PostalAddress {
    private String code;
    private String name;
    
    public PostalAddress(String postalCode, String postalName){
        this.code = postalCode;
        this.name = postalName;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    		StringBuffer addr = new StringBuffer();
    		if(this.code!=null) {
				addr.append(this.code).append(" ");
			}
    		if(this.name !=null) {
				addr.append(this.name);
			}
    		return addr.toString();
    }
}
