package com.idega.data;

import javax.ejb.*;

public interface TextEntity extends com.idega.data.IDOLegacyEntity
{
 public int getTextId();
 public void setTextId(int p0);
 public String getName();
 public String getInfo();
 public void setInfo(String info);
 public void setName(String name);
}
