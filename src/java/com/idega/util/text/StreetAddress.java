/*
 * $Id: StreetAddress.java,v 1.2 2006/04/09 12:13:12 laddi Exp $
 * Created on 20.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.text;

/**
 *  Formats street addresses
 *  Last modified: $Date: 2006/04/09 12:13:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class StreetAddress {
    private String street;
    private String number;
    
    public StreetAddress(String streetName, String streetNumber){
        this.street = streetName;
        this.number = streetNumber;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer addr = new StringBuffer();
    		if (this.street != null) {
					addr.append(this.street).append(" ");
				}
    		if (this.number != null) {
					addr.append(this.number);
				}
    		return TextSoap.capitalize(addr.toString(), " ");
    }
    
}
