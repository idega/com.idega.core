/*
 * Created on 1.7.2004
 */
package com.idega.core.appserver;
/**
 * Implementation of ApplicationServer for the Oracle Application Server
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */
public class OracleAS extends DefaultAppServer {

    /**
     * Constructor is to be used in this package only
     */    
    OracleAS(){}
    
    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#getName()
     */
    public String getName() {
        return "Oracle AS";
    }
    /* (non-Javadoc)
     * @see com.idega.core.appserver.AppServer#getVendor()
     */
    public String getVendor() {
        return "Oracle";
    }
    /**
     * This appserver has a bug for this so we return true
     */
    public boolean getSupportsSettingCharactersetInRequest() {
        return true;
    }    
}
