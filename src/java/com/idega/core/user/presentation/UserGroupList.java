package com.idega.core.user.presentation;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.idega.core.data.GenericGroup;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.user.data.User;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SelectionDoubleBox;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.Window;
import com.idega.util.Disposable;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserGroupList extends UserTab implements Disposable, IWLinkListener {

  private Link addLink;
  private IFrame memberofFrame;

  private DropdownMenu primaryGroupField;

  private String primaryGroupFieldName;

  private Text primaryGroupText;

  public static final String PARAMETER_USER_ID = "ic_user_id";
  public static final String SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED = "ic_user_ic_group_direct_UGL";
  public static final String SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED = "ic_user_ic_group_not_direct_UGL";

  protected Text memberof;

  public UserGroupList() {
    super();
    this.setName("Groups");

  }
  public void initFieldContents() {
    this.addLink.setWindowToOpen(UserGroupList.UserGroupSetter.class);
    this.addLink.addParameter(UserGroupList.PARAMETER_USER_ID,this.getUserId());
    List userGroups = UserBusiness.getUserGroupsDirectlyRelated(this.getUserId());
    if(userGroups != null){
      Iterator iter = userGroups.iterator();
      while (iter.hasNext()) {
        GenericGroup item = (GenericGroup)iter.next();
        this.primaryGroupField.addMenuElement(item.getID(),item.getName());
      }
    }
    try {
      User user = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(this.getUserId());
      int prgroupid = user.getPrimaryGroupID();
      this.fieldValues.put(this.primaryGroupFieldName, (prgroupid != -1)?Integer.toString(prgroupid):"");
    }
    catch (SQLException ex) {

    }
    updateFieldsDisplayStatus();
  }
  public void updateFieldsDisplayStatus() {
    this.primaryGroupField.setSelectedElement((String)this.fieldValues.get(this.primaryGroupFieldName));
  }
  public void initializeFields() {
    this.memberofFrame = new IFrame("ic_user_memberof_ic_group",UserGroupList.GroupList.class);
    this.memberofFrame.setHeight(280);
    this.memberofFrame.setWidth(370);
    this.memberofFrame.setScrolling(IFrame.SCROLLING_YES);

    this.primaryGroupField = new DropdownMenu(this.primaryGroupFieldName);
    this.primaryGroupField.keepStatusOnAction();

    this.addLink = new Link("  Add/Remove  ");
  }

  public void actionPerformed(IWLinkEvent e){
    this.collect(e.getIWContext());
  }

  public void initializeTexts() {
    this.memberof = this.getTextObject();
    this.memberof.setText("Member of:");

    this.primaryGroupText = this.getTextObject();
    this.primaryGroupText.setText("Primarygroup");
  }
  public boolean store(IWContext iwc) {
    try {
      String pr = (String)this.fieldValues.get(this.primaryGroupFieldName);
      UserBusiness.setPermissionGroup(((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(this.getUserId()), ("".equals(pr))?null:new Integer(pr));
      return true;
    }
    catch (SQLException ex) {
      return false;
    }
  }
  public void lineUpFields() {
    this.resize(1,4);

    Table prTable = new Table(2,1);

    prTable.add(this.primaryGroupText,1,1);
    prTable.add(this.primaryGroupField,2,1);
    prTable.setHeight(1,"30");
    prTable.setWidth(1,"100");

    this.add(prTable,1,1);
    this.add(this.memberof,1,2);
    this.add(this.memberofFrame,1,3);

    this.setHeight(1,"30");
    this.setHeight(2,super.rowHeight);
    this.setHeight(4,super.rowHeight);

    this.add(this.addLink,1,4);
  }
  public boolean collect(IWContext iwc) {
    String prgroup = iwc.getParameter(this.primaryGroupFieldName);
    if(prgroup != null){
      this.fieldValues.put(this.primaryGroupFieldName,prgroup);
    }
    return true;
  }
  public void initializeFieldNames() {
    this.primaryGroupFieldName = "primary_group";
  }
  public void initializeFieldValues() {
    this.fieldValues.put(this.primaryGroupFieldName,"");
    this.updateFieldsDisplayStatus();
  }

  public void dispose(IWContext iwc){
    iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
    iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);
  }

  public void main(IWContext iwc) throws Exception {
    this.primaryGroupField.removeElements();
    this.primaryGroupField.addSeparator();
    List userGroups = UserBusiness.getUserGroupsDirectlyRelated(this.getUserId());
    if(userGroups != null){
      Iterator iter = userGroups.iterator();
      while (iter.hasNext()) {
        GenericGroup item = (GenericGroup)iter.next();
        this.primaryGroupField.addMenuElement(item.getID(),item.getName());
      }
    }
    this.primaryGroupField.setSelectedElement((String)this.fieldValues.get(this.primaryGroupFieldName));


    Object obj = UserBusiness.getUserGroupsDirectlyRelated(this.getUserId());
    if(obj != null){
      iwc.setSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED,obj);
    }else{
      iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
    }

    Object ob = UserBusiness.getUserGroupsNotDirectlyRelated(this.getUserId());
    if(ob != null){
      iwc.setSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED,ob);
    }else{
      iwc.removeSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);
    }
  }


  public static class GroupList extends Page {

    public GroupList(){
      super();
    }

    public Table getGroupTable(IWContext iwc){

      List direct = (List)iwc.getSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_DIRECTLY_RELATED);
      List notDirect = (List)iwc.getSessionAttribute(UserGroupList.SESSIONADDRESS_USERGROUPS_NOT_DIRECTLY_RELATED);

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

    public void main(IWContext iwc) throws Exception {
      this.getParentPage().setAllMargins(0);
      Table tb = getGroupTable(iwc);
      if(tb != null){
        this.add(tb);
      }
    }



  } // InnerClass


  public static class UserGroupSetter extends Window {

    private static final String FIELDNAME_SELECTION_DOUBLE_BOX = "related_groups";

    public UserGroupSetter(){
      super("add user to groups");
      this.setAllMargins(0);
      this.setWidth(400);
      this.setHeight(300);
      this.setBackgroundColor("#d4d0c8");
    }


    private void LineUpElements(IWContext iwc){

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



      String stringUserId = iwc.getParameter(UserGroupList.PARAMETER_USER_ID);
      int userId = Integer.parseInt(stringUserId);
      form.addParameter(UserGroupList.PARAMETER_USER_ID,stringUserId);

      List directGroups = UserBusiness.getUserGroupsDirectlyRelated(userId);

      Iterator iter = null;
      if(directGroups != null){
        iter = directGroups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          right.addElement(Integer.toString(((GenericGroup)item).getID()),((GenericGroup)item).getName());
        }
      }
      List notDirectGroups = UserBusiness.getAllGroupsNotDirectlyRelated(userId,iwc);
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

    public void main(IWContext iwc) throws Exception {


      String save = iwc.getParameter("save");
      if(save != null){
        String stringUserId = iwc.getParameter(UserGroupList.PARAMETER_USER_ID);
        int userId = Integer.parseInt(stringUserId);

        String[] related = iwc.getParameterValues(UserGroupSetter.FIELDNAME_SELECTION_DOUBLE_BOX);

        User user = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId);
        List currentRelationShip = UserBusiness.getUserGroupsDirectlyRelated(user);


        if(related != null){

          if(currentRelationShip != null){
            for (int i = 0; i < related.length; i++) {
              int id = Integer.parseInt(related[i]);
              GenericGroup gr = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(id);
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
              //((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(Integer.parseInt(related[i])).addUser(user);
              com.idega.core.data.GenericGroupBMPBean.addUser(Integer.parseInt(related[i]),user);
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
        LineUpElements(iwc);
      }

/*
      Enumeration enum = iwc.getParameterNames();
       System.err.println("--------------------------------------------------");
      if(enum != null){
        while (enum.hasMoreElements()) {
          Object item = enum.nextElement();
          if(item.equals("save")){
            this.close();
          }
          String val[] = iwc.getParameterValues((String)item);
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
    }

  } // InnerClass



} // Class

