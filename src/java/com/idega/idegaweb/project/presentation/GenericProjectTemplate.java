package com.idega.idegaweb.project.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.idegaweb.template.TemplatePage;
import com.idega.idegaweb.project.business.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia - iceland
 * @author
 * @version 1.0
 */

public class GenericProjectTemplate extends TemplatePage {


    public Table mainTable;
    public Table headerTable;
    public Table headerStripe;

    private Link linkCommunications;
    private Link linkCalendar;
    private Link linkParticipants;
    private Link linkHandbook;
    private Link linkTimesheets;
    private Link linkHome;

    private ProjectWebPresentation projectObject;
    private ProjectWebProperties properties;


    public GenericProjectTemplate(){
      super();
      init();
      this.setMarginHeight(0);
      this.setMarginWidth(0);
      this.setLeftMargin(0);
      this.setTopMargin(0);
      this.setAlinkColor("black");
      this.setVlinkColor("black");
      this.setLinkColor("black");
    }

    public void initProjectObject(ModuleInfo modinfo){
      if (modinfo.getSessionAttribute("pw_ProjectObject") == null){
        ProjectWebPresentation obj = new ProjectWebPresentation();
        modinfo.setSessionAttribute("pw_ProjectObject" , obj );
      }
      projectObject = (ProjectWebPresentation)modinfo.getSessionAttribute("pw_ProjectObject");
    }

    public void initProjectWebProperties(ModuleInfo modinfo){
      if (modinfo.getSessionAttribute("pw_ProjectProperties") == null){
        ProjectWebProperties obj = new ProjectWebProperties();
        modinfo.setSessionAttribute("pw_ProjectProperties" , obj );
      }
      properties = (ProjectWebProperties)modinfo.getSessionAttribute("pw_ProjectProperties");
    }

    public Table getMainTable(ModuleInfo modinfo){
      this.initMainTable(modinfo);
      return mainTable;
    }

    public Table getHeaderTable(ModuleInfo modinfo){
      this.initHeaderTable(modinfo);
      return headerTable;
    }

    public Table getHeaderStripe(ModuleInfo modinfo){
      this.initHeaderStripe(modinfo);
      return headerStripe;
    }

    public void initMainTable(ModuleInfo modinfo){
//      mainTable.setBorder(1);
      mainTable.setCellpadding(0);
      mainTable.setCellspacing(0);
      mainTable.setHeight(0);
      mainTable.setWidth("100%");
      mainTable.setWidth(1,1,"0");
      mainTable.setWidth(2,1,"100%");
      mainTable.setWidth(3,1,"177");

      mainTable.setAlignment(1,1,"left");
      mainTable.setAlignment(2,1,"left");
      mainTable.setAlignment(3,1,"right");
      mainTable.setVerticalAlignment(1,1,"top");
      mainTable.setVerticalAlignment(2,1,"top");
      mainTable.setVerticalAlignment(3,1,"top");
    }

    public void initHeaderTable(ModuleInfo modinfo){
//      headerTable.setBorder(1);
      headerTable.setCellpadding(0);
      headerTable.setCellspacing(0);
      headerTable.setHeight(0);
      headerTable.setWidth("100%");
      headerTable.setWidth(1,1,"0");
      headerTable.setWidth(2,1,"100%");
      headerTable.setWidth(3,1,"0");
      headerTable.add(properties.getTopLeftImage(modinfo),1,1);
      headerTable.setBackgroundImage(2,1,properties.getTopCenterImage(modinfo));
      headerTable.add(properties.getTopRightImage(modinfo),3,1);
    }

    public void initHeaderStripe(ModuleInfo modinfo){
//      headerStripe.setBorder(1);
      headerStripe.setCellpadding(0);
      headerStripe.setCellspacing(0);
      headerStripe.setHeight(0);
      headerStripe.setWidth("100%");
      headerStripe.setWidth(1,1,"0");
      headerStripe.setWidth(2,1,"100%");
      headerStripe.setWidth(3,1,"0");
//      headerStripe.add(properties.getBottomLeftImage(modinfo),1,1);

      Image tiler = properties.getBottomCenterImage(modinfo);
      headerStripe.setBackgroundImage(1,1,tiler);
      headerStripe.setBackgroundImage(2,1,tiler);
      headerStripe.setBackgroundImage(3,1,tiler);
//      headerStripe.add(properties.getBottomRightImage(modinfo),3,1);

      headerStripe.setAlignment("center");
    }


    public void init(){
      mainTable = new Table(3,1);
      headerTable = new Table(3,1);
      headerStripe = new Table(3,1);
    }

    public void locateObjects(ModuleInfo modinfo) throws Exception{
      linkCommunications = projectObject.getCommunicationLink();
      linkCommunications.setObject(properties.getComunicationLinkImage(modinfo));

      linkCalendar = projectObject.getCalendarLink();
      linkCalendar.setObject(properties.getCalendarLinkImage(modinfo));

      linkParticipants = projectObject.getParticipantsLink();
      linkParticipants.setObject(properties.getParticipantsLinkImage(modinfo));

      linkHandbook = projectObject.getHandbookLink();
      linkHandbook.setObject(properties.getHandbookLinkImage(modinfo));

      linkTimesheets = projectObject.getTimesheetLink();
      linkTimesheets.setObject(properties.getTimesheetLinkImage(modinfo));

      linkHome = projectObject.getFrontpageLink();
      linkHome.setObject(properties.getFrontpageLinkImage(modinfo));

      headerStripe.add(linkCommunications,1,1);
      headerStripe.add(linkCalendar,1,1);
      headerStripe.add(linkParticipants,1,1);
      headerStripe.add(linkHandbook,1,1);
      headerStripe.add(linkTimesheets,1,1);
      headerStripe.add(linkHome,1,1);

      this.add(projectObject);
      this.addLeft(projectObject.getCurrentLeftImage(modinfo));
    }

    public void add(ModuleObject objectToAdd){
      mainTable.add(Text.getBreak(),2,1);
      mainTable.add(objectToAdd,2,1);
    }

    public void addLeft(ModuleObject objectToAdd){
      mainTable.add(objectToAdd,1,1);
    }

    public void addRight(ModuleObject objectToAdd){
      mainTable.add(objectToAdd,3,1);
    }

    public void main(ModuleInfo modinfo) throws Exception {
      initProjectObject(modinfo);
      initProjectWebProperties(modinfo);
      super.add(getHeaderTable(modinfo));
      super.add(getHeaderStripe(modinfo));
      super.add(getMainTable(modinfo));
      this.locateObjects(modinfo);
      String title = (modinfo.getSpokenLanguage().equalsIgnoreCase("IS")) ? "Verkefnavefur" : "ProjectWeb";
      this.setTitle(title);
    }


} // Class GenericProjectTemplate


/*
    public Table template(ModuleInfo modinfo) {

      Table header = new Table(3,1);
      header.setBorder(1);
      Table headerStripe = new Table(3,1);
      headerStripe.setBorder(1);
      frame.setWidth("100%");

      header.setHeight("0");
      header.setCellpadding(0);
      header.setCellspacing(0);
      header.setWidth("100%");

      headerStripe.setHeight("0");
      headerStripe.setCellpadding(0);
      headerStripe.setCellspacing(0);
      headerStripe.setWidth("100%");


      tafla.setWidth("100%");
      tafla.setHeight("100%");
      tafla.setHeight(1,1,"0");
      tafla.setCellpadding(0);
      tafla.setCellspacing(0);
      tafla.setAlignment(1,1,"left");
      tafla.setAlignment(2,1,"left");
      tafla.setAlignment(3,1,"center");
      tafla.setWidth(1,1,"0");
      tafla.setWidth(3,1,"177");

      tafla.setAlignment("top");
      tafla.setVerticalAlignment("top");
      frame.setRowVerticalAlignment(1,"top");

      frame.setVerticalAlignment(1,1,"top");
      tafla.setVerticalAlignment(1,1,"top");
      tafla.setVerticalAlignment(2,1,"top");
      tafla.setVerticalAlignment(3,1,"top");


      linkCommunications = projectObject.getCommunicationLink();
      linkCommunications.setObject(properties.getComunicationLinkImage(modinfo));

      linkCalendar = projectObject.getCalendarLink();
      linkCalendar.setObject(properties.getCalendarLinkImage(modinfo));

      linkParticipants = projectObject.getParticipantsLink();
      linkParticipants.setObject(properties.getParticipantsLinkImage(modinfo));

      linkHandbook = projectObject.getHandbookLink();
      linkHandbook.setObject(properties.getHandbookLinkImage(modinfo));

      linkTimesheets = projectObject.getTimesheetLink();
      linkTimesheets.setObject(properties.getTimesheetLinkImage(modinfo));

      linkHome = projectObject.getFrontpageLink();
      linkHome.setObject(properties.getFrontpageLinkImage(modinfo));


      Image topLeft = properties.getTopLeftImage(modinfo);
      Image topCenter = properties.getTopCenterImage(modinfo);
      Image bottomCenter = properties.getBottomCenterImage(modinfo);
      Image topRight = properties.getTopRightImage(modinfo);
      Image bottomRight = properties.getBottomRightImage(modinfo);

      header.setWidth(1,1,"0");
      header.setWidth(2,1,"100%");
      header.setWidth(3,1,"0");
      header.setHeight(0);

      headerStripe.setWidth(1,1,"0");
      headerStripe.setWidth(2,1,"700");
      headerStripe.setWidth(3,1,"0");
      headerStripe.setHeight(0);

      header.add(topLeft,1,1);
      header.setBackgroundImage(2,1,topCenter);
      header.add(topRight,3,1);

      headerStripe.setBackgroundImage(1,1,bottomCenter);

      headerStripe.setBackgroundImage(2,1,bottomCenter);
      header.add(new Image("/pics/spacer.gif","",518,50),1,1);
      headerStripe.add(bottomRight,3,1);
      headerStripe.setAlignment(3,1,"right");
      headerStripe.add(linkCommunications,1,1);
      headerStripe.add(linkCalendar,1,1);
      headerStripe.add(linkParticipants,1,1);
      headerStripe.add(linkHandbook,1,1);
      headerStripe.add(linkTimesheets,1,1);
      headerStripe.add(linkHome,1,1);


      headerStripe.setVerticalAlignment(1,1,"bottom");
      headerStripe.setVerticalAlignment(2,1,"bottom");

      frame.add(header,1,1);
      frame.add(headerStripe,1,1);
      frame.add(tafla, 1,1);

      return frame;
    }
*/
