/*
 * Created on 26.2.2004 by  tryggvil in project com.project
 */
package com.idega.event;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.FileUploadUtil;
import com.idega.util.FileUtil;
import com.idega.util.LocaleUtil;
import com.oreilly.servlet.MultipartWrapper;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

/**
 * IWEventProcessor //TODO: tryggvil Describe class
 * Copyright (C) idega software 2004
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */
public class IWEventProcessor implements Singleton {

	private final static String PRM_HISTORY_ID = ICBuilderConstants.PRM_HISTORY_ID;
	private final static String SESSION_OBJECT_STATE = ICBuilderConstants.SESSION_OBJECT_STATE;
	private static Logger log = Logger.getLogger(IWEventProcessor.class.toString());

	private static Instantiator instantiator = new Instantiator() { @Override
	public Object getInstance() { return new IWEventProcessor();}};

	protected IWEventProcessor() {
		// default constructor
	}

	/**
	 *
	 * @uml.property name="instance"
	 */
	public static IWEventProcessor getInstance() {
		return (IWEventProcessor) SingletonRepository.getRepository().getInstance(IWEventProcessor.class, instantiator);
	}

	public void processAllEvents(IWContext iwc) {
		try {
			processBusinessEvent(iwc);
			processApplicationEvents(iwc);
			processAWTEvent(iwc, iwc.getRequest(), iwc.getResponse());
			increaseHistoryID(iwc);
			handleEvent(iwc);
			handleLocaleParameter(iwc);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IWException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ServletException e1) {
			e1.printStackTrace();
		}
	}

	public void processBusinessEvent(IWContext iwc) throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		String[] eventListeners = iwc.getParameterValues(IWMainApplication.IdegaEventListenerClassParameter);
		if (eventListeners != null) {
			for (int i = 0; i < eventListeners.length; i++) {
				processIWEventEncrypted(iwc, eventListeners[i]);
			}
		}
	}

	private void processIWEvent(IWContext iwc, String eventListenerClass) throws IllegalAccessException, IWException, InstantiationException {
		if (eventListenerClass != null) {
			if (iwc.getApplicationSettings().getIfDebug()) {
				log.info("IWEventListener: " + eventListenerClass);
			}

			try {
				Class<?> eventClass = RefactorClassRegistry.forName(eventListenerClass);
				IWPageEventListener listener = (IWPageEventListener) eventClass.newInstance();
				listener.actionPerformed(iwc);
			} catch (ClassNotFoundException cnfe){
				log.warning(cnfe.getMessage());
			}
		}
	}

	private void processIWEventEncrypted(IWContext iwc, String EncryptedEventListenerClassName) throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		String eventClass = IWMainApplication.decryptClassName(EncryptedEventListenerClassName);
		processIWEvent(iwc, eventClass);
	}

	public void processApplicationEvents(IWContext iwc) throws ClassNotFoundException, IllegalAccessException, IWException, InstantiationException {
		java.util.List<?> eventListeners = iwc.getIWMainApplication().getApplicationEventListeners();
		Iterator<?> iter = eventListeners.iterator();
		while (iter.hasNext()) {
			String className = (String) iter.next();
			processIWEvent(iwc, className);
		}
	}
	public boolean processAWTEvent(IWContext iwc, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String sessionAddress = iwc.getParameter(IWMainApplication.IWEventSessionAddressParameter);
		if (sessionAddress != null && !"".equals(sessionAddress)) {
			Object obj = iwc.getSessionAttribute(sessionAddress);
			if (obj != null) {
				if (obj instanceof ActiveEvent && obj instanceof AWTEvent) {
					/*
					 * if (Page.isRequestingTopPage(iwc)) {
					 * __theService(request, response);
					 */
					//theServiceDone = true;
					if (obj instanceof IWModuleEvent) {
						((IWModuleEvent) obj).setIWContext(iwc);
					}
					/*else {
						this.getPage()._setIWContext(iwc);
					}*/
					((ActiveEvent) obj).dispatch();
					return true;
					/*
					 * Kommentað út þar til kerfið ræður við þræði EventQueue q =
					 * Toolkit.getDefaultToolkit().getSystemEventQueue();
					 * q.postEvent((AWTEvent)obj);
					 */
				}
			}
		}
		return false;
	}

	public void handleEvent(IWContext iwc) {
		try {
			//    System.err.println("-------------------------------------");
			//    System.err.println("handleEvent begin");
			String historyID = iwc.getParameter(PRM_HISTORY_ID);
			if (historyID != null) {
				PresentationObject[] listeners = EventLogic.getIWPOListeners(iwc);
				LinkedList state = (LinkedList) iwc.getSessionAttribute(SESSION_OBJECT_STATE);
				int historySize = 5;
				boolean listJustConstructed = false;
				//      System.err.println("PresentationServelt - State = "+ state);
				if (state == null) {
					state = new LinkedList();
					state.addLast(historyID);
					state.addLast(new Hashtable());
					iwc.setSessionAttribute(SESSION_OBJECT_STATE, state);
					listJustConstructed = true;
				}
				synchronized (state) {
					//        System.err.println("PresentationServelt -
					// !listJustConstructed = "+ !listJustConstructed);
					//        System.err.println("PresentationServelt -
					// state.contains(historyID) = "+
					// state.contains(historyID));
					ListIterator iter2 = state.listIterator();
					while (iter2.hasNext()) {
						iter2.next();
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
						state.addLast(iwc.getParameter(PRM_HISTORY_ID));
						if (copyFrom >= 1) {
							try {
								state.addLast(((Hashtable) state.get(copyFrom)).clone());
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						} else {
							state.addLast(new Hashtable());
						}
						//System.err.println("PresentationServelt - checking
						// stateList");
						// object geta safnast upp í hashtöflunum því þarf að
						// fjarlægja þau instöns sem ekki eru á nýju síðunni
						/**
						 * @todo handle pages in frames or iframes with
						 * different pageIds
						 */
						Map newStateMap = (Map) state.getLast();
						//Map pageObjectInstances =
						// logic.getCashedObjectInstancesForPage(iwc.getParameter(logic.IB_PAGE_PARAMETER));
						Map pageObjectInstances = EventLogic.getCashedObjectInstancesForPage(this.getPage().getPageID());
						//System.err.println("PresentationServelt -
						// pageObjects "+pageObjectInstances + " for page
						// "+this.getPage().getPageID());
						Iterator iter = newStateMap.keySet().iterator();
						while (iter.hasNext()) {
							Object item = iter.next();
							if (!((pageObjectInstances != null) && pageObjectInstances.containsKey(item))) {
								//System.err.println("PresentationServelt -
								// removing : "+ item);
								iter.remove();
								//newStateMap.remove(item);
							} else {
								//System.err.println("PresentationServelt -
								// not removing : "+ item);
							}
						}
					}
				}
				if (listeners != null && listeners.length > 0) {
					PresentationObject source = EventLogic.getIWPOEventSource(iwc);
					for (int i = 0; i < listeners.length; i++) {
						//System.err.println("listener = " +
						// listeners[i].getParentObjectInstanceID());
						//System.err.println("newStateString =
						// "+listeners[i].changeState(source,iwc));
						String newState = listeners[i].changeState(source, iwc);
						if (newState != null) {
							((Hashtable) state.getLast()).put(Integer.toString(listeners[i].getParentObjectInstanceID()), newState);
						} else {
							((Hashtable) state.getLast()).remove(Integer.toString(listeners[i].getParentObjectInstanceID()));
						}
						//listeners[i].changeState(source,iwc);
					}
				}
			}
			//System.err.println("handleEvent end");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void handleLocaleParameter(IWContext iwc) {
		Locale locale = iwc.getCurrentLocale();
		String localeValue = iwc.getParameter(LocaleSwitcher.languageParameterString);
		if (localeValue != null) {
			Locale newLocale = LocaleUtil.getLocale(localeValue);
			if (newLocale != null && !newLocale.equals(locale)) {
				iwc.setCurrentLocale(newLocale);
			}
		}
		//IWEventProcessor.getInstance().handleLocaleParameter(iwc);
	}

	public void increaseHistoryID(IWContext iwc) {
		String historyIDSession = (String) iwc.getSessionAttribute(PRM_HISTORY_ID);
		if (historyIDSession == null) {
			historyIDSession = Integer.toString((int) (Math.random() * 1000));
			iwc.setSessionAttribute(PRM_HISTORY_ID, historyIDSession);
		} else {
			try {
				historyIDSession = Integer.toString(Integer.parseInt(historyIDSession) + 1);
				iwc.setSessionAttribute(PRM_HISTORY_ID, historyIDSession);
			} catch (NumberFormatException ex) {
				//System.err.print("NumberformatException when trying to
				// increase historyID, historyIDSession:"+historyIDSession);
				historyIDSession = Integer.toString((int) (Math.random() * 1000));
				iwc.setSessionAttribute(PRM_HISTORY_ID, historyIDSession);
			}
		}
	}

	public Page getPage(IWContext iwc) {
		return Page.getPage(iwc);
	}

	public Page getPage() {
		return getPage(IWContext.getInstance());
	}

	public void handleMultipartFormData(IWContext iwc) throws Exception {
		String sep = FileUtil.getFileSeparator();
		StringBuffer pathToFile = new StringBuffer();
		pathToFile.append(iwc.getIWMainApplication().getApplicationRealPath());
		pathToFile.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
		pathToFile.append(sep);
		pathToFile.append("upload");
		pathToFile.append(sep);
		FileUtil.createFolder(pathToFile.toString());
		int maxSize = iwc.getRequest().getContentLength();
		Logger.getLogger(getClass().getName()).info("content length of request is " + maxSize + ", max size of multipart data is " + (maxSize*=1.3));

		if(iwc.getRequest() instanceof MultipartWrapper){
			//oreilly This ONLY supports one file
		    // Cast the request to a MultipartWrapper
	        MultipartWrapper multi = (MultipartWrapper) iwc.getRequest();

	        // Show which files we received
	        Enumeration<?> files = multi.getFileNames();
	        while (files.hasMoreElements()) {
	          String name = (String)files.nextElement();
	          String fileName = multi.getFilesystemName(name);
	          String mimetype = multi.getContentType(name);
	          File f = multi.getFile(name);

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
					if (mimetype != null) {
						StringTokenizer tokenizer = new StringTokenizer(mimetype, " ;:");
						if (tokenizer.hasMoreTokens()) {
							mimetype = tokenizer.nextToken();
						}
					}
					UploadFile file = new UploadFile(fileName, filePath, iwc.getIWMainApplication().getTranslatedURIWithContext(webPath.toString()), mimetype, - 1);
				    FileUtil.copyFile(f,file);
					long size = f.length();
					file.setSize(size);
					iwc.setUploadedFile(file);
				}
	        }
		}
		else if(IWMainApplication.useJSF){
			//This is a hack so we don't have to add the myfaces dependency yet
			FileUploadUtil.handleMyFacesMultiPartRequest(iwc);
		}
		else{
			MultipartParser mp = new MultipartParser(iwc.getRequest(), maxSize);
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
							if (tokenizer.hasMoreTokens()) {
								mimetype = tokenizer.nextToken();
							}
						}
						UploadFile file = new UploadFile(fileName, filePath, iwc.getIWMainApplication().getTranslatedURIWithContext(webPath.toString()), mimetype, - 1);
						long size = filePart.writeTo(file);
						file.setSize(size);
						iwc.setUploadedFile(file);
					}
				}
			}
		}
	}

}