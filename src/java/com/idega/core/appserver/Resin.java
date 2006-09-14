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
public class Resin extends DefaultAppServer {
    
    /**
     * Constructor is to be used in this package only
     */
    Resin(){}
    
    
    public String getVendor(){
        return "Caucho";
    }

    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#getName()
     */
    public String getName() {
        return "Resin";
    }
}
