package com.idega.util;

/**
 * Title:        idega default
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class Executer {

  public Executer() {
  }

  public static void main(String[] args){
    try{
      String command=args[0];
      Runtime.getRuntime().exec(command);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
  }


  public static void executeInAnotherVM(String[] args){
    try{
      Runtime.getRuntime().exec("java com.idega.util.Executer",args);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }

  }

}