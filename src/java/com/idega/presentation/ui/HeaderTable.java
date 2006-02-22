// idega 2000 - laddi

package com.idega.presentation.ui;





import java.io.IOException;

import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;



public class HeaderTable extends PresentationObjectContainer {



private boolean leftHeader = true;

private boolean rightHeader = true;

private String headlineColor = "#FFFFFF";

private int headlineSize = 2;

private int headerSize = 21;

private boolean headerLeft = false;

private boolean headerRight = false;

private String headerText = "Header";

private String borderColor = "#000000";

private String headerFont = "Verdana, Arial, Helvetica, sans-serif";

private int tableWidth = 100;

private String tableColor = "FFFFFF";

private String headlineAlign = "center";



private Table contentTable;

private Table outlineTable;



public HeaderTable(){

	outlineTable = new Table(1,1);

	contentTable = new Table(1,2);

	super.add(contentTable);



}



	public void main(IWContext iwc) throws IOException {



		drawTables();

	}



	private void drawTables() throws IOException {



			contentTable.setCellpadding(0);

			contentTable.setCellspacing(0);

			contentTable.setColor("#FFFFFF");

			contentTable.setWidth(tableWidth);

                       // if( tableHeight!=0 ) contentTable.setHeight(tableHeight);



			outlineTable.setCellpadding(0);

			outlineTable.setCellspacing(1);

			outlineTable.setColor(borderColor);

			outlineTable.setColor(1,1,tableColor);

			outlineTable.setWidth("100%");



		Table headerTable = new Table(3,1);

			headerTable.setCellpadding(0);

			headerTable.setCellspacing(0);

			headerTable.setColor(borderColor);

			headerTable.setWidth(1,"17");

			headerTable.setWidth(3,"17");

			headerTable.setWidth("100%");

			headerTable.setHeight(String.valueOf(headerSize));

			headerTable.setVerticalAlignment(1,1,"top");

			headerTable.setVerticalAlignment(2,1,"middle");

			headerTable.setVerticalAlignment(3,1,"top");

			headerTable.setAlignment(1,1,"left");

			headerTable.setAlignment(2,1,headlineAlign);

			headerTable.setAlignment(3,1,"right");



			if ( leftHeader ) {

				headerTable.add(new Image("/pics/jmodules/headerTable/leftcorner.gif","",17,17),1,1);

			}



			Text header = new Text(headerText);



			if ( headerLeft ) {

				header = new Text("&nbsp;"+headerText);

				headerTable.empty();

				headerTable.mergeCells(1,1,2,1);

				headerTable.setWidth(1,"100%");

				headerTable.setVerticalAlignment(1,1,"middle");

				headerTable.add(header,1,1);

			}



			else if ( headerRight ) {

				header = new Text(headerText+"&nbsp;");

				headerTable.empty();

				headerTable.mergeCells(2,1,3,1);

				headerTable.setWidth(2,"100%");

				headerTable.setAlignment(2,1,"right");

				headerTable.add(header,2,1);

				headerTable.add(new Image("/pics/jmodules/headerTable/leftcorner.gif","",17,17),1,1);

			}



			else {

				headerTable.add(header,2,1);

			}



				header.setBold();

				header.setFontColor(headlineColor);

				header.setFontSize(headlineSize);

				header.setFontFace(headerFont);





			if ( rightHeader ) {

				headerTable.add(new Image("/pics/jmodules/headerTable/rightcorner.gif",""),3,1);

			}



		contentTable.add(headerTable,1,1);

		contentTable.add(outlineTable,1,2);



	}



	public void add(PresentationObject objectToAdd){

		outlineTable.add(objectToAdd,1,1);

	}



	public void setWidth(int tableWidth){

		this.tableWidth=tableWidth;

	}



        public void setHeight(int tableHeight){

	}



	public void setColor(String tableColor){

		this.tableColor=tableColor;

	}



	public void setHeadlineColor(String headlineColor){

		this.headlineColor=headlineColor;

	}



	public void setHeadlineSize(int headlineSize){

		this.headlineSize=headlineSize;

	}



    public void setLeftHeader(boolean leftHeader){

		this.leftHeader=leftHeader;

	}



    public void setHeadlineAlign(String headlineAlign){

                this.headlineAlign=headlineAlign;

        }



    public void setRightHeader(boolean rightHeader){

		this.rightHeader=rightHeader;

	}



    public void setHeadlineLeft(){

		this.headerLeft=true;

		this.leftHeader=false;

	}



    public void setHeadlineRight(){

		this.headerRight=true;

		this.rightHeader=false;

	}



    public void setHeaderText(String headerText){

		this.headerText=headerText;

	}



    public void setHeaderFont(String headerFont){

		this.headerFont=headerFont;

	}



    public void setHeaderSize(int headerSize){

		this.headerSize=headerSize;

	}



    public void setBorderColor(String borderColor){

		this.borderColor=borderColor;

	}



}

