package com.idega.servlet;

import com.idega.idegaweb.presentation.IWApplicationsLogin;
import com.idega.presentation.Page;

/**
 * Title: idegaclasses Description: Copyright: Copyright (c) 2001 Company:
 * idega
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */

public class IWApplicationsServlet extends IWPresentationServlet {


	public IWApplicationsServlet() {
	}

	public void initializePage() {
		Page thePage = new IWApplicationsLogin();
		setPage(thePage);
	}
}