package com.idega.presentation;

import com.idega.presentation.ui.Window;
import com.idega.presentation.FrameSet;
import com.idega.presentation.IWContext;
import com.idega.presentation.TabbedPropertyPanel;
import com.idega.presentation.text.Text;


/**
 * Title:        IW
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class TabbedPropertyWindow extends Window {

  protected TabbedPropertyPanel panel = null;

  public TabbedPropertyWindow(){
    this(410,482);
  }

  public TabbedPropertyWindow(int width, int height){
    super(width,height);
    super.setScrollbar(false);
    super.setAllMargins(0);
    super.setTopMargin(3);
  }

  public void _main(IWContext iwc) throws Exception {
    this.empty();
    panel = TabbedPropertyPanel.getInstance(getSessionAddressString(), iwc );
    if(panel.justConstructed()){
      panel.setAlignment("center");
      panel.setVerticalAlignment("middle");
      initializePanel(iwc, panel);
    }

    if(panel.clickedCancel() || panel.clickedOk()){
      if(panel.clickedOk()){
        this.setParentToReload();
      }
      this.close();
      panel.dispose(iwc);
    }else{
      this.add(panel);
    }
    super._main(iwc);

  }

  public PresentationObject[] getAddedTabs(){
    if(panel != null){
      return panel.getAddedTabs();
    } else {
      throw new RuntimeException("TabbedPropertyPanel not set. TabbedPropertyPanel is set in main(IWContext iwc)");
    }
  }

  public abstract String getSessionAddressString();

  public abstract void initializePanel( IWContext iwc, TabbedPropertyPanel panel);



}