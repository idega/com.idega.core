/*
 * $Id: IDOProcedure.java,v 1.1 2004/09/07 12:06:32 gummi Exp $
 * Created on 31.8.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data;

import java.sql.ResultSet;



/**
 * 
 *  Last modified: $Date: 2004/09/07 12:06:32 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public interface IDOProcedure {
    /**
     * Returns the name of the method represented by this <code>IDOProcedure</code> 
     * object, as a <code>String</code>.
     */
    public String getName();

//    /**
//     * Returns a <code>Class</code> object that represents the formal return type
//     * of the method represented by this <code>IDOProcedure</code> object.
//     * 
//     * @return the return type for the method this object represents
//     */
//    public Class getReturnType();
    
//    public boolean returnsCollection();

    /**
     * Returns an array of <code>Class</code> objects that represent the formal
     * parameter types, in declaration order, of the method
     * represented by this <code>IDOProcedure</code> object.  Returns an array of length
     * 0 if the underlying method takes no parameters.
     * 
     * @return the parameter types for the method this object
     * represents
     */
    public Class[] getParameterTypes();
    
    public Object processResultSet(ResultSet rs);
    public boolean isAvailable();
    
}
