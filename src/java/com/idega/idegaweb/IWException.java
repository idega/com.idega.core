/*
 * $Id: IWException.java,v 1.2 2001/05/14 14:27:27 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
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