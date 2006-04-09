//idega 2001 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.presentation;



//import com.idega.jmodule.*;






/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class IWApplication extends Page{



  private String applicationName;



  public void setApplicationName(String applicationName){

    this.applicationName=applicationName;

  }



  public String getApplicationName(){

    return this.applicationName;

  }



  public Image getIcon(){

    return new Image("");

  }



}//End class

