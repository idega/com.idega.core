package com.idega.core.user.presentation;

import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.core.user.business.UserBusiness;
import com.idega.util.datastructures.Collectable;
import java.util.Hashtable;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public abstract class UserGroupTab extends Table implements Collectable {

 private int groupId = -1;

  protected String rowHeight = "40";
  protected int fontSize = 2;

  protected Text proxyText;

  protected UserBusiness business;

  protected Hashtable fieldValues;


  public UserGroupTab() {
    super();
    this.business = new UserBusiness();
    init();
    this.setCellpadding(0);
    this.setCellspacing(0);
    this.setWidth("370");
    this.fieldValues = new Hashtable();
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

  public abstract boolean collect(IWContext iwc);
  public abstract boolean store(IWContext iwc);
  public abstract void initFieldContents();

  private void initProxyText(){
    this.proxyText = new Text("");
    this.proxyText.setFontSize(this.fontSize);

  }

  public Text getTextObject(){
    if(this.proxyText == null){
      initProxyText();
    }
    return (Text)this.proxyText.clone();
  }

  public void setGroupId(int id){
    this.groupId = id;
    initFieldContents();
  }

  public int getGroupId(){
    return this.groupId;
  }


}
