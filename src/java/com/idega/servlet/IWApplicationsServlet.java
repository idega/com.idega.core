package com.idega.servlet;

import com.idega.block.login.presentation.Login;

import com.idega.jmodule.object.Page;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.ModuleInfo;

import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.jmodule.object.app.IWControlCenter;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWApplicationsServlet extends IWPresentationServlet {

  public IWApplicationsServlet(){
  }

  public void initializePage(){
    Page thePage = new IWASPage();
    setPage(thePage);
  }

  private class IWASPage extends Page{

    String backgroundColor = "#D4D0C8";

    public IWASPage(){
    }

    public void main(ModuleInfo modinfo){

      Page thePage = this;
      thePage.setBackgroundColor(backgroundColor);
      thePage.setMarginWidth(50);
      thePage.setMarginHeight (50);
      thePage.setLeftMargin(50);
      thePage.setTopMargin(50);

      thePage.setTitle("idegaWeb Applications");

      Table mainTable = new Table();
      thePage.add(mainTable);
      mainTable.setAlignment("center");

      Login login = new Login();
      mainTable.add(login,1,1);

      try{
        if(AccessControl.isAdmin(modinfo)){
          IWControlCenter iwcc = new IWControlCenter();
          mainTable.add(iwcc,1,2);
        }
      }
      catch(Exception e){
      }

    }

  }

}