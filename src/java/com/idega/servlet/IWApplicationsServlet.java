package com.idega.servlet;

import com.idega.block.login.presentation.Login;
import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.development.presentation.Localizer;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.app.IWControlCenter;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWApplicationsServlet extends IWPresentationServlet {

private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
private IWBundle iwb;
private IWResourceBundle iwrb;

  public IWApplicationsServlet(){
  }

  public void initializePage(){
    Page thePage = new IWASPage();
    setPage(thePage);
  }

  private class IWASPage extends Page{
    String backgroundColor = "#B0B29D";

    public IWASPage(){
    }

    public void main(IWContext iwc){
      iwb = this.getBundle(iwc);
      iwrb = this.getResourceBundle(iwc);

      Page thePage = this;
      thePage.setBackgroundColor(backgroundColor);
      thePage.setAllMargins(0);

      thePage.setTitle("idegaWeb Applications");

      Table frameTable = new Table(1,1);
        frameTable.setWidth("100%");
        frameTable.setHeight("100%");
        frameTable.setCellpadding(0);
        frameTable.setCellspacing(0);
        frameTable.setAlignment(1,1,"center");
        frameTable.setVerticalAlignment(1,1,"middle");

      Table outerTable = new Table(1,1);
        //outerTable.setWidth(325);
        //outerTable.setHeight(433);
        outerTable.setCellspacing(1);
        outerTable.setCellpadding(0);
        outerTable.setColor("#000000");
        frameTable.add(outerTable,1,1);

      Table mainTable = new Table(1,4);
        mainTable.setWidth("100%");
        mainTable.setHeight("100%");
        mainTable.setCellspacing(0);
        mainTable.setCellpadding(0);
        mainTable.setBackgroundImage(1,1,iwb.getImage("logintiler.gif"));
        mainTable.setAlignment(1,1,"right");
        mainTable.setAlignment(1,2,"right");
        mainTable.setAlignment(1,3,"right");
        mainTable.setAlignment(1,4,"center");
        mainTable.setVerticalAlignment(1,1,"top");
        mainTable.setVerticalAlignment(1,2,"top");
        mainTable.setVerticalAlignment(1,3,"top");
        mainTable.setVerticalAlignment(1,4,"bottom");
        mainTable.setHeight(4,"12");
        mainTable.setHeight(1,"196");
        mainTable.setColor("#FFFFFF");
        outerTable.add(mainTable,1,1);

      Image headerImage;

      Image bottomImage = iwrb.getImage("login/bottom.gif","",323,12);
      mainTable.add(bottomImage,1,4);

      boolean isLoggedOn = false;
      try{
        isLoggedOn = iwc.isLoggedOn();
      }
      catch(Exception e){
        isLoggedOn = false;
      }

      if(isLoggedOn){
        IWControlCenter iwcc = new IWControlCenter();
        mainTable.setHeight(2,"165");
        mainTable.setAlignment(1,2,"center");
        mainTable.setAlignment(1,3,"right");
        mainTable.setVerticalAlignment(1,2,"middle");
        mainTable.setVerticalAlignment(1,3,"middle");
        mainTable.add(iwcc,1,2);
        headerImage = iwrb.getImage("login/header_app_suite.jpg","",323,196);

        Login login = new Login();
          login.setLogoutButton(iwrb.getImage("login/logout.gif"));
          login.setHeight("60");
          login.setWidth("70");
          login.setViewOnlyLogoutButton(true);
          login.setLoginAlignment("right");
        mainTable.add(login,1,3);

      }

      else {
        mainTable.setHeight(2,"175");
        mainTable.setHeight(3,"50");

        Table loginTable = new Table(1,1);
          loginTable.setAlignment("right");
          loginTable.setCellpadding(0);
          loginTable.setCellspacing(0);

        Login login = new Login();
          login.setLoginButton(iwrb.getImage("login/login.gif"));
          login.setLogoutButton(iwrb.getImage("login/logout.gif"));
          login.setUserTextSize(1);
          login.setPasswordTextSize(1);
          login.setHeight("130");
          login.setWidth("160");
          login.setStyle("font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; border-style:solid; border-width:1; border-color: #000000");
          login.setInputLength(13);
          login.setLayout(Login.LAYOUT_STACKED);
          loginTable.add(login,1,1);
        mainTable.add(loginTable,1,2);

        Table dropdownTable = new Table(1,1);
          dropdownTable.setWidth(148);
          dropdownTable.setCellpadding(0);
          dropdownTable.setCellspacing(0);
          dropdownTable.setAlignment("right");
          dropdownTable.setAlignment(1,1,"center");
        mainTable.add(dropdownTable,1,3);

        Form myForm = new Form();
          myForm.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());
        DropdownMenu dropdown = LocalePresentationUtil.getAvailableLocalesDropdown(iwc);
          dropdown.setStyleAttribute("font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 8pt; border-style:solid; border-width:1; border-color: #000000");
          myForm.add(dropdown);
          dropdownTable.add(myForm);


        headerImage = iwrb.getImage("/login/header.jpg","",323,196);
      }
       Link lheaderLink = new Link(headerImage,iwc.getApplication().getApplicationContextURI());
      //mainTable.add(headerImage,1,1);
      mainTable.add(lheaderLink,1,1);
      thePage.add(frameTable);
   }

  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}
