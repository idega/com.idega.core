package com.idega.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.repository.data.RefactorClassRegistry;
//import com.idega.idegaweb.template.*;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*/
public class DynamicTemplateServlet extends IWJSPPresentationServlet {
	private static String DEFAULT = "default";
	public void initializePage() {
		try {
			//Not a good enough implementation, is dependent upon
			//where storeObject("idega_iwc",iwc) is called
			//in IWPresentationServlet
			IWContext iwc = getIWContext();
			String templateClassName = iwc.getParameter(IWMainApplication.templateClassParameter);
			/*if(templateClassName!=null){
			
			  setTemplateClassName(templateClassName);
			
			}*/
			String templateName = iwc.getParameter(IWMainApplication.templateParameter);
			if (templateName != null) {
				//Properties prop = getDefaultProperties();
				//setTemplateClassName(prop.getProperty("idegaweb.template."+templateName+".classname"));
				setTemplateClassName(getApplicationSettings().getDefaultTemplateClass());
			}
			//String servletName = this.getServletConfig().getServletName();
			//System.out.println("Inside initializePage for "+servletName);
			setPage(getThisPage(templateClassName));
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	/*public void init()throws ServletException{
	
	      super.init();
	
	    }*/
	//TEMPORARY IMPLEMENTATION
	public void __theService(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		IWContext iwc = getIWContext();
		try {
			main(iwc);
		}
		catch (Exception ex) {
			ex.printStackTrace(iwc.getWriter());
			ex.printStackTrace(System.err);
			ServletException exception = new ServletException(ex.getMessage());
			throw (ServletException) exception.fillInStackTrace();
		}
		_jspService(request, response);
	}
	//TEMPORARY IMPLEMENTATION
	public void main(IWContext iwc) throws Exception {
	}
	/*public void setFirstTemplatePage(Page page){
	
	}*/
	public String getTemplateName() {
		return DEFAULT;
	}
	public String getFirstTemplatePageClass() throws Exception {
		//Properties prop = getDefaultProperties();
		//String className = prop.getProperty("idegaweb.template.default.classname");
		String className = null;
		IWMainApplicationSettings settings = getApplicationSettings();
		if (settings != null) {
			className = settings.getDefaultTemplateClass();
		}
		//System.err.println("className="+className);
		//Page page = (Page)Class.forName(className).newInstance();
		//return page;
		return className;
	}
	public void setTemplateClassName(String className) {
		getServletContext().setAttribute("idegaweb_template_class", className);
	}
	public String getTemplateClassName() {
		return (String) getServletContext().getAttribute("idegaweb_template_class");
	}
	public Page getFirstTemplatePage(IWContext iwc) throws Exception {
		//Properties prop = getDefaultProperties(iwc);
		//Properties prop = getDefaultProperties();
		//String className = prop.getProperty("idegaweb.default.templatepage.classname");
		String className = getApplicationSettings().getDefaultTemplateClass();
		//System.err.println("className="+className);
		Page page = (Page) RefactorClassRegistry.forName(className).newInstance();
		return page;
	}
	public Page getThisPage(String className) throws Exception {
		//String className = getTemplateClassName();
		if (className == null) {
			className = getFirstTemplatePageClass();
			if (className == null) {
				className = "com.idega.presentation.Page";
			}
			setTemplateClassName(className);
		}
		Page page = (Page) RefactorClassRegistry.forName(className).newInstance();
		if (page == null) {
			page = new Page();
		}
		return page;
	}
	/*
	
	
	
	        public Page getTemplatePage(IWContext iwc)throws Exception{
	
	          Page page = (Page)iwc.getServletContext().getAttribute("idega_template_page");
	
	          if(page==null){
	
	            page = getFirstTemplatePage(iwc);
	
	            iwc.getServletContext().setAttribute("idega_template_page",page);
	
	          }
	
	          return (Page)page.clone();
	
	        }
	
	
	
	*/
	/*        public Page getPage(){
	
	          try{
	
	            return getTemplatePage(getIWContext());
	
	          }
	
	          catch(Exception ex){
	
	            ex.printStackTrace(System.err);
	
	            return null;
	
	          }
	
	        }
	
	*/
	/*
	
	        protected Properties getDefaultProperties(IWContext iwc)throws IOException{
	
	          IdegawebProperties properties = (IdegawebProperties)iwc.getServletContext().getAttribute("idegaweb_default_properties");
	
		  if (properties==null){
	
	            properties = new IdegawebProperties(getServletContext());
	
	          }
	
	          iwc.getServletContext().setAttribute("idegaweb_default_properties",properties);
	
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
	/*
	
	    public TemplatePage getTemplatePage(){
	
	      return (TemplatePage) getPage();
	
	    }
	
	*/
	/*methods from com.idega.idegaweb.template.TemplatePage*/
	/*
	
	  public void add2(PresentationObject obj){
	
	    getTemplatePage().add2(obj);
	
	  }
	
	
	
	  public void add3(PresentationObject obj){
	
	    getTemplatePage().add3(obj);
	
	  }
	
	
	
	  public void add4(PresentationObject obj){
	
	    getTemplatePage().add4(obj);
	
	  }
	
	
	
	  public void add5(PresentationObject obj){
	
	  getTemplatePage().add5(obj);
	
	  }
	
	
	
	  public void add6(PresentationObject obj){
	
	    getTemplatePage().add6(obj);
	
	  }
	
	
	
	  public void add7(PresentationObject obj){
	
	    getTemplatePage().add7(obj);
	
	  }
	
	
	
	  public void add8(PresentationObject obj){
	
	    getTemplatePage().add8(obj);
	
	  }
	
	
	
	  public void add9(PresentationObject obj){
	
	    getTemplatePage().add9(obj);
	
	  }
	
	
	
	  public boolean isAdministrator(IWContext iwc)throws Exception{
	
	    return this.getTemplatePage().isAdministrator(iwc);
	
	  }
	
	
	
	  public boolean isDeveloper(IWContext iwc)throws Exception{
	
	    return this.getTemplatePage().isDeveloper(iwc);
	
	  }
	
	
	
	  public boolean isUser(IWContext iwc)throws Exception{
	
	    return this.getTemplatePage().isUser(iwc);
	
	  }
	
	
	
	  public boolean isMemberOf(IWContext iwc,String groupName)throws Exception{
	
	    return this.getTemplatePage().isMemberOf(iwc, groupName);
	
	  }
	
	
	
	  public boolean hasPermission(String permissionType,IWContext iwc,PresentationObject obj)throws Exception{
	
	    return this.getTemplatePage().hasPermission(permissionType, iwc, obj);
	
	  }
	
	
	
	*/
} // Class DynamicTemplateServlet
