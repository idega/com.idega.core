package com.idega.core.user.presentation;

import com.idega.jmodule.object.ModuleObjectContainer;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.textObject.Link;
import com.idega.jmodule.object.textObject.Text;
import com.idega.jmodule.object.Page;
import com.idega.core.user.data.User;
import com.idega.data.EntityFinder;
import java.util.List;
import com.idega.core.user.presentation.UserPropertyWindow;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class BasicUserOverview extends Page {


  public BasicUserOverview(ModuleInfo modinfo) throws Exception {
    //this.empty();
    //this.add(this.getUsers(modinfo));
  }
  public BasicUserOverview(){
    super();
  }


  public Table getUsers(ModuleInfo modinfo) throws Exception{
    List users = EntityFinder.findAllOrdered(User.getStaticInstance(),User.getColumnNameFirstName());
    Table userTable = null;
    if(users != null){
      userTable = new Table(3,(users.size()>8)?users.size():8);
      userTable.setCellspacing(0);
      userTable.setHorizontalZebraColored("D8D4CD","C3BEB5");
      userTable.setWidth("100%");
      for (int i = 0; i < users.size(); i++) {
        User tempUser = (User)users.get(i);
        if(tempUser != null){

          Link aLink = new Link(new Text(tempUser.getName()));
          aLink.setWindowToOpen(UserPropertyWindow.class);
          aLink.addParameter(UserPropertyWindow.PARAMETERSTRING_USER_ID, tempUser.getID());
          userTable.add(aLink,2,i+1);
        }
      }
    }

    return userTable;
  }




  public void main(ModuleInfo modinfo) throws Exception {
    this.empty();
    this.add(this.getUsers(modinfo));
    this.getParentPage().setAllMargins(0);
    this.getParentPage().setBackgroundColor("#d4d0c8");
  }



} //Class end