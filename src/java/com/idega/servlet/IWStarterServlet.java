package com.idega.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.idega.idegaweb.IWMainApplicationStarter;

/**
 * This servlet Starts idegaWeb
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class IWStarterServlet extends GenericServlet {
    IWMainApplicationStarter starter;

    public void init() throws ServletException {
        starter = new IWMainApplicationStarter(this.getServletContext());
    }

    public void service(ServletRequest _req, ServletResponse _res)
            throws IOException {
        _res.getWriter().println("No Service");
    }

    public void destroy() {
        sendShutdownMessage("Destroying IdegaWebStarterServlet");
        starter.shutdown();
        starter=null;
        sendShutdownMessage("Destroyed IdegaWebStarterServlet");
    }

    public void sendStartMessage(String message) {
        System.out.println("[idegaWeb] : startup : " + message);
    }

    public void sendShutdownMessage(String message) {
        System.out.println("[idegaWeb] : shutdown : " + message);
    }
}