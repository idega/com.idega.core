package com.idega.servlet;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.jsp.JSPModule;
import com.idega.repository.data.RefactorClassRegistry;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*@deprecated This class should no longer be used in platform 3 and is replaced by FacesServlet
*/
public class PageInstanciator extends JSPModule {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4018825668623560730L;
	public void initializePage() {
		try {
			//String servletName = this.getServletConfig().getServletName();
			//System.out.println("Inside initializePage for "+servletName);
			setPage(getThisPage(getIWContext()));
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	private Page getThisPage(IWContext iwc) {
		String className =
			IWMainApplication.decryptClassName(iwc.getParameter(IWMainApplication.classToInstanciateParameter));
		try {
			return (Page) RefactorClassRegistry.forName(className).newInstance();
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
			return null;
		}
	}
}
//-------------
//- End of file
//-------------
