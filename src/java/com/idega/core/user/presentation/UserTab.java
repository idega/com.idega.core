package com.idega.core.user.presentation;

import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.interfaceobject.TextInput;
import com.idega.jmodule.object.interfaceobject.TextArea;
import com.idega.jmodule.object.interfaceobject.DateInput;
import com.idega.jmodule.object.interfaceobject.DropdownMenu;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.textObject.Text;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.user.data.User;
import com.idega.util.datastructures.Collectable;
import com.idega.util.idegaTimestamp;
import java.util.Hashtable;
import java.util.StringTokenizer;


/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class UserTab extends Table implements Collectable{

  private int userId = -1;

  protected String columnHeight = "37";
  protected int fontSize = 2;


  public UserTab() {
    super();
    init();
    this.setCellpadding(0);
    this.setCellspacing(0);
    this.setWidth("370");
    initializeFieldNames();
    initializeFields();
    initializeTexts();
    initializeFieldValues();
    lineUpFields();
  }

  public UserTab(int userId){
    this();
    this.setUserID(userId);
  }

  public void init(){}
  public abstract void initializeFieldNames();
  public abstract void initializeFieldValues();
  public abstract void updateFieldsDisplayStatus();
  public abstract void initializeFields();
  public abstract void initializeTexts();
  public abstract void lineUpFields();

  public abstract boolean collect(ModuleInfo modinfo);
  public abstract boolean store(ModuleInfo modinfo);
  public abstract void initFieldContents();


  public void setUserID(int id){
    userId = id;
    initFieldContents();
  }

  public int getUserId(){
    return userId;
  }



  public void main(ModuleInfo modinfo) throws Exception {
    String id = modinfo.getParameter(BasicUserOverview.userIdParameterString);
    if(id != null){
      int newId = Integer.parseInt(id);
      if(userId != newId){
        this.setUserID(newId);
      }
    }
  }


} // Class GeneralUserInfoTab