package com.idega.core.user.presentation;

import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.textObject.Text;
import com.idega.core.user.business.UserBusiness;
import com.idega.util.datastructures.Collectable;
import java.util.Hashtable;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class UserGroupTab extends Table implements Collectable {

 private int groupId = -1;

  protected String columnHeight = "40";
  protected int fontSize = 2;

  protected Text proxyText;

  protected UserBusiness business;

  protected Hashtable fieldValues;


  public UserGroupTab() {
    super();
    business = new UserBusiness();
    init();
    this.setCellpadding(0);
    this.setCellspacing(0);
    this.setWidth("370");
    fieldValues = new Hashtable();
    initializeFieldNames();
    initializeFields();
    initializeTexts();
    initializeFieldValues();
    lineUpFields();
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

  private void initProxyText(){
    proxyText = new Text("");
    proxyText.setFontSize(fontSize);

  }

  public Text getTextObject(){
    if(proxyText == null){
      initProxyText();
    }
    return (Text)proxyText.clone();
  }

  public void setGroupId(int id){
    groupId = id;
    initFieldContents();
  }

  public int getGroupId(){
    return groupId;
  }


}