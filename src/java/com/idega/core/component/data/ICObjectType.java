package com.idega.core.component.data;


public interface ICObjectType extends com.idega.data.IDOEntity,com.idega.core.component.data.BundleComponent
{
 public java.lang.Class getFinalReflectionClass();
 public java.lang.String getFinalReflectionClassName();
 public java.lang.String[] getMethodStartFilters();
 public java.lang.String getMethodStartFiltersString();
 public java.lang.String getName();
 public java.lang.Class getPrimaryKeyClass();
 public java.lang.Class[] getRequiredInterfaces();
 public java.lang.String getRequiredInterfacesString();
 public java.lang.Class getRequiredSuperClass();
 public java.lang.String getRequiredSuperClassName();
 public java.lang.String getType();
 public java.util.Vector seperateStringIntoVector(java.lang.String p0);
 public void setFinalReflectionClassName(java.lang.String p0);
 public void setMethodStartFiltersString(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setRequiredInterfacesString(java.lang.String p0);
 public void setRequiredSuperClassName(java.lang.String p0);
 public void setType(java.lang.String p0);
 public java.lang.String type();
 public boolean validateInterfaces(java.lang.Class p0);
 public boolean validateSuperClasses(java.lang.Class p0);
}
