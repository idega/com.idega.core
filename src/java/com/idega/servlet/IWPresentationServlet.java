/*
 * $Id: IWPresentationServlet.java,v 1.19 2001/10/01 14:12:43 gummi Exp $
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
import com.idega.event.IWModuleEvent;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/

public  class IWPresentationServlet extends IWCoreServlet{

  private static final String IW_BUNDLE_IDENTIFIER = "com.idega.core";

  private static final String IW_MODULEINFO_KEY="idegaweb_moduleinfo";
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


          storeObject(IW_MODULEINFO_KEY,moduleinfo);
          processBusinessEvent(moduleinfo);
          initializePage();
	}

	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		__main(servReq,servRes);
	}

	public void doPost(HttpServletRequest servReq,HttpServletResponse servRes)throws ServletException, IOException {
		__main(servReq,servRes);
	}

        public void processBusinessEvent(ModuleInfo moduleinfo)throws ClassNotFoundException,IllegalAccessException,IWException,InstantiationException{
          String eventClassEncr = moduleinfo.getParameter(IWMainApplication.IdegaEventListenerClassParameter);
          String eventClass = IWMainApplication.decryptClassName(eventClassEncr);
          if (eventClass != null) {
            IWEventListener listener = (IWEventListener)Class.forName(eventClass).newInstance();
            listener.actionPerformed(moduleinfo);
          }
        }

	public void __main(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException{
          try {

long time1 = System.currentTimeMillis();
com.idega.core.accesscontrol.business.AccessControl._COUNTER = 0;
            __initialize(request,response);
            ModuleInfo moduleinfo = getModuleInfo();

            //added by gummi@idega.is
            //begin
            boolean theServiceDone = false;
            String sessionAddress = moduleinfo.getParameter(IWMainApplication.IWEventSessionAddressParameter);

            if (sessionAddress != null && !"".equals(sessionAddress)){
              Object obj = moduleinfo.getSessionAttribute(sessionAddress);
              if(obj != null) {
                if(obj instanceof ActiveEvent && obj instanceof AWTEvent ) {

                    if(Page.isRequestingTopPage(moduleinfo)){
                      __theService(request,response);
                    }
                  theServiceDone = true;
                  if(obj instanceof IWModuleEvent){
                    ((IWModuleEvent)obj).setModuleInfo(moduleinfo);
                  }else{
                    this.getPage()._setModuleInfo(moduleinfo);
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

            //if (isActionPerformed(request,response)){
                    //actionPerformed(new ModuleEvent(request,response));
                    //actionPerformed(new ModuleEvent(moduleinfo));
            //}
            //else{
                  if(!theServiceDone) //gummi@idega.is
                  {
                    if(Page.isRequestingTopPage(moduleinfo)){
                      __theService(request,response);
                    }
                  }
            //}
      //      response.getWriter().println("\n");
            _main(moduleinfo);

            __print(moduleinfo);

long time2 = System.currentTimeMillis();
PrintWriter writer = moduleinfo.getWriter();
writer.println("<!-- --------------------------------------- -->");
writer.println("<!-- Printing page: "+ (time2 - time1 )+ " ms -->");
writer.println("<!-- viewpermission: "+com.idega.core.accesscontrol.business.AccessControl._COUNTER +" -->");
writer.println("<!-- --------------------------------------- -->");

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
    setPage(Page.loadPage(getModuleInfo()));
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
      return Page.getPage(getModuleInfo());
    }

    public ModuleInfo getModuleInfo(){
          return (ModuleInfo) retrieveObject(IW_MODULEINFO_KEY);
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

	public void __print(ModuleInfo modinfo)throws Exception{


		if (modinfo.getLanguage().equals("WML")){
			getResponse().setContentType("text/vnd.wap.wml");
		}

		getPage()._print(modinfo);
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


  public IWBundle getBundle(ModuleInfo modinfo){
    IWMainApplication iwma = modinfo.getApplication();
    return iwma.getBundle(getBundleIdentifier());
  }

  public IWResourceBundle getResourceBundle(ModuleInfo modinfo){
    IWBundle bundle = getBundle(modinfo);
    if(bundle!=null){
      return bundle.getResourceBundle(modinfo.getCurrentLocale());
    }
    return null;
  }

  public String getLocalizedString(String key,ModuleInfo modinfo){
    IWResourceBundle bundle = getResourceBundle(modinfo);
    if(bundle!=null){
      return bundle.getLocalizedString(key);
    }
    return null;
  }

}
