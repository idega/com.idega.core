//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import java.io.*;
import java.util.*;
import javax.servlet.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class IdegaWebProperties extends Properties{



        public IdegaWebProperties(){//throws IOException{
            String pathSeparator = System.getProperty("file.separator");
            try{
              super.load(new FileInputStream(pathSeparator+"idegaweb"+pathSeparator+"default.properties"));
            }
            catch(FileNotFoundException ex){
              try{
                FileOutputStream output = new FileOutputStream(pathSeparator+"idegaweb"+pathSeparator+"default.properties");
                output.write(0);
                output.close();
              }
              catch(FileNotFoundException exe){
                 System.err.println("Error writing to "+pathSeparator+"idegaweb"+pathSeparator+"default.properties or file not found");
              }
              catch(IOException excep){
                excep.printStackTrace(System.err);
              }

            }
            catch(IOException ioexcep){
                ioexcep.printStackTrace(System.err);
            }

        }

        public IdegaWebProperties(ServletContext cont){
            String prefix =  cont.getRealPath("/");
            String pathSeparator = System.getProperty("file.separator");
            try{
              super.load(new FileInputStream(prefix+pathSeparator+"idegaweb"+pathSeparator+"default.properties"));
            }
            catch(FileNotFoundException ex){
              try{
                FileOutputStream output = new FileOutputStream(prefix+pathSeparator+"idegaweb"+pathSeparator+"default.properties");
                output.write(0);
                output.close();
              }
              catch(FileNotFoundException exe){
                System.err.println("Error writing to "+prefix+pathSeparator+"idegaweb"+pathSeparator+"default.properties or file not found");
              }
              catch(IOException excep){
                excep.printStackTrace(System.err);
              }

            }
            catch(IOException ioexcep){
                ioexcep.printStackTrace(System.err);
            }
        }

}
