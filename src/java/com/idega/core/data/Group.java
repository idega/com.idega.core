//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import java.util.Vector;
import com.idega.core.user.data.User;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/

public class Group extends GeneralGroup{

	public Group(){
		super();
	}

	public Group(int id)throws SQLException{
		super(id);
	}


        public String getGroupTypeValue(){
          return "general";
        }

        public static String getClassName(){
          return "com.idega.data.genericentity.Group";
        }

}   // Class Group
