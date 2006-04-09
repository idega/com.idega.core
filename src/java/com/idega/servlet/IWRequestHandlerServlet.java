package com.idega.servlet;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWRequestHandlerServlet extends IWCoreServlet {
	private static final String IDEGAWEB_START_URI = "/idegaweb";
	private static final String SLASH = "/";
	private String currentIdegaWebAppPath;
	public IWRequestHandlerServlet() {
	}
	protected String getCurrentIdegaWebAppURI() {
		if (this.currentIdegaWebAppPath == null) {
			this.currentIdegaWebAppPath = this.getApplication().getTranslatedURIWithContext(IDEGAWEB_START_URI);
		}
		return this.currentIdegaWebAppPath;
	}
	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		processRequest(servReq, servRes);
	}
	public void doPost(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		processRequest(servReq, servRes);
	}
	protected void processRequest(HttpServletRequest servReq, HttpServletResponse servRes)
		throws ServletException, IOException {
		try {
			String requestURI = servReq.getRequestURI();
			boolean isAccessingIdegaWeb = requestURI.startsWith(getCurrentIdegaWebAppURI());
			if (isAccessingIdegaWeb) {
				boolean processAsRegularFile = false;
				if (requestURI.endsWith(".jpg")) {
					processAsRegularFile = true;
				}
				if (requestURI.endsWith(".gif")) {
					processAsRegularFile = true;
				}
				else if (requestURI.endsWith(".jpeg")) {
					processAsRegularFile = true;
				}
				else if (requestURI.endsWith(".png")) {
					processAsRegularFile = true;
				}
				else if (requestURI.endsWith(".js")) {
					processAsRegularFile = true;
				}
				else if (requestURI.endsWith(".html")) {
					processAsRegularFile = true;
				}
				if (processAsRegularFile) {
					processRegularFileRequest(requestURI, servReq, servRes);
				}
				else {
					//doSomething
					processIdegaWebApplicationsRequest(servReq, servRes);
				}
			}
			else {
				boolean builderPageRequest = false;
				if (this.getApplication().isRunningUnderRootContext()) {
					builderPageRequest = requestURI.equals(SLASH);
				}
				else {
					String requestURI2;
					if (requestURI.endsWith(SLASH)) {
						requestURI2 = requestURI.substring(0, requestURI.length() - 1);
					}
					else {
						requestURI2 = requestURI + SLASH;
					}
					String appContext = this.getApplication().getApplicationContextURI();
					if (appContext.equals(requestURI) || appContext.equals(requestURI2)) {
						builderPageRequest = true;
					}
				}
				if (builderPageRequest) {
					processBuilderPageRequest(servReq, servRes);
				}
				else {
					processRegularFileRequest(requestURI, servReq, servRes);
				}
			}
		}
		catch (IOException ioe) {
			processNotFoundRequest(servReq, servRes);
			ioe.printStackTrace();
		}
	}
	protected void processRegularFileRequest(
		String requestURI,
		HttpServletRequest servReq,
		HttpServletResponse servRes)
		throws IOException {
		String RealPath = this.getApplication().getRealPath(requestURI);
		FileInputStream input = new FileInputStream(RealPath);
		int bufferLength = 100;
		byte[] buffer = new byte[bufferLength];
		int noRead = input.read(buffer);
		OutputStream outStream = servRes.getOutputStream();
		servRes.setContentType(getContentType(requestURI));
		while (noRead != -1) {
			outStream.write(buffer);
			noRead = input.read(buffer);
		}
		input.close();
	}
	protected String getContentType(String requestURI) {
		if (requestURI.endsWith(".jpg")) {
			return "image/jpeg";
		}
		else if (requestURI.endsWith(".gif")) {
			return "image/gif";
		}
		else if (requestURI.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		else if (requestURI.endsWith(".png")) {
			return "image/png";
		}
		else if (requestURI.endsWith(".js")) {
			return "text/javascript";
		}
		else if (requestURI.endsWith(".html")) {
			return "text/html";
		}
		else {
			return "text/html";
		}
	}
	protected void processNotFoundRequest(HttpServletRequest servReq, HttpServletResponse servRes)
		throws ServletException, IOException {
		servRes.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
	protected void processIdegaWebApplicationsRequest(HttpServletRequest servReq, HttpServletResponse servRes)
		throws ServletException, IOException {
		String redirectURI = this.getApplication().getIdegaWebApplicationsURI();
		System.out.println("IWRequestHandlerServlet.processIdegaWebApplicationsRequest(): redirectURI=" + redirectURI);
		servReq.getRequestDispatcher(redirectURI).forward(servReq, servRes);
		//servReq.getRequestDispatcher(redirectURI).include(servReq, servRes);
	}
	protected void processBuilderPageRequest(HttpServletRequest servReq, HttpServletResponse servRes)
		throws ServletException, IOException {
		String redirectURI = this.getApplication().getBuilderPagePrefixURI();
		System.out.println("IWRequestHandlerServlet.processBuilderPageRequest(): redirectURI=" + redirectURI);
		servReq.getRequestDispatcher(redirectURI).forward(servReq, servRes);
		//servReq.getRequestDispatcher(redirectURI).include(servReq, servRes);
	}
}
