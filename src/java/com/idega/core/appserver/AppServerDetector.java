/*
 * Created on 1.7.2004
 */
package com.idega.core.appserver;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 * 
 * This class is an interface to detect and set the currently used Appserver
 * (e.g. Tomcat 5, Oracle 9iAS etc.). <br>
 * In general this class should never excplicitly be used, but the AppServer
 * instance fetched with IWMainApplication.getApplicationServer().
 */
public class AppServerDetector {

    private static String contextKey = "IW_APPSERVER";

    /**
     * Gets the ApplicationServer instance. <br>
     * In general this method should never excplicitly be used, but the
     * AppServer instance fetched with IWMainApplication.getApplicationServer().
     * 
     * @param context
     * @return
     */
    public static AppServer getAppServerForApplication(ServletContext context) {
        AppServer appserver = (AppServer) context.getAttribute(contextKey);
        if (appserver == null) {
            appserver = setAppServerForApplication(context);
        }
        return appserver;
    }

    /**
     * Sets the ApplicationServer instance. <br>
     * This method should only be called once. And is called when the IWMainApplication is instanciated.
     * 
     * @param context
     * @return
     */
    public static AppServer setAppServerForApplication(ServletContext context) {
        AppServer appserver = null;
        String serverInfo = context.getServerInfo();
        //set the server info string to lower case
        String lowerServerString = serverInfo.toLowerCase();
        if (lowerServerString.indexOf("tomcat") != -1) {
            Tomcat tomcat = new Tomcat();
            appserver = tomcat;
            int indexOfSlash = serverInfo.indexOf("/");
            if (indexOfSlash != -1) {
                String version = serverInfo.substring(indexOfSlash + 1,
                        serverInfo.length());
                tomcat.setVersion(version);
            }
        }
        else if (lowerServerString.indexOf("oracle") != -1) {
            OracleAS oas = new OracleAS();
            appserver = oas;
            int indexBeginPar = serverInfo.indexOf("(");
            int indexEndPar = serverInfo.indexOf(")");
            if (indexBeginPar != -1 && indexEndPar != -1) {
                String version = serverInfo.substring(indexBeginPar + 1,indexEndPar);
                oas.setVersion(version);
            }        
        }
        else if (lowerServerString.indexOf("jetty") != -1) {
            Jetty jetty = new Jetty();
            appserver = jetty;
            /*int indexBeginPar = serverInfo.indexOf("(");
            int indexEndPar = serverInfo.indexOf(")");
            if (indexBeginPar != -1 && indexEndPar != -1) {
                String version = serverInfo.substring(indexBeginPar + 1,indexEndPar);
                jetty.setVersion(version);
            }*/
        }
        else if (lowerServerString.indexOf("resin") != -1) {
            Resin resin = new Resin();
            appserver = resin;
            /*int indexBeginPar = serverInfo.indexOf("(");
            int indexEndPar = serverInfo.indexOf(")");
            if (indexBeginPar != -1 && indexEndPar != -1) {
                String version = serverInfo.substring(indexBeginPar + 1,indexEndPar);
                resin.setVersion(version);
            }*/
        }
        else{
            appserver = new UnknownUnsupportedAppServer();
        }
        context.setAttribute(contextKey, appserver);
        return appserver;
    }

}