package com.idega.idegaweb.project.business;

import com.idega.block.BlockProperties;
import com.idega.jmodule.object.*;

/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author idega - team 2001
 * @version 1.0
 */

public class ProjectWebProperties extends BlockProperties {

  public static String[] IS = {"Nafn", "Staða", "Sími", "Fax", "Tölvupóstur"};
  public static String[] EN = {"Name", "Organisation", "Phone", "Fax", "Email"};

  public static String currentAttributeName;

  public String comunicationlinkImageName = "comm.gif";
  public String calendarlinkImageName = "calendar.gif";
  public String participantslinkImageName = "participants.gif";
  public String handbooklinkImageName = "handbook.gif";
  public String timesheetlinkImageName = "timesheets.gif";
  public String frontpagelinkImageName = "home.gif";

  public String comunicationlinkImagePublicName = "Communications";
  public String calendarlinkImagePublicName = "Calendar";
  public String participantslinkImagePublicName = "Participants";
  public String handbooklinkImagePublicName = "Handbook";
  public String timesheetlinkImagePublicName = "Timesheets";
  public String frontpagelinkImagePublicName = "Home";



  public String comunicationleftImageName = "left/communications.gif";
  public String calendarleftImageName = "left/calendar.gif";
  public String participantsleftImageName = "left/participants.gif";
  public String handbookleftImageName = "left/handbook.gif";
  public String timesheetleftImageName = "left/timesheets.gif";
  public String frontpageleftImageName = "left/home.gif";

  public String comunicationleftImagePublicName = "Communications";
  public String calendarleftImagePublicName = "Calendar";
  public String participantsleftImagePublicName = "Participants";
  public String handbookleftImagePublicName = "Handbook";
  public String timesheetleftImagePublicName = "Timesheets";
  public String frontpageleftImagePublicName = "Home";



  public String topleftImageName = "topleft.gif";
  public String topcenterImageName = "topcenter.gif";
  public String toprightImageName = "topright.gif";
  public String bottomleftImageName = "bottomleft.gif";
  public String bottomcenterImageName = "bottomcenter.gif";
  public String bottomrightImageName = "bottomright.gif";       // 177,18

  public String topleftImagePublicName = "ProjectWeb";
  public String topcenterImagePublicName = "ProjectWeb";
  public String toprightImagePublicName = "ProjectWeb";
  public String bottomleftImagePublicName = "ProjectWeb";
  public String bottomcenterImagePublicName = "ProjectWeb";
  public String bottomrightImagePublicName = "ProjectWeb";



  /*
      String newsImage = "/pics/project/"+language+"/home.gif";
      String HeaderImage = "/pics/project/"+language+"/committee.gif";
      String topImage = "/pics/project/"+language+"/topleft.gif";
  */

  private static String projectidSessionString = "pw_project_id";


  public ProjectWebProperties() {
    super();
    super.setBlockName("ProjectWeb");
    currentAttributeName = BlockName;
  }

  public static void setCurrentProjectID(ModuleInfo modinfo, int id){
    setCurrentProjectID(modinfo, new Integer(id));
  }

  public static void setCurrentProjectID(ModuleInfo modinfo, Integer id){
    modinfo.setSessionAttribute(getProjectIdSessionString(),id);
  }

  public static int getCurrentProjectID(ModuleInfo modinfo){
    return ((Integer)modinfo.getSessionAttribute(getProjectIdSessionString())).intValue();
  }

  public static String getProjectIdSessionString(){
    return projectidSessionString;
  }

  public static String getCurrentAttributeName(ModuleInfo modinfo){
    return currentAttributeName;
  }



  public String getComunicationLinkImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, comunicationlinkImageName);
  }

  public String getCalendarLinkImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, calendarlinkImageName);
  }

  public String getParticipantsLinkImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, participantslinkImageName);
  }

  public String getHandbookLinkImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, handbooklinkImageName);
  }

  public String getTimesheetLinkImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, timesheetlinkImageName);
  }

  public String getFrontpageLinkImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, frontpagelinkImageName);
  }



  public Image getComunicationLinkImage(ModuleInfo modinfo){
    return this.getImage(modinfo, comunicationlinkImageName, comunicationlinkImagePublicName);
  }

  public Image getCalendarLinkImage(ModuleInfo modinfo){
    return this.getImage(modinfo, calendarlinkImageName, calendarlinkImagePublicName);
  }

  public Image getParticipantsLinkImage(ModuleInfo modinfo){
    return this.getImage(modinfo, participantslinkImageName, participantslinkImagePublicName);
  }

  public Image getHandbookLinkImage(ModuleInfo modinfo){
    return this.getImage(modinfo, handbooklinkImageName, handbooklinkImagePublicName);
  }

  public Image getTimesheetLinkImage(ModuleInfo modinfo){
    return this.getImage(modinfo, timesheetlinkImageName, timesheetlinkImagePublicName);
  }

  public Image getFrontpageLinkImage(ModuleInfo modinfo){
    return this.getImage(modinfo, frontpagelinkImageName, frontpagelinkImagePublicName);
  }



  public String getComunicationLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, comunicationleftImageName);
  }

  public String getCalendarLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, calendarleftImageName);
  }

  public String getParticipantsLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, participantsleftImageName);
  }

  public String getHandbookLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, handbookleftImageName);
  }

  public String getTimesheetLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, timesheetleftImageName);
  }

  public String getFrontpageLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, frontpageleftImageName);
  }



  public Image getCommunicationLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, comunicationleftImageName, comunicationleftImagePublicName);
  }

  public Image getCalendarLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, calendarleftImageName, calendarleftImagePublicName);
  }

  public Image getParticipantsLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, participantsleftImageName, participantsleftImagePublicName);
  }

  public Image getHandbookLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, handbookleftImageName, handbookleftImagePublicName);
  }

  public Image getTimesheetLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, timesheetleftImageName, timesheetleftImagePublicName);
  }

  public Image getFrontpageLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, frontpageleftImageName, frontpageleftImagePublicName);
  }




  public String getTopLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, topleftImageName);
  }

  public String getTopCenterImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, topcenterImageName);
  }

  public String getTopRightImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, toprightImageName);
  }

  public String getBottomLeftImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, bottomleftImageName);
  }

  public String getBottomCenterImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, bottomcenterImageName);
  }

  public String getBottomRightImageUrl(ModuleInfo modinfo){
    return this.getImageUrl(modinfo, bottomrightImageName);
  }



  public Image getTopLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, topleftImageName, topleftImagePublicName);
  }

  public Image getTopCenterImage(ModuleInfo modinfo){
    return this.getImage(modinfo, topcenterImageName, topcenterImagePublicName);
  }

  public Image getTopRightImage(ModuleInfo modinfo){
    return this.getImage(modinfo, toprightImageName, toprightImagePublicName);
  }

  public Image getBottomLeftImage(ModuleInfo modinfo){
    return this.getImage(modinfo, bottomleftImageName, bottomleftImagePublicName);
  }

  public Image getBottomCenterImage(ModuleInfo modinfo){
    return this.getImage(modinfo, bottomcenterImageName, bottomcenterImagePublicName);
  }

  public Image getBottomRightImage(ModuleInfo modinfo){
    return this.getImage(modinfo, bottomrightImageName, bottomrightImagePublicName);
  }


}