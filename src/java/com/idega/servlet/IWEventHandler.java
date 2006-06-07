package com.idega.servlet;

import com.idega.presentation.EventViewerPage;
import com.idega.presentation.Page;

/**
 * @author <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * @deprecated Replaced with FacesServlet
 */
public class IWEventHandler extends IWPresentationServlet {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2485851372557676906L;

	public void initializePage() {
		Page page = new EventViewerPage();
		// page.add("EventHandler");
		setPage(page);
	}
}