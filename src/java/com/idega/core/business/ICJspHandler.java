package com.idega.core.business;



import java.sql.SQLException;

import com.idega.builder.data.IBJspPage;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;



/**

 * Title:        IC

 * Description:

 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved

 * Company:      idega margmiðlun

 * @author idega 2001 - <a href="mailto:idega@idega.is">idega team</a>

 * @version 1.0

 */



public class ICJspHandler {



  public ICJspHandler() {



  }



  public static int setICJspPage(String Url, String AttributeName, String AttributeValue) throws SQLException{

    IBJspPage page = ((com.idega.builder.data.IBJspPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBJspPage.class)).createLegacy();

    page.setUrl(Url);

    page.setAttributeName(AttributeName);

    page.setAttributeValue(AttributeValue);

    page.insert();

    return page.getID();

  }





  public static void setJspPageInstanceID(IWContext iwc, String AttributeName, String AttributeValue) throws SQLException{



    ICJspHandler.ICJspHandlerVariables variables;

    Object SessionObject = iwc.getSession().getAttribute("ICJspHandlerVariables");

    String Url = iwc.getRequest().getRequestURI();



    if (SessionObject == null){

      variables = (new ICJspHandler()).new ICJspHandlerVariables();

    }else{

      variables = (ICJspHandlerVariables)SessionObject;

    }



    if (variables.getUrl() != Url || variables.getAttributeName() != AttributeName || variables.getAttributeValue() != AttributeValue ){

      IBJspPage page = ((com.idega.builder.data.IBJspPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBJspPage.class)).createLegacy();



      IBJspPage Pages[] = (IBJspPage[])page.findAll("SELECT * FROM " + page.getEntityName() + " WHERE " + page.getUrlColumnName() + " = " + Url + " AND " + page.getAttributeNameColumnName() + " = " + AttributeName + " AND " + page.getAttributeValueColumnName() + " = " + AttributeValue );



      if (Pages == null){

        iwc.setSessionAttribute("JspPageInstanceID", new Integer(setICJspPage(Url, AttributeName, AttributeValue)));

      }else{

        iwc.setSessionAttribute("JspPageInstanceID", new Integer(Pages[0].getID()));

      }

      variables.setUrl(Url);

      variables.setAttributeName(AttributeName);

      variables.setAttributeValue(AttributeValue);

      iwc.getSession().setAttribute("ICJspHandlerVariables",variables);

    }



  }





  public static int getJspPageInstanceID(IWUserContext iwc){

    return ((Integer)iwc.getSessionAttribute("JspPageInstanceID")).intValue();

  }



  public static IBJspPage getIBJspPage(IWUserContext iwc) throws SQLException{

    return ((com.idega.builder.data.IBJspPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBJspPage.class)).findByPrimaryKeyLegacy(getJspPageInstanceID(iwc));

  }













  public class ICJspHandlerVariables {



    String URL;

    String Attribute_name;

    String Attribute_value;





    public ICJspHandlerVariables(){



    }





    public String getUrl(){

      return URL;

    }



    public String getAttributeName(){

      return Attribute_name;

    }



    public String getAttributeValue(){

      return Attribute_value;

    }





    public void setUrl(String url){

      URL = url;

    }



    public void setAttributeName(String AttributeName){

      Attribute_name =AttributeName;

    }



    public void setAttributeValue(String AttributeValue){

      Attribute_value =AttributeValue;

    }







  }  // inner Class ICJspHandlerVariables









} // class ICJspHandler
