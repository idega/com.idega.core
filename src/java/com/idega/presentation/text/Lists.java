//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.presentation.text;



import java.io.*;

import java.util.*;

import com.idega.presentation.*;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class Lists extends PresentationObjectContainer{



public static final String ARABIC_NUMBERS = "1";

public static final String LOWER_ALPHA = "a";

public static final String UPPER_ALPHA = "A";

public static final String LOWER_ROMAN = "i";

public static final String UPPER_ROMAN = "I";



public static final String DISC = "disc";

public static final String CIRCLE = "circle";

public static final String SQUARE = "square";



private boolean compact = false;

private boolean ordered = false;



private Image bullet;



  public Lists(){

    super();

  }



  public void setClass(String s){

    setAttribute("class",s);

  }



  public void setStyle(String s){

    setAttribute("style",s);

  }



  public void setType(String type) {

    setAttribute("type",type);

  }



  public void setCompact(boolean compact) {

    this.compact = compact;

  }



  public void setStartNumber(int number) {

    setAttribute("start",Integer.toString(number));

  }



  public void setListOrdered(boolean ordered) {

    this.ordered = ordered;

  }



  public void setBulletImage(Image image) {

    bullet = image;

  }



  private String getListType() {

    if ( ordered )

      return "OL";

    return "UL";

  }



  private void getBullet() {

    if ( bullet != null ) {

      /**todo use MediaBusiness.getMediaURL(fileid,iwma);**/

      String styleString = "list-style-image: url("+bullet.getMediaURL()+");";

      setStyle(styleString);

    }

  }



  public void print(IWContext iwc)throws Exception{


    getBullet();



    if (getLanguage().equals("HTML")){

      if ( !compact )

	println("<"+getListType()+" "+getAttributeString()+">");



      List theObjects = this.getAllContainingObjects();

      if ( theObjects != null ) {

	Iterator iter = theObjects.iterator();

	while (iter.hasNext()) {

	  PresentationObject item = (PresentationObject) iter.next();

	  if ( item instanceof Lists )

	    item._print(iwc);

	  else {

	    if ( compact ) {

	      StringBuffer buffer = new StringBuffer();

	      buffer.append("<LI");

	      if ( getAttribute("style") != null )

		buffer.append(" style=\""+getAttribute("style")+"\"");

	      buffer.append(">");

	      print(buffer.toString());

	    }

	    else

	      print("<LI>");

	    item._print(iwc);

	    println("</LI>");

	  }

	}

      }



      if ( !compact )

	println("</"+getListType()+">");

    }

    else if (getLanguage().equals("WML")){

      println("<ul>");

      super.print(iwc);

      println("</ul>");

    }

  }

}



