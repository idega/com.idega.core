//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.presentation.ui;





import java.io.*;

import java.util.*;

import com.idega.data.*;

import com.idega.presentation.*;

import com.idega.presentation.text.*;

import java.sql.*;

import javax.servlet.http.*;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class EntityManipulator extends InterfaceObjectContainer{



private IDOLegacyEntity[] entity;

private Hashtable displayColumns;







public EntityManipulator(){

	super();

}



public EntityManipulator(IDOLegacyEntity[] entity){

	super();

	setEntity(entity);

}





public EntityManipulator(IDOLegacyEntity entity){



	IDOLegacyEntity[] entityArr = (IDOLegacyEntity[])java.lang.reflect.Array.newInstance(entity.getClass(),1);

	entityArr[0]=entity;

	setEntity(entityArr);

}





public void setEntity(IDOLegacyEntity[] entity){

	this.entity=entity;

}





public IDOLegacyEntity[] getEntity(){

	return entity;

}



public IDOLegacyEntity getFirstEntity(){

	return entity[0];

}











/**

*Return the appropriate component and its settings for a column in an entity

**/

public PresentationObject getComponent(String columnName,int rowIndex){



	PresentationObject theReturn = null;



	if (getEntity()[rowIndex-1].getRelationShipType(columnName).equals("one-to-very-many")){



		Window window = new Window();



		IDOLegacyEntity entity = (IDOLegacyEntity)getEntity()[rowIndex-1].getColumnValue(columnName);

		Form form = new Form(window);





		if (entity == null){

			form.add("Ekkert vali&eth;");

		}

		else{

			form.add(entity.getName());

		}





		form.add(new SubmitButton("Velja"));

		theReturn = form;



	}

	else{



          String storageClassName = getEntity()[rowIndex-1].getStorageClassName(columnName);



		if (storageClassName.equals("java.lang.Integer")){

			if(getEntity()[rowIndex-1].getRelationShipClass(columnName)==null){

				if( ! getEntity()[rowIndex-1].isNull(columnName)){

					IntegerInput tempInput = new IntegerInput(columnName,getEntity()[rowIndex-1].getIntColumnValue(columnName));

					tempInput.setSize(10);

					theReturn = (PresentationObject) tempInput;

				}

				else{

					IntegerInput tempInput = new IntegerInput(columnName);

					tempInput.setSize(10);

					theReturn = (PresentationObject)  tempInput;

				}

			}

			else{

                              DropdownMenu tempInput=null;

                              try{

                                  IDOLegacyEntity entity = (IDOLegacyEntity)getEntity()[rowIndex-1].getRelationShipClass(columnName).newInstance();

                                  List list = getColumnValueRange(columnName);

                                  if(list==null){

                                    tempInput = new DropdownMenu(entity.findAll());

                                  }

                                  else{

                                    tempInput = new DropdownMenu(list);

                                  }



                                  tempInput.setName(columnName);

                                  tempInput.setSelectedElement(getEntity()[rowIndex-1].getStringColumnValue(columnName));

                                  theReturn = (PresentationObject) tempInput;

                                }

                                catch(Exception ex){

                                  ex.printStackTrace(System.err);

                                }



				if( ! getEntity()[rowIndex-1].isNull(columnName)){

					try{

						//IDOLegacyEntity entity = (IDOLegacyEntity)Class.forName(getEntity()[rowIndex-1].getRelationShipClassName(columnName)).newInstance();

						//DropdownMenu tempInput = new DropdownMenu(entity.findAll());

						//tempInput.setName(columnName);

						tempInput.setSelectedElement(getEntity()[rowIndex-1].getStringColumnValue(columnName));

						//theReturn = (PresentationObject) tempInput;

					}

					catch(Exception ex){

                                            ex.printStackTrace(System.err);

					}

				}

				/*else{

					try{

						IDOLegacyEntity entity = (IDOLegacyEntity)Class.forName(getEntity()[rowIndex-1].getRelationShipClassName(columnName)).newInstance();

						DropdownMenu tempInput = new DropdownMenu(entity.findAll());

						tempInput.setName(columnName);

						theReturn = (PresentationObject) tempInput;

					}

					catch(Exception ex){

						ex.printStackTrace(System.err);

					}

				}*/

			}

		}

		else if (storageClassName.equals("java.lang.String")){

			if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

				TextInput tempInput = new TextInput(columnName,getEntity()[rowIndex-1].getStringColumnValue(columnName));

				tempInput.setSize(30);

				theReturn = (PresentationObject) tempInput;

			}

			else{

				TextInput tempInput = new TextInput(columnName);

				tempInput.setSize(30);

				theReturn =  (PresentationObject) tempInput;

			}



		}

		else if (storageClassName.equals("java.lang.Boolean")){

			/*CheckBox box = new CheckBox(columnName,"Y");

			if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

				if (getEntity()[rowIndex-1].getBooleanColumnValue(columnName) == true){

					box.setChecked(true);

				}

			}

			theReturn = (PresentationObject) box;*/

                        BooleanInput drop = new BooleanInput(columnName);

			//DropdownMenu drop = new DropdownMenu(columnName);

			//drop.addMenuElement("N","Nei");

			//drop.addMenuElement("Y","Já");

			if(! getEntity()[rowIndex-1].isNull(columnName)){

				/*if (getEntity()[rowIndex-1].getBooleanColumnValue(columnName) == true){

					drop.setSelectedElement("Y");



                                }

                                else{



                                }*/

                                drop.setSelected(getEntity()[rowIndex-1].getBooleanColumnValue(columnName));

			}



			theReturn = (PresentationObject) drop;

		}

		else if (storageClassName.equals("java.lang.Float")){

			if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

				FloatInput tempInput = new FloatInput(columnName,getEntity()[rowIndex-1].getFloatColumnValue(columnName));

				tempInput.setSize(10);

				theReturn = (PresentationObject) tempInput;

			}

			else{

				FloatInput tempInput = new FloatInput(columnName);

				tempInput.setSize(10);

				theReturn = (PresentationObject) tempInput;

			}

		}

		else if (storageClassName.equals("java.lang.Double")){

			if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

				FloatInput tempInput = new FloatInput(columnName,getEntity()[rowIndex-1].getFloatColumnValue(columnName));

				tempInput.setSize(10);

				theReturn = (PresentationObject) tempInput;

			}

			else{

				FloatInput tempInput = new FloatInput(columnName);

				tempInput.setSize(10);

				theReturn = (PresentationObject) tempInput;

			}

		}

		else if (storageClassName.equals("java.sql.Timestamp")){

			if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

				TimestampInput tempInput = new TimestampInput(columnName);

				tempInput.setTimestamp((Timestamp)getEntity()[rowIndex-1].getColumnValue(columnName));

				theReturn = (PresentationObject) tempInput;

			}

			else{

				TimestampInput tempInput = new TimestampInput(columnName);

				theReturn = (PresentationObject) tempInput;

			}

		}

		else if (storageClassName.equals("java.sql.Date")){

			if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

				DateInput tempInput = new DateInput(columnName);

				tempInput.setDate((java.sql.Date)getEntity()[rowIndex-1].getColumnValue(columnName));

				theReturn = (PresentationObject) tempInput;

			}

			else{

				DateInput tempInput = new DateInput(columnName);

				theReturn = (PresentationObject) tempInput;

			}

		}

		else if (storageClassName.equals("java.sql.Time")){

			if(getEntity()[rowIndex-1].getStringColumnValue(columnName) != null){

				TimeInput tempInput = new TimeInput(columnName);

				tempInput.setTime((java.sql.Time)getEntity()[rowIndex-1].getColumnValue(columnName));

				theReturn = (PresentationObject) tempInput;

			}

			else{

				TimeInput tempInput = new TimeInput(columnName);

				theReturn = (PresentationObject) tempInput;

			}

		}

		else if (storageClassName.equals("com.idega.util.Gender")){

			String gender = getEntity()[rowIndex-1].getStringColumnValue(columnName);

                        if(gender != null){

                                DropdownMenu menu = new DropdownMenu(columnName);

                                menu.addMenuElement("M","Karlkyns");

                                menu.addMenuElement("F","Kvenkyns");



                                if(gender.equalsIgnoreCase("M")){

                                  menu.setSelectedElement("M");

                                }

                                else{

                                  menu.setSelectedElement("F");

                                }



                                //menu.addMenuElement("F","Hvort tveggja");

				theReturn = (PresentationObject) menu;

			}

			else{

                                DropdownMenu menu = new DropdownMenu(columnName);

                                menu.addMenuElement("M","Karlkyns");

                                menu.addMenuElement("F","Kvenkyns");

                                //menu.addMenuElement("F","Hvort tveggja");

				theReturn = (PresentationObject) menu;

			}

		}



	}



	return theReturn;



}



/*public void setColumnValue(String columnName,Object columnValue){

  setColumnValue(columnName,columnValue,0);

}



public void setColumnValue(String columnName,Object columnValue,int entityArrayIndex){



  if (columnValue instanceof List){

    if(displayColumns==null){

      displayColumns = new Hashtable();

    }

    displayColumns.put(columnName,columnValue);

  }

  else{

    this.getEntity()[entityArrayIndex].setColumn(columnName,columnValue);

  }

}





public Object getSetColumnValue(String columnName){

  if(displayColumns==null){

     return this.getFirstEntity().getColumnValue(columnName);

  }

  else{

    Object obj = displayColumns.get(columnName);

    if(obj==null){

      return this.getFirstEntity().getColumnValue(columnName);

    }

    else{

      return obj;

    }

  }

}*/





/**

 * Sets a valid range for selection in referential columns as a list of IDOLegacyEntity objects i.e. columns that references other tables

 */

public void setColumnValueRange(String columnName,List entityList){

    if(displayColumns==null){

      displayColumns = new Hashtable();

    }

    displayColumns.put(columnName,entityList);

}





/**

 * gets the valid range for selection in referential columns as a list of IDOLegacyEntity objects i.e. columns that references other tables. Returns null if there is no constraint

 */

public List getColumnValueRange(String columnName){

  if(displayColumns==null){

     return null;

  }

  else{

    List obj = (List)displayColumns.get(columnName);

    return obj;

  }

}



}

