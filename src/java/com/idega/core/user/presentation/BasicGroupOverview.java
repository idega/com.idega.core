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
import com.idega.core.data.GenericGroup;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class BasicGroupOverview extends Page {

  public BasicGroupOverview(){
    super();
  }


  public Table getGroups(ModuleInfo modinfo) throws Exception{
    List groups = EntityFinder.findAllOrdered(GenericGroup.getStaticInstance(),GenericGroup.getNameColumnName());
    Table groupTable = null;
    if(groups != null){
      groupTable = new Table(3,(groups.size()>8)?groups.size():8);
      groupTable.setCellspacing(0);
      groupTable.setHorizontalZebraColored("D8D4CD","C3BEB5");
      groupTable.setWidth("100%");
      for (int i = 0; i < groups.size(); i++) {
        GenericGroup tempGroup = (GenericGroup)groups.get(i);
        if(tempGroup != null){

          Link aLink = new Link(new Text(tempGroup.getName()));
          aLink.setWindowToOpen(GroupPropertyWindow.class);
          aLink.addParameter(GroupPropertyWindow.PARAMETERSTRING_GROUP_ID, tempGroup.getID());
          groupTable.add(aLink,2,i+1);
        }
      }
    }

    return groupTable;
  }




  public void main(ModuleInfo modinfo) throws Exception {
    this.empty();
    this.add(this.getGroups(modinfo));
    this.getParentPage().setAllMargins(0);
    this.getParentPage().setBackgroundColor("#d4d0c8");
  }



} //Class end