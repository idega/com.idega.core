package com.idega.core.component.data;

import com.idega.data.*;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ICObjectFieldBMPBean extends GenericEntity {

  private static String FIELD_IDENTIFIER = "FIELD_IDENTIFIER";
  private static String FIELD_METHOD = "FIELD_METHOD";

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("FIELD_NAME","Description",String.class);
    this.addManyToOneRelationship("IC_OBJECT_ID",ICObject.class);
    addAttribute(FIELD_IDENTIFIER,"Name",String.class);
    addAttribute(FIELD_METHOD,"Method",String.class);
  }
  public String getEntityName() {
    return "IC_OBJECT_FIELD";
  }

  public void setFieldName(String name){
    super.setColumn(FIELD_IDENTIFIER,name);
  }

  public String getFieldName(){
    return super.getStringColumnValue(FIELD_IDENTIFIER);
  }

  public String getSQLFieldName(){
    return getFieldName();
  }


}