package com.idega.core.category.data;


public interface ICInformationCategoryTranslation extends com.idega.data.IDOEntity
{
 public java.lang.String getDescription();
 public com.idega.core.localisation.data.ICLocale getLocale();
 public java.lang.String getName();
 public com.idega.core.category.data.ICInformationCategory getSuperInformationCategory();
 public void initializeAttributes();
 public void setDescription(java.lang.String p0);
 public void setLocale(int p0);
 public void setLocale(com.idega.core.localisation.data.ICLocale p0);
 public void setName(java.lang.String p0);
 public void setSuperInformationCategory(int p0);
 public void setSuperInformationCategory(com.idega.core.category.data.ICInformationCategory p0);
}
