//idega 2000 - Tryggvi Larusson

/*

 *Copyright 2000 idega.is All Rights Reserved.

 */

package com.idega.presentation.jsp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspPage;

import com.idega.servlet.IWPresentationServlet;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 * @deprecated This class should no longer be used
 */
public class JSPModule extends IWPresentationServlet implements JspPage {

	public void jspInit() {
		/*
		 * try{ super.init(); } catch(ServletException e){ }
		 */
	}

	public void __theService(HttpServletRequest request,
			HttpServletResponse response)
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

	public void _jspService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}

