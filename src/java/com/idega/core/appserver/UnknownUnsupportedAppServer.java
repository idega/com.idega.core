/*
 * Created on 1.7.2004
 */
package com.idega.core.appserver;

/**
 * This class is instaciated for unknown and unsupported application servers.
 * Copyright (c) 2002-2004 idega software
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class UnknownUnsupportedAppServer extends DefaultAppServer {
    
    /**
     * Constructor is to be used in this package only
     */
    UnknownUnsupportedAppServer(){}
    
    
    public String getVendor(){
        return "Unknown";
    }

    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#getName()
     */
    public String getName() {
        return "Unknown";
    }

    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#isOfficiallySupported()
     */
    public boolean isOfficiallySupported() {
        return false;
    }
    
}