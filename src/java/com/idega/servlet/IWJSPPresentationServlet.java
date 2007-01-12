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
	boolean handleJSPTags = true;
	
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
			
			if(this.handleJSPTags){
				_jspService(request, response);
			}
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
	/**
	 * Returns the handleJSPTags.
	 * @return boolean
	 */
	public boolean canHandleJSPTags() {
		return this.handleJSPTags;
	}

	/**
	 * Sets the handleJSPTags boolean. If set to true the content of a jsp will be sent <br>
	 * to the application server for parsing a compiling otherwise it is ignored.
	 * @param handleJSPTags The handleJSPTags to set
	 */
	public void setHandleJSPTags(boolean handleJSPTags) {
		this.handleJSPTags = handleJSPTags;
	}

}