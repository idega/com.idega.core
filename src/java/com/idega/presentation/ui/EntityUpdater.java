/*

 * $Id: EntityUpdater.java,v 1.4 2002/06/12 18:28:23 laddi Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.presentation.ui;





import com.idega.data.*;

import java.io.*;

import com.idega.presentation.text.*;

import com.idega.presentation.*;

import javax.servlet.http.*;

import java.sql.SQLException;

import java.util.*;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class EntityUpdater extends EntityManipulator{



private Table theTable;

private Form theForm;

private Parameter objectParameter;

private String action;

private int debug1;

private String buttonText="Update";



/**

**Constructor to handle an update of one row

**/

public EntityUpdater(IDOLegacyEntity entity){

	super();

	action="update";

	theForm = new Form();

	IDOLegacyEntity[] entityArr = (IDOLegacyEntity[])java.lang.reflect.Array.newInstance(entity.getClass(),1);

	entityArr[0]=entity;

	setEntity(entityArr);

}



/**

**Constructor to handle updates on one or many rows

**/

public EntityUpdater(IDOLegacyEntity[] entity){

	super();

	action="update";

	theForm = new Form();

	setEntity(entity);

}



public String getAction(){

	return action;

}









/*protected void addComponent(String columnName,int columnIndex,int rowIndex){

	if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

		TextInput tempInput = new TextInput(columnName,getEntity()[rowIndex-1].getStringColumnValue(columnName));

		tempInput.setSize(10);

		theTable.add(tempInput,columnIndex+1,rowIndex+1);

	}

	else{

		TextInput tempInput = new TextInput(columnName);

		tempInput.setSize(10);

		theTable.add(tempInput,columnIndex+1,rowIndex+1);

	}

}

*/











/*public void setInsertableList(IDOLegacyEntity entity){

	if (entity != null){



		initializeTable(entity.getVisibleColumnNames().length,3);





		for(int x = 0;x<entity.getVisibleColumnNames().length;x++){

			theTable.add(new Text(entity.getLongName(entity.getVisibleColumnNames()[x])),x+1,1);

		}



		for(int x = 0;x<entity.getVisibleColumnNames().length;x++){

			addComponent(entity.getVisibleColumnNames()[x],x,1);

		}



		theTable.add(new SubmitButton("insert"),entity.getVisibleColumnNames().length,3);

	}

}*/





public void setList(IDOLegacyEntity[] entity){

	setUpdatableList(entity);

}



public void setUpdatableList(IDOLegacyEntity[] entity){

	if (entity != null){

		if(entity.length > 0){

			initializeTable( entity[0].getVisibleColumnNames().length,entity.length+2);

			for(int y = 0;y<=entity.length;y++){

				if(y==0){

					for(int x = 0;x<entity[0].getVisibleColumnNames().length;x++){

						theTable.add(new Text(getEntity()[0].getLongName(entity[0].getVisibleColumnNames()[x])),x+1,y+1);

					}

				}

				else{

					for(int x = 0;x<entity[0].getVisibleColumnNames().length;x++){

						String columnName = entity[0].getVisibleColumnNames()[x];

						theTable.add(getComponent(columnName,y),x+1,y+1);

						//addComponent(getEntity()[0].getVisibleColumnNames()[x],x,y);



					}

				}

			}

			theTable.add(new SubmitButton("update"),entity[0].getVisibleColumnNames().length,entity.length+2);

		}



	}

}



public Parameter getObjectParameter(){

	if ( objectParameter == null){

		if (getEntity().length > 0){

			objectParameter = new Parameter("idega_special_form",getEntity()[0].getEntityName());

		}

	}

	return objectParameter;

}



public boolean thisObjectSubmitted(IWContext iwc){

	if(iwc.parameterEquals(getObjectParameter())){

		return true;

	}

	else{

		return false;

	}

}



private void initializeTable(){

	initializeTable(1,1);

}



private void initializeTable(int columns,int rows){



	theTable = new Table(columns,rows);

	theTable.setBorder(0);

	theTable.setRowColor(1,"D0D0D0");

	if (getEntity()[0] != null){

		theForm.add(getObjectParameter());

	}

	theForm.add(theTable);

}



protected void setVerticalInsertableList(IDOLegacyEntity entity){

	if (entity != null){



		initializeTable(2,entity.getVisibleColumnNames().length+1);





		for(int y = 0;y<entity.getVisibleColumnNames().length;y++){

			theTable.add(new Text(entity.getLongName(entity.getVisibleColumnNames()[y])),1,y+1);

		}



		for(int y = 0;y<entity.getVisibleColumnNames().length;y++){

			theTable.add(getComponent(entity.getVisibleColumnNames()[y],1),2,y+1);

		}



		theTable.setAlignment(2,entity.getVisibleColumnNames().length+1,"right");

		theTable.add(new SubmitButton(buttonText),2,entity.getVisibleColumnNames().length+1);

	}

}









/*private void handleInsert(IWContext iwc)throws SQLException{

	HttpServletRequest request = iwc.getRequest();

	HttpServletResponse response = iwc.getResponse();

	int i=0;

	String[] columns = getEntity()[i].getVisibleColumnNames();



	for(int n = 0;n<columns.length;n++){

		//request parameter not empty

		if (!(request.getParameterValues(columns[n])[i].equals(""))){

			getEntity()[i].setStringColumn(columns[n],request.getParameterValues(columns[n])[i]);

		}

	}

	getEntity()[i].insert();



}*/



private void handleUpdate(IWContext iwc)throws SQLException{

	HttpServletRequest request = iwc.getRequest();

	HttpServletResponse response = iwc.getResponse();

	for (int i=0;i<getEntity().length;i++){

		boolean entityChanged=false;

		String[] columns = getEntity()[i].getVisibleColumnNames();

			for(int n = 0;n<columns.length;n++){

				//request parameter not empty

				if (!(request.getParameterValues(columns[n])[i].equals(""))){

					if (!(request.getParameterValues(columns[n])[i].equals(getEntity()[i].getStringColumnValue(columns[n])))){

						entityChanged=true;

						getEntity()[i].setStringColumn(columns[n],request.getParameterValues(columns[n])[i]);

					}

				}

				//request parameter empty - ""

				else{

					if( getEntity()[i].getStringColumnValue(columns[n]) != null ){

						entityChanged=true;

						getEntity()[i].setStringColumn(columns[n],request.getParameterValues(columns[n])[i]);

					}

				}

			}



		if (entityChanged){

			getEntity()[i].update();

		}

	}

}



public void theMain(IWContext iwc)throws IOException{

	HttpServletRequest request = iwc.getRequest();

	HttpServletResponse response = iwc.getResponse();

	if (thisObjectSubmitted(iwc)){

		PresentationObjectContainer cont = (PresentationObjectContainer) request.getSession().getAttribute("idega_special_editablelist_parameters");

		if (cont != null){

			theForm.add(cont);

		}

		try{

			if (getAction().equals("update")){

				handleUpdate(iwc);

			}

			/*else if (getAction().equals("insert")){

				handleInsert(iwc);

			}*/

		}

		catch(SQLException ex){

			throw new IOException(ex.getMessage());

		}

	}

	else{

		PresentationObjectContainer cont = new PresentationObjectContainer();

		for (Enumeration enum = request.getParameterNames();enum.hasMoreElements();){

			String tempString = (String)enum.nextElement();

			cont.add(new Parameter(tempString,request.getParameter(tempString)));

		}

		theForm.add(cont);

		request.getSession().setAttribute("idega_special_editablelist_parameters",cont);

	}

}



public void beforePrint(IWContext iwc)throws IOException{

	if(getAction().equals("update")){

                IDOLegacyEntity[] entity = getEntity();

		if(entity.length>1){

                  setUpdatableList(entity);

                }

                else{

                  setVerticalInsertableList(entity[0]);

                }



        }

	/*else if (getAction().equals("insert")){

		//setInsertableList(getEntity()[0]);

		setVerticalInsertableList(getEntity()[0]);

	}*/

}



public void print(IWContext iwc)throws Exception{


	theMain(iwc);

	beforePrint(iwc);

	if(theForm != null){

		theForm._print(iwc);

	}

}







}

