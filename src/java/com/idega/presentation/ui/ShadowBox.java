// idega 2000 - laddi
package com.idega.presentation.ui;

import com.idega.data.*;
import java.io.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.presentation.*;
import javax.servlet.http.*;
import java.util.*;

public class ShadowBox extends PresentationObjectContainer {

private String boxWidth = "100";
private String boxHeight = "100";
private String horizontalAlignment = "left";
private String verticalAlignment = "top";
private Table myTable;

public ShadowBox(){
	myTable = new Table(3,3);
  setDefaultValues();
	super.add(myTable);
}

	public void main(IWContext iwc) {
    initialize();
	}

	private void setDefaultValues() {
    boxWidth = "100";
    boxHeight = "100";
    horizontalAlignment = "left";
    verticalAlignment = "top";
	}

  private void initialize() {
		try {
      myTable.setBorder(0);
      myTable.setWidth(boxWidth);
      myTable.setHeight(boxHeight);
      myTable.setCellpadding(0);
      myTable.setCellspacing(0);
      myTable.setColor(2,2,"FFFFFF");
      myTable.setAlignment("center");
      myTable.setWidth(2,2,"100%");
      myTable.setHeight(3,"9");
      myTable.setHeight(1,"9");

      myTable.setAlignment(2,2,horizontalAlignment);
      myTable.setVerticalAlignment(2,2,verticalAlignment);

      myTable.add(new Image("/pics/jmodules/shadowBox/boxHorntoppV.gif"),1,1);
      myTable.setBackgroundImage(2,1,new Image("/pics/jmodules/shadowBox/boxTopphlid.gif"));
      myTable.addText("",2,1);
      myTable.add(new Image("/pics/jmodules/shadowBox/boxHorntoppH.gif"),3,1);

      myTable.setBackgroundImage(1,2,new Image("/pics/jmodules/shadowBox/boxVhlid.gif"));
      myTable.setColor(3,2,"#FFFFFF");

      myTable.add(new Image("/pics/jmodules/shadowBox/boxHornbotnV.gif"),1,3);
      myTable.setColor(2,3,"#FFFFFF");
      myTable.addText("",2,3);
      myTable.add(new Image("/pics/jmodules/shadowBox/boxHornbotnH.gif"),3,3);
		}
    catch (Exception e) {
    }
	}

	public void add(PresentationObject objectToAdd){
		myTable.add(objectToAdd,2,2);
	}

	public void add(String stringToAdd){
		myTable.add(stringToAdd,2,2);
	}

	public void setWidth(String boxWidth){
		this.boxWidth=boxWidth;
	}

	public void setWidth(int boxWidth){
		setWidth(Integer.toString(boxWidth));
	}

	public void setHeight(String boxHeight){
		this.boxHeight=boxHeight;
	}

	public void setHeight(int boxHeight){
		setHeight(Integer.toString(boxHeight));
	}

	public void setAlignment(String horizontalAlignment){
		this.horizontalAlignment=horizontalAlignment;
	}

	public void setVerticalAlignment(String verticalAlignment){
		this.verticalAlignment=verticalAlignment;
	}

  public Object clone() {
    ShadowBox obj = null;
    try {
      obj = (ShadowBox)super.clone();

      if (this.myTable != null) {
        obj.myTable=(Table)this.myTable.clone();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}
