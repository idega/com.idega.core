/*
 * Created on 1.7.2004
 *
 */
package com.idega.core.appserver;

/**
 * This class is an interface and abstraction to the currently running Application Server
 * (e.g. Tomcat 5, Oracle 9iAS etc.)
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */
public interface AppServer {
    /**
     * Gets the name of the product, e.g. Tomcat
     */
    public String getName();

    /**
     * Gets the name of the vendor, e.g. Apache Foundation
     */
    public String getVendor();

    /**
     * Gets the version of the product, e.g. 5.0.12
     */
    public String getVersion();

    /**
     * Gets if this ApplicationServer is officially supported by idegaWeb
     */
    public boolean isOfficiallySupported();

    /**
     * Gets if this ApplicationServer supports setting the character encoding in
     * the HttpServletRequest object
     */
    public boolean getSupportsSettingCharactersetInRequest();
}