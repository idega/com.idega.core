package com.idega.servlet;

import com.idega.presentation.EventViewerPage;
import com.idega.presentation.Page;

/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 */
public class IWEventHandler extends IWPresentationServlet {
  
  
  
  public void initializePage(){
    Page page = new EventViewerPage();
    //page.add("EventHandler");
    setPage(page);
    
    
  }
}