package com.idega.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspPage;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/
public class IWJSPPresentationServlet extends IWPresentationServlet implements JspPage {
	public void jspInit() {
		/*try{
		 	super.init();
		}
		catch(ServletException e){
		}*/
	}
	public void __theService(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			_jspService(request, response);
		}
		catch (Exception ex) {
			handleException(ex, this);
		}
	}
	public void jspDestroy() {
		super.destroy();
	}
	public void _jspService(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
	}
}