/*
 * $Id: IWPresentationServlet.java,v 1.57 2004/05/26 12:53:41 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.servlet;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.idega.business.IBOLookup;
import com.idega.event.IWEventMachine;
import com.idega.event.IWEventProcessor;
import com.idega.event.IWModuleEvent;
import com.idega.event.IWPresentationEvent;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.UploadFile;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.util.FileUtil;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
/**
 * This is the base abstract servlet for presenting pages built with com.idega.presentation.PresentationObject objects and subclasses of that base class.
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWPresentationServlet extends IWCoreServlet {
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.core";
	protected static final String IW_CONTEXT_KEY = "idegaweb_iwc";
	private Boolean checkedCurrentAppContext = null;
	/*
	public void init(ServletConfig config)
	  throws ServletException{
		super.init(config);
	
	}*/
	/*public void init()throws ServletException{
		//System.out.println("Inside init() for "+getServletConfig().getServletName());
		super.init();
		String servletName = this.getServletConfig().getServletName();
		System.out.println("Inside init for "+servletName);
		initializePage();
	}*/
	protected boolean hasCheckedCurrentAppContext() {
		if (checkedCurrentAppContext == null) {
			synchronized (this) {
				if (checkedCurrentAppContext == null) {
					checkedCurrentAppContext = Boolean.TRUE;
					return false;
				}
				else {
					return true;
				}
			}
		}
		else {
			return true;
		}
	}
	protected void initializeIWContext(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//TODO: Find a better solution for this
		IWContext iwc = getIWContext();
		if(iwc==null) {
			if (!hasCheckedCurrentAppContext()) {
				this.getApplication().setApplicationContextURI(request.getContextPath());
			}
	
			iwc = new IWContext(request, response,getServletContext());
			
	
			if (iwc.isMultipartFormData()) {
				//writer.println("form is multipart");
				handleMultipartFormData(iwc);
			}
			//          else {
			//            writer.println("form is not multipart");
			//            writer.println("<br>type: "+iwc.getRequestContentType());
			//          }
			String markup = iwc.getParameter("idega_special_markup");
			if (markup != null) {
				iwc.setLanguage(markup);
			}
			storeObject(IW_CONTEXT_KEY, iwc);
		}
	}
	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		__main(servReq, servRes);
	}
	public void doPost(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		__main(servReq, servRes);
	}
	public void processBusinessEvent(IWContext iwc)
		throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		IWEventProcessor.getInstance().processBusinessEvent(iwc);
		/*
		String eventClassEncr = iwc.getParameter(IWMainApplication.IdegaEventListenerClassParameter);
		processIWEvent(iwc,eventClassEncr);
		*/
	}
	private void processApplicationEvents(IWContext iwc) throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException{
		IWEventProcessor.getInstance().processApplicationEvents(iwc);
	}
	public boolean processAWTEvent(IWContext iwc, HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		String sessionAddress = iwc.getParameter(IWMainApplication.IWEventSessionAddressParameter);
		if (sessionAddress != null && !"".equals(sessionAddress)) {
			Object obj = iwc.getSessionAttribute(sessionAddress);
			if (obj != null) {
				if (obj instanceof ActiveEvent && obj instanceof AWTEvent) {
					if (Page.isRequestingTopPage(iwc)) {
						__theService(request, response);
					}
					//theServiceDone = true;
					if (obj instanceof IWModuleEvent) {
						((IWModuleEvent) obj).setIWContext(iwc);
					}
					else {
						this.getPage()._setIWContext(iwc);
					}
					((ActiveEvent) obj).dispatch();
					return true;
					/* Kommentað út þar til kerfið ræður við þræði
					  EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
					  q.postEvent((AWTEvent)obj);
					*/
				}
			}
		}
		return false;
	}
	public void __main(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//PrintWriter writer = null;
		try {
			//long time1 = System.currentTimeMillis();
			//com.idega.core.accesscontrol.business.AccessControl._COUNTER = 0;
			initializeIWContext(request, response);
			IWContext iwc = getIWContext();
			com.idega.util.ThreadContext tc = this.getThreadContext();
			tc.getAttribute("");
			//try {
				handleLocaleParameter(iwc);
				//writer = iwc.getWriter(); //get the writer
				try{
					processBusinessEvent(iwc);
					processApplicationEvents(iwc);
				}
				catch(Exception e){
					e.printStackTrace();
				}

				initializePage();
				processPresentationEvent(iwc);
				//added by gummi@idega.is
				//begin
				boolean theServiceDone = processAWTEvent(iwc, request, response);
				//end
				// new Event system
				increaseHistoryID(iwc);
				handleEvent(iwc);
				// event system end
				//if (isActionPerformed(request,response)){
				//actionPerformed(new ModuleEvent(request,response));
				//actionPerformed(new ModuleEvent(iwc));
				//}
				//else{
				if (!theServiceDone) //gummi@idega.is
					{
					if (Page.isRequestingTopPage(iwc)) {
						__theService(request, response);
					}
				}
				//}
				//      iwc.getWriter().println("\n");
				_main(iwc);
			//}
			/*catch (IWPageInitializationException iwe) {
				iwe.printStackTrace();
				ErrorPage errorPage = new ErrorPage();
				errorPage.setErrorMessage(iwe.getMessage());
				this.setPage(errorPage);
			}
			catch (Exception e) {
				e.printStackTrace();
				ErrorPage errorPage = new ErrorPage();
				errorPage.setErrorMessage("There was an error, your session is probably expired");
				this.setPage(errorPage);
			}*/
			__print(iwc);
			//long time2 = System.currentTimeMillis();
			
			//writer.println("<!--" + (time2 - time1) + " ms-->");
			
			//done
			finished(iwc);
			/*
			writer.println("<!--");
			writer.println("---------- Session Attributes -----------");
			Enumeration enum = iwc.getSession().getAttributeNames();
			if(enum != null){
			  writer.println("Session.hashCode() = "+ iwc.getSession().hashCode());
			  while (enum.hasMoreElements()) {
			    String item = (String)enum.nextElement();
			    writer.println("Attribute: "+item+" = "+iwc.getSession().getAttribute(item));
			  }
			
			
			} else {
			  writer.println("Session empty");
			}
			writer.println("-->");
			*/
			//writer.println("<!-- viewpermission: "+com.idega.core.accesscontrol.business.AccessControl._COUNTER +" -->");
			/*if (connectionRequested()){
				    freeConnection();
			}*/
			//getThreadContext().releaseThread(Thread.currentThread());
		}
		catch (Exception ex) {
			if (ex instanceof java.io.IOException){
				  //throw (java.io.IOException) ex.fillInStackTrace();
				throw (java.io.IOException)ex;
			}
			else if (ex instanceof javax.servlet.ServletException){
				  //throw (javax.servlet.ServletException) ex.fillInStackTrace();
				throw (javax.servlet.ServletException)ex;
			}
			else{
				throw new javax.servlet.ServletException(ex);
				/*writer.println("<H2>IWError</H2>");
				writer.println("<pre>");
				writer.println(ex.getMessage());
				ex.printStackTrace(System.err);
				writer.println("</pre>");*/
			}
		}
	}
	/**
	 * This method is called after everything is done
	 * @param iwc
	 */
	protected void finished(IWContext iwc) {
	}
	
	public void __theService(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
	}
	public void initializePage() throws Exception {
		//String servletName = this.getServletConfig().getServletName();
		//System.out.println("Inside initializePage for "+servletName);
		setPage(Page.loadPage(getIWContext()));
	}
	public void setPage(Page myPage) {
		//System.out.println("Inside setPage()");
		//storeObject(Page.IW_PAGE_KEY,(Page)myPage.clone());
		Page.setTopPage(myPage);
		//storeObject(Page.IW_PAGE_KEY,myPage);
		//this.page=page;
		/*String servletName = this.getServletConfig().getServletName();
		String attributeKey =servletName+"_idega_page";
		//System.out.println("AttributeKey="+attributeKey+" for setPage()");
		getServletContext().setAttribute(attributeKey,myPage);*/
	}
	public Page getPage() {
		return Page.getPage(getIWContext());
	}
	public static IWContext getIWContext() {
		return (IWContext) retrieveObject(IW_CONTEXT_KEY);
	}
	/**
	 * @deprecated
	 */
	public HttpServletRequest getRequest() {
		return getIWContext().getRequest();
	}
	/**
	 * @deprecated
	 */
	public HttpSession getSession() {
		return getIWContext().getSession();
	}
	/**
	 * @deprecated
	 */
	public HttpServletResponse getResponse() {
		return getIWContext().getResponse();
	}
	public String getParameter(String parameterName) {
		return getRequest().getParameter(parameterName);
	}
	public String[] getParameterValues(String parameterName) {
		return getRequest().getParameterValues(parameterName);
	}
	public Object getSessionAttribute(String attributeName) {
		return getSession().getAttribute(attributeName);
	}
	public void setSessionAttribute(String attributeName, Object attribute) {
		getSession().setAttribute(attributeName, attribute);
	}
	public void removeSessionAttribute(String attributeName) {
		getSession().removeAttribute(attributeName);
	}
	public void add(PresentationObject objectToAdd) {
		getPage().add(objectToAdd);
	}
	public void add(String text) {
		add(new Text(text));
	}
	public void addToTemplate(PresentationObject obj) {
	}
	public void _main(IWContext iwc) throws Exception {
		//getPage().setTreeID("0");
		//getPage().updateTreeIDs();
		//String node = iwc.getParameter("idega_special_tree_node");
		//if( node != null){
		//  PresentationObject obj = getPage().getContainedObject(node);
		//iwc.getResponse().getWriter().println("klasi:"+obj.getClass().getName());
		//  if(obj!=null){
		//     obj._main(iwc);
		//  }
		//}
		getPage()._main(iwc);
		//System.out.println("Inside _main() for: "+this.getClass().getName()+" - Tread: "+Thread.currentThread().toString());
	}
	public void __print(IWContext iwc) throws Exception {
		String language = iwc.getLanguage();
		if (language.equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			iwc.setContentType("text/html");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			iwc.setContentType("text/vnd.wap.wml");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)){
			iwc.setContentType("application/pdf");
		}
		//getPage()._print(iwc);
		getPage()._initPrinting(iwc);
		//getPage().renderComponent(iwc);
		//System.out.println("Inside __print() for: "+this.getClass().getName()+" - Tread: "+Thread.currentThread().toString());
	}
	private boolean isActionPerformed(HttpServletRequest request, HttpServletResponse response) {
		if (request.getParameter("idega_special_form_event") != null) {
			//if (true){
			return true;
			//}
			//else{
			//	return false;
			//}
		}
		else {
			return false;
		}
	}
	//public void actionPerformed(ModuleEvent e)throws Exception{
	//try{
	//	__theService(e.getRequest(),e.getResponse());
	/*}
	catch(IOException, ex){
		  throw new Exception(ex.getMessage());
	}*/
	//}
	public void debug(String debugString) {
		try {
			//eiki commented out for testing
			//getIWContext().getWriter().println(debugString);
		}
		catch (Exception ex) {
			System.err.println("Error in IWPresentationServlet.debug() : " + ex.getMessage());
			ex.printStackTrace(System.err);
		}
	}
	public void setPageAttribute(String attributeName, Object object) {
		setSessionAttribute(getServletConfig().getServletName() + attributeName, object);
	}
	public Object getPageAttribute(String attributeName) {
		return getSessionAttribute(getServletConfig().getServletName() + attributeName);
	}
	public void removePageAttribute(String attributeName) {
		removeSessionAttribute(getServletConfig().getServletName() + attributeName);
	}
	protected void handleException(Exception ex, Object thrower) {
		Text text = new Text(thrower.getClass().getName());
		add(new ExceptionWrapper(ex, text));
	}
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	public IWBundle getBundle(IWContext iwc) {
		IWMainApplication iwma = iwc.getIWMainApplication();
		return iwma.getBundle(getBundleIdentifier());
	}
	public IWResourceBundle getResourceBundle(IWContext iwc) {
		IWBundle bundle = getBundle(iwc);
		if (bundle != null) {
			return bundle.getResourceBundle(iwc.getCurrentLocale());
		}
		return null;
	}
	public String getLocalizedString(String key, IWContext iwc) {
		IWResourceBundle bundle = getResourceBundle(iwc);
		if (bundle != null) {
			return bundle.getLocalizedString(key);
		}
		return null;
	}
	public void increaseHistoryID(IWContext iwc) {
		IWEventProcessor.getInstance().increaseHistoryID(iwc);
	}
	
	private void handleLocaleParameter(IWContext iwc) {
		IWEventProcessor.getInstance().handleLocaleParameter(iwc);
	}
	
	public void handleEvent(IWContext iwc) {
		IWEventProcessor.getInstance().handleEvent(iwc);
	}
	private void handleMultipartFormData(IWContext iwc) throws Exception {
		String sep = FileUtil.getFileSeparator();
		StringBuffer pathToFile = new StringBuffer();
		pathToFile.append(iwc.getIWMainApplication().getApplicationRealPath());
		pathToFile.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
		pathToFile.append(sep);
		pathToFile.append("upload");
		pathToFile.append(sep);
		FileUtil.createFolder(pathToFile.toString());
		MultipartParser mp = new MultipartParser(iwc.getRequest(), iwc.getRequest().getContentLength());
		/**@todo the maximum size should be flexible could just match the filesiz we have? or don't we**/
		Part part;
		while ((part = mp.readNextPart()) != null) {
			if (part.isParam()) {
				ParamPart paramPart = (ParamPart) part;
				iwc.setMultipartParameter(paramPart.getName(), paramPart.getStringValue());
				//System.out.println(" PARAMETERS "+paramPart.getName()+" : "+paramPart.getStringValue());
			}
			else if (part.isFile()) {
				// it's a file part
				FilePart filePart = (FilePart) part;
				String fileName = filePart.getFileName();
				if (fileName != null) {
					pathToFile.append(fileName);
					String filePath = pathToFile.toString();
					StringBuffer webPath = new StringBuffer();
					webPath.append('/');
					webPath.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
					webPath.append('/');
					webPath.append("upload");
					webPath.append('/');
					webPath.append(fileName);
					// Opera mimetype fix ( aron@idega.is )
					String mimetype = filePart.getContentType();
					if (mimetype != null) {
						StringTokenizer tokenizer = new StringTokenizer(mimetype, " ;:");
						if (tokenizer.hasMoreTokens())
							mimetype = tokenizer.nextToken();
					}
					UploadFile file = new UploadFile(fileName, filePath, iwc.getIWMainApplication().getTranslatedURIWithContext(webPath.toString()), mimetype, (long) - 1);
					long size = filePart.writeTo(file);
					file.setSize(size);
					iwc.setUploadedFile(file);
				}
			}
		}
	}
	public void processPresentationEvent(IWContext iwc) throws RemoteException {
		if (IWPresentationEvent.anyEvents(iwc)) {
			IWEventMachine eventMachine = (IWEventMachine) IBOLookup.getSessionInstance(iwc, IWEventMachine.class);
			eventMachine.processEvent(this.getPage(), iwc);
		}
	}
}
