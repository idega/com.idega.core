package com.idega.idegaweb.project.business;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.block.boxoffice.presentation.*;
import com.idega.block.calendar.presentation.*;
import com.idega.block.forum.presentation.*;
import com.idega.block.news.presentation.*;

/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author idega - team 2001
 * @version 1.0
 */

public class ProjectWebPresentation extends JModuleObject {

  private Link communicationsLink;
  private Link calendarLink;
  private Link participantsLink;
  private Link handbookLink;
  private Link timesheetLink;
  private Link frontpageLink;

  private static String stateParameterString = "pw_state";
  private static String communicationsStateString = "com";
  private static String calendarStateString = "cal";
  private static String participantsStateString = "par";
  private static String handbookStateString = "hand";
  private static String timesheetStateString = "time";
  private static String frontpageStateString = "home";

  private ModuleObject lastObj = null;
  private Image lastLeftImage = null;

  private ModuleObject communications = null;
  private ModuleObject calendar = null;
  private ModuleObject participants = null;
  private ModuleObject handbook = null;
  private ModuleObject timesheet = null;
  private ModuleObject frontpage = null;

  private boolean first;
  private ProjectWebProperties properties;


  public ProjectWebPresentation() {
    properties = new ProjectWebProperties();
    first = true;
  }


  private void initCommunicationLink(){
    communicationsLink = new Link("Communications");
    communicationsLink.addParameter(stateParameterString, communicationsStateString );
  }

  public Link getCommunicationLink(){
    if (communicationsLink == null)
      initCommunicationLink();

    return communicationsLink;
  }

  private void initCaledarLink(){
    calendarLink = new Link("Calendar");
    calendarLink.addParameter(stateParameterString, calendarStateString );
  }

  public Link getCalendarLink(){
    if (calendarLink == null)
      initCaledarLink();

    return calendarLink;
  }


  private void initParticipantsLink(){
    participantsLink = new Link("Participants");
    participantsLink.addParameter(stateParameterString, participantsStateString );
  }

  public Link getParticipantsLink(){
    if (participantsLink == null)
      initParticipantsLink();

    return participantsLink;
  }



  private void initHandbookLink(){
    handbookLink = new Link("Handbook");
    handbookLink.addParameter(stateParameterString, handbookStateString );
  }

  public Link getHandbookLink(){
    if (handbookLink == null)
      initHandbookLink();

    return handbookLink;
  }



  private void initTimesheetLink(){
    timesheetLink = new Link("Timesheet");
    timesheetLink.addParameter(stateParameterString, timesheetStateString );
  }

  public Link getTimesheetLink(){
    if (timesheetLink == null)
      initTimesheetLink();

    return timesheetLink;
  }


  private void initFrontpageLink(){
    frontpageLink = new Link("Home");
    frontpageLink.addParameter(stateParameterString, frontpageStateString );
  }

  public Link getFrontpageLink(){
    if (frontpageLink == null)
      initFrontpageLink();

    return frontpageLink;
  }





  public ModuleObject getCurrentObject(ModuleInfo modinfo) throws Exception {
    ModuleObject obj = null;
    String state = modinfo.getParameter(stateParameterString);

    if (state == null){
      if (lastObj == null)
        lastObj = getFrontpageState(modinfo);

      obj = lastObj;
    }else if (state.equals(frontpageStateString)){
            obj = getFrontpageState(modinfo);
    }else if (state.equals(communicationsStateString)){
            obj = getCommunicationState(modinfo);
    }else if (state.equals(calendarStateString)){
            obj = getCalendatState(modinfo);
    }else if (state.equals(participantsStateString)){
            obj = getParticipantsState(modinfo);
    }else if (state.equals(handbookStateString)){
            obj = getHandbookState(modinfo);
    }else if (state.equals(timesheetStateString)){
            obj = getTimeSheetState(modinfo);
    }else{
      obj = lastObj;
    }

    lastObj = obj;

    return obj;

  }

  public Image getCurrentLeftImage(ModuleInfo modinfo) throws Exception {
    Image myImage = null;
    String state = modinfo.getParameter(stateParameterString);

    if (state == null){
      if (lastLeftImage == null)
        lastLeftImage = properties.getFrontpageLeftImage(modinfo) ;

      myImage = lastLeftImage;
    }else if (state.equals(frontpageStateString)){
            myImage = properties.getFrontpageLeftImage(modinfo) ;
    }else if (state.equals(communicationsStateString)){
            myImage = properties.getCommunicationLeftImage(modinfo);
    }else if (state.equals(calendarStateString)){
            myImage = properties.getCalendarLeftImage(modinfo);
    }else if (state.equals(participantsStateString)){
            myImage = properties.getParticipantsLeftImage(modinfo);
    }else if (state.equals(handbookStateString)){
            myImage = properties.getHandbookLeftImage(modinfo);
    }else if (state.equals(timesheetStateString)){
            myImage = properties.getTimesheetLeftImage(modinfo);
    }else{
      myImage = lastLeftImage;
      if (myImage == null)
        myImage = properties.getFrontpageLeftImage(modinfo) ;
    }

    lastLeftImage = myImage;

    return myImage;

  }

  private ModuleObject getCommunicationState(ModuleInfo modinfo) throws Exception {
    if (communications == null)
      initCommunicationState(modinfo);

    return communications;
  }

  private ModuleObject getCalendatState(ModuleInfo modinfo) throws Exception {
    if (calendar == null)
      initCalendarState(modinfo);

    return calendar;
  }

  private ModuleObject getParticipantsState(ModuleInfo modinfo) throws Exception {
    if (participants == null)
      initParticipantsState(modinfo);

    return participants;
  }

  private ModuleObject getHandbookState(ModuleInfo modinfo) throws Exception {
    if (handbook == null)
      initHandbookState(modinfo);

    return handbook;
  }

  private ModuleObject getTimeSheetState(ModuleInfo modinfo) throws Exception {
    if (timesheet == null)
      initTimesheetState(modinfo);

    return timesheet;
  }

  private ModuleObject getFrontpageState(ModuleInfo modinfo) throws Exception {
    if (frontpage == null)
      initFrontpageState(modinfo);

    return frontpage;
  }



  public void initCommunicationState(ModuleInfo modinfo) throws Exception {
    if (modinfo.getSessionAttribute("pw_grayForums") == null){
      GrayForums theForums = new GrayForums();
      theForums.setUseLogin(false); // TEMP ####################
      modinfo.setSessionAttribute("pw_grayForums" , theForums );
    }
    GrayForums myForums = (GrayForums)modinfo.getSessionAttribute("pw_grayForums");
    ProjectWebProperties.setCurrentProjectID(modinfo, new Integer(1));   // TEMP &###&#&#&#&#&#&#&#&#&&&&&&&&&&&&&###############&&&&&&&&&&&#########&&&&&&&&##########&
    myForums.setConnectionAttributes(ProjectWebProperties.getCurrentAttributeName(modinfo),ProjectWebProperties.getCurrentProjectID(modinfo));
    this.setCommunicationState(myForums);
  }

  public void initCalendarState(ModuleInfo modinfo) throws Exception {
    this.setCalendarState(new Calendar());
    //this.setCalendarState(new Calendar());
  }

  public void initParticipantsState(ModuleInfo modinfo) throws Exception {
    this.setParticipantsState(new Text("participants"));
  //  this.setParticipantsState(new ProjectWebParticipants());
  }

  public void initHandbookState(ModuleInfo modinfo) throws Exception {
    this.setHandbookState(new BoxReader());
    //this.setHandbookState(new Boxoffice());
  }

  public void initTimesheetState(ModuleInfo modinfo) throws Exception {
    this.setTimesheetState(new Text("timesheet"));
    //this.setTimesheetState(new Timesheet());
  }

  public void initFrontpageState(ModuleInfo modinfo) throws Exception {
    this.setFrontpageState(new NewsReader());
    //this.setFrontpageState(new News());
  }


  public void setCommunicationState(ModuleObject obj) throws Exception {
    communications = obj;
  }

  public void setCalendarState(ModuleObject obj) throws Exception {
    calendar = obj;
  }

  public void setParticipantsState(ModuleObject obj) throws Exception {
    participants = obj;
  }

  public void setHandbookState(ModuleObject obj) throws Exception {
    handbook = obj;
  }

  public void setTimesheetState(ModuleObject obj) throws Exception {
    timesheet = obj;
  }

  public void setFrontpageState(ModuleObject obj) throws Exception {
    frontpage = obj;
  }


  public void main(ModuleInfo modinfo) throws Exception {
    if (first){
//      lastObj = getFrontpageState(modinfo);
//      lastLeftImage = properties.getFrontpageLeftImage(modinfo);
      first = false;
    }
    this.empty();
    add(this.getCurrentObject(modinfo));
  }




}