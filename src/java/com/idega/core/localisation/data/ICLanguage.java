package com.idega.core.localisation.data;


public interface ICLanguage extends com.idega.data.IDOEntity
{
 public java.lang.String getDescription();
 public java.lang.String getIsoAbbreviation();
 public java.lang.String getName();
 public void initializeAttributes();
 public void setDescription(java.lang.String p0);
 public void setIsoAbbreviation(java.lang.String p0);
 public void setName(java.lang.String p0);

	public String getLocalizedName();
}
