package com.idega.core.user.presentation;

import com.idega.jmodule.object.interfaceobject.IFrame;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.textObject.Link;
import com.idega.jmodule.object.Page;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.textObject.Text;
import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.interfaceobject.SelectionDoubleBox;
import com.idega.jmodule.object.interfaceobject.SelectionBox;
import com.idega.jmodule.object.interfaceobject.SubmitButton;
import com.idega.jmodule.object.interfaceobject.Form;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.business.UserGroupBusiness;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.data.User;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import com.idega.util.Disposable;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GroupMembershipTab extends UserGroupTab {


  //private Link addLink;
  private IFrame memberofFrame;
//  public static final String PARAMETER_GROUP_ID = "ic_group_id";
  public static final String SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED = "ic_group_ic_group_direct_GMT";
  public static final String SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED = "ic_group_ic_group_not_direct_GMT";
  public static final String SESSIONADDRESS_USERS_DIRECTLY_RELATED = "ic_user_ic_group_direct_GMT";
  public static final String SESSIONADDRESS_USERS_NOT_DIRECTLY_RELATED = "ic_user_ic_group_not_direct_GMT";

  protected Text memberof;

  public GroupMembershipTab() {
    super();
    this.setName("Members");

  }
  public void initFieldContents() {
    //addLink.setWindowToOpen(GroupMembershipTab.UserGroupSetter.class);
    //addLink.addParameter(GroupMembershipTab.PARAMETER_GROUP_ID,this.getUserId());
  }
  public void updateFieldsDisplayStatus() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }
  public void initializeFields() {
    memberofFrame = new IFrame("ic_group_members",GroupMembershipTab.GroupList.class);
    memberofFrame.setHeight(320);
    memberofFrame.setWidth(370);
    memberofFrame.setScrolling(IFrame.SCROLLING_YES);

    //addLink = new Link("  Add  ");

  }
  public void initializeTexts() {
    memberof = this.getTextObject();
    memberof.setText("Members:");
  }
  public boolean store(ModuleInfo modinfo) {
    return true;
  }
  public void lineUpFields() {
    this.resize(1,3);

    this.add(memberof,1,1);
    this.add(memberofFrame,1,2);

    this.setHeight(1,"30");
    this.setHeight(3,super.columnHeight);

    //this.add(addLink,1,3);
  }
  public boolean collect(ModuleInfo modinfo) {
    return true;
  }
  public void initializeFieldNames() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }
  public void initializeFieldValues() {
    /**@todo: implement this com.idega.core.user.presentation.UserTab abstract method*/
  }

  public void dispose(ModuleInfo modinfo){
    modinfo.removeSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
    modinfo.removeSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);
  }

  public void main(ModuleInfo modinfo) throws Exception {
    Object obj = UserGroupBusiness.getAllGroupMembersDirectlyRelated(this.getGroupId());
    if(obj != null){
      modinfo.setSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED,obj);
    }else{
      modinfo.removeSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
    }

    Object ob = UserGroupBusiness.getAllGroupMembersNotDirectlyRelated(this.getGroupId());
    if(ob != null){
      modinfo.setSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED,ob);
    }else{
      modinfo.removeSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);
    }
  }


  public static class GroupList extends Page {

    private List groups = null;

    public GroupList(){
      super();
    }

    public Table getGroupTable(ModuleInfo modinfo){

      List direct = (List)modinfo.getSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
      List notDirect = (List)modinfo.getSessionAttribute(GroupMembershipTab.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);

      Table table = null;
      Iterator iter = null;
      int row = 1;
      if(direct != null && notDirect != null){
        table = new Table(5,direct.size()+notDirect.size());

        iter = direct.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          table.add("D",1,row);
          table.add(((GenericGroup)item).getName(),3,row++);
        }

        iter = notDirect.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          table.add("E",1,row);
          table.add(((GenericGroup)item).getName(),3,row++);
        }

      } else if(direct != null){
        table = new Table(5,direct.size());
        iter = direct.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          table.add("D",1,row);
          table.add(((GenericGroup)item).getName(),3,row++);
        }
      }

      if(table != null){
        table.setWidth("100%");
        table.setWidth(1,"10");
        table.setWidth(2,"3");
        table.setWidth(4,"10");
        table.setWidth(5,"10");
      }



      return table;
    }

    public void main(ModuleInfo modinfo) throws Exception {
      this.getParentPage().setAllMargins(0);
      Table tb = getGroupTable(modinfo);
      if(tb != null){
        this.add(tb);
      }
    }



  } // InnerClass

/*
  public static class UserGroupSetter extends Window {

    private static final String FIELDNAME_SELECTION_DOUBLE_BOX = "related_groups";

    public UserGroupSetter(){
      super("add user to groups");
      this.setAllMargins(0);
      this.setWidth(400);
      this.setHeight(300);
      this.setBackgroundColor("#d4d0c8");
    }


    private void LineUpElements(ModuleInfo modinfo){

      Form form = new Form();

      Table frameTable = new Table(3,3);
      frameTable.setWidth("100%");
      frameTable.setHeight("100%");
      //frameTable.setBorder(1);


      SelectionDoubleBox sdb = new SelectionDoubleBox(FIELDNAME_SELECTION_DOUBLE_BOX,"Not in","In");

      SelectionBox left = sdb.getLeftBox();
      left.setHeight(8);
      left.selectAllOnSubmit();


      SelectionBox right = sdb.getRightBox();
      right.setHeight(8);
      right.selectAllOnSubmit();



      String stringUserId = modinfo.getParameter(GroupMembershipTab.PARAMETER_GROUP_ID);
      int userId = Integer.parseInt(stringUserId);
      form.addParameter(GroupMembershipTab.PARAMETER_GROUP_ID,stringUserId);

      List directGroups = UserBusiness.getUserGroupsDirectlyRelated(userId);

      Iterator iter = null;
      if(directGroups != null){
        iter = directGroups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          right.addElement(Integer.toString(((GenericGroup)item).getID()),((GenericGroup)item).getName());
        }
      }
      List notDirectGroups = UserBusiness.getAllGroupsNotDirectlyRelated(userId);
      if(notDirectGroups != null){
        iter = notDirectGroups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          left.addElement(Integer.toString(((GenericGroup)item).getID()),((GenericGroup)item).getName());
        }
      }


      frameTable.setAlignment(2,2,"center");
      frameTable.add("UserId: "+userId,2,1);
      frameTable.add(sdb,2,2);
      frameTable.add(new SubmitButton("  Save  ","save","true"),2,3);
      frameTable.setAlignment(2,3,"right");
      form.add(frameTable);
      this.add(form);
    }

    public void main(ModuleInfo modinfo) throws Exception {


      String save = modinfo.getParameter("save");
      if(save != null){
        String stringUserId = modinfo.getParameter(GroupMembershipTab.PARAMETER_GROUP_ID);
        int userId = Integer.parseInt(stringUserId);

        String[] related = modinfo.getParameterValues(UserGroupSetter.FIELDNAME_SELECTION_DOUBLE_BOX);

        User user = new User(userId);
        List currentRelationShip = UserBusiness.getUserGroupsDirectlyRelated(user);


        if(related != null){

          if(currentRelationShip != null){
            for (int i = 0; i < related.length; i++) {
              int id = Integer.parseInt(related[i]);
              GenericGroup gr = new GenericGroup(id);
              if(!currentRelationShip.remove(gr)){
                //user.addTo(gr);
                gr.addUser(user);
              }
            }

            Iterator iter = currentRelationShip.iterator();
            while (iter.hasNext()) {
              Object item = iter.next();
              //user.removeFrom((GenericGroup)item);
              ((GenericGroup)item).removeUser(user);
            }

          } else{
            for (int i = 0; i < related.length; i++) {
              //user.addTo(GenericGroup.class,Integer.parseInt(related[i]));
              //new GenericGroup(Integer.parseInt(related[i])).addUser(user);
              GenericGroup.addUser(Integer.parseInt(related[i]),user);
            }
          }

        }else if (currentRelationShip != null){
            Iterator iter = currentRelationShip.iterator();
            while (iter.hasNext()) {
              Object item = iter.next();
              ((GenericGroup)item).removeUser(user);
            }
          }

        this.close();
        this.setParentToReload();
      } else {
        LineUpElements(modinfo);
      }
*/
/*
      Enumeration enum = modinfo.getParameterNames();
       System.err.println("--------------------------------------------------");
      if(enum != null){
        while (enum.hasMoreElements()) {
          Object item = enum.nextElement();
          if(item.equals("save")){
            this.close();
          }
          String val[] = modinfo.getParameterValues((String)item);
          System.err.print(item+" = ");
          if(val != null){
            for (int i = 0; i < val.length; i++) {
              System.err.print(val[i]+", ");
            }
          }
          System.err.println();
        }
      }
*/
//    }

//  } // InnerClass

}