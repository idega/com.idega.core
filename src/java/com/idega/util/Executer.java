package com.idega.util;

import com.idega.util.text.TextSoap;

import java.io.*;

import java.lang.Process;



/**

 *  Title: idega default Description: Copyright: Copyright (c) 2001 Company:

 *  idega

 *

 *@author     <a href="tryggvi@idega.is">Tryggvi Larusson</a>

 *@created    12. mars 2002

 *@version    1.0

 */



public class Executer {



  /**

   *  Constructor for the Executer object

   */

  public Executer() { }





  /**

   *  The main program for the Executer class

   *

   *@param  args  The command line arguments

   */

  public static void main(String[] args) {

    try {

      if (args != null) {



        for (int i = 0; i < args.length; i++) {

          System.out.println("com.idega.util.Executer args["+i+"] = "+args[i]);

          if( canExecute(args[i]) ){

            Process p = Runtime.getRuntime().exec(args[i]);

            StringBuffer sbOut = new StringBuffer(1000);

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while (true) {

              String s = br.readLine();

              if (s == null) {

                break;

              }

              System.out.println(s);

            }

            br.close();

            //p.waitFor();

            System.out.println(sbOut.toString());

            System.out.println("Exit status: " + p.exitValue());

            p.destroy();

          }

          else{

            System.out.println("WARNING SOMEONE IS TRYING TO USE EXECUTE_AFTER_RESTART TO CORRUPT OR DELETE FILES!");

          }

        }

      }

    } catch (Exception ex) {

      ex.printStackTrace(System.err);

      System.out.println(ex.getMessage());

    }

  }



  private static boolean canExecute(String exec){

    exec = exec.toLowerCase();

    if(exec.indexOf("rm -r")!=-1) {
		return false;
	}
	else if(exec.indexOf("rm -f")!=-1) {
		return false;
	}
	else if(exec.indexOf("rm /")!=-1) {
		return false;
	}
	else if(exec.indexOf("rmdir")!=-1) {
		return false;
	}
	else if(exec.startsWith("del ")) {
		return false;
	}
	else {
		return true;
	}

  }



  /**

   *  Description of the Method

   * only works in tomcat at the moment

   *@param  args  Description of the Parameter

   */

  public static void executeInAnotherVM(String[] args) {

    try {

      String javaHome;



      //jre path

      if(System.getProperty("os.name").toLowerCase().indexOf("win")!=-1){

        javaHome = System.getProperty("sun.boot.library.path");

      }

      else{//unix

        javaHome = System.getProperty("java.home")+FileUtil.getFileSeparator()+"bin";

      }



      //@todo update this to reflect other versions of application servers classpath

      String classPath = null;

      classPath = System.getProperty("tc_path_add");//tomcat 3.3

      if(classPath==null) {
		classPath = System.getProperty("java.class.path");
	}



      if(classPath!=null ) {
		classPath = TextSoap.findAndCut(classPath," ");//take away all spaces
	}



      //put together the string

      StringBuffer exec = new StringBuffer();

      exec.append(javaHome);

      exec.append(FileUtil.getFileSeparator());

      exec.append("java ");

      if(classPath!=null) {

        exec.append(" -cp ");

        exec.append(classPath);

        exec.append(" ");

      }



      exec.append(Executer.class.getName());

      exec.append(" ");



      if (args != null) {

        for (int i = 0; i < args.length; i++) {

          exec.append(args[i]);

          exec.append(" ");

        }



        System.out.println("EXECUTING : " + exec);



        Runtime runner = Runtime.getRuntime();

        Process p = runner.exec(exec.toString());



        StringBuffer sbOut = new StringBuffer(1000);

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while (true) {

          String s = br.readLine();

          if (s == null) {

            break;

          }

          System.out.println(s);

        }

        br.close();

        p.waitFor();

        System.out.println(sbOut.toString());

        System.out.println("Exit status: " + p.exitValue());

      }

    } catch (IOException ex) {

      System.err.println("Executer: IOException");

      ex.printStackTrace(System.out);

    } catch (InterruptedException ex) {

      System.err.println("Executer: InterruptedException");

      ex.printStackTrace(System.out);

    }





  }



}

