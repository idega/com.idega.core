package com.idega.idegaweb.project.business;

import com.idega.jmodule.object.*;
import com.idega.idegaweb.project.data.*;
import com.idega.data.EntityFinder;
import java.sql.SQLException;

/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author idega - team 2001
 * @version 1.0
 */

public class ProjectBusiness extends JModuleObject {

  private static String ProjectIdParameterString = "pm_project_id";
  private ProjectWebList webList;

  public ProjectBusiness() {
    webList = ProjectWebList.getStaticInstance();
  }


  public static String getProjectIdParameterString(){
    return ProjectIdParameterString;
  }

  public static void setCurrentProjectID(ModuleInfo modinfo, int project_id ){
    modinfo.setSessionAttribute(ProjectIdParameterString, Integer.toString(project_id));
  }

  public static int getCurrentProjectID(ModuleInfo modinfo){
    return Integer.parseInt((String)modinfo.getSessionAttribute(ProjectIdParameterString));
  }


  public ProjectWebList[] getWebList() throws SQLException {
    try {
      return (ProjectWebList[])(EntityFinder.findAll(webList).toArray((Object[])java.lang.reflect.Array.newInstance(new Object().getClass(),0)));
    }
    catch (NullPointerException ex) {
      return null;
    }
  }




}