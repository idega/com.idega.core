package com.idega.data;



/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public abstract class CategoryEntityBMPBean extends com.idega.data.GenericEntity implements com.idega.data.CategoryEntity {

  public static String getColumnCategoryId(){return "IC_CATEGORY_ID";}

  public CategoryEntityBMPBean(){
    super();
  }
  public CategoryEntityBMPBean(int id)throws java.sql.SQLException{
    super(id);
  }
  protected final void afterInitializeAttributes(){
    addAttribute(getColumnCategoryId(),"Category",true,true,Integer.class,"many-to_one",com.idega.core.data.ICCategory.class);
  }
  public final int getCategoryId(){
    return getIntColumnValue(getColumnCategoryId());
  }
  public final void setCategoryId(int ic_category_id){
    setColumn(getColumnCategoryId(),ic_category_id);
  }


}
