package com.idega.core.accesscontrol.app;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.core.component.data.ICObject;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.business.UserGroupBusiness;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.FrameSet;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.app.IWApplication;
import com.idega.presentation.app.IWApplicationComponent;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SelectionDoubleBox;
import com.idega.presentation.ui.SubmitButton;


/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AccessControllerApp extends IWApplication {

  public static String _FRAME_NAME_HEADER = "accessapp_header";
  public static String _FRAME_NAME_MAIN = "accessapp_main";
  public static String _FRAME_NAME_FOOTER = "accessapp_footer";

  public AccessControllerApp() {
    super("AccessController",550,370);
    super.add(AppHeader.class);
    super.add(AppMainFrame.class);
    super.add(AppFooter.class);
    super.setFrameName(1,_FRAME_NAME_HEADER);
    super.setFrameName(2,_FRAME_NAME_MAIN);
    super.setFrameName(2,_FRAME_NAME_FOOTER);
    this.setSpanPixels(1,25);
    this.setSpanPixels(3,35);
    this.setScrollbar(false);
    this.setScrolling(1,false);
    this.setScrolling(2,false);
    this.setScrolling(3,false);
  }


  public static class AppHeader extends Page{
    private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";

    public AppHeader(){
      super();
      this.setBackgroundColor("#0E2456");
      this.setAllMargins(0);
    }

    public void main(IWContext iwc) throws Exception {
      IWBundle iwbCore = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);

      Table headerTable = new Table();
      headerTable.setCellpadding(0);
      headerTable.setCellspacing(0);
      headerTable.setWidth("100%");
      headerTable.setAlignment(2,1,"right");
      Image idegaweb = iwbCore.getImage("/editorwindow/idegaweb.gif","idegaWeb");
      headerTable.add(idegaweb,1,1);

      this.add(headerTable);
    }

  }

  public static class AppFooter extends Page{
    public AppFooter(){
      super();
      this.setBackgroundColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      this.setAllMargins(0);
    }

    public void main(IWContext iwc) throws Exception {
      if(iwc.getParameter("close") != null){
        //
      }else{
        Table footerTable = new Table(2,1);
        footerTable.setCellpadding(0);
        footerTable.setCellspacing(0);
        footerTable.setWidth(2,"20");
        footerTable.setHeight("100%");
        footerTable.setVerticalAlignment(1,1,"middle");
        /*SubmitButton close = new SubmitButton(" Close ");
        close.setAttribute("OnClick","parent.close()");*/

        CloseButton close = new CloseButton();

        footerTable.add(close,1,1);
/*
        Form myForm = new Form();
        myForm.add(footerTable);
        this.add(myForm);
  */
        this.add(footerTable);
      }
    }
  }

  public static class AppMainFrame extends FrameSet{

    public static String _FRAME_NAME_1 = "accessapp_main_frame1";
    public static String _FRAME_NAME_2 = "accessapp_main_frame2";

    public AppMainFrame(){
      super();
      this.add(ICObjectLinkList.class);
      this.add(IBPermissionFrame.class);
      this.setFrameName(1,_FRAME_NAME_1);
      this.setFrameName(2,_FRAME_NAME_2);
      this.setSpanPixels(1,220);
      this.setHorizontal();
      this.setScrollbar(false);
      this.setScrolling(2,false);
    }

    public static class ICObjectLinkList extends IWApplicationComponent {

      public static Link bundleLinkTemplate;
      public static Link ICObjectLinkTemplate;

      public ICObjectLinkList() {
        initBundleLinkTemplate();
        initICObjectLinkTemplate();
      }

      public void initBundleLinkTemplate(){
        bundleLinkTemplate = new Link("",IBPermissionFrame.class,AppMainFrame._FRAME_NAME_2);

        bundleLinkTemplate.addParameter(IBPermissionFrame._PARAMETER_DISPOSE,"true");
        bundleLinkTemplate.addParameter(IBPermissionFrame._PARAMETERSTRING_PERMISSION_CATEGORY,AccessController.CATEGORY_BUNDLE);
      }

      public List tranceformBundleListToLinkList(List iwBundleList){
        if(iwBundleList != null){
          List linkList = new Vector(iwBundleList.size());
          Iterator iter = iwBundleList.iterator();
          while (iter.hasNext()) {
            IWBundle item = (IWBundle)iter.next();
            Link bundleLink = (Link)bundleLinkTemplate.clone();
            bundleLink.setText(item.getBundleName());
            bundleLink.addParameter(IBPermissionFrame._PARAMETERSTRING_IDENTIFIER,item.getBundleIdentifier());
            bundleLink.addParameter(IBPermissionFrame._PARAMETER_OBJ_NAME,item.getBundleName());
            linkList.add(bundleLink);
          }
          return linkList;
        }else{
          return null;
        }
      }


      public void initICObjectLinkTemplate(){
        ICObjectLinkTemplate = new Link("",IBPermissionFrame.class,AppMainFrame._FRAME_NAME_2);
        ICObjectLinkTemplate.addParameter(IBPermissionFrame._PARAMETER_DISPOSE,"true");
        ICObjectLinkTemplate.addParameter(IBPermissionFrame._PARAMETERSTRING_PERMISSION_CATEGORY,AccessController.CATEGORY_OBJECT);
      }

      public List tranceformICObjectListToLinkList(List ICObjectList){
        if(ICObjectList != null){
          List linkList = new Vector(ICObjectList.size());
          Iterator iter = ICObjectList.iterator();
          while (iter.hasNext()) {
            ICObject item = (ICObject)iter.next();
            Link icObjcetLink = (Link)ICObjectLinkTemplate.clone();
            icObjcetLink.setText(item.getName());
            icObjcetLink.addParameter(IBPermissionFrame._PARAMETERSTRING_IDENTIFIER,item.getID());
            icObjcetLink.addParameter(IBPermissionFrame._PARAMETER_OBJ_NAME,item.getName());
            linkList.add(icObjcetLink);
          }
          return linkList;
        }else{
          return null;
        }
      }

      public void main(IWContext iwc) throws Exception {
        IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);

        ICObject staticICO = (ICObject)com.idega.core.component.data.ICObjectBMPBean.getStaticInstance(ICObject.class);

        //List bundles = iwc.getApplication().getRegisteredBundles();
        //List bundleLinks = tranceformBundleListToLinkList(bundles);

        List elements = EntityFinder.findAllByColumn(staticICO,com.idega.core.component.data.ICObjectBMPBean.getObjectTypeColumnName(),com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_ELEMENT);
        List elementLinks = tranceformICObjectListToLinkList(elements);

        List blocks = EntityFinder.findAllByColumn(staticICO,com.idega.core.component.data.ICObjectBMPBean.getObjectTypeColumnName(),com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_BLOCK);
        List blockLinks = tranceformICObjectListToLinkList(blocks);

        List applications = EntityFinder.findAllByColumn(staticICO,com.idega.core.component.data.ICObjectBMPBean.getObjectTypeColumnName(),com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_APPLICATION);
        List applicationLinks = tranceformICObjectListToLinkList(applications);


        String sBundles = iwrb.getLocalizedString("bundle_header","Bundles");
        String sElements = iwrb.getLocalizedString("elements_header","Elements");
        String sBlocks = iwrb.getLocalizedString("blocks_header","Blocks");
        String sApplications = iwrb.getLocalizedString("applicaitions_header","Applications");

        Text labelText = new Text("");
        labelText.setFontSize(Text.FONT_SIZE_12_HTML_3);
        labelText.setBold();

        Text tBundles = (Text)labelText.clone();
        tBundles.setText(sBundles);
        Text tElements = (Text)labelText.clone();
        tElements.setText(sElements);
        Text tBlocks = (Text)labelText.clone();
        tBlocks.setText(sBlocks);
        Text tApplications = (Text)labelText.clone();
        tApplications.setText(sApplications);

        Table tempTable = new Table();
        int index = 1;

        /*
        if(bundleLinks != null){
          tempTable.add(tBundles,1,index++);
          Iterator iter = bundleLinks.iterator();
          while (iter.hasNext()) {
            PresentationObject item = (PresentationObject)iter.next();
            tempTable.add(item,1,index++);
          }
        }
        */

        if(elementLinks != null){
          tempTable.add(tElements,1,index++);
          Iterator iter2 = elementLinks.iterator();
          while (iter2.hasNext()) {
            PresentationObject item2 = (PresentationObject)iter2.next();
            tempTable.add(item2,1,index++);
          }
        }


        if(blockLinks != null){
          tempTable.add(tBlocks,1,index++);
          Iterator iter3 = blockLinks.iterator();
          while (iter3.hasNext()) {
            PresentationObject item3 = (PresentationObject)iter3.next();
            tempTable.add(item3,1,index++);
          }
        }


        if(applicationLinks != null){
          tempTable.add(tApplications,1,index++);
          Iterator iter4 = applicationLinks.iterator();
          while (iter4.hasNext()) {
            PresentationObject item4 = (PresentationObject)iter4.next();
            tempTable.add(item4,1,index++);
          }
        }

        this.add(tempTable);

      }

    }


    public static class IBPermissionFrame extends IWApplicationComponent {

      public static final String _PARAMETERSTRING_IDENTIFIER = AccessController._PARAMETERSTRING_IDENTIFIER;
      public static final String _PARAMETERSTRING_PERMISSION_CATEGORY = AccessController._PARAMETERSTRING_PERMISSION_CATEGORY;
      public static final String _PARAMETER_DISPOSE = "dispose";
      public static final String _PARAMETER_OBJ_NAME = "accessapp_permissionframe_obj_name";

      private static final String permissionKeyParameterString = "permission_type";
      private static final String lastPermissionKeyParameterString = "last_permission_key";
      private static final String permissionGroupParameterString = "permission_groups";
      private static final String SessionAddressPermissionMap = "ib_permission_hashtable";
      private static final String SessionAddressPermissionMapOldValue = "ib_permission_hashtable_old_value";
      private boolean collectOld = false;



      private Table lineUpElements(IWContext iwc,String permissionType) throws Exception{

        String identifier = iwc.getParameter(_PARAMETERSTRING_IDENTIFIER);
        String category = iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY);
        String objname = iwc.getParameter(_PARAMETER_OBJ_NAME);

        Table myTable = new Table(1,1);
        myTable.setCellpadding(2);
        myTable.setHeight("100%");
        myTable.setWidth("100%");

        if(objname != null){
          Text objName = new Text(Text.NON_BREAKING_SPACE+objname);
          objName.setFontFace(Text.FONT_FACE_ARIAL);
          objName.setFontSize(Text.FONT_SIZE_12_HTML_3);
          objName.setBold();

          myTable.add(objName);
          myTable.add(Text.getBreak());
          myTable.add(Text.getBreak());
        }
        Table frameTable = new Table(1,4);
        if(identifier != null && category != null){
          int intPermissionCategory = Integer.parseInt(category);

          frameTable.setAlignment(1,1,"left");
          frameTable.setAlignment(1,2,"left");
          frameTable.setAlignment(1,3,"left");
          frameTable.setAlignment(1,4,"right");

          // PermissionString
          Text permissionKeyText = new Text("Permission Key");

          DropdownMenu permissionTypes = new DropdownMenu(permissionKeyParameterString);
          permissionTypes.keepStatusOnAction();
          permissionTypes.setOnChange("selectAllInSelectionBox(this.form."+permissionGroupParameterString+"_left);selectAllInSelectionBox(this.form."+permissionGroupParameterString+")");
          permissionTypes.setToSubmit();

          String[] keys = null;

          Class objectClass = null;
          switch (intPermissionCategory) {
            case AccessControl.CATEGORY_OBJECT_INSTANCE :
              objectClass = ICObjectBusiness.getInstance().getICObjectClassForInstance(Integer.parseInt(identifier));
              keys = iwc.getAccessController().getICObjectPermissionKeys(objectClass);
              break;
            case AccessControl.CATEGORY_OBJECT :
              objectClass = ICObjectBusiness.getInstance().getICObjectClass(Integer.parseInt(identifier));
              keys = iwc.getAccessController().getICObjectPermissionKeys(objectClass);
              break;
            case AccessControl.CATEGORY_BUNDLE :
              keys = iwc.getAccessController().getBundlePermissionKeys(identifier);
              break;
            case AccessControl.CATEGORY_PAGE_INSTANCE :
              keys = iwc.getAccessController().getPagePermissionKeys();
              break;
            case AccessControl.CATEGORY_PAGE :
              keys = iwc.getAccessController().getPagePermissionKeys();
              break;
            case AccessControl.CATEGORY_JSP_PAGE :
              keys = new String[0];
              break;
          }


//          switch (intPermissionCategory) {
//            case AccessController._CATEGORY_OBJECT_INSTANCE :
//              keys = iwc.getAccessController().getICObjectPermissionKeys(ICObjectBusiness.getInstance().getICObjectClassForInstance(Integer.parseInt(identifier)));
//              break;
//            case AccessController._CATEGORY_OBJECT :
//              keys = iwc.getAccessController().getICObjectPermissionKeys(ICObjectBusiness.getInstance().getICObjectClass(Integer.parseInt(identifier)));
//              break;
//            case AccessController._CATEGORY_BUNDLE :
//              keys = iwc.getAccessController().getBundlePermissionKeys(identifier);
//              break;
//            case AccessController._CATEGORY_PAGE_INSTANCE :
//              keys = iwc.getAccessController().getPagePermissionKeys();
//              break;
//            case AccessController._CATEGORY_PAGE :
//              keys = iwc.getAccessController().getPagePermissionKeys();
//              break;
//            case AccessController._CATEGORY_JSP_PAGE :
//              keys = new String[0];
//              break;
//          }


          for (int i = 0; i < keys.length; i++) {
            permissionTypes.addMenuElement(keys[i],keys[i]);
          }

          if(permissionType != null){
            permissionTypes.setSelectedElement(permissionType);
          } else if(keys.length > 0){
            permissionType = keys[0];
          }


          //PermissionGroups
          SelectionDoubleBox permissionBox = new SelectionDoubleBox(permissionGroupParameterString,"Unspecified","Allowed");

          SelectionBox left = permissionBox.getLeftBox();
            left.setHeight(8);
            left.selectAllOnSubmit();


          SelectionBox right = permissionBox.getRightBox();
            right.setHeight(8);
            right.selectAllOnSubmit();


          Map hash = (Map)iwc.getSessionAttribute(this.SessionAddressPermissionMap);
          List directGroups = null;
          if(hash != null && hash.get(permissionType)!=null){
            directGroups = UserGroupBusiness.getGroups((String[])hash.get(permissionType));
            collectOld = false;
          } else {
            directGroups = iwc.getAccessController().getAllowedGroups(intPermissionCategory, identifier,permissionType);
            collectOld = true;

          }



          Iterator iter = null;
          if(directGroups != null){
            iter = directGroups.iterator();
            if(collectOld){
              List oldValueIDs = new Vector();
              while (iter.hasNext()) {
                Object item = iter.next();
                String groupId = Integer.toString(((GenericGroup)item).getID());
                right.addElement(groupId,((GenericGroup)item).getName());
                oldValueIDs.add(groupId);
              }
              this.collectOldValues(iwc,oldValueIDs, permissionType);
            } else {
              while (iter.hasNext()) {
                Object item = iter.next();
                String groupId = Integer.toString(((GenericGroup)item).getID());
                right.addElement(groupId,((GenericGroup)item).getName());
              }
            }

          }

          List notDirectGroups = iwc.getAccessController().getAllPermissionGroups();
          if(notDirectGroups != null){
            if(directGroups != null){
              notDirectGroups.removeAll(directGroups);
            }
            iter = notDirectGroups.iterator();
            while (iter.hasNext()) {
              Object item = iter.next();
              left.addElement(Integer.toString(((GenericGroup)item).getID()),((GenericGroup)item).getName());
            }
          }



          // Submit
          SubmitButton save = new SubmitButton(" Save ","subm","save");
          //SubmitButton cancel = new SubmitButton("  Cancel  ","subm","cancel");

          //buttonTable.add(submit,1,1);
          //buttonTable.add(cancel,2,1);

          frameTable.add(permissionKeyText,1,1);
          frameTable.add(permissionTypes,1,2);
          /*frameTable.add(new SubmitButton("->"),1,2);*/
          frameTable.add(permissionBox,1,3);
          frameTable.add(save,1,4);
          frameTable.add(new HiddenInput(lastPermissionKeyParameterString, permissionType ));

        }
        myTable.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
        myTable.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_MIDDLE);
        myTable.add(frameTable, 1, 1);
        return myTable;
      }

      public void showMessage(String message){
        Table t = new Table();
        Text messageText = new Text(message,true,false,false);
        messageText.setFontSize(3);
        t.add(messageText);
        this.add(t);
      }


      public void main(IWContext iwc)throws Exception{

          if(iwc.getParameter(_PARAMETER_DISPOSE) != null){
            this.dispose(iwc);
          }

          String submit = iwc.getParameter("subm");
          Form myForm = new Form();
          myForm.maintainParameter(_PARAMETERSTRING_IDENTIFIER);
          myForm.maintainParameter(_PARAMETERSTRING_PERMISSION_CATEGORY);
          myForm.maintainParameter(_PARAMETER_OBJ_NAME);

          if(submit != null){
            if(submit.equals("save")){
              String permissionType = iwc.getParameter(permissionKeyParameterString);
              if(permissionType != null){
                this.collect(iwc);
                this.store(iwc);
                this.dispose(iwc);
                this.showMessage(iwc.getParameter(_PARAMETER_OBJ_NAME) +" saved");
              }else {
                this.showMessage("ERROR: nothing to save");
              }
              //this.setParentToReload();
            }/*else if(submit.equals("cancel")){
              this.dispose(iwc);
            } */else {
              String permissionType = iwc.getParameter(permissionKeyParameterString);
              if(permissionType != null){
                collect(iwc);
              }
              myForm.add(this.lineUpElements(iwc,permissionType));
            }
          }else{
            String permissionType = iwc.getParameter(permissionKeyParameterString);
            if(permissionType != null){
              collect(iwc);
            }
            myForm.add(this.lineUpElements(iwc,permissionType));
          }
          this.add(myForm);

      }




      private void collect(IWContext iwc){
        Object obj = iwc.getSessionAttribute(SessionAddressPermissionMap);
        Map hash = null;
        if(obj != null){
          hash = (Map)obj;
          if(!hash.get(_PARAMETERSTRING_IDENTIFIER).equals(iwc.getParameter(_PARAMETERSTRING_IDENTIFIER)) && !hash.get(_PARAMETERSTRING_PERMISSION_CATEGORY).equals(iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY))){
            hash = new Hashtable();
            hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
            hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
            iwc.setSessionAttribute(SessionAddressPermissionMap,hash);
          }
        }else{
          hash = new Hashtable();
          hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
          hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
          iwc.setSessionAttribute(SessionAddressPermissionMap,hash);
        }
        String[] groups = iwc.getParameterValues(permissionGroupParameterString);
        if(groups != null){
          hash.put(iwc.getParameter(lastPermissionKeyParameterString),groups);
        } else{
          hash.put(iwc.getParameter(lastPermissionKeyParameterString),new String[0]);
        }
      }


      private void collectOldValues(IWContext iwc, List groups, String permissionKey){
        Object obj = iwc.getSessionAttribute(SessionAddressPermissionMapOldValue);
        Map hash = null;
        if(obj != null){
          hash = (Map)obj;
          if(!hash.get(_PARAMETERSTRING_IDENTIFIER).equals(iwc.getParameter(_PARAMETERSTRING_IDENTIFIER)) && !hash.get(_PARAMETERSTRING_PERMISSION_CATEGORY).equals(iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY))){
            hash = new Hashtable();
            hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
            hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
            iwc.setSessionAttribute(SessionAddressPermissionMapOldValue,hash);
          }
        }else{
          hash = new Hashtable();
          hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
          hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
          iwc.setSessionAttribute(SessionAddressPermissionMapOldValue,hash);
        }

        if(hash.get(permissionKey) == null){
          if(groups != null){
            hash.put(permissionKey,groups);
          } else{
            hash.put(permissionKey,new Vector());
          }
        }
      }



      private void store(IWContext iwc) throws Exception {
        Object obj = iwc.getSessionAttribute(SessionAddressPermissionMap);
        Object oldObj= iwc.getSessionAttribute(SessionAddressPermissionMapOldValue);
        if(obj != null && oldObj != null){
          Map map = (Map)obj;
          Map oldMap = (Map)oldObj;
          String instanceId = (String)map.remove(_PARAMETERSTRING_IDENTIFIER);
          String category = (String)map.remove(_PARAMETERSTRING_PERMISSION_CATEGORY);

          if((instanceId != null && instanceId.equals(iwc.getParameter(_PARAMETERSTRING_IDENTIFIER)) && instanceId.equals(oldMap.get(_PARAMETERSTRING_IDENTIFIER)))&&(category != null && category.equals(iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY)) && category.equals(oldMap.get(_PARAMETERSTRING_PERMISSION_CATEGORY)))){
            Iterator iter = map.keySet().iterator();
            while (iter.hasNext()) {
              Object item = iter.next();
              String[] groups = (String[])map.get(item);
              List oldGroups = (List)oldMap.get(item);
              if(oldGroups == null){
                oldGroups = new Vector();
              }
              int intCategory = Integer.parseInt(category);
              for (int i = 0; i < groups.length; i++) {
                oldGroups.remove(groups[i]);
                iwc.getAccessController().setPermission(intCategory, iwc, groups[i],instanceId,(String)item,Boolean.TRUE);
              }
              if(oldGroups.size()>0){
                String[] groupsToRemove = new String[oldGroups.size()];
                Iterator iter2 = oldGroups.iterator();
                int index2 = 0;
                while (iter2.hasNext()) {
                  groupsToRemove[index2++] = (String)iter2.next();
                }
                AccessControl.removePermissionRecords(intCategory,iwc, instanceId,(String)item, groupsToRemove);
              }
            }
          }else{
            throw new RuntimeException("identifier or permissionCategory not set or does not match");
          }
        }
      }

      private void dispose(IWContext iwc){
        try {
          iwc.removeSessionAttribute(SessionAddressPermissionMap);
        }
        catch (Exception ex) {

        }
        try {
          iwc.removeSessionAttribute(SessionAddressPermissionMapOldValue);
        }
        catch (Exception ex) {

        }
      }


    }


  }





}
