package com.idega.core.user.presentation;



import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.IFrame;


/**
 * Title:        IW User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserModule extends com.idega.idegaweb.presentation.IWAdminWindow {//com.idega.presentation.app.IWApplication {

  boolean justConstructed = true;


  public UserModule() {
    super();
    this.setName("idegaWeb User");
    //add(UserModule.UserModulePage.class);
    this.justConstructed = true;
    super.setResizable(false);
    super.setScrollbar(false);
    super.setWidth(555);
    super.setHeight(303);
  }

  public void main(IWContext iwc) throws Exception {
    super.main(iwc);
    if(this.justConstructed){
      add(new UserModule.UserModulePage());
      this.justConstructed = false;
    }
  }


  public static class UserModulePage extends Table{//Page{

		private IWBundle iwb;

    public UserModulePage(){
      /*this.setMarginHeight(0);
      this.setMarginWidth(0);
      this.setTopMargin(0);
      this.setLeftMargin(0);
      this.setBackgroundColor("#d4d0c8");
      */
      this.setCellpadding(0);
      this.setCellspacing(0);
      super.setColor("#d4d0c8");
      super.setWidth("100%");
      super.setHeight("100%");
      super.setVerticalAlignment(1,1,"top");
      super.setAlignment(1,1,"center");
    }

		public String getBundleIdentifier(){
	    return "com.idega.core.user";
		}

    public void main(IWContext iwc)throws Exception{

		  this.iwb = getBundle(iwc);


      Table tb = new Table(2,4);

      IFrame ifr1 = new IFrame("groupOberview",BasicGroupOverview.class);
      ifr1.setWidth(220);
      ifr1.setHeight(180);
      ifr1.setScrolling(IFrame.SCROLLING_YES);

      IFrame ifr = new IFrame("userOverview",BasicUserOverview.class);
      ifr.setWidth(300);
      ifr.setHeight(180);
      ifr.setScrolling(IFrame.SCROLLING_YES);
      //ifr.setBorder(IFrame.FRAMEBORDER_OFF);


      tb.add("Groups",1,1);
      tb.add("Users",2,1);
      //tb.setAlignment(1,1,"");

      tb.add(ifr1,1,2);
      tb.add(ifr,2,2);

			Link tLink12 = new Link(this.iwb.getImage("group.gif"));
      tLink12.setWindowToOpen(CreateUserGroup.class);
      tb.add(Text.getNonBrakingSpace(),1,3);
      tb.add(Text.getNonBrakingSpace(),1,3);
      tb.add(tLink12,1,3);

			Link tLink11 = new Link(this.iwb.getImage("user.gif"));
      tLink11.setWindowToOpen(CreateUser.class);
      tb.add(Text.getNonBrakingSpace(),2,3);
      tb.add(Text.getNonBrakingSpace(),2,3);

      tb.add(tLink11,2,3);


      add(tb);

      tb.add(new CloseButton(" Close "),2,4);
      tb.setAlignment(2,4,"right");
      tb.setHeight(2,4,"30");




      //add(ifr);

    } // main

  } // InnerClass UserModule


} // Class UserModule
