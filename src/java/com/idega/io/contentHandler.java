package com.idega.io;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.sql.*;
import java.net.*;

/**	This class provides an interface between servlets and jsp pages
*
*
*
*	© idega multimedia 2000.
**/

public class contentHandler {

private String node_id;
private String language;

public contentHandler(String node_id,String language)	{
	this.node_id = node_id;
	this.language = language;
}

public String getModule(Connection conn, String region_id)	{

	String returnString = "";
	String object_url;
	String object_attributes;
	String base_ref = "";

	try {

		/*

		//initialize the PoolManager
		poolMgr = PoolManager.getInstance();
		//Get the specified databaseconnection from the PoolManager
		conn = poolMgr.getConnection("idegaweb_admin");

	    if (conn == null){
	       System.err.print("Can't get connection");
	    }

	    */

		Statement Stmt;
		ResultSet RS,RS2,RS3;
		String qry="";

		Stmt = conn.createStatement();
		RS = Stmt.executeQuery("select parametervalue from web_inf where parametername='base_ref'");
		base_ref=RS.getString("parametervalue");


		 RS.close();
         Stmt.close();

         //finna node id og tengja vid regionid og finna thannig objectid og object attribute id
         //urlid a objectinum faum vid fra object toflunni en stillingarnar fra object_atrribute toflunni
         //sendum stillingarnar afram a servlettin med urli og faum strauminn til baka

        Stmt = conn.createStatement();
        RS = Stmt.executeQuery("SELECT * from page where node_id='"+this.node_id+"' AND region_id='"+region_id+"'" +" AND language='"+language+"' AND valid='Y'");
        Stmt = conn.createStatement();
        RS2 = Stmt.executeQuery("SELECT object_url from object where object_id='"+RS.getString("object_id")+"'  AND valid='Y'");


        object_url = base_ref+RS2.getString("object_url");

        Stmt = conn.createStatement();
        RS3 = Stmt.executeQuery("SELECT * from object_attributes where object_attribute_id='"+RS.getString("object_attribute_id")+"'");


        //fara i gegnum stillingarnar og smí›a svo url sem kallar á servletti›

        while (RS3.next())	{
  			qry = qry + URLEncoder.encode(RS3.getString("attribute_name")) + "=" + URLEncoder.encode(RS3.getString("attribute_value")) +"&";
        	//qry = qry + URLEncoder.encode(RS3.getString("attribute_name")) + "=" + URLEncoder.encode(RS3.getString("attribute_value"));
        	//qry = qry + URLEncoder.encode("text_id") + "=" + URLEncoder.encode("1") +"&";
        	//returnString = returnString+ " name :"+RS3.getString("attribute_name"+ " value :"+RS3.getString("attribute_value"));
        	//returnString = returnString + " <br> name : "+RS3.getString("attribute_name")+" value : "+RS3.getString("attribute_value");
        	//returnString = object_url+qry;

        }

         URL url = new URL(object_url);
         //URL url = new URL("http://localhost/servlet/plainTextModule");

         URLConnection uc = url.openConnection();
         uc.setDoOutput(true);
         uc.setDoInput(true);
         uc.setUseCaches(false);

		 //uc.setRequestProperty("Content-type", "text/html");
		 uc.setRequestProperty("Content-type","application/x-www-form-urlencoded");

         DataOutputStream dos = new DataOutputStream(uc.getOutputStream());
         dos.writeBytes(qry);
         dos.flush();
         dos.close();

         InputStreamReader in = new InputStreamReader(uc.getInputStream());

         int chr = in.read();
         while (chr != -1) {
            returnString = returnString + (String.valueOf((char) chr));
            chr = in.read();
         }

         //clean up!

         in.close();

         Stmt.close();
         RS.close();
         RS2.close();
         RS3.close();


      } catch(MalformedURLException e) {
         System.err.println(e.toString());
      } catch(IOException e) {
         System.err.println(e.toString());
      }
      catch (SQLException E) {
    	System.err.print("In contentHandler SQLException: " + E.getMessage());
        System.err.print("In contentHandler SQLState:     " + E.getSQLState());
        System.err.print("In contentHandler VendorError:  " + E.getErrorCode());
      }
      catch (Exception E) {
		//out.println("<br> Unable to load driver"+ "<br>");
		System.err.print("In contentHandler Exception E");
		E.printStackTrace();
	  }


   return returnString;
}//end getStream


}//end class


