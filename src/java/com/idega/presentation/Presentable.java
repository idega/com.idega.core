package com.idega.presentation;

import java.io.Serializable;
import java.io.Writer;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5 UNFINISHED -  UNDER DEVELOPMENT
 */

public interface Presentable extends Cloneable,Serializable{


  public void main(IWContext iwc);

  public void print(IWContext iwc);

  public String getID();

  public String setID(String id);

  public String toXML(Writer writer);

}
