/*
 * $Id: StreetAddress.java,v 1.1 2004/10/20 15:00:07 aron Exp $
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
 *  Last modified: $Date: 2004/10/20 15:00:07 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
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
    		if (street != null)
    		    addr.append(street).append(" ");
    		if (number != null)
    		    addr.append(number);
    		return TextSoap.capitalize(addr.toString(), " ");
    }
    
}
