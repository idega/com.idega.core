//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.servlet;



import java.io.*;

import javax.servlet.*;

import javax.servlet.http.*;

import javax.servlet.jsp.*;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class IWJSPPresentationServlet extends IWPresentationServlet implements JspPage{



	public void jspInit(){

          /*try{

                  super.init();

          }

          catch(ServletException e){

          }*/

	}





	public void __theService(HttpServletRequest request, HttpServletResponse response)

	throws ServletException,IOException{

                try{

		  _jspService(request,response);

                }

                catch(Exception ex){

                  handleException(ex,this);

                }

	}



	public void jspDestroy(){

		super.destroy();

	}





	public void _jspService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException{



	}



}

