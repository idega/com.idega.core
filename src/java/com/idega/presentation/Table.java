/*
 * $Id: Table.java,v 1.18 2002/03/09 19:22:00 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import com.idega.presentation.text.Text;
import com.idega.util.IWColor;
import com.idega.idegaweb.IWMainApplication;

import java.util.Iterator;

/**
 * A class to use for presentation of 2 dimensional (grid) layout.
 *
 *   Note: xpos is in [1:cols]
 *    ,ypos is in [1:rows]
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class Table extends PresentationObjectContainer {
  protected static Image transparentcell;
  protected static final String IW_BUNDLE_IDENTIFIER = "com.idega.core";

  protected PresentationObjectContainer theObjects[][];

  protected int cols = 0;
  protected int rows = 0;

  //Variables to hold coordinates of merge point of cells
  //Initialized only if needed
  protected Vector beginMergedxpos;
  protected Vector beginMergedypos;
  protected Vector endMergedxpos;
  protected Vector endMergedypos;
  protected boolean isResizable;

  protected boolean cellsAreMerged;

  protected static final String COLOR_ATTRIBUTE="bgcolor";


  protected boolean addLineTop = false;
  protected boolean addLinesBetween = false;
  protected boolean addLinesBottom = false;
  protected boolean addLineLeft = false;
  protected boolean addVerticalLinesBetween = false;
  protected boolean addLineRight = false;
  protected String lineColor = "#000000";
  protected String lineHeight = "1";
  protected String lineWidth = "1";
  protected int lineColspan = 0;
  protected int[] lineRows = new int[0];
  protected int[] lineCols = new int[0];


  protected static final String HTML_TR_START="\n<tr>";
  protected static final String HTML_TR_END = "\n</tr>";

  public static final String HUNDRED_PERCENT = "100%";
  public static final String VERTICAL_ALIGN_TOP = "top";
  public static final String VERTICAL_ALIGN_MIDDLE = "middle";
  public static final String VERTICAL_ALIGN_BOTTOM = "bottom";
  public static final String HORIZONTAL_ALIGN_LEFT = "left";
  public static final String HORIZONTAL_ALIGN_RIGHT = "right";
  public static final String HORIZONTAL_ALIGN_CENTER = "center";

  /**
   * Constructor that defaults with 1 column and 1 row
   */
  public Table() {
    this(1,1);
    //setWidth("100%");
    //setHeight("100%");
    isResizable = true;
  }

  /**
   * Constructor that takes in the initial rows and columns of the table
   */
  public Table(int cols, int rows) {
    super();
    isResizable = false;
    theObjects = new PresentationObjectContainer[cols][rows];
    this.cols = cols;
    this.rows = rows;
    setBorder("0");
    cellsAreMerged = false;
  }

  /**
  *Add an object inside this Table in cell with coordinates 1,1 - same as the add() function
  *@deprecated replaced by the add function
  */
  public void addObject(PresentationObject modObject){
    addObject( modObject,1,1);
  }

  /**
  *Add an object inside this Table in cell with coordinates 1,1
  */
  public void add(PresentationObject modObject){
    add( modObject,1,1);
  }



  public void addText(String theText,String format,int xpos,int ypos){
    if(isResizable){
      if(xpos > this.getColumns()){
        setColumns(xpos);
      }
      if(ypos > this.getRows()){
        setRows(ypos);
      }
    }
    if (theObjects[xpos-1][ypos-1] == null)
          {
                  theObjects[xpos-1][ypos-1] = new PresentationObjectContainer();
                  //super.add(theObjects);
          }

    theObjects[xpos-1][ypos-1].addText(theText,format);
  }

  /**
  *Add an object inside this Table in cell with coordinates x,y from top right
  */
  public void add(PresentationObject modObject,int xpos,int ypos){
    if (modObject != null) {
      try {
      if (isResizable) {
        if (xpos > this.getColumns()) {
          setColumns(xpos);
        }
        if(ypos > this.getRows()) {
          setRows(ypos);
        }
      }
      if (theObjects[xpos-1][ypos-1] == null)
                        {
                          theObjects[xpos-1][ypos-1] = new PresentationObjectContainer();
                          //super.add(theObjects);
                        }

      theObjects[xpos-1][ypos-1].add(modObject);
      modObject.setParentObject(this);

            }
            catch(Exception ex){
              add(new ExceptionWrapper(ex,this));
            }
          }

  }

  /**
  *Add an object inside this Table in cell with coordinates x,y from top right - same as the add() function
  *@deprecated replaced by the add function
  */
  public void addObject(PresentationObject modObject,int xpos,int ypos){
    add(modObject,xpos,ypos);
  }

  public void add(String text,int xpos,int ypos){
    addText(text,xpos,ypos);
  }

  public void addText(String theText,int xpos,int ypos){
    add(new Text(theText),xpos,ypos);
  }



  public void addText(int integerToInsert,int xpos,int ypos){
    addText(Integer.toString(integerToInsert),xpos,ypos);
  }

/**@todo : this must implemented in the print method...like in the Link class
 * IMPORTANT! for this to work you must have an application property called
 * IW_USES_OLD_MEDIA_TABLES   (set to anything)
 *
 */
  public void setBackgroundImage(int xpos, int ypos, Image backgroundImage){
    if(backgroundImage!=null){
      setBackgroundImageURL(xpos,ypos,getImageUrl(backgroundImage));
    }
  }
/**@todo : this must implemented in the print method...like in the Link class
 * IMPORTANT! for this to work you must have an application property called
 * IW_USES_OLD_MEDIA_TABLES   (set to anything)
 *
 */
  public void setBackgroundImage(Image backgroundImage){
    if(backgroundImage!=null){
      setBackgroundImageURL( getImageUrl(backgroundImage) );
    }
  }
/**@todo : replace this with a implementation in print
 * IMPORTANT! for this to work you must have an application property called
 * IW_USES_OLD_MEDIA_TABLES   (set to anything)
 *
 */
  protected String getImageUrl(Image image){

    if(image.getURL()!=null ){
      return image.getURL();
    }
    else{
      return image.getMediaServletString();
    }

  }

  public void setBackgroundImageURL(String backgroundImageURL){
    this.setAttribute("background",backgroundImageURL);
  }

  public void setBackgroundImageURL(int xpos, int ypos, String backgroundImageURL){
    this.setAttribute(xpos,ypos,"background",backgroundImageURL);
  }

  public void setVerticalAlignment(int xpos, int ypos, String alignment){
    setAttribute(xpos,ypos,"valign",alignment);
  }

  public void setAlignment(int xpos, int ypos, String alignment){
    setAttribute(xpos,ypos,"align",alignment);
  }

  public void setRowAlignment(int ypos, String alignment){
    setRowAttribute(ypos,"align",alignment);
  }

  public void setColumnAlignment(int xpos, String alignment){
    setColumnAttribute(xpos,"align",alignment);
  }

  public void setRowVerticalAlignment(int ypos, String alignment){
    setRowAttribute(ypos,"valign",alignment);
  }

  public void setColumnVerticalAlignment(int xpos, String alignment){
    setColumnAttribute(xpos,"valign",alignment);
  }

  public void resize(int columns,int rows){

    if(columns != cols || rows != this.rows){
      int minCols = Math.min(columns,cols);
      int minRows = Math.min(rows,this.rows);
      PresentationObjectContainer theNewObjects[][];
      theNewObjects = new PresentationObjectContainer[columns][rows];

      for (int x=0;x<minCols;x++){
        System.arraycopy(theObjects[x],0,theNewObjects[x],0,minRows);
      }
      theObjects=theNewObjects;
      this.cols=columns;
      this.rows=rows;
    }
  }

  /**
  *Empties the Table of all objects stored inside
  */
  public void empty(){
    for (int x=0;x<cols;x++){
      for(int y=0;y<rows;y++){
        if( theObjects[x][y] != null ){
          theObjects[x][y].empty();
        }

      }
    }
  }


  public void emptyCell(int xpos,int ypos){
        if( theObjects[xpos-1][ypos-1] != null ){
                theObjects[xpos-1][ypos-1].empty();
        }
  }

  public int getColumns(){
    return this.cols;
  }

  public void setColumns(int columns){
    resize(columns,this.rows);
  }

  public void setRows(int rows){
    resize(this.cols,rows);
  }

  public int getRows(){
    return this.rows;
  }

  public void mergeCells(int beginxpos,int beginypos, int endxpos, int endypos){
    if (beginMergedxpos == null && beginMergedypos == null && endMergedxpos == null && endMergedypos == null){
      beginMergedxpos = new Vector(1);
      beginMergedypos = new Vector(1);
      endMergedxpos = new Vector(1);
      endMergedypos = new Vector(1);
    }
    cellsAreMerged = true;

    //Do nothing if the either of the cells are already merged with something else
    if (! ( isInMergedCell(beginxpos,beginypos) && isInMergedCell(endxpos,endypos) ) ){
      beginMergedxpos.addElement( (Object) new Integer(beginxpos));
      beginMergedypos.addElement( (Object) new Integer(beginypos));
      endMergedxpos.addElement( (Object) new Integer(endxpos));
      endMergedypos.addElement( (Object) new Integer(endypos));
    }
  }

  public void setWidth(String s){
    setAttribute("width",s);
  }

  public void setWidth(int width){
    setWidth(Integer.toString(width));
  }

  public void setHeight(int height){
    setHeight(Integer.toString(height));
  }

  public String getWidth(){
    return getAttribute("width");
  }


  public String getHeight(){
    return getAttribute("height");
  }

  public void setHeight(String s){
    setAttribute("height",s);
  }

  public void setHeight(int xpos,int ypos, String height){
    setAttribute(xpos,ypos,"height",height);
    /*
    Image spacer = (Image) transparentcell.clone();
    spacer.setHeight(height);
    add(spacer,xpos,ypos);*/
  }

  public void setWidth(int xpos, int ypos, String width){
    setAttribute(xpos,ypos,"width",width);
  }

  public void setColor(String s){
    setAttribute("bgcolor",s);
  }

  public void setColor(IWColor s){
    setAttribute("bgcolor",s.getHexColorString());
  }

  public void setBorder(String border){
    setAttribute("border",border);
  }

  public void setBorderColor(String color) {
    setAttribute("bordercolor",color);
  }

  public void setBorder(int i){
    setBorder(Integer.toString(i));
  }

  /**
   * displays top and bottom edges
   */
  public void setFrameHsides() {
      setFrame("hsides");
  }

  /**
   * displays left and right edges
   */
  public void setFrameVsides() {
      setFrame("vsides");
  }

  /**
   * displays no border
   */
  public void setFrameVoid() {
      setFrame("void");
  }

  /**
   * displays top edge only
   */
  public void setFrameAbove() {
      setFrame("above");
  }

  /**
   * below: displays bottom edge only
   * border: displays all four sides (default)
   * box: displays all four sides (like border)
   * hsides: displays top and bottom edges
   * lhs: displays left edge only
   * rhs: displays right edge only
   * void: displays no border
   * vsides: displays left and right edges
   */
  public void setFrame(String frame) {
      setAttribute("FRAME",frame);
  }



  public void setCellspacing(int i){
    setCellspacing(Integer.toString(i));
  }

  public void setCellpadding(int i){
    setCellpadding(Integer.toString(i));
  }

  public void setCellspacing(String s){
    setAttribute("cellspacing",s);
  }

  public void setCellpadding(String s){
    setAttribute("cellpadding",s);
  }


  public void setColor(int xpos,int ypos, String s){
    setAttribute(xpos,ypos,COLOR_ATTRIBUTE,s);
  }

  public void setColor(int xpos,int ypos, IWColor s){
    setAttribute(xpos,ypos,COLOR_ATTRIBUTE,s.getHexColorString());
  }

  public void setRowColor(int ypos, String s){
    setRowAttribute(ypos,COLOR_ATTRIBUTE,s);
  }

  public void setColumnColor(int xpos,String s){
    setColumnAttribute(xpos,COLOR_ATTRIBUTE,s);
  }

  public void setWidth(int xpos,String s){
    setColumnAttribute(xpos,"width",s);
  }

  public void setHeight(int ypos, String s){
    setRowAttribute(ypos,"height",s);
  }

  public void setHeight(int ypos, int height){
    setRowAttribute(ypos,"height",String.valueOf(height));
  }

  public void setAlignment(String align){
    setAttribute("align",align);
  }

  public void setVerticalAlignment(String verticalAlignment){
    setAttribute("valign",verticalAlignment);
  }

  public void setColumnAttribute(int xpos, String attributeName,String attributeValue){
    for(int temp=1;temp<=rows;){
      setAttribute(xpos,temp,attributeName,attributeValue);
      temp++;
    }
  }

  public void setRowAttribute(int ypos, String attributeName,String attributeValue){
    for(int temp=1;temp<=cols;){
      setAttribute(temp,ypos,attributeName,attributeValue);
      temp++;
    }
  }

  public void setAttribute(int xpos, int ypos, String attributeName,String attributeValue){

    if(isResizable){
      if(xpos > this.getColumns()){
        setColumns(xpos);
      }
      if(ypos > this.getRows()){
        setRows(ypos);
      }
    }

    if(this.theObjects[xpos-1][ypos-1] == null){
      this.theObjects[xpos-1][ypos-1] = new PresentationObjectContainer();
                 // super.add(theObjects);
    }

    this.theObjects[xpos-1][ypos-1].setAttribute(attributeName,attributeValue);

  }

  /*Tells if a cell in a table is merged with another*/
  protected boolean isInMergedCell(int xpos, int ypos){
    boolean theReturn = false;
    boolean xcheck=false;
    boolean ycheck=false;

    if( ! cellsAreMerged){
      theReturn=false;
    }
    else
    {
      int i = 0;
      for (Enumeration e = beginMergedxpos.elements();e.hasMoreElements();){

        ycheck = false;
        xcheck = false;

        Integer temp1 = (Integer) e.nextElement();
        Integer temp2 = (Integer) endMergedxpos.elementAt(i);
        int xlength;

        Integer temp3 = (Integer) beginMergedypos.elementAt(i);
        Integer temp4 = (Integer) endMergedypos.elementAt(i);
        int ylength;

        int lowerx=0;
        int lowery=0;

        if (temp1.intValue()<=temp2.intValue()){
          lowerx = temp1.intValue();
          xlength = temp2.intValue() - temp1.intValue();
        }
        else{
          lowerx = temp2.intValue();
          xlength = temp1.intValue() - temp2.intValue();
        }

        if (temp3.intValue()<=temp4.intValue()){
          lowery = temp3.intValue();
          ylength = temp4.intValue() - temp3.intValue();
        }
        else{
          lowery = temp4.intValue();
          ylength = temp3.intValue() - temp4.intValue();
        }



        //Check the x coordinate
        if( xpos >= (lowerx) ){
          if(xpos <= (lowerx+xlength) ){
            xcheck=true;
          }
          else{
            xcheck=false;
          }
        }
        //Check the y coordinate
        if( ypos >= (lowery) ){
          if(ypos <= (lowery+ylength) ){
            ycheck=true;
          }
          else{
            ycheck=false;
          }
        }

        if ( xcheck && ycheck)
        {
          theReturn = true;
        }

        i++;
      }
    }
    return theReturn;
  }

  public void addBreak(int xpos,int ypos){
    Text myText = Text.getBreak();
    this.add(myText,xpos,ypos);
  }

  protected int getWidthOfMergedCell(int startxpos, int startypos){
    int returnint = 1;
    int i = 0;
    for (Enumeration e = beginMergedxpos.elements();e.hasMoreElements();){
      Integer temp1 = (Integer) e.nextElement();
      Integer temp2 = (Integer) endMergedxpos.elementAt(i);
      int xlength;

      Integer temp3 = (Integer) beginMergedypos.elementAt(i);
      Integer temp4 = (Integer) endMergedypos.elementAt(i);
      int ylength;

      int lowerx=0;
      int lowery=0;


      if (temp1.intValue()<=temp2.intValue()){
        lowerx = temp1.intValue();
        xlength = temp2.intValue() - temp1.intValue();
      }
      else{
        lowerx = temp2.intValue();
        xlength = temp1.intValue() - temp2.intValue();
      }

      if (temp3.intValue()<=temp4.intValue()){
        lowery = temp3.intValue();
        ylength = temp4.intValue() - temp3.intValue();
      }
      else{
        lowery = temp4.intValue();
        ylength = temp3.intValue() - temp4.intValue();
      }

      if (lowerx==startxpos && lowery==startypos){
        returnint = xlength+1;
      }
      i++;

    }
    return returnint;
  }

  protected int getHeightOfMergedCell(int startxpos, int startypos){
    int returnint = 1;

    int i = 0;
    for (Enumeration e = beginMergedxpos.elements();e.hasMoreElements();){
      Integer temp1 = (Integer) e.nextElement();
      Integer temp2 = (Integer) endMergedxpos.elementAt(i);
      int xlength;

      Integer temp3 = (Integer) beginMergedypos.elementAt(i);
      Integer temp4 = (Integer) endMergedypos.elementAt(i);
      int ylength;

      int lowerx=0;
      int lowery=0;


      if (temp1.intValue()<=temp2.intValue()){
        lowerx = temp1.intValue();
        xlength = temp2.intValue() - temp1.intValue();
      }
      else{
        lowerx = temp2.intValue();
        xlength = temp1.intValue() - temp2.intValue();

      }

      if (temp3.intValue()<=temp4.intValue()){
        lowery = temp3.intValue();
         ylength = temp4.intValue() - temp3.intValue();
      }
      else{
        lowery = temp4.intValue();
         ylength = temp3.intValue() - temp4.intValue();
      }

      if (lowerx==startxpos && lowery==startypos){
        returnint = ylength+1;
      }
      i++;
    }
    return returnint;
  }



  protected boolean isTopLeftOfMergedCell(int xpos, int ypos){
    boolean theReturn = false;

    if( ! cellsAreMerged){
      theReturn=false;
    }
    else
    {
      int i = 0;
      for (Enumeration e = beginMergedxpos.elements();e.hasMoreElements();){
        Integer temp1 = (Integer) e.nextElement();
        Integer temp2 = (Integer) endMergedxpos.elementAt(i);
        int xlength;

        Integer temp3 = (Integer) beginMergedypos.elementAt(i);
        Integer temp4 = (Integer) endMergedypos.elementAt(i);
        int ylength;

        int lowerx=0;
        int lowery=0;

        if (temp1.intValue()<=temp2.intValue()){
          lowerx = temp1.intValue();
          xlength = temp2.intValue() - temp1.intValue();

        }
        else{
          lowerx = temp2.intValue();
          xlength = temp1.intValue() - temp2.intValue();
        }

        if (temp3.intValue()<=temp4.intValue()){
          lowery = temp3.intValue();
          ylength = temp4.intValue() - temp3.intValue();
        }
        else{
          lowery = temp4.intValue();
          ylength = temp3.intValue() - temp4.intValue();
        }



        if ((lowerx==xpos) && (lowery == ypos)){
          theReturn=true;
        }

        i++;
      }
    }
    return theReturn;
  }

  /**
  *
  *Sets the table to be horizontally striped
  */
  public void setHorizontalZebraColored(String Color1, String Color2){
    int y = 1;
    boolean theEnd = false;
    while ( ! theEnd){
      setRowColor(y,Color1);
      y++;
      if (y>this.rows){
        theEnd=true;
      }
      else{
        setRowColor(y,Color2);
        y++;
        if (y>this.rows){
          theEnd=true;
        }
      }

    }
  }

  /**
  *
  *Sets the table to be vertically striped
  */
  public void setVerticalZebraColored(String Color1, String Color2){
    int x = 1;
    boolean theEnd = false;
    while ( ! theEnd){
      setColumnColor(x,Color1);
      x++;
      if (x>this.cols){
        theEnd=true;
      }
      else{
        setColumnColor(x,Color2);
        x++;
        if (x>this.cols){
          theEnd=true;
        }
      }

    }

  }

  /**
   * index lies from [0,xlength-1] , puts in yindex=0
   */
  public Object set(int index,PresentationObject o){
    return set(index,0,o);
  }


  /**
   * xindex lies from [0,xlength-1],yindex lies from [0,ylength-1]
   */
  public Object set(int xindex,int yindex, PresentationObject o){
    return set(xindex,0,0,o);
  }

  /**
   * xindex lies from 0,[xlength-1],yindex lies from [0,ylength-1],innercontainerindex lies from [0,lengthofcontainer-1]
   */
  public Object set(int xindex,int yindex, int innercontainerindex,PresentationObject o){
    //return set(index,0,o);
    PresentationObjectContainer moc = theObjects[xindex][yindex];
    if(moc==null){
      moc = new PresentationObjectContainer();
      theObjects[xindex][yindex]=moc;
    }
    return moc.set(innercontainerindex,o);
  }


  public List getAllContainedObjectsRecursive(){
    if(allObjects == null){
      List toReturn = null;
      List containedObjects = this.getAllContainingObjects();
      if(theObjects != null){
        toReturn = new Vector();
        toReturn.containsAll(containedObjects);
        Iterator iter = containedObjects.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          if(item instanceof PresentationObjectContainer){
            toReturn.add(item);
            //if(!toReturn.contains(item)){
              List tmp = ((PresentationObjectContainer)item).getAllContainedObjectsRecursive();
              if(tmp != null){
                toReturn.addAll(tmp);
              }
            //}
          }else{
            toReturn.add(item);
          }
        }
      }
      allObjects = toReturn;
    }
    return allObjects;
  }


  public List getAllContainingObjects(){
    Vector theReturn = new Vector();
    for (int x = 0;x<theObjects.length;x++){
      for(int y = 0; y < theObjects[x].length;y++){
        if (theObjects[x][y] != null){
          theReturn.addElement(theObjects[x][y]);
        }
      }
    }

    return theReturn;
  }

  //Prints out the no-breaking-space for cells
  protected void printNbsp(IWContext iwc, int xpos,int ypos){
    if (theObjects[xpos-1][ypos-1] != null){
      if (theObjects[xpos-1][ypos-1].isEmpty()){
        if( false){
          //not implemented
        }
        else{
          //print(Text.getNonBrakingSpace().getText());
          print("<img src=\""+transparentcell.getURL()+"\" width=\"1\" height=\"1\" alt=\"\">");
        }
      }

    }
    else{
      print("<img src=\""+transparentcell.getURL()+"\" width=\"1\" height=\"1\" alt=\"\" >");
      //print(Text.getNonBrakingSpace().getText());
    }
  }

  protected void printLine(IWContext iwc) throws Exception{
    //println("\n<tr>");
    println(this.getRowStartTag(iwc));
    //    for(int x=1;x<=cols;){
      //println("\n<td "+"height="+this.lineHeight+" colspan="+cols+" "+COLOR_ATTRIBUTE+"="+this.lineColor+" >");
      println("\n<td "+"height=\""+this.lineHeight+((lineColspan > 1)?("\" colspan=\""+lineColspan+"\" "):("\" "))+COLOR_ATTRIBUTE+"=\""+this.lineColor+"\" >");
      //if(!iwc.isOpera()){
        transparentcell._print(iwc);
        println("</td>");
      //} else {
        //println("</td>");  // ?????
      //}

//    }
    //println("</tr>");
    println(this.getRowEndTag(iwc));
  }

  protected void printVerticalLine(IWContext iwc) throws Exception{
    println("<td width=\""+this.lineWidth+"\" "+COLOR_ATTRIBUTE+"=\""+this.lineColor+"\" >");
    if(!iwc.isOpera()){
      transparentcell._print(iwc);
      println("</td>");
    } else {
      //println("</td>");  // ?????
    }
  }

  public void print(IWContext iwc) throws Exception{
    initVariables(iwc);
    this.transparentcell = getTransparentCell(iwc);
    //if( doPrint(iwc)){
      if (getLanguage().equals("HTML")){
        String theErrorMessage = getErrorMessage();
        if (theErrorMessage==null){
          //if (getInterfaceStyle().equals("something")){
          //}
          //else{
          StringBuffer printString = new StringBuffer();
          printString.append("<table ");
          printString.append(getAttributeString());
          printString.append(" >");
          println(printString.toString());
          if( ! cellsAreMerged){
            lineColspan = cols;
            if(addVerticalLinesBetween){
              lineColspan += (cols-1);
            } else {
              lineColspan += lineRows.length;
            }
            if(addLineLeft){
              lineColspan++;
            }
            if(addLineRight){
              lineColspan++;
            }

            if(addLineTop){
              printLine(iwc);
            }
            for(int y=1;y<=rows;){
              //println("\n<tr>");
              println(this.getRowStartTag(iwc));
              for(int x=1;x<=cols;){

                if(this.addLineLeft && x==1){
                  printVerticalLine(iwc);
                }

                if(theObjects[x-1][y-1] != null){
                  if ( theObjects[x-1][y-1].getAttributeString().indexOf("align") == -1 ) {
                    setAlignment(x,y,"left");
                  }
                  if ( theObjects[x-1][y-1].getAttributeString().indexOf("valign") == -1 ) {
                    setVerticalAlignment(x,y,"middle");
                  }

                  if (printString==null){
                    printString = new StringBuffer();
                  }
                  else{
                    printString.delete(0,printString.length());
                  }
                  printString.append("\n<td ");
                  printString.append(theObjects[x-1][y-1].getAttributeString());
                  printString.append(" >");
                  println(printString.toString());
                  theObjects[x-1][y-1]._print(iwc);
                  printNbsp(iwc,x,y);

                }
                else{
                  println("\n<td>");
                  printNbsp(iwc,x,y);
                }
                println("</td>");

                if((addLineRight && x==cols) || (addVerticalLinesBetween && x!=cols)){
                  printVerticalLine(iwc);
                } else {
                  for (int i = 0; i < lineCols.length; i++) {
                    if(x == lineCols[i]){
                      printVerticalLine(iwc);
                      break;
                    }
                  }
                }

                x++;
              }

              //println("\n</tr>");
              println(this.getRowEndTag(iwc));

              if(this.addLinesBetween && y!=rows){
                printLine(iwc);
              } else {
                for (int i = 0; i < lineRows.length; i++) {
                  if(y == lineRows[i]){
                    printLine(iwc);
                    break;
                  }
                }
              }

              y++;
            }

            if(this.addLinesBottom){
              printLine(iwc);
            }
          }
          else // if merged
          {
            for(int y=1;y<=rows;){
              //println("\n<tr>");
              println(this.getRowStartTag(iwc));
              for(int x=1;x<=cols;){

                if(isInMergedCell(x,y)){
                  if(isTopLeftOfMergedCell(x,y)){
                    if (theObjects[x-1][y-1] == null){
                      theObjects[x-1][y-1] = new PresentationObjectContainer();
                    }
                    if (printString==null){
                      printString = new StringBuffer();
                    }
                    else{
                      printString.delete(0,printString.length());
                    }
                    printString.append("\n<td ");
                    printString.append(theObjects[x-1][y-1].getAttributeString());
                    printString.append(" colspan=\"");
                    printString.append(getWidthOfMergedCell(x,y));
                    printString.append("\" rowspan=\"");
                    printString.append(getHeightOfMergedCell(x,y));
                    printString.append("\" >");
                    println(printString.toString());

                    theObjects[x-1][y-1]._print(iwc);
                    printNbsp(iwc,x,y);
                    println("</td>");

                  }
                }
                else{

                  if(theObjects[x-1][y-1] != null){
                    if (printString==null){
                      printString = new StringBuffer();
                    }
                    else{
                      printString.delete(0,printString.length());
                    }
                    printString.append("\n<td ");
                    printString.append(theObjects[x-1][y-1].getAttributeString());
                    printString.append(" >");
                    println(printString.toString());

                    theObjects[x-1][y-1]._print(iwc);
                    printNbsp(iwc,x,y);

                  }
                  else{
                    println("\n<td>");
                    printNbsp(iwc,x,y);
                  }
                  println("</td>");
                }
              x++;
              }

              println(this.getRowEndTag(iwc));
             // println("\n</tr>");
              y++;
            }
          }
          println("\n</table>");
        //}
      }
      else{
        println("<pre>");
        println("Exception:");
        println(theErrorMessage);
        println("</pre>");
      }
      }
      else if (getLanguage().equals("WML")){
        for(int y=1;y<=rows;){
          for(int x=1;x<=cols;){
            if(theObjects[x-1][y-1] != null){
              theObjects[x-1][y-1]._print(iwc);
            }
            x++;
          }
          y++;
        }
      }//end if (getLanguage(...
                  else{
                          for(int y=1;y<=rows;){
                                  for(int x=1;x<=cols;){
                                          if(theObjects[x-1][y-1] != null){
                                                  theObjects[x-1][y-1]._print(iwc);
                                          }
                                          x++;
                                  }
                                  y++;
                          }
                  }
            //}//end doPrint(iwc)
  }

  public int numberOfObjects(){
    if(theObjects!=null){
      return cols*rows;
    }
    else{
      return 0;
    }
  }

  public PresentationObject objectAt(int index){
    if(theObjects!=null){
      if (rows!=0){
        int x=Math.round(index/rows);
        int y=index-x*rows;
        return theObjects[x][y];
      }
      else{
        return null;
      }
    }
    else{
      return null;
    }
  }

  public boolean isEmpty(){
    if (theObjects != null){
      return false;
    }
    else{
      return true;
    }
  }

  public int[] getTableIndex(PresentationObject o) {
    if (theObjects == null)
      return(null);

    for (int i = 0; i < rows; i++)
      for (int j = 0; j < cols; j++) {
        PresentationObjectContainer cont = (PresentationObjectContainer)theObjects[j][i];
        if (cont != null) {
          int index = cont.getIndex(o);
          if (index > -1) {
            int ret[] = {i+1,j+1};
            return ret;
          }
        }
     }
    return(null);
  }


public boolean isEmpty(int x, int y){
  if (theObjects != null){
    if(theObjects[x-1][y-1] == null){
      return true;
    }else{
      return false;
    }
  }
  else{
    return true;
  }
}



  public void setProperty(String key, String values[]) {
    if (key.equalsIgnoreCase("border"))
      setBorder(Integer.parseInt(values[0]));
    else if (key.equalsIgnoreCase("width")) {
      setWidth(values[0]);
    }
    else if (key.equalsIgnoreCase("height")) {
      setHeight(values[0]);
    }
    else if (key.equalsIgnoreCase("columns")) {
      setColumns(Integer.parseInt(values[0]));
    }
    else if (key.equalsIgnoreCase("rows")) {
      setRows(Integer.parseInt(values[0]));
    }
  }


  public Object clone(IWContext iwc, boolean askForPermission) {
    Table obj = null;

    try {
      obj = (Table)super.clone(iwc, askForPermission);
      if (this.theObjects != null) {
        obj.theObjects=new PresentationObjectContainer[cols][rows];
          for (int x = 0;x<theObjects.length;x++){
                  for(int y = 0; y < theObjects[x].length;y++){
                          if (this.theObjects[x][y] != null){
                            obj.theObjects[x][y]=(PresentationObjectContainer)((PresentationObjectContainer)this.theObjects[x][y]).clone(iwc,askForPermission); // not _clone(m,a) because moduleObject is cunstructed in table class
                            obj.theObjects[x][y].setParentObject(obj);
                            //obj.theObjects[x][y].remove(NULL_CLONE_OBJECT);
                          }
                  }
          }
      }
      obj.cols = this.cols;
      obj.rows = this.rows;
      if (this.beginMergedxpos != null) {
        obj.beginMergedxpos = (Vector)this.beginMergedxpos.clone();
      }
      if (this.beginMergedypos != null) {
        obj.beginMergedypos = (Vector)this.beginMergedypos.clone();
      }
      if (this.endMergedxpos != null) {
        obj.endMergedxpos = (Vector)this.endMergedxpos.clone();
      }
      if (this.endMergedypos != null) {
        obj.endMergedypos = (Vector)this.endMergedypos.clone();
      }
      obj.isResizable = this.isResizable;
      obj.cellsAreMerged = this.cellsAreMerged;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

  public PresentationObjectContainer containerAt(int x,int y) {
    PresentationObjectContainer cont = null;
    try {
      cont = theObjects[x-1][y-1];
      if (cont == null) {
        cont = new PresentationObjectContainer();
        theObjects[x-1][y-1] = cont ;
        cont.setParentObject(this);
      }
    }
    catch(ArrayIndexOutOfBoundsException e) {}
    return cont;
  }


  public boolean remove(PresentationObject obj){
    if(theObjects!=null){
      for (int x = 0;x<theObjects.length;x++){
              for(int y = 0; y < theObjects[x].length;y++){
			if (theObjects[x][y] != null){
				if(theObjects[x][y].remove(obj)){
                                  return true;
                                }
			}
		}
	}
    }
    return false;
  }

  /**
   * Returns the color of a Cell.
   * Returns NULL if no color set
   */
  public String getColor(int xpos,int ypos){
    PresentationObjectContainer cont = theObjects[xpos-1][ypos-1];
    if(cont==null){
      return null;
    }
    else{
      return cont.getAttribute(COLOR_ATTRIBUTE);
    }
  }

  public void setResizable(boolean resizable){
    this.isResizable=resizable;
  }

  public static Image getTransparentCell(IWContext iwc){
    if(transparentcell==null){
      transparentcell = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER).getImage("transparentcell.gif");
    }
    return transparentcell;
  }

  /**
   *
   */
  public void lock(int xpos, int ypos) {
    PresentationObjectContainer cont = containerAt(xpos,ypos);
    if (cont != null)
      cont.lock();
  }

  /**
   *
   */
  public void unlock(int xpos, int ypos) {
    PresentationObjectContainer cont = containerAt(xpos,ypos);
    if (cont != null)
      cont.unlock();
  }

  /**
   *
   */
  public boolean isLocked(int xpos, int ypos) {
    PresentationObjectContainer cont = containerAt(xpos,ypos);
    if (cont != null)
      return(cont.isLocked());
    else
      return(true);
  }

  /**
   *
   */
  public void setLabel(String label, int xpos, int ypos) {
    PresentationObjectContainer cont = containerAt(xpos,ypos);
    if (cont != null)
      cont.setLabel(label);
  }

  /**
   *
   */
  public String getLabel(int xpos, int ypos) {
    PresentationObjectContainer cont = containerAt(xpos,ypos);
    if (cont != null)
      return(cont.getLabel());
    else
      return(null);
  }




  public void setLinesBetween(boolean value){
    addLinesBetween = value;
  }

  public void setTopLine(boolean value){
    addLineTop = value;
  }

  public void setBottomLine(boolean value){
    addLinesBottom = value;
  }

  public void setLineColor(String color){
    lineColor = color;
  }

  public void setLineHeight(String height){
    lineHeight = height;
  }

  public void setVerticatLinesBetween(boolean value){
    addVerticalLinesBetween = value;
  }

  public void setRightLine(boolean value){
    addLineRight = value;
  }

  public void setLeftLine(boolean value){
    addLineLeft = value;
  }

  public void setLineWidth(String width){
    lineWidth = width;
  }

  public void setLineFrame(boolean value){
    this.setLeftLine(value);
    this.setRightLine(value);
    this.setTopLine(value);
    this.setBottomLine(value);
  }

  /**
   * @deprecated: only for builderuse until handler has been implemented
   */
  public void setLineAfterRow(int row, boolean value){
    if(value){
      setLineAfterRow(row);
    } else {
      for (int i = 0; i < lineRows.length; i++) {
        if(lineRows[i] == row){
          lineRows[i] = -1;  // should decrease array to
        }
      }
    }
  }

  /**
   * @deprecated: only for builderuse until handler has been implemented
   */
  public void setLineAfterColumn(int column, boolean value){
    if(value){
      setLineAfterColumn(column);
    } else {
      for (int i = 0; i < lineCols.length; i++) {
        if(lineCols[i] == column){
          lineCols[i] = -1;  // should decrease array to
        }
      }
    }
  }



  public void setLineAfterRow(int row){
    // increase length
    int[] newLines = new int[lineRows.length+1];
    System.arraycopy(lineRows,0,newLines,0,lineRows.length);
    lineRows = newLines;
    // done
    lineRows[lineRows.length-1] = row;
  }

  public void setLineAfterColumn(int column){
    // increase length
    int[] newLines = new int[lineCols.length+1];
    System.arraycopy(lineCols,0,newLines,0,lineCols.length);
    lineCols = newLines;
    // done
    lineCols[lineCols.length-1] = column;
  }


  protected String getRowStartTag(IWContext iwc,int numberOfRow){
    return getRowStartTag(iwc);
  }


  protected String getRowEndTag(IWContext iwc,int numberOfRow){
    return getRowEndTag(iwc);
  }

  protected String getRowStartTag(IWContext iwc){
    return HTML_TR_START;
  }


  protected String getRowEndTag(IWContext iwc){
    return HTML_TR_END;
  }

}