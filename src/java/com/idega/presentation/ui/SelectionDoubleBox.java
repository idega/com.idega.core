//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;


import com.idega.presentation.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class SelectionDoubleBox extends InterfaceObjectContainer{


private SelectionBox leftBox;
private SelectionBox rightBox;
private GenericButton toTheRight;
private GenericButton toTheLeft;

public SelectionDoubleBox(){
	this("untitled");
}

public SelectionDoubleBox(String nameOfRightBox){
  this(nameOfRightBox+"_left",nameOfRightBox);
}

public SelectionDoubleBox(String nameOfRightBox,String headerOfLeftBox,String headerOfRightBox){
  this(nameOfRightBox+"_left",nameOfRightBox);
  this.getLeftBox().setTextHeading(headerOfLeftBox);
  this.getRightBox().setTextHeading(headerOfRightBox);
}



public SelectionDoubleBox(String nameOfLeftBox,String nameOfRightBox){
    leftBox = new SelectionBox(nameOfLeftBox);
    rightBox = new SelectionBox(nameOfRightBox);

    Table table = new Table(3,1);
    add(table);

    table.add(leftBox,1,1);

    toTheRight = new GenericButton("sdb_right",">>");
    toTheRight.setOnClick("move( this.form."+leftBox.getName()+", this.form."+rightBox.getName()+" )");
    table.add(toTheRight,2,1);

    table.addBreak(2,1);

    toTheLeft = new GenericButton("sdb_left","<<");
    toTheLeft.setOnClick("move( this.form."+rightBox.getName()+", this.form."+leftBox.getName()+" )");
    table.add(toTheLeft,2,1);

    table.add(rightBox,3,1);
}

public SelectionBox getLeftBox(){
  return leftBox;
}

public SelectionBox getRightBox(){
  return rightBox;
}

public GenericButton getLeftButton() {
  return toTheLeft;
}

public GenericButton getRightButton() {
  return toTheRight;
}

public void addToScripts(Script script) {
    script.addFunction("addOpt","function addOpt( list, val, text, idx, selected ) {  if( selected == null ) selected = false;  if( idx != null ) {          list.options[idx] = new Option( text, val, false, selected );  } else {          list.options[list.length] = new Option( text, val, false, selected );  }}");
    script.addFunction("move","function move( from, to ) {\n  var here =  from ;\n  var there =  to ;\n\n  if( here.selectedIndex != -1 && here.length > 0) {\n\n    for( h=0; h<here.length;h++) {\n      if( here.options[h].selected ) {\n        addOpt( there, here.options[h].value, here.options[h].text );\n      }\n    }\n    for( h1=here.length-1;h1>-1; h1-- ) {\n      if( here.options[h1].selected ) {\n        here.options[h1] = null;\n      }\n    }\n          \n  }\n}");
}
public void main(IWContext iwc)throws Exception{
    //this.getParentPage().initializeAssociatedScript();
    Script script = this.getParentPage().getAssociatedScript();
    addToScripts(script);
}



}
