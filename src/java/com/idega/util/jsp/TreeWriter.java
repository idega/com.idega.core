package com.idega.util.jsp;



import java.sql.*;

import java.io.*;





public class TreeWriter{







public void printnode(int currentnode,PrintWriter out,Connection conn)throws SQLException,IOException{

	Statement Stmt = conn.createStatement();

	ResultSet RS = Stmt.executeQuery("select * from verk where parent_id='"+currentnode+"'");



	int current_node;

	boolean selected;



	while (RS.next()){



		current_node = RS.getInt("node_id");



		selected = false;



		if (selected){

			out.println("<select>"+current_node+"</select>");

		}

		else{

			out.println("<select>"+current_node+"</select>");

		}



		//Stmt Stmt2 = conn.createStatement();

		//ResultSet RS2 = Stmt2.executeQuery("select * from verk where parent_id='"+current_node+"'");



		printnode(currentnode,out,conn);



	}



}







}

