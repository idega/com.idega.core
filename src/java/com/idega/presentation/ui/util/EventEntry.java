/*
 * Created on 23.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.presentation.ui.util;

import java.util.Date;

/**
 * @author aron
 *
 * EventEntry TODO Describe this type
 */
public interface EventEntry {
    
    /**
     * Gets the date of this event
     * @return  date
     */
    public Date getDate();
   
    /**
     * Gets the type of the event
     * @return type
     */
    public String getType();
    /**
     * Gets the source of the event
     * @return source
     */
    public String getSource();
    /** 
     * Gets the user name responsible for the event
     * @return user
     */
    public String getUser();
    /**
     * Gets the event identifier
     * @return event identifier
     */
    public String getEvent();

}
