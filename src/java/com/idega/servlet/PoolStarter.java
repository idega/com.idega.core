//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.servlet;



import java.io.*;

import java.util.*;

import javax.servlet.*;

import javax.servlet.http.*;

import java.sql.*;

import com.idega.util.database.*;

//import javax.sql.*;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*@deprecated Replaced with IWStarterServlet

*/

public class PoolStarter extends IWStarterServlet

{



/*



	public PoolManager poolMgr;



	public void init() throws ServletException{

                // gimmi 06.02.2001

                String separator = System.getProperty("file.separator");

		//String file =

                //getServletContext().getRealPath("/")+separator+"db"+separator+"db.properties";

		String file = "db/db.properties";

                System.out.println("DB : "+file);

                try {

  		    this.poolMgr = PoolManager.getInstance(file);

                }

                catch (Exception e) {

                    System.out.println("DB : "+file);

                    e.printStackTrace(System.out);

                }

  		//getServletContext().setAttribute("poolmanager",poolMgr);

  		System.out.println("initializing PoolStarter");

  		System.out.println(file);

	}



	public void doGet( HttpServletRequest _req, HttpServletResponse _res) throws IOException{

		//_res.getWriter().println(getServletContext().getRealPath("/"));

	}



	public void doPost( HttpServletRequest _req, HttpServletResponse _res) throws IOException

	{

		doGet(_req,_res);

	}





	public void destroy(){

		//poolMgr.release();

		System.out.println("destroying PoolStarter");

		super.destroy();

	}

*/



}



//-------------

//- End of file

//-------------

