/*
 * $Id: IWPresentationServlet.java,v 1.40 2002/11/20 20:49:10 eiki Exp $
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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.idega.builder.business.BuilderLogic;
import com.idega.business.IBOLookup;
import com.idega.business.IWEventListener;
import com.idega.event.IWEventMachine;
import com.idega.event.IWModuleEvent;
import com.idega.event.IWPresentationEvent;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.UploadFile;
import com.idega.presentation.ErrorPage;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.IWPageInitializationException;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.util.FileUtil;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWPresentationServlet extends IWCoreServlet {
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.core";
	private static final String IW_MODULEINFO_KEY = "idegaweb_iwc";
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
	private void __initializeIWC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//TODO
		//Find a better solution for this:
		IWContext iwc = null;
		if (!hasCheckedCurrentAppContext()) {
			this.getApplication().setApplicationContextURI(request.getContextPath());
		}
		//IWContext iwc = (IWContext)request.getSession().getAttribute("idega_special_iwc");
		if (iwc == null) {
			iwc = new IWContext(request, response);
			iwc.setServletContext(getServletContext());
			request.getSession().setAttribute("idega_special_iwc", iwc);
		}
		else {
			iwc.setRequest(request);
			iwc.setResponse(response);
		}
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
		storeObject(IW_MODULEINFO_KEY, iwc);
	}
	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		__main(servReq, servRes);
	}
	public void doPost(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		__main(servReq, servRes);
	}
	public void processBusinessEvent(IWContext iwc)
		throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		String[] eventListeners = iwc.getParameterValues(IWMainApplication.IdegaEventListenerClassParameter);
		if (eventListeners != null) {
			for (int i = 0; i < eventListeners.length; i++) {
				processIWEventEncrypted(iwc, eventListeners[i]);
			}
		}
		/*
		String eventClassEncr = iwc.getParameter(IWMainApplication.IdegaEventListenerClassParameter);
		processIWEvent(iwc,eventClassEncr);
		*/
	}
	private void processIWEvent(IWContext iwc, String EventListenerClass)
		throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		if (EventListenerClass != null) {
			System.out.println("IWEventListener: " + EventListenerClass);
			IWEventListener listener = (IWEventListener) Class.forName(EventListenerClass).newInstance();
			listener.actionPerformed(iwc);
		}
	}
	private void processIWEventEncrypted(IWContext iwc, String EncryptedEventListenerClassName)
		throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		String eventClass = IWMainApplication.decryptClassName(EncryptedEventListenerClassName);
		processIWEvent(iwc, eventClass);
	}
	private void processApplicationEvents(IWContext iwc)
		throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		java.util.List eventListeners = iwc.getApplication().getApplicationEventListeners();
		Iterator iter = eventListeners.iterator();
		while (iter.hasNext()) {
			String className = (String) iter.next();
			processIWEvent(iwc, className);
		}
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
		PrintWriter writer = null;
		try {
			long time1 = System.currentTimeMillis();
			//com.idega.core.accesscontrol.business.AccessControl._COUNTER = 0;
			__initializeIWC(request, response);
			IWContext iwc = getIWContext();
			try {
				writer = iwc.getWriter(); //get the writer
				processBusinessEvent(iwc);
				processApplicationEvents(iwc);
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
			}
			catch (IWPageInitializationException iwe) {
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
			}
			__print(iwc);
			long time2 = System.currentTimeMillis();
			writer.println("<!--" + (time2 - time1) + " ms-->");
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
			/*if (ex instanceof java.io.IOException){
				  throw (java.io.IOException) ex.fillInStackTrace();
			}
			else if (ex instanceof javax.servlet.ServletException){
				  throw (javax.servlet.ServletException) ex.fillInStackTrace();
			}
			else{*/
			writer.println("<H2>IWError</H2>");
			writer.println("<pre>");
			writer.println(ex.getMessage());
			ex.printStackTrace(System.err);
			writer.println("</pre>");
			//}
		}
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
		return (IWContext) retrieveObject(IW_MODULEINFO_KEY);
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
		if (iwc.getLanguage().equals("HTML")) {
			iwc.setContentType("text/html");
		}
		else if (iwc.getLanguage().equals("WML")) {
			iwc.setContentType("text/vnd.wap.wml");
		}
		//getPage()._print(iwc);
		getPage()._initPrinting(iwc);
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
		IWMainApplication iwma = iwc.getApplication();
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
		String historyIDSession = (String) iwc.getSessionAttribute(BuilderLogic.PRM_HISTORY_ID);
		if (historyIDSession == null) {
			historyIDSession = Integer.toString((int) (Math.random() * 1000));
			iwc.setSessionAttribute(BuilderLogic.PRM_HISTORY_ID, historyIDSession);
		}
		else {
			try {
				historyIDSession = Integer.toString(Integer.parseInt(historyIDSession) + 1);
				iwc.setSessionAttribute(BuilderLogic.PRM_HISTORY_ID, historyIDSession);
			}
			catch (NumberFormatException ex) {
				//System.err.print("NumberformatException when trying to increase historyID, historyIDSession:"+historyIDSession);
				historyIDSession = Integer.toString((int) (Math.random() * 1000));
				iwc.setSessionAttribute(BuilderLogic.PRM_HISTORY_ID, historyIDSession);
			}
		}
	}
	public void handleEvent(IWContext iwc) {
		try {
			//    System.err.println("-------------------------------------");
			//    System.err.println("handleEvent begin");
			String historyID = iwc.getParameter(BuilderLogic.PRM_HISTORY_ID);
			if (historyID != null) {
				BuilderLogic logic = BuilderLogic.getInstance();
				PresentationObject[] listeners = logic.getIWPOListeners(iwc);
				LinkedList state = (LinkedList) iwc.getSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE);
				int historySize = 5;
				boolean listJustConstructed = false;
				//      System.err.println("PresentationServelt - State = "+ state);
				if (state == null) {
					state = new LinkedList();
					state.addLast(historyID);
					state.addLast(new Hashtable());
					iwc.setSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE, state);
					listJustConstructed = true;
				}
				synchronized (state) {
					//        System.err.println("PresentationServelt - !listJustConstructed = "+ !listJustConstructed);
					//        System.err.println("PresentationServelt - state.contains(historyID) = "+ state.contains(historyID));
					ListIterator iter2 = state.listIterator();
					while (iter2.hasNext()) {
						Object item = iter2.next();
						int index = iter2.nextIndex() - 1;
						//System.err.println("PresentationServelt - State index : "+index+" = "+item);
					}
					if (!listJustConstructed && state.contains(historyID)) {
						// go back in history
						int index = state.indexOf(historyID);
						int size = state.size();
						for (int i = 0; i <= size - (index + 1); i++) {
							state.removeLast();
						}
					}
					if (!listJustConstructed) {
						if (state.size() >= historySize * 2) {
							state.removeFirst();
							state.removeFirst();
						}
						int copyFrom = state.size() - 1;
						state.addLast(iwc.getParameter(BuilderLogic.PRM_HISTORY_ID));
						if (copyFrom >= 1) {
							try {
								state.addLast(((Hashtable) state.get(copyFrom)).clone());
							}
							catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						else {
							state.addLast(new Hashtable());
						}
						//System.err.println("PresentationServelt - checking stateList");
						// object geta safnast upp í hashtöflunum því þarf að fjarlægja þau instöns sem ekki eru á nýju síðunni
						/**
						 * @todo handle pages in frames or iframes with different pageIds
						 */
						Map newStateMap = (Map) state.getLast();
						//Map pageObjectInstances = logic.getCashedObjectInstancesForPage(iwc.getParameter(logic.IB_PAGE_PARAMETER));
						Map pageObjectInstances = logic.getCashedObjectInstancesForPage(this.getPage().getPageID());
						//System.err.println("PresentationServelt - pageObjects "+pageObjectInstances + " for page "+this.getPage().getPageID());
						Iterator iter = newStateMap.keySet().iterator();
						while (iter.hasNext()) {
							Object item = iter.next();
							if (!((pageObjectInstances != null) && pageObjectInstances.containsKey(item))) {
								//System.err.println("PresentationServelt - removing : "+ item);
								iter.remove();
								//newStateMap.remove(item);
							}
							else {
								//System.err.println("PresentationServelt - not removing : "+ item);
							}
						}
					}
				}
				if (listeners != null && listeners.length > 0) {
					PresentationObject source = logic.getIWPOEventSource(iwc);
					for (int i = 0; i < listeners.length; i++) {
						//System.err.println("listener = " + listeners[i].getParentObjectInstanceID());
						//System.err.println("newStateString = "+listeners[i].changeState(source,iwc));
						String newState = listeners[i].changeState(source, iwc);
						if (newState != null) {
							((Hashtable) state.getLast()).put(
								Integer.toString(listeners[i].getParentObjectInstanceID()),
								newState);
						}
						else {
							((Hashtable) state.getLast()).remove(
								Integer.toString(listeners[i].getParentObjectInstanceID()));
						}
						//listeners[i].changeState(source,iwc);
					}
				}
			}
			//System.err.println("handleEvent end");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private void handleMultipartFormData(IWContext iwc) throws Exception {
		String sep = FileUtil.getFileSeparator();
		StringBuffer pathToFile = new StringBuffer();
		pathToFile.append(iwc.getApplication().getApplicationRealPath());
		pathToFile.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
		pathToFile.append(sep);
		pathToFile.append("upload");
		pathToFile.append(sep);
		FileUtil.createFolder(pathToFile.toString());
		MultipartParser mp = new MultipartParser(iwc.getRequest(), iwc.getRequest().getContentLength());
		/**@todo the maximum size should be flexible could just match the filesiz we have? or don't we**/
		Part part;
		File dir = null;
		String value = null;
		while ((part = mp.readNextPart()) != null) {
			String name = part.getName();
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
					UploadFile file = new UploadFile(fileName, filePath, webPath.toString(), mimetype, (long) - 1);
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
