//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

/**
 *
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*
*/
public class IWException extends Exception{


  public IWException(){
      super("IWException");
  }

  public IWException(String message){
      super("IWException: "+message);
  }

}