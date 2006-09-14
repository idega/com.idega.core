/*
 * Created on 1.7.2004
 */
package com.idega.core.appserver;
/**
 * Implementation of ApplicationServer for the Tomcat Servlet Container
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */
public class Jetty extends DefaultAppServer {
    
    /**
     * Constructor is to be used in this package only
     */
    Jetty(){}
    
    
    public String getVendor(){
        return "Mortbay";
    }

    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#getName()
     */
    public String getName() {
        return "Jetty";
    }
}
