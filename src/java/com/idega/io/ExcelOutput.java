package com.idega.io;
/**
 * This example was written by Jason Clapham
 * for iText users who want a very simple example
 * of how to code a servlet to output a PDF document.
 */

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class ExcelOutput extends HttpServlet
{
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {

    try
    {
      // take the message from the URL or create default message
      String spath = (String)request.getParameter("dir");
      if (spath == null || spath.trim().length() <= 0)
        spath = "[ specify a message in the 'msg' argument on the URL ]";
      FilePermission FP = new FilePermission(spath,"read,execute");
      // create simple doc and write to a ByteArrayOutputStream
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      FileInputStream fis = new FileInputStream(spath);
        // Read the entire contents of the file.
      while (fis.available() > 0)
      {
          baos.write(fis.read());
      }

      // write ByteArrayOutputStream to the ServletOutputStream
      response.setContentType("application/x-msexcel");
      response.setContentLength(baos.size());
      ServletOutputStream out = response.getOutputStream();
      baos.writeTo(out);
      out.flush();

    }
    catch (Exception e2)
    {
      System.out.println("Error in "+getClass().getName()+"\n"+e2);
    }
  }

  public void destroy()
  {
  }
}
