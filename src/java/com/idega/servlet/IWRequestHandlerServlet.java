package com.idega.servlet;

import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.*;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWRequestHandlerServlet extends IWCoreServlet {

  private static final String IDEGAWEB_START_URI="/idegaweb";
  private static final String SLASH="/";

  public IWRequestHandlerServlet() {
  }

	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes)throws ServletException,IOException{
		processRequest(servReq,servRes);
	}


	public void doPost(HttpServletRequest servReq, HttpServletResponse servRes)throws ServletException,IOException {
		processRequest(servReq,servRes);
	}

    protected void processRequest(HttpServletRequest servReq, HttpServletResponse servRes)throws ServletException,IOException {
      try{

        String requestURI = servReq.getRequestURI();

        boolean isAccessingIdegaWeb = requestURI.startsWith(IDEGAWEB_START_URI);
        if(isAccessingIdegaWeb){
          boolean processAsRegularFile = false;
          if(requestURI.endsWith(".jpg")){
            processAsRegularFile=true;
          }
          if(requestURI.endsWith(".gif")){
            processAsRegularFile=true;
          }
          else if(requestURI.endsWith(".jpeg")){
            processAsRegularFile=true;
          }
          else if(requestURI.endsWith(".png")){
            processAsRegularFile=true;
          }
          else if(requestURI.endsWith(".js")){
            processAsRegularFile=true;
          }
          else if(requestURI.endsWith(".html")){
            processAsRegularFile=true;
          }

          if(processAsRegularFile){
            processRegularFileRequest(requestURI,servReq,servRes);
          }
          else{
            //doSomething
            processIdegaWebApplicationsRequest(servReq,servRes);
          }
        }
        else{
          boolean builderPageRequest = requestURI.equals(SLASH);
          if(builderPageRequest){
            processBuilderPageRequest(servReq,servRes);
          }
          else{
            processRegularFileRequest(requestURI,servReq,servRes);
          }
        }
      }
      catch(IOException ioe){
        servRes.sendError(servRes.SC_NOT_FOUND);
      }
    }


    protected void processRegularFileRequest(String requestURI, HttpServletRequest servReq, HttpServletResponse servRes)throws IOException{
        OutputStream outStream = servRes.getOutputStream();

        servRes.setContentType(getContentType(requestURI));
        String RealPath = this.getServletContext().getRealPath(requestURI);

        FileInputStream input = new FileInputStream(RealPath);

        int bufferLength = 100;
        byte[] buffer = new byte[bufferLength];

        int noRead = input.read(buffer);
        while(noRead!=-1){
          outStream.write(buffer);
          noRead=input.read(buffer);
        }

        input.close();
    }

    protected String getContentType(String requestURI){
        if(requestURI.endsWith(".jpg")){
          return "image/jpeg";
        }
        else if(requestURI.endsWith(".gif")){
          return "image/gif";
        }
        else if(requestURI.endsWith(".jpeg")){
          return "image/jpeg";
        }
        else if(requestURI.endsWith(".png")){
          return "image/png";
        }
        else if(requestURI.endsWith(".js")){
          return "text/javascript";
        }
        else if(requestURI.endsWith(".html")){
          return "text/html";
        }
        else{
          return "text/html";
        }
    }


  protected void processIdegaWebApplicationsRequest(HttpServletRequest servReq, HttpServletResponse servRes)throws ServletException, IOException {
    String redirectURI = "/servlet/idegaweb";
    servReq.getRequestDispatcher(redirectURI).forward(servReq,servRes);
  }

  protected void processBuilderPageRequest(HttpServletRequest servReq, HttpServletResponse servRes)throws ServletException, IOException {
    String redirectURI = "/servlet/builder";
    servReq.getRequestDispatcher(redirectURI).forward(servReq,servRes);

    //servRes.sendRedirect("/servlet/builder");
  }

}
