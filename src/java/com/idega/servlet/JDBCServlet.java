/********************************************************************
 * Made by Daniel.Schneider@tecfa.unige.ch 1999 TECFA.
 * This is Freeware
 *
 * Java Servlet Example
 * Connects to the COFFEEBREAK DB
 * See http://tecfa.unige.ch/guides/java/staf2x/ex/jdbc/coffee-break/
 *     for application and applet demos and more info
 *
 * Needs some polishing, some abstraction wouldn't hurt either ...
 * (Todo: An update script with synchronisation)
 *
 *******************************************************************/

package com.idega.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import com.idega.jmodule.JSPModule;
import com.idega.util.database.ConnectionBroker;


/**
 * @author Daniel Schneider, modified by <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
  *@version 1.0
  *Borrowed from <a href="http://tecfa.unige.ch/guides/java/staf2x/ex/jdbc/coffee-break/</a>
*/
public class JDBCServlet extends JSPModule {

    // The database connection
    Connection con;
    // The statement
    Statement stmt;
    // The queryString
    String queryString = null;

    public void init(ServletConfig conf) throws ServletException {
	super.init(conf);

	// ---- configure this for your site
	//String username = "nobody";
        String username = ConnectionBroker.getUserName();
	//String password = null;
        String password = ConnectionBroker.getPassword();
	// The URL that will connect to TECFA's MySQL server
	// Syntax: jdbc:TYPE:machine:port/DB_NAME
	// String url = "jdbc:mysql://localhost:3306/COFFEEBREAK";
	//String url = "jdbc:mysql://tecfa2.unige.ch:3306/COFFEEBREAK";
	  String url = ConnectionBroker.getURL();
        // ---- configure END

	try {
	    Class.forName(ConnectionBroker.getDriverClass());
	    // Establish Connection to the database at URL with usename and password
	    con = DriverManager.getConnection(url, username, password);
	    System.out.println ("Ok, connection to the DB is working.");
	} catch (Exception e) // (ClassNotFoundException and SQLException)
	    {
                e.printStackTrace(System.err);
		throw(new ServletException("Sorry! The Database didn't load!"));

	    }
    }

/**
 * service() method to handle user interaction
 */
    public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	res.setContentType ( "text/html" );
	PrintWriter  out = res.getWriter ( );

	try {
	    //String title = "Coffee Break JDBC Demo Java Servlet";
	    String title = "idegaWeb SQL DatastoreInterface";
                          out.println ( "<html><head><title>" + title
			  + "</title></head>" );
	    out.println ( "<body><H1>" + title + "</H1>" );

	    String  queryString = req.getParameter ("QUERYSTRING");

	    if ((queryString != "") && (queryString != null))
		{

		    out.println ("<h2>You asked: </h2>");
		    out.println ( "Query:  " + queryString + "<BR>" );
		    out.println("<h3>Query Result</h3>");
		    out.println("<table border>");
                                                Statement stmt = con.createStatement();
                                                if(queryString.toLowerCase().indexOf("select")==-1){
                                                    int i = stmt.executeUpdate(queryString);
                                                    //if (i>0){
                                                            out.println("<tr><td>"+i+" rows altered</td></tr>");
                                                    //}
                                                    //else{

                                                    //}
                                                }
                                                else{


                                                    ResultSet rs = stmt.executeQuery(queryString);
                                                    ResultSetMetaData rsMeta = rs.getMetaData();
                                                    // Get the N of Cols in the ResultSet
                                                    int noCols = rsMeta.getColumnCount();
                                                    out.println("<tr>");
                                                    for (int c=1; c<=noCols; c++) {
                                                        String el = rsMeta.getColumnLabel(c);
                                                        out.println("<th> " + el + " </th>");
                                                    }
                                                    out.println("</tr>");
                                                    while (rs.next()) {
                                                        out.println("<tr>");
                                                        for (int c=1; c<=noCols; c++) {
                                                            String el = rs.getString(c);
                                                            out.println("<td> " + el + " </td>");
                                                        }
                                                        out.println("</tr>");
                                                    }
                                                }
		    out.println("</table>");
		}
	} catch (SQLException ex ) {
	    out.println ( "<P><PRE>" );
	      while (ex != null) {
		  out.println("Message:   " + ex.getMessage ());
		  out.println("SQLState:  " + ex.getSQLState ());
		  out.println("ErrorCode: " + ex.getErrorCode ());
		  ex = ex.getNextException();
		  out.println("");
	      }
	      out.println ( "</PRE><P>" );
	}

	//out.println ("<hr>You can now try to retrieve something.");
	//out.println("<FORM METHOD=POST ACTION=\"/servlet/CoffeeBreakServlet\">");
	out.println("<FORM METHOD=POST ACTION=\""+req.getRequestURI()+"\">");
        out.println("Query: <INPUT TYPE=TEXT SIZE=50 NAME=\"QUERYSTRING\"> ");
	out.println("<INPUT TYPE=SUBMIT VALUE=\"GO!\">");
	out.println("</FORM>");
	//out.println("<hr><pre>e.g.:");
	//out.println("SELECT * FROM COFFEES");
	//out.println("SELECT * FROM COFFEES WHERE PRICE > 9");
	//out.println("SELECT PRICE, COF_NAME FROM COFFEES");
	//out.println("<pre>");

	out.println ("<hr><a href=\""+req.getRequestURI()+"\">Query again ?</a>");// | Source: <A HREF=\"/develop/servlets-ex/coffee-break/CoffeeBreakServlet.java\">CoffeeBreakServlet.java</A>");
	out.println ( "</body></html>" );
	return ;
    }



}
