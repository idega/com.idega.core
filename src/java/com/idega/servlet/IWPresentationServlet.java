/*
 * $Id: IWPresentationServlet.java,v 1.27 2002/02/11 17:15:21 gummi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.*;
import com.idega.business.IWEventListener;
import com.idega.event.*;
import java.awt.EventQueue;
import java.awt.ActiveEvent;
import java.awt.Toolkit;
import java.awt.AWTEvent;
import com.idega.event.IWModuleEvent;
import com.idega.builder.business.BuilderLogic;
import java.util.Vector;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Map;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Enumeration;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/

public  class IWPresentationServlet extends IWCoreServlet{

  private static final String IW_BUNDLE_IDENTIFIER = "com.idega.core";

  private static final String IW_MODULEINFO_KEY="idegaweb_iwc";
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

	private void __initialize(HttpServletRequest request, HttpServletResponse response) throws Exception {
          //TODO
          //Find a better solution for this:
          IWContext iwc = null;
          //IWContext iwc = (IWContext)request.getSession().getAttribute("idega_special_iwc");

          if (iwc == null) {
            iwc = new IWContext(request,response);
            iwc.setServletContext(getServletContext());
            request.getSession().setAttribute("idega_special_iwc",iwc);
          }
          else {
            iwc.setRequest(request);
            iwc.setResponse(response);
          }

          String markup = iwc.getParameter("idega_special_markup");
          if(markup != null) {
            iwc.setLanguage(markup);
          }


          storeObject(IW_MODULEINFO_KEY,iwc);
          processBusinessEvent(iwc);
          initializePage();
	}

	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		__main(servReq,servRes);
	}

	public void doPost(HttpServletRequest servReq,HttpServletResponse servRes)throws ServletException, IOException {
		__main(servReq,servRes);
	}

        public void processBusinessEvent(IWContext iwc)throws ClassNotFoundException,IllegalAccessException,IWException,InstantiationException{
          String eventClassEncr = iwc.getParameter(IWMainApplication.IdegaEventListenerClassParameter);
          String eventClass = IWMainApplication.decryptClassName(eventClassEncr);
          if (eventClass != null) {
            IWEventListener listener = (IWEventListener)Class.forName(eventClass).newInstance();
            listener.actionPerformed(iwc);
          }
        }

	public void __main(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException{
          try {

long time1 = System.currentTimeMillis();
//com.idega.core.accesscontrol.business.AccessControl._COUNTER = 0;
            __initialize(request,response);
            IWContext iwc = getIWContext();


            //added by gummi@idega.is
            //begin
            boolean theServiceDone = false;
            String sessionAddress = iwc.getParameter(IWMainApplication.IWEventSessionAddressParameter);

            if (sessionAddress != null && !"".equals(sessionAddress)){
              Object obj = iwc.getSessionAttribute(sessionAddress);
              if(obj != null) {
                if(obj instanceof ActiveEvent && obj instanceof AWTEvent ) {

                    if(Page.isRequestingTopPage(iwc)){
                      __theService(request,response);
                    }
                  theServiceDone = true;
                  if(obj instanceof IWModuleEvent){
                    ((IWModuleEvent)obj).setIWContext(iwc);
                  }else{
                    this.getPage()._setIWContext(iwc);
                  }
                  ((ActiveEvent)obj).dispatch();
                /* Kommentað út þar til kerfið ræður við þræði
                  EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
                  q.postEvent((AWTEvent)obj);
                */
                }
              }
            }

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
                  if(!theServiceDone) //gummi@idega.is
                  {
                    if(Page.isRequestingTopPage(iwc)){
                      __theService(request,response);
                    }
                  }
            //}
      //      response.getWriter().println("\n");
            _main(iwc);

            __print(iwc);

long time2 = System.currentTimeMillis();
PrintWriter writer = iwc.getWriter();
writer.println("<!--"+ (time2 - time1 )+ " ms-->");

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
          } catch(Exception ex){
                  /*if (ex instanceof java.io.IOException){
                          throw (java.io.IOException) ex.fillInStackTrace();
                  }
                  else if (ex instanceof javax.servlet.ServletException){
                          throw (javax.servlet.ServletException) ex.fillInStackTrace();
                  }
                  else{*/
                          response.getWriter().println("<H2>IWError</H2>");
                          response.getWriter().println("<pre>");
                          ex.printStackTrace(response.getWriter());
                          response.getWriter().println("</pre>");
                  //}
          }
	}

  public void __theService(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException{

  }

  public void initializePage(){
    //String servletName = this.getServletConfig().getServletName();
    //System.out.println("Inside initializePage for "+servletName);
    setPage(Page.loadPage(getIWContext()));
  }

  public void setPage(Page myPage){
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

  public Page getPage(){
    return Page.getPage(getIWContext());
  }

  public static IWContext getIWContext(){
        return (IWContext) retrieveObject(IW_MODULEINFO_KEY);
  }


  /**
   * @deprecated
   */
  public HttpServletRequest getRequest(){
          return getIWContext().getRequest();
  }

  /**
   * @deprecated
   */
  public HttpSession getSession(){
          return getIWContext().getSession();
  }

  /**
   * @deprecated
   */
  public HttpServletResponse getResponse(){
          return getIWContext().getResponse();
  }

  public String getParameter(String parameterName){
    return getRequest().getParameter(parameterName);
  }

  public String[] getParameterValues(String parameterName){
    return getRequest().getParameterValues(parameterName);
  }

  public Object getSessionAttribute(String attributeName){
    return getSession().getAttribute(attributeName);
  }

  public void setSessionAttribute(String attributeName, Object attribute){
    getSession().setAttribute(attributeName,attribute);
  }

  public void removeSessionAttribute(String attributeName){
    getSession().removeAttribute(attributeName);
  }


  public void add(PresentationObject objectToAdd){
    getPage().add(objectToAdd);
  }

  public void add(String text){
    add(new Text(text));
  }


  public void addToTemplate(PresentationObject obj){

  }

  public void _main(IWContext iwc)throws Exception{
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

  public void __print(IWContext iwc)throws Exception{


          if (iwc.getLanguage().equals("WML")){
                  getResponse().setContentType("text/vnd.wap.wml");
          }

          getPage()._print(iwc);
          //System.out.println("Inside __print() for: "+this.getClass().getName()+" - Tread: "+Thread.currentThread().toString());
  }

  private boolean isActionPerformed(HttpServletRequest request, HttpServletResponse response){
          if(request.getParameter("idega_special_form_event") != null){
                  //if (true){
                          return true;
                  //}
                  //else{
                  //	return false;
                  //}
          }
          else{
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


  public void debug(String debugString){
    try{
      getIWContext().getWriter().println(debugString);
    }
    catch(Exception ex){
      System.err.println("Error in IWPresentationServlet.debug() : "+ex.getMessage());
      ex.printStackTrace(System.err);
    }
  }


  public void setPageAttribute(String attributeName,Object object){
      setSessionAttribute(getServletConfig().getServletName()+attributeName,object);
  }

  public Object getPageAttribute(String attributeName){
    return getSessionAttribute(getServletConfig().getServletName()+attributeName);
  }

  public void removePageAttribute(String attributeName){
    removeSessionAttribute(getServletConfig().getServletName()+attributeName);
  }


  protected void handleException(Exception ex,Object thrower){
    Text text = new Text(thrower.getClass().getName());
    add(new ExceptionWrapper(ex,text));
  }



  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }


  public IWBundle getBundle(IWContext iwc){
    IWMainApplication iwma = iwc.getApplication();
    return iwma.getBundle(getBundleIdentifier());
  }

  public IWResourceBundle getResourceBundle(IWContext iwc){
    IWBundle bundle = getBundle(iwc);
    if(bundle!=null){
      return bundle.getResourceBundle(iwc.getCurrentLocale());
    }
    return null;
  }

  public String getLocalizedString(String key,IWContext iwc){
    IWResourceBundle bundle = getResourceBundle(iwc);
    if(bundle!=null){
      return bundle.getLocalizedString(key);
    }
    return null;
  }

  public void increaseHistoryID(IWContext  iwc){
    String historyIDSession = (String)iwc.getSessionAttribute(BuilderLogic.PRM_HISTORY_ID);
    if(historyIDSession == null){
      historyIDSession = Integer.toString((int)(Math.random()*1000));
      iwc.setSessionAttribute(BuilderLogic.PRM_HISTORY_ID,historyIDSession);
    } else{
      try {
        historyIDSession = Integer.toString(Integer.parseInt(historyIDSession)+1);
        iwc.setSessionAttribute(BuilderLogic.PRM_HISTORY_ID,historyIDSession);
      }
      catch (NumberFormatException ex) {
        //System.err.print("NumberformatException when trying to increase historyID, historyIDSession:"+historyIDSession);
        historyIDSession = Integer.toString((int)(Math.random()*1000));
        iwc.setSessionAttribute(BuilderLogic.PRM_HISTORY_ID,historyIDSession);
      }
    }
  }

  public void handleEvent(IWContext  iwc){
  try {
//    System.err.println("-------------------------------------");
//    System.err.println("handleEvent begin");
    String historyID = iwc.getParameter(BuilderLogic.PRM_HISTORY_ID);
    if( historyID != null ){
      BuilderLogic logic = BuilderLogic.getInstance();
      PresentationObject[] listeners = logic.getIWPOListeners(iwc);
      LinkedList state = (LinkedList)iwc.getSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE);
      int historySize = 5;
      boolean listJustConstructed = false;
//      System.err.println("PresentationServelt - State = "+ state);
      if(state == null){

        state = new LinkedList();
        state.addLast(historyID);
        state.addLast(new Hashtable());
        iwc.setSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE, state);
        listJustConstructed = true;
      }
      synchronized (state){

//        System.err.println("PresentationServelt - !listJustConstructed = "+ !listJustConstructed);
//        System.err.println("PresentationServelt - state.contains(historyID) = "+ state.contains(historyID));

        ListIterator iter2 = state.listIterator();
        while (iter2.hasNext()) {
          Object item = iter2.next();
          int index = iter2.nextIndex()-1;
          //System.err.println("PresentationServelt - State index : "+index+" = "+item);
        }

        if(!listJustConstructed && state.contains(historyID)){
          // go back in history
          int index = state.indexOf(historyID);
          int size = state.size();
          for (int i = 0; i <= size-(index+1); i++) {
            state.removeLast();
          }
        }

        if(!listJustConstructed){
          if(state.size() >= historySize*2){
            state.removeFirst();
            state.removeFirst();
          }

          int copyFrom = state.size()-1;
          state.addLast(iwc.getParameter(BuilderLogic.PRM_HISTORY_ID));
          if(copyFrom >= 1){
            try {
              state.addLast(((Hashtable)state.get(copyFrom)).clone());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          } else{
            state.addLast(new Hashtable());
          }
          //System.err.println("PresentationServelt - checking stateList");
          // object geta safnast upp í hashtöflunum því þarf að fjarlægja þau instöns sem ekki eru á nýju síðunni
          /**
           * @todo handle pages in frames or iframes with different pageIds
           */
          Map newStateMap = (Map)state.getLast();
          //Map pageObjectInstances = logic.getCashedObjectInstancesForPage(iwc.getParameter(logic.IB_PAGE_PARAMETER));
          Map pageObjectInstances = logic.getCashedObjectInstancesForPage(this.getPage().getPageID());

          //System.err.println("PresentationServelt - pageObjects "+pageObjectInstances + " for page "+this.getPage().getPageID());


          Iterator iter = newStateMap.keySet().iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            if(!((pageObjectInstances != null) && pageObjectInstances.containsKey(item))){
              //System.err.println("PresentationServelt - removing : "+ item);
              iter.remove();
              //newStateMap.remove(item);
            }else{
              //System.err.println("PresentationServelt - not removing : "+ item);
            }
          }
        }

      }
      if(listeners != null && listeners.length > 0){
        PresentationObject source = logic.getIWPOEventSource(iwc);
        for (int i = 0; i < listeners.length; i++) {
          //System.err.println("listener = " + listeners[i].getParentObjectInstanceID());
          //System.err.println("newStateString = "+listeners[i].changeState(source,iwc));
          String newState = listeners[i].changeState(source,iwc);
          if(newState != null){
            ((Hashtable)state.getLast()).put(Integer.toString(listeners[i].getParentObjectInstanceID()),newState);
          } else {
            ((Hashtable)state.getLast()).remove(Integer.toString(listeners[i].getParentObjectInstanceID()));
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

}
