//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@deprecated Under revision
*@version 1.2
*/
public class ModuleEvent{

HttpServletRequest request;
HttpServletResponse response;


	public ModuleEvent(HttpServletRequest request,HttpServletResponse response){
		this.request=request;
		this.response=response;
	}

	public EventSource getSource(){
		return new EventSource(request,response);
	}

	public HttpServletRequest getRequest(){
		return request;
	}

	public HttpServletResponse getResponse(){
		return response;
	}

}
