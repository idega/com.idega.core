/*
 * $Id: IndexColumn.java,v 1.1 2004/11/01 10:05:31 aron Exp $
 * Created on 28.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.dbschema;

/**
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface IndexColumn {
    /**
     * Gets the name of the table field to be indexed
     * @return
     */
    public String getName();
    /**
     * By default, the values in a field are indexed in ascending order, 
     * but if you want to index them in descending order, you can add the reserved word DESC. 
     * You can also index more than one field simply by listing the fields within parentheses. 
     * @return
     */
    public boolean isDescending();
}
