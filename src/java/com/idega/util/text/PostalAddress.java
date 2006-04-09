/*
 * $Id: PostalAddress.java,v 1.2 2006/04/09 12:13:12 laddi Exp $
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
 *  Last modified: $Date: 2006/04/09 12:13:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
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
