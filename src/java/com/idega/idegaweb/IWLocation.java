package com.idega.idegaweb;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface IWLocation extends Cloneable{

  public String getTarget();

  public boolean isTopPage();
  public boolean isInPopUpWindow();

  public boolean isInFrameSet();
  public Class getApplicationClass();
  public String getApplicationClassName();

  public String getLocationString();

  public int getICObjectInstanceID();



  public void setICObjectInstanceID(int id);

  public void setTarget(String target);

  public void isTopPage(boolean value);
  public void isInPopUpWindow(boolean value);

  public void isInFrameSet(boolean value);
  public void setApplicationClass(Class appClass);

  public Object clone();


}