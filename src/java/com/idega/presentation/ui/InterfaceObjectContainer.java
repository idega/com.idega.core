//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class InterfaceObjectContainer extends PresentationObjectContainer{


public InterfaceObjectContainer(){
	setID();
}





  public synchronized Object clone() {
    InterfaceObjectContainer obj = null;
    try {
      obj = (InterfaceObjectContainer)super.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }



}

