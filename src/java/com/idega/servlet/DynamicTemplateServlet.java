//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.idega.jmodule.object.*;
import com.idega.idegaweb.*;
import com.idega.idegaweb.template.*;
import com.idega.jmodule.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class DynamicTemplateServlet extends PageJSPModule{


  private static String DEFAULT = "default";


  public void initializePage(){
    try{
        //Not a good enough implementation, is dependent upon
        //where storeObject("idega_moduleinfo",moduleinfo) is called
        //in IWPresentationServlet
            ModuleInfo modinfo = getModuleInfo();
            String templateClassName=modinfo.getParameter("idegaweb_template_class");
            if(templateClassName!=null){
              setTemplateClassName(templateClassName);
            }
            String templateName=modinfo.getParameter("idegaweb_template");
            if(templateName!=null){
              //Properties prop = getDefaultProperties();
              //setTemplateClassName(prop.getProperty("idegaweb.template."+templateName+".classname"));
              setTemplateClassName(getApplicationSettings().getDefaultTemplateClass());
            }

      //String servletName = this.getServletConfig().getServletName();
      //System.out.println("Inside initializePage for "+servletName);
      setPage(getThisPage());
    }
    catch(Exception ex){
      ex.printStackTrace(System.err);
    }
  }


	/*public void init()throws ServletException{
          super.init();
        }*/

        //TEMPORARY IMPLEMENTATION
	public void __theService(HttpServletRequest request, HttpServletResponse response)
	throws ServletException,IOException{

                try{
	        main(getModuleInfo());
                }
                catch(Exception ex){
                  ex.printStackTrace(response.getWriter());
                  ServletException exception = new ServletException(ex.getMessage());
                  throw (ServletException)exception.fillInStackTrace();
                }
                _jspService(request,response);
        }

        //TEMPORARY IMPLEMENTATION
        public void main(ModuleInfo modinfo)throws Exception{
        };

        /*public void setFirstTemplatePage(Page page){
        }*/


        public String getTemplateName(){
                return DEFAULT;
        }


        public String getFirstTemplatePageClass()throws Exception{
          //Properties prop = getDefaultProperties();
          //String className = prop.getProperty("idegaweb.template.default.classname");
          String className = null;
          IWMainApplicationSettings settings = getApplicationSettings();
          if(settings!=null){
            className = settings.getDefaultTemplateClass();
          }
          //System.err.println("className="+className);
          //Page page = (Page)Class.forName(className).newInstance();
          //return page;
          return className;
        }

        public void setTemplateClassName(String className){
          getServletContext().setAttribute("idegaweb_template_class",className);
        }

        public String getTemplateClassName(){
          return (String)getServletContext().getAttribute("idegaweb_template_class");
        }

        public Page getFirstTemplatePage(ModuleInfo modinfo)throws Exception{
          //Properties prop = getDefaultProperties(modinfo);
          //Properties prop = getDefaultProperties();
          //String className = prop.getProperty("idegaweb.default.templatepage.classname");
          String className = getApplicationSettings().getDefaultTemplateClass();
          //System.err.println("className="+className);
          Page page = (Page)Class.forName(className).newInstance();
          return page;
        }




        public Page getThisPage()throws Exception{
          String className = getTemplateClassName();
          if(className==null){
            className = getFirstTemplatePageClass();
            if(className==null){
              className="com.idega.jmodule.object.Page";
            }
            setTemplateClassName(className);
          }
          Page page = (Page)Class.forName(className).newInstance();
          if(page==null){
            page = new Page();
          }
          return page;
        }


/*

        public Page getTemplatePage(ModuleInfo modinfo)throws Exception{
          Page page = (Page)modinfo.getServletContext().getAttribute("idega_template_page");
          if(page==null){
            page = getFirstTemplatePage(modinfo);
            modinfo.getServletContext().setAttribute("idega_template_page",page);
          }
          return (Page)page.clone();
        }

*/

/*        public Page getPage(){
          try{
            return getTemplatePage(getModuleInfo());
          }
          catch(Exception ex){
            ex.printStackTrace(System.err);
            return null;
          }
        }
*/


/*
        protected Properties getDefaultProperties(ModuleInfo modinfo)throws IOException{
          IdegawebProperties properties = (IdegawebProperties)modinfo.getServletContext().getAttribute("idegaweb_default_properties");
	  if (properties==null){
            properties = new IdegawebProperties(getServletContext());
          }
          modinfo.getServletContext().setAttribute("idegaweb_default_properties",properties);
          return properties;
        }

        protected Properties getDefaultProperties()throws IOException{
          IdegawebProperties properties = (IdegawebProperties)getServletContext().getAttribute("idegaweb_default_properties");
	  if (properties==null){
            properties = new IdegawebProperties();
          }
          getServletContext().setAttribute("idegaweb_default_properties",properties);
          return properties;
        }


*/





    public TemplatePage getTemplatePage(){
      return (TemplatePage) getPage();
    }

  /*methods from com.idega.idegaweb.template.TemplatePage*/



  public void add2(ModuleObject obj){
    getTemplatePage().add2(obj);
  }

  public void add3(ModuleObject obj){
    getTemplatePage().add3(obj);
  }

  public void add4(ModuleObject obj){
    getTemplatePage().add4(obj);
  }

  public void add5(ModuleObject obj){
  getTemplatePage().add5(obj);
  }

  public void add6(ModuleObject obj){
    getTemplatePage().add6(obj);
  }

  public void add7(ModuleObject obj){
    getTemplatePage().add7(obj);
  }

  public void add8(ModuleObject obj){
    getTemplatePage().add8(obj);
  }

  public void add9(ModuleObject obj){
    getTemplatePage().add9(obj);
  }

  public boolean isAdministrator(ModuleInfo modinfo)throws Exception{
    return this.getTemplatePage().isAdministrator(modinfo);
  }

  public boolean isDeveloper(ModuleInfo modinfo)throws Exception{
    return this.getTemplatePage().isDeveloper(modinfo);
  }

  public boolean isUser(ModuleInfo modinfo)throws Exception{
    return this.getTemplatePage().isUser(modinfo);
  }

  public boolean isMemberOf(ModuleInfo modinfo,String groupName)throws Exception{
    return this.getTemplatePage().isMemberOf(modinfo, groupName);
  }

  public boolean hasPermission(String permissionType,ModuleInfo modinfo,ModuleObject obj)throws Exception{
    return this.getTemplatePage().hasPermission(permissionType, modinfo, obj);
  }




}   // Class DynamicTemplateServlet
