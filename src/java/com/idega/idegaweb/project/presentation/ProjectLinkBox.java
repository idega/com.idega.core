package com.idega.idegaweb.project.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.Link;
import com.idega.idegaweb.project.business.ProjectBusiness;
import com.idega.idegaweb.project.data.ProjectWebList;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */

public class ProjectLinkBox extends JModuleObject {

  private ProjectBusiness business;
  private Link[] projectLinks;
  private Link LinkObject; // never visible, used to access getClass()

  public ProjectLinkBox() {
    business = new ProjectBusiness();
    LinkObject = new Link();
  }


  private Link[] getProjectLinks() throws SQLException {
    ProjectWebList[] list = business.getWebList();
    Vector LinkVector = new Vector();

    // fails if list is null
    try {
      for (int i = 0; i < list.length; i++) {
        LinkVector.add(i,new Link(list[i].getName()));
        ((Link)LinkVector.lastElement()).addParameter(ProjectBusiness.getProjectIdParameterString(),list[i].getID());
      }

    }
    catch (NullPointerException  ex) {
      return null;
    }

    // fails if LinkVector is null?
    try {
      return (Link[])LinkVector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
    }
    catch (Exception ex) {
      return null;
    }


  }



  public void main(ModuleInfo modinfo) throws Exception {
    this.empty();
    projectLinks = this.getProjectLinks();

    if (projectLinks != null){
      for (int i = 0; i < projectLinks.length; i++) {
        add(projectLinks[i]);
      }
    }

  }

}// Class ProjectLinkBox