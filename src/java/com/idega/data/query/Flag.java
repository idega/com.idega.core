/*
 * $Id: Flag.java,v 1.1 2004/09/15 16:00:32 aron Exp $
 * Created on 15.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data.query;

/**
 * 
 *  Last modified: $Date: 2004/09/15 16:00:32 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface Flag {
    public void flag(boolean flag);
    public boolean isFlagged();
}
