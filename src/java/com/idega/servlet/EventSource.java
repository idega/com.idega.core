//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.idega.jmodule.object.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class EventSource{

HttpServletRequest request;
HttpServletResponse response;

	
	public EventSource(HttpServletRequest request,HttpServletResponse response){
		this.request=request;
		this.response=response;
	}
	
	public boolean equals(ModuleObject modobj){
		if (modobj != null){
			if(request.getParameter(modobj.getName()) != null){
				return true;
			}
			else{
				return false;
			}

		}
		else{
			return false;
		}
	}
	

}
