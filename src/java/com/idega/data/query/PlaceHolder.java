/*
 * $Id: PlaceHolder.java,v 1.1 2004/09/10 12:53:06 aron Exp $
 * Created on 10.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data.query;

/**
 *  PlaceHolder interface defines methods for PlaceHolder object used
 *  for parameter values in PreparedStatement
 * 
 *  Last modified: $Date: 2004/09/10 12:53:06 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface PlaceHolder {
    /**
     * Returns placeholder value
     * @return
     */
    public Object getPlaceValue();
}
