package com.idega.presentation;

/**

 * Title:        JForum<p>

 * Description:  <p>

 * Copyright:    Copyright (c) idega 2000 - idega team - <a href="mailto:gummi@idega.is">gummi@idega.is</a><p>

 * Company:      idega margmiðlun hf.<p>

 * @author idega 2000 - idega team - <a href="mailto:gummi@idega.is">gummi@idega.is</a>

 * @version 1.0

 */



import java.util.*;



public class ListTable extends Table {



//  protected boolean leftTOright;

//  protected boolean topTObottom;

//  protected boolean finalRows;

  protected int tableRows;

  protected int tableColumns;

  protected int rowToAddIn;

  protected int colToAddIn;



  public ListTable() {

    super();

    tableRows = 1;

    tableColumns = 1;

    rowToAddIn = 1;

    colToAddIn = 1;

  }







  public ListTable( Vector ofString, int numberOfColumns ){

    super();

    tableRows = 1;

    tableColumns = 1;

    rowToAddIn = 1;

    colToAddIn = 1;

    doTable(ofString, numberOfColumns );

  }





  public ListTable( String[] text, int numberOfColumns ){

    super();

    tableRows = 1;

    tableColumns = 1;

    rowToAddIn = 1;

    colToAddIn = 1;

    doTable(text, numberOfColumns );

  }



  public ListTable( PresentationObject[] obj, int numberOfColumns ){

    super();

    tableRows = 1;

    tableColumns = 1;

    rowToAddIn = 1;

    colToAddIn = 1;

    doTable(obj, numberOfColumns );

  }





  public void doTable(Vector ofString, int numberOfColumns ){

    int vectorSize = ofString.size();

    int Rows = vectorSize/numberOfColumns;



    if (vectorSize%numberOfColumns > 0)

      Rows++;



    this.resize(numberOfColumns, Rows);



    for (int i = 0; i < vectorSize; i++ ){

        this.addText((String)ofString.elementAt(i));

    }

  }



  public void doTable(String[] text, int numberOfColumns ){

    int arraySize = text.length;

    int Rows = arraySize/numberOfColumns;



    if (arraySize%numberOfColumns > 0)

      Rows++;



    this.resize(numberOfColumns, Rows);



    for (int i = 0; i < arraySize; i++ ){

        this.addText(text[i]);

    }

  }



  public void doTable(PresentationObject[] obj, int numberOfColumns ){

    int arraySize = obj.length;

    int Rows = arraySize/numberOfColumns;



    if (arraySize%numberOfColumns > 0)

      Rows++;



    this.resize(numberOfColumns, Rows);



    for (int i = 0; i < arraySize; i++ ){

        this.add(obj[i]);

    }

  }







/*

  public void doTable(Vector ofString, String colORrows, int numberOf_ ){

    int vectorSize = ofPresentationObjects.size();

    int RowCol = vectorSize/numberOf_;



    if (vectorSize%numberOf_ > 0)

      RowCol++;



    boolean Rows;

    if ( colORrows.equals("rows") || colORrows.equals("Rows") || colORrows.equals("row") ||colORrows.equals("Row") )

      finalRows = true;

    else

      finalRows = false;



    this.empty();



    if (finalRows)

      this.resize(numberOf_, RowCol);

    else

      this.resize( RowCol, numberOf_);





    for (int i = 0; i < vectorSize; i++ ){

//      try{

        this.addText((String)ofPresentationObjects.elementAt(i));

//      }catch(){     // ef ekki moduleObject þá er hlaupið yfir reitinn



//      }

    }

  }

*/





  public void resize(int columns,int rows){

    super.resize(columns, rows);

    tableRows = rows;

    tableColumns = columns;

  }



  public void empty(){

    super.empty();

    rowToAddIn = 1;

    colToAddIn = 1;

  }



  protected void nextCell(){

    colToAddIn++;

    if (colToAddIn > tableColumns){

      colToAddIn = 1;

      rowToAddIn++;

    }

    if(rowToAddIn > tableRows)

      this.resize(tableColumns, rowToAddIn);



  }



  public void add(PresentationObject obj){

    nextCell();

    this.add(obj, rowToAddIn, colToAddIn);

  }





  public void addText(String text){

    nextCell();

    this.addText(text, rowToAddIn, colToAddIn);

  }







  public void setRowPatternColor( String[] colors){

    int length = colors.length;

    String nextColor;

    for(int i = 1; i <= tableRows; i++){

      nextColor = colors[i%length];

      if (nextColor != null){

      this.setRowColor(i, nextColor);

      }

    }

  }





  public void setRowPatternHeight( String[] height){

    int length = height.length;

    String nextHeight;

    for(int i = 1; i <= tableRows; i++){

      nextHeight = height[i%length];

      if (nextHeight != null){

      this.setHeight(i, nextHeight);

      }

    }

  }







} // Class ListTable
