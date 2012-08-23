package com.idega.io;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemoryFileBufferWriter{

  public final static String PRM_SESSION_BUFFER = "smb";

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
  {

    try
    {
      String sessionName = request.getParameter(PRM_SESSION_BUFFER);
      if(sessionName !=null){
        if(request.getSession().getAttribute(sessionName)!=null){
          MemoryFileBuffer mfb = (MemoryFileBuffer) request.getSession().getAttribute(sessionName);
          request.getSession().removeAttribute(sessionName);
          MemoryInputStream mis = new MemoryInputStream(mfb);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          // Read the entire contents of the file.

          while (mis.available() > 0)
          {
            baos.write(mis.read());
          }
          response.setContentType(mfb.getMimeType());
          response.setContentLength(baos.size());
          ServletOutputStream out = response.getOutputStream();
          baos.writeTo(out);
          out.flush();
          
          mis.close();
          baos.close();
        }
      }
    }
    catch (Exception e2){
      e2.printStackTrace();
      System.out.println("Error in "+getClass().getName()+"\n"+e2);
    }
  }

}
