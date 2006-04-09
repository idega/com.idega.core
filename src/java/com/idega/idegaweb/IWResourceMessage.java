/*
 * $Id: IWResourceMessage.java,v 1.2 2006/04/09 12:13:14 laddi Exp $
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
 *  Last modified: $Date: 2006/04/09 12:13:14 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
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
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
