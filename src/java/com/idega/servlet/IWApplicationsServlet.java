package com.idega.servlet;

import com.idega.idegaweb.presentation.IWApplicationsLogin;
import com.idega.presentation.Page;

/**
 * Title: idegaclasses Description: Copyright: Copyright (c) 2001 Company:
 * idega
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 * @deprecated Removed in platform 3 and replaced with the workspace environment
 */

public class IWApplicationsServlet extends IWPresentationServlet {


	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2261118353083984499L;

	public IWApplicationsServlet() {
	}

	public void initializePage() {
		Page thePage = new IWApplicationsLogin();
		setPage(thePage);
	}
}