/*
 * $Id: IWPresentationServlet.java,v 1.7 2001/05/14 14:27:27 palli Exp $
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
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.idegaweb.*;
import com.idega.business.IWEventListener;
import com.idega.event.*;
import java.awt.EventQueue;
import java.awt.ActiveEvent;
import java.awt.Toolkit;
import java.awt.AWTEvent;
import com.idega.event.IWEvent;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWPresentationServlet extends IWCoreServlet {
	private void __initialize(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //TODO
    //Find a better solution for this:
    ModuleInfo moduleinfo = null;
    //ModuleInfo moduleinfo = (ModuleInfo)request.getSession().getAttribute("idega_special_moduleinfo");

    if (moduleinfo == null) {
      moduleinfo = new ModuleInfo(request,response);
      moduleinfo.setServletContext(getServletContext());
      request.getSession().setAttribute("idega_special_moduleinfo",moduleinfo);
    }
    else {
      moduleinfo.setRequest(request);
      moduleinfo.setResponse(response);
    }

    String markup = moduleinfo.getParameter("idega_special_markup");
    if(markup != null) {
      moduleinfo.setLanguage(markup);
    }

		storeObject("idega_moduleinfo",moduleinfo);
		initializePage();
	}

	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		__main(servReq,servRes);
	}

	public void doPost(HttpServletRequest servReq,HttpServletResponse servRes)throws ServletException, IOException {
		__main(servReq,servRes);
	}

	public void __main(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException{
		try {
      __initialize(request,response);
      ModuleInfo moduleinfo = getModuleInfo();
      String eventClassEncr = moduleinfo.getParameter(IWMainApplication.IdegaEventListenerClassParameter);
      String eventClass = IWMainApplication.decryptClassName(eventClassEncr);
      if (eventClass != null) {
        IWEventListener listener = (IWEventListener)Class.forName(eventClass).newInstance();
        listener.actionPerformed(moduleinfo);
      }

      //added by gummi@idega.is
      //begin
      boolean theServiceDone = false;
      String sessionAddress = moduleinfo.getParameter(IWMainApplication.IWEventSessionAddressParameter);
      System.out.println("EventAddress: " + sessionAddress);
      if (sessionAddress != null && !"".equals(sessionAddress)) {
        Object obj = moduleinfo.getSessionAttribute(sessionAddress);
        if(obj != null) {
          if(obj instanceof ActiveEvent && obj instanceof AWTEvent ) {
            __theService(request,response);
            theServiceDone = true;
            this.getPage()._setModuleInfo(moduleinfo);
            ((ActiveEvent)obj).dispatch();
          /* Kommentað út þar til kerfið ræður við þræði
            EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
            q.postEvent((AWTEvent)obj);
          */
          }
        }
      }

      //end

      //if (isActionPerformed(request,response)){
              //actionPerformed(new ModuleEvent(request,response));
              //actionPerformed(new ModuleEvent(moduleinfo));
      //}
      //else{
            if(!theServiceDone) //gummi@idega.is
              __theService(request,response);
      //}
      response.getWriter().println("\n");
      _main(moduleinfo);

      __print(moduleinfo);
      /*if (connectionRequested()){
                      freeConnection();
      }*/
      //getThreadContext().releaseThread(Thread.currentThread());
		}
		catch(Exception ex){
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
    setPage(new Page());
  }

  public void setPage(Page myPage){
                //System.out.println("Inside setPage()");
		//storeObject("idega_page",(Page)myPage.clone());
                storeObject("idega_page",myPage);
		//this.page=page;
                /*String servletName = this.getServletConfig().getServletName();
                String attributeKey =servletName+"_idega_page";
                //System.out.println("AttributeKey="+attributeKey+" for setPage()");
		getServletContext().setAttribute(attributeKey,myPage);*/

  }

    public Page getPage(){

      return (Page) retrieveObject("idega_page");
    /*    Page page = (Page)retrieveObject("idega_page");
        if (page==null){
          String servletName = this.getServletConfig().getServletName();
          String attributeKey =servletName+"_idega_page";
          Page newPage = (Page)getServletContext().getAttribute(attributeKey);
          //System.out.println("AttributeKey="+attributeKey+" for getPage()");
          page = (Page)newPage.clone();
          storeObject("idega_page",page);
        }
        return page;*/
    }

    public ModuleInfo getModuleInfo(){
          return (ModuleInfo) retrieveObject("idega_moduleinfo");
    }


        /**
         * @deprecated
         */
      	public HttpServletRequest getRequest(){
		return getModuleInfo().getRequest();
	}

        /**
         * @deprecated
         */
	public HttpSession getSession(){
		return getModuleInfo().getSession();
	}

        /**
         * @deprecated
         */
	public HttpServletResponse getResponse(){
		return getModuleInfo().getResponse();
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


 	public void add(ModuleObject objectToAdd){
	  getPage().add(objectToAdd);
	}

	public void add(String text){
	  add(new Text(text));
	}


	public void addToTemplate(ModuleObject obj){

	}

	public void _main(ModuleInfo modinfo)throws Exception{
                //getPage().setTreeID("0");
                //getPage().updateTreeIDs();
                //String node = modinfo.getParameter("idega_special_tree_node");
                //if( node != null){
                //  ModuleObject obj = getPage().getContainedObject(node);
                  //modinfo.getResponse().getWriter().println("klasi:"+obj.getClass().getName());
                //  if(obj!=null){
               //     obj._main(modinfo);
                //  }
                //}



		getPage()._main(modinfo);
               //System.out.println("Inside _main() for: "+this.getClass().getName()+" - Tread: "+Thread.currentThread().toString());

	}

	public void __print(ModuleInfo modinfo)throws IOException{


		if (modinfo.getLanguage().equals("WML")){
			getResponse().setContentType("text/vnd.wap.wml");
		}

		getPage().print(modinfo);
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
            getModuleInfo().getWriter().println(debugString);
          }
          catch(Exception ex){
            System.err.println("Error in JPresentationModule.debug() : "+ex.getMessage());
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
}
