package com.idega.presentation.plaf;



import com.idega.util.IWColor;

import javax.swing.SwingConstants;



/**

 * Title:        idegaWeb project

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author

 * @version 1.0

 */



public interface IWTabbedPaneUI  extends SwingConstants{

//  public TabbedPaneFrame getFrame();

  public TabPresentation getTabPresentation();

  public TabPagePresentation getTabPagePresentation();

  public void setMainColor(IWColor color);

  public IWColor getMainColor();

}
