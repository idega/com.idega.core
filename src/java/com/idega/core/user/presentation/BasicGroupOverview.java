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
import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.interfaceobject.Form;
import com.idega.jmodule.object.interfaceobject.SubmitButton;
import com.idega.jmodule.object.interfaceobject.CloseButton;
import com.idega.core.business.UserGroupBusiness;
import java.util.Iterator;
import java.util.Vector;
import com.idega.core.accesscontrol.business.AccessControl;

import com.idega.core.user.data.UserGroupRepresentative;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class BasicGroupOverview extends Page {

  private static final String PARAMETER_DELETE_GROUP =  "delete_ic_group";


  public BasicGroupOverview(){
    super();
  }


  public Table getGroups(ModuleInfo modinfo) throws Exception{
    String[] types = new String[1];
    types[0] = ((UserGroupRepresentative)UserGroupRepresentative.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    List groups = GenericGroup.getAllGroups(types,false);



    //groups.remove(com.idega.core.accesscontrol.business.AccessControl.getAdministratorGroup())

    Table groupTable = null;
    if(groups != null){
      List notDelet = (List)((Vector)AccessControl.getStandardGroups()).clone();
      notDelet.add(AccessControl.getPermissionGroupAdministrator());
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

          //if(!tempGroup.equals(AccessControl.getPermissionGroupAdministrator()) && !tempGroup.equals(AccessControl.getPermissionGroupEveryOne()) && !tempGroup.equals(AccessControl.getPermissionGroupUsers())){
          if(!notDelet.contains(tempGroup)){
            Link delLink = new Link(new Text("Delete"));
            delLink.setWindowToOpen(ConfirmWindowBGO.class);
            delLink.addParameter(BasicGroupOverview.PARAMETER_DELETE_GROUP , tempGroup.getID());
            groupTable.add(delLink,3,i+1);
          }

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





  public static class ConfirmWindowBGO extends Window{

    public Text question;
    public Form myForm;

    public SubmitButton confirm;
    public CloseButton close;
    public Table myTable = null;

    public static final String PARAMETER_CONFIRM = "confirm";

    public Vector parameters;

    public ConfirmWindowBGO(){
      super("ConfirmWindow",300,130);
      super.setBackgroundColor("#d4d0c8");
      super.setScrollbar(false);
      super.setAllMargins(0);

      question = Text.getBreak();
      myForm = new Form();
      parameters = new Vector();
      confirm = new SubmitButton(ConfirmWindowBGO.PARAMETER_CONFIRM,"   Yes   ");
      close = new CloseButton("   No    ");
      // close.setOnFocus();
      initialze();

    }


    public void lineUpElements(){
      myTable = new Table(2,2);
      myTable.setWidth("100%");
      myTable.setHeight("100%");
      myTable.setCellpadding(5);
      myTable.setCellspacing(5);
      //myTable.setBorder(1);


      myTable.mergeCells(1,1,2,1);

      myTable.add(question,1,1);

      myTable.add(confirm,1,2);

      myTable.add(close,2,2);

      myTable.setAlignment(1,1,"center");
//      myTable.setAlignment(2,1,"center");
      myTable.setAlignment(1,2,"right");
      myTable.setAlignment(2,2,"left");

      myTable.setVerticalAlignment(1,1,"middle");
      myTable.setVerticalAlignment(1,2,"middle");
      myTable.setVerticalAlignment(2,2,"middle");

      myTable.setHeight(2,"30%");

      myForm.add(myTable);

    }

    public void setQuestion(Text Question){
      question = Question;
    }


    /*abstract*/
    public void initialze(){
      this.setQuestion(new Text("Are you sure you want to delete this group?"));
      this.maintainParameter(BasicGroupOverview.PARAMETER_DELETE_GROUP);
    }


    public void maintainParameter(String parameter){
      parameters.add(parameter);
    }

    /*abstract*/
    public void actionPerformed(ModuleInfo modinfo)throws Exception{
      String groupDelId = modinfo.getParameter(BasicGroupOverview.PARAMETER_DELETE_GROUP);

      if(groupDelId != null){
        UserGroupBusiness.deleteGroup(Integer.parseInt(groupDelId));
      }
    }


    public void _main(ModuleInfo modinfo) throws Exception {
      Iterator iter = parameters.iterator();
      while (iter.hasNext()) {
        String item = (String)iter.next();
        myForm.maintainParameter(item);
      }

      String confirmThis = modinfo.getParameter(ConfirmWindowBGO.PARAMETER_CONFIRM);

      if(confirmThis != null){
        this.actionPerformed(modinfo);
        this.setParentToReload();
        this.close();
      } else{
        this.empty();
        if(myTable == null){
          lineUpElements();
        }
        this.add(myForm);
      }
      super._main(modinfo);
    }

  }
















} //Class end