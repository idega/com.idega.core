//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.presentation.ui;



import java.io.*;

import java.util.*;

import com.idega.presentation.*;

import com.idega.presentation.text.*;

/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*UNIMPLEMENTED

*/



public class Panel extends InterfaceObjectContainer{



private Table table;

private static String imagePrefix = "/common/pics/interfaceobject/FramePane/";



public Panel(){

}



public void add(PresentationObject obj){

  table.add(obj,2,2);

}





public void setWidth(int width){

  table.setWidth(width);

}



public void setHeight(int height){

  table.setHeight(height);

}



}
