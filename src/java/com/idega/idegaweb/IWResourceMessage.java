/*
 * $Id: IWResourceMessage.java,v 1.1 2004/10/05 10:55:09 aron Exp $
 * Created on 5.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

/**
 * 
 *  Last modified: $Date: 2004/10/05 10:55:09 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class IWResourceMessage {
    
    private String message;
    private String key;
    /**
     * 
     */
    public IWResourceMessage() {
       this(null,null);
    }
    
    public IWResourceMessage(String key) {
        this(key,null);
    }
    
    public IWResourceMessage(String key,String message) {
        this.message = message;
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
