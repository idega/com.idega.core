package com.idega.io;
/**
 * This example was written by Jason Clapham
 * for iText users who want a very simple example
 * of how to code a servlet to output a PDF document.
 */

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PDFOutput extends HttpServlet
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
      String spath = request.getParameter("dir");
      if (spath == null || spath.trim().length() <= 0)
        spath = "[ specify a message in the 'msg' argument on the URL ]";
      new FilePermission(spath,"read,execute");
      // create simple doc and write to a ByteArrayOutputStream
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      FileInputStream fis = new FileInputStream(spath);
        // Read the entire contents of the file.
      while (fis.available() > 0)
      {
          baos.write(fis.read());
      }

      // write ByteArrayOutputStream to the ServletOutputStream
      response.setContentType("application/pdf");
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
