package com.idega.core.user.presentation;



import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.app.*;
import com.idega.idegaweb.IWConstants;
import com.idega.core.data.GenericGroup;
import com.idega.core.accesscontrol.data.PermissionGroup;


/**
 * Title:        IW User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserModule extends com.idega.jmodule.object.app.IWApplication {

  public UserModule() {
    super("idegaWeb User");
    add(UserModule.UserModulePage.class);
    super.setResizable(true);
    super.setWidth(550);
    super.setHeight(300);
  }


  public static class UserModulePage extends Page{

    public UserModulePage(){
      this.setMarginHeight(0);
      this.setMarginWidth(0);
      this.setTopMargin(0);
      this.setLeftMargin(0);
      super.setBackgroundColor("#d4d0c8");
    }

    public void main(ModuleInfo modinfo)throws Exception{

      Table toolbar = new Table(16,1);
      toolbar.setCellpadding(0);
      toolbar.setCellspacing(0);

      Link tLink1 = new Link(new Image("/temp_pics/toolbar/toolbar_user_01.gif"));
      toolbar.add(tLink1,1,1);
      Link tLink2 = new Link(new Image("/temp_pics/toolbar/toolbar_user_02.gif"));
      toolbar.add(tLink2,2,1);
      Link tLink3 = new Link(new Image("/temp_pics/toolbar/toolbar_user_03.gif"));
      toolbar.add(tLink3,3,1);
      Link tLink4 = new Link(new Image("/temp_pics/toolbar/toolbar_user_04.gif"));
      toolbar.add(tLink4,4,1);
      Link tLink5 = new Link(new Image("/temp_pics/toolbar/toolbar_user_05.gif"));
      toolbar.add(tLink5,5,1);
      Link tLink6 = new Link(new Image("/temp_pics/toolbar/toolbar_user_06.gif"));
      toolbar.add(tLink6,6,1);
      Link tLink7 = new Link(new Image("/temp_pics/toolbar/toolbar_user_07.gif"));
      toolbar.add(tLink7,7,1);
      Link tLink8 = new Link(new Image("/temp_pics/toolbar/toolbar_user_08.gif"));
      toolbar.add(tLink8,8,1);
      Link tLink9 = new Link(new Image("/temp_pics/toolbar/toolbar_user_09.gif"));
      toolbar.add(tLink9,9,1);
      Link tLink10 = new Link(new Image("/temp_pics/toolbar/toolbar_user_10.gif"));
      toolbar.add(tLink10,10,1);

      Link tLink11 = new Link(new Image("/temp_pics/toolbar/toolbar_user_11.gif"));
      tLink11.setWindowToOpen(CreateUser.class);
      toolbar.add(tLink11,11,1);

      Link tLink12 = new Link(new Image("/temp_pics/toolbar/toolbar_user_12.gif"));
      tLink12.setWindowToOpen(CreateUserGroup.class);
      toolbar.add(tLink12,12,1);

      Link tLink13 = new Link(new Image("/temp_pics/toolbar/toolbar_user_13.gif"));
      toolbar.add(tLink13,13,1);
      Link tLink14 = new Link(new Image("/temp_pics/toolbar/toolbar_user_14.gif"));
      toolbar.add(tLink14,14,1);
      Link tLink15 = new Link(new Image("/temp_pics/toolbar/toolbar_user_15.gif"));
      toolbar.add(tLink15,15,1);
      Link tLink16 = new Link(new Image("/temp_pics/toolbar/toolbar_user_16.gif"));
      toolbar.add(tLink16,16,1);


      add(toolbar);

      Table tb = new Table(2,1);



      IFrame ifr1 = new IFrame("groupOberview");
      ifr1.setWidth(170);
      ifr1.setHeight(180);
      ifr1.setScrolling(IFrame.SCROLLING_YES);

      IFrame ifr = new IFrame("userOverview",BasicUserOverview.class);
      ifr.setWidth(350);
      ifr.setHeight(180);
      ifr.setScrolling(IFrame.SCROLLING_YES);
      //ifr.setBorder(IFrame.FRAMEBORDER_OFF);

      tb.add(ifr1,1,1);
      tb.add(ifr,2,1);
      add(tb);
      //add(ifr);

    } // main

  } // InnerClass UserModule


} // Class UserModule
