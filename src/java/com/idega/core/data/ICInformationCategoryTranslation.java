package com.idega.core.data;


public interface ICInformationCategoryTranslation extends com.idega.data.IDOEntity
{
 public java.lang.String getDescription();
 public com.idega.core.data.ICLocale getLocale();
 public java.lang.String getName();
 public com.idega.core.data.ICInformationCategory getSuperInformationCategory();
 public void initializeAttributes();
 public void setDescription(java.lang.String p0);
 public void setLocale(int p0);
 public void setLocale(com.idega.core.data.ICLocale p0);
 public void setName(java.lang.String p0);
 public void setSuperInformationCategory(int p0);
 public void setSuperInformationCategory(com.idega.core.data.ICInformationCategory p0);
}
