/*
 * $Id: ICFileVersion.java,v 1.1 2004/11/29 16:02:35 aron Exp $
 * Created on 29.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.file.business;

import java.util.Date;

/**
 *  ICFileVersion describes a version of a ICFile.
 *  Version number, change date.
 * 
 *  Last modified: $Date: 2004/11/29 16:02:35 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface ICFileVersion {
    public String getVersion();
    public Date getDate();
}
