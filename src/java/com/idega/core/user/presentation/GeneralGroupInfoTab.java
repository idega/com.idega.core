package com.idega.core.user.presentation;

import java.util.Iterator;
import java.util.List;

import com.idega.core.data.GenericGroup;
import com.idega.core.user.business.UserGroupBusiness;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SelectionDoubleBox;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
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

public class GeneralGroupInfoTab extends UserGroupTab implements Disposable{


  private TextInput nameField;
  private TextArea descriptionField;

  private Text nameText;
  private Text descriptionText;

  private String nameFieldName;
  private String descriptionFieldName;

  private Link addLink;
  private IFrame memberofFrame;
  public static final String PARAMETER_GROUP_ID = "ic_group_id";
  public static final String SESSIONADDRESS_GROUPS_DIRECTLY_RELATED = "ic_group_ic_group_direct_GGIT";
  public static final String SESSIONADDRESS_GROUPS_NOT_DIRECTLY_RELATED = "ic_group_ic_group_not_direct_GGIT";

  protected Text memberof;

  public GeneralGroupInfoTab() {
    super();
    this.setName("General");
  }

  public void initFieldContents() {
    this.addLink.setWindowToOpen(GeneralGroupInfoTab.GroupGroupSetter.class);
    this.addLink.addParameter(GeneralGroupInfoTab.PARAMETER_GROUP_ID,this.getGroupId());

     try{
      GenericGroup group = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(getGroupId());

      this.fieldValues.put(this.nameFieldName,(group.getName() != null) ? group.getName():"" );
      this.fieldValues.put(this.descriptionFieldName,(group.getDescription() != null) ? group.getDescription():"" );
      this.updateFieldsDisplayStatus();

    }catch(Exception e){
      System.err.println("GeneralGroupInfoTab error initFieldContents, GroupId : " + getGroupId());
    }


  }
  public void updateFieldsDisplayStatus() {
    this.nameField.setContent((String)this.fieldValues.get(this.nameFieldName));

    this.descriptionField.setContent((String)this.fieldValues.get(this.descriptionFieldName));
  }
  public void initializeFields() {


    this.nameField = new TextInput(this.nameFieldName);
    this.nameField.setLength(26);

    this.descriptionField = new TextArea(this.descriptionFieldName);
    this.descriptionField.setHeight(5);
    this.descriptionField.setWidth(43);
    this.descriptionField.setWrap(true);

    this.memberofFrame = new IFrame("ic_user_memberof_ic_group",GeneralGroupInfoTab.GroupList.class);
    this.memberofFrame.setHeight(150);
    this.memberofFrame.setWidth(367);
    this.memberofFrame.setScrolling(IFrame.SCROLLING_YES);

    this.addLink = new Link("  Add/Remove  ");

  }
  public void initializeTexts() {

    this.nameText = this.getTextObject();
    this.nameText.setText("Name:");

    this.descriptionText = getTextObject();
    this.descriptionText.setText("Description:");

    this.memberof = this.getTextObject();
    this.memberof.setText("Member of:");


  }
  public boolean store(IWContext iwc) {
    try{
      if(getGroupId() > -1){

        GenericGroup group = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(getGroupId());
        group.setName((String)this.fieldValues.get(this.nameFieldName));
        group.setDescription((String)this.fieldValues.get(this.descriptionFieldName));

        group.update();

      }
    }catch(Exception e){
      //return false;
      e.printStackTrace(System.err);
      throw new RuntimeException("update group exception");
    }
    return true;
  }
  public void lineUpFields() {
    this.resize(1,5);
    this.setCellpadding(0);
    this.setCellspacing(0);

    Table nameTable = new Table(2,1);
    nameTable.setCellpadding(0);
    nameTable.setCellspacing(0);
    nameTable.setWidth(1,1,"50");
    nameTable.add(this.nameText,1,1);
    nameTable.add(this.nameField,2,1);
    this.add(nameTable,1,1);

    Table descriptionTable = new Table(1,2);
    descriptionTable.setCellpadding(0);
    descriptionTable.setCellspacing(0);
    descriptionTable.setHeight(1,this.rowHeight);
    descriptionTable.add(this.descriptionText,1,1);
    descriptionTable.add(this.descriptionField,1,2);
    this.add(descriptionTable,1,2);

    this.add(this.memberof,1,3);
    this.add(this.memberofFrame,1,4);

    this.setHeight(3,"30");
    this.setHeight(1,super.rowHeight);
    this.setHeight(5,super.rowHeight);

    this.add(this.addLink,1,5);
  }

  public boolean collect(IWContext iwc) {
    if(iwc != null){

      String gname = iwc.getParameter(this.nameFieldName);
      String desc = iwc.getParameter(this.descriptionFieldName);

      if(gname != null){
        this.fieldValues.put(this.nameFieldName,gname);
      }

      if(desc != null){
        this.fieldValues.put(this.descriptionFieldName,desc);
      }

      this.updateFieldsDisplayStatus();

      return true;
    }
    return false;

  }
  public void initializeFieldNames() {
    this.descriptionFieldName = "UM_group_desc";
    this.nameFieldName = "UM_group_name";
  }
  public void initializeFieldValues() {
    this.fieldValues.put(this.nameFieldName,"");
    this.fieldValues.put(this.descriptionFieldName,"");

    this.updateFieldsDisplayStatus();
  }

  public void dispose(IWContext iwc){
    iwc.removeSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_DIRECTLY_RELATED);
    iwc.removeSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_NOT_DIRECTLY_RELATED);
  }

  public void main(IWContext iwc) throws Exception {
    Object obj = UserGroupBusiness.getGroupsContainingDirectlyRelated(this.getGroupId());
    if(obj != null){
      iwc.setSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_DIRECTLY_RELATED,obj);
    }else{
      iwc.removeSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_DIRECTLY_RELATED);
    }

    Object ob = UserGroupBusiness.getGroupsContainingNotDirectlyRelated(this.getGroupId());
    if(ob != null){
      iwc.setSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_NOT_DIRECTLY_RELATED,ob);
    }else{
      iwc.removeSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_NOT_DIRECTLY_RELATED);
    }
  }


  public static class GroupList extends Page {

    public GroupList(){
      super();
    }

    public Table getGroupTable(IWContext iwc){

      List direct = (List)iwc.getSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_DIRECTLY_RELATED);
      List notDirect = (List)iwc.getSessionAttribute(GeneralGroupInfoTab.SESSIONADDRESS_GROUPS_NOT_DIRECTLY_RELATED);

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


  public static class GroupGroupSetter extends Window {

    private static final String FIELDNAME_SELECTION_DOUBLE_BOX = "related_groups";

    public GroupGroupSetter(){
      super("add groups to groups");
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


      SelectionDoubleBox sdb = new SelectionDoubleBox(GroupGroupSetter.FIELDNAME_SELECTION_DOUBLE_BOX,"Not in","In");

      SelectionBox left = sdb.getLeftBox();
      left.setHeight(8);
      left.selectAllOnSubmit();


      SelectionBox right = sdb.getRightBox();
      right.setHeight(8);
      right.selectAllOnSubmit();



      String stringGroupId = iwc.getParameter(GeneralGroupInfoTab.PARAMETER_GROUP_ID);
      int groupId = Integer.parseInt(stringGroupId);
      form.addParameter(GeneralGroupInfoTab.PARAMETER_GROUP_ID,stringGroupId);

      List directGroups = UserGroupBusiness.getGroupsContainingDirectlyRelated(groupId);

      Iterator iter = null;
      if(directGroups != null){
        iter = directGroups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          right.addElement(Integer.toString(((GenericGroup)item).getID()),((GenericGroup)item).getName());
        }
      }
      List notDirectGroups = UserGroupBusiness.getAllGroupsNotDirectlyRelated(groupId,iwc);
      if(notDirectGroups != null){
        iter = notDirectGroups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          left.addElement(Integer.toString(((GenericGroup)item).getID()),((GenericGroup)item).getName());
        }
      }

      //left.addSeparator();
      //right.addSeparator();

      frameTable.setAlignment(2,2,"center");
      frameTable.add("GroupId: "+groupId,2,1);
      frameTable.add(sdb,2,2);
      frameTable.add(new SubmitButton("  Save  ","save","true"),2,3);
      frameTable.setAlignment(2,3,"right");
      form.add(frameTable);
      this.add(form);
    }

    public void main(IWContext iwc) throws Exception {


      String save = iwc.getParameter("save");
      if(save != null){
        String stringGroupId = iwc.getParameter(GeneralGroupInfoTab.PARAMETER_GROUP_ID);
        int groupId = Integer.parseInt(stringGroupId);

        String[] related = iwc.getParameterValues(GroupGroupSetter.FIELDNAME_SELECTION_DOUBLE_BOX);

        GenericGroup group = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId);
        List currentRelationShip = group.getParentGroups();


        if(related != null){

          if(currentRelationShip != null){
            for (int i = 0; i < related.length; i++) {
              int id = Integer.parseInt(related[i]);
              GenericGroup gr = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(id);
              if(!currentRelationShip.remove(gr)){
                gr.addGroup(group);
              }
            }

            Iterator iter = currentRelationShip.iterator();
            while (iter.hasNext()) {
              Object item = iter.next();
              ((GenericGroup)item).removeGroup(group);
            }

          } else{
            for (int i = 0; i < related.length; i++) {
              ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(Integer.parseInt(related[i])).addGroup(group);
            }
          }

        }else if (currentRelationShip != null){
            Iterator iter = currentRelationShip.iterator();
            while (iter.hasNext()) {
              Object item = iter.next();
              ((GenericGroup)item).removeGroup(group);
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



}
