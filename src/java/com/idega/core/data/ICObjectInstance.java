//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/

package com.idega.core.data;



//import java.util.*;

import java.sql.*;

import com.idega.data.*;
import com.idega.presentation.*;

import com.idega.core.business.ICObjectBusiness;


/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.3

*/

public class ICObjectInstance extends GenericEntity{



	public ICObjectInstance(){

		super();

	}



	public ICObjectInstance(int id)throws SQLException{

		super(id);

	}

  public static final String IBPAGEID = "ib_page_id";



	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...

		addAttribute(getIDColumnName());

		addManyToOneRelationship("ic_object_id","Module",ICObject.class);

        addManyToOneRelationship(IBPAGEID,"Page",com.idega.builder.data.IBPage.class);



	}



	public String getEntityName(){

		return "ic_object_instance";

	}



	public void setDefaultValues(){

		//setColumn("image_id",1);

	}



	public String getName(){

		return getObject().getName()+" nr. "+this.getID();

	}



        public void setICObjectID(int id){

          this.setColumn("ic_object_id",id);

        }



        public void setICObject(ICObject object){

          this.setColumn("ic_object_id",object);

        }

  public int getIBPageID(int id){
    return getIntColumnValue(IBPAGEID);
  }

  public void setIBPageID(int id){
    this.setColumn(IBPAGEID,id);
  }



	public ICObject getObject(){
        int icObjectID = this.getIntColumnValue("ic_object_id");
		return ICObjectBusiness.getInstance().getICObject(icObjectID);
	}



	public PresentationObject getNewInstance()throws ClassNotFoundException,IllegalAccessException,InstantiationException{

		return getObject().getNewInstance();

	}



	/*public IBObjectProperty[] getProperties(){

		IBObjectProperty[] array = new IBObjectProperty[0];

		try{

			array = (IBObjectProperty[])(new IBObjectProperty()).findAllByColumn(this.getIDColumnName(),this.getID());

		}

		catch(Exception ex){

			System.err.println("There was an error in IBObjectInstance: "+ex.getMessage());

		}

		return array;

	}*/







        /**

         * Unimplemented

         */

        /*public void addInstance(IBObjectInstance instanceToAdd)throws Exception{

          int id = instanceToAdd.getID();

          boolean checkIfOthers=false;

          IBObjectProperty[] properties = this.getProperties();

          for(int i=0;i<properties.length;i++){



            if(properties[i].getObjectInstanceID() == this.getID() && properties[i].getName().equals("idega_special_add")){

              checkIfOthers=true;

              IBObjectPropertyValue value = new IBObjectPropertyValue();

              value.setProperty(properties[i]);

              value.setPropertyValue(Integer.toString(id));

              value.insert();

              //theReturn = new IBObjectInstance[values.length];

              //for(int n=0;n<values.length;n++){

              //  theReturn[n] = new IBObjectInstance(Integer.parseInt(values[n].getPropertyValue()));

              //}





            }



            if(!checkIfOthers){

              IBObjectProperty property = new IBObjectProperty();

              property.setObjectInstance(this);

              property.setName("idega_special_add");

              property.insert();



              IBObjectPropertyValue value = new IBObjectPropertyValue();

              value.setProperty(property);

              value.setPropertyValue(Integer.toString(id));

              value.insert();



            }





          }





        }*/





        /**

         * Returns null if nothing found

         */

        public ICObjectInstance[] getContainingObjects()throws Exception{

          /*Connection conn = this.getConnection();

          Statement stmt = conn.createStatement();

          ResultSet RS = stmt.executeQuery("select ib_object_instance_id from ib_object_property where ib_object_property_name='idega_special_add' and ");

          while(RS.next()){



          }



          finally{

            if(conn!= null){

              freeConnection(conn);

            }

          }*/

          ICObjectInstance[] theReturn = null;



          /*IBObjectProperty[] properties = this.getProperties();

          for(int i=0;i<properties.length;i++){

            if(properties[i].getObjectInstanceID() == this.getID() && properties[i].getName().equals("idega_special_add")){

              IBObjectPropertyValue[] values = properties[i].getPropertyValues();

              theReturn = new IBObjectInstance[values.length];

              for(int n=0;n<values.length;n++){

                theReturn[n] = new IBObjectInstance(Integer.parseInt(values[n].getPropertyValue()));

              }

            }



          }*/

          return theReturn;

        }

}

