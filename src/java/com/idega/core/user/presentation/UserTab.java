package com.idega.core.user.presentation;

import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.core.user.business.UserBusiness;
import com.idega.util.datastructures.Collectable;
import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;


/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class UserTab extends Table implements Collectable{

  private int userId = -1;

  private List errorStrings;

  protected String rowHeight = "37";
  /**
   * @deprecated replaced by rowHeight
   */
  protected String columnHeight = rowHeight;//"37";
  protected int fontSize = 2;

  protected Text proxyText;

  protected UserBusiness business;

  protected Hashtable fieldValues;


  public UserTab() {
    super();
    errorStrings = new Vector();
    business = new UserBusiness();
    fieldValues = new Hashtable();
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

  public abstract boolean collect(IWContext iwc);
  public abstract boolean store(IWContext iwc);
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

  public void setUserID(int id){
    userId = id;
    initFieldContents();
  }

  public int getUserId(){
    return userId;
  }

  public void addErrorMessage(String message){
    errorStrings.add(message);
  }


  public String[] clearErrorMessages(){
    String[] st = new String[errorStrings.size()];

    Iterator iter = errorStrings.iterator();
    int index = 0;
    while (iter.hasNext()) {
      st[index++] = (String)iter.next();
    }
    errorStrings.clear();

    return st;
  }

  public boolean someErrors(){
    return (0 < errorStrings.size());
  }

} // Class GeneralUserInfoTab
