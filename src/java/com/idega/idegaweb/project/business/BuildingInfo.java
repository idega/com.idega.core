package com.idega.idegaweb.project.business;

import com.idega.jmodule.object.ModuleInfo;

/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author idega - team 2001
 * @version 1.0
 */

public class BuildingInfo {

  public static String defaultAttributeName = "projectweb";



  public BuildingInfo() {
  }


  public int getProjectID(ModuleInfo modinfo){
    return ProjectBusiness.getCurrentProjectID(modinfo);
  }

  public int getDefaultAttributeValue(ModuleInfo modinfo){
    return getProjectID(modinfo);
  }




  public static String getCommunicationsAttributeName(){
    return defaultAttributeName;
  }

  public int getCommunicationsAttributeValue(ModuleInfo modinfo){
    return getProjectID(modinfo);
  }



  public static String getCalendarAttributeName(){
    return defaultAttributeName;
  }

  public int getCalendarAttributeValue(ModuleInfo modinfo){
    return getProjectID(modinfo);
  }


  public static String getParticipantAttributeName(){
    return defaultAttributeName;
  }

  public int getParticipantAttributeValue(ModuleInfo modinfo){
    return getProjectID(modinfo);
  }



  public static String getHandbookAttributeName(){
    return defaultAttributeName;
  }

  public int getHandbookAttributeValue(ModuleInfo modinfo){
    return getProjectID(modinfo);
  }



  public static String getTimeSheetAttributeName(){
    return defaultAttributeName;
  }

  public int getTimeSheetAttributeValue(ModuleInfo modinfo){
    return getProjectID(modinfo);
  }



  public static String getNewsAttributeName(){
    return defaultAttributeName;
  }

  public int getNewsAttributeValue(ModuleInfo modinfo){
    return getProjectID(modinfo);
  }




} //  Class BuildingInfo