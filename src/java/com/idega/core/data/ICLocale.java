package com.idega.core.data;


public interface ICLocale extends com.idega.data.IDOLegacyEntity
{
 public int getCountryId();
 public boolean getInUse();
 public int getLanguageId();
 public java.lang.String getLocale();
 public java.util.Locale getLocaleObject();
 public java.lang.String getName();
 public void setCountryId(int p0);
 public void setCountryId(java.lang.Integer p0);
 public void setInUse(boolean p0);
 public void setLanguageId(int p0);
 public void setLanguageId(java.lang.Integer p0);
 public void setLocale(java.lang.String p0);
}
