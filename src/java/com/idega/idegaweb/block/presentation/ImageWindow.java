package com.idega.idegaweb.block.presentation;

import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Window;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class ImageWindow extends Window{

  public static final String prmImageId = "imid";
  public static final String prmInfo = "info";
  Image image = null;
  String info = null;

  public ImageWindow() {
    setResizable(true);
    setHeight(700);
  }

  public ImageWindow(Image image){
    setResizable(true);
    this.image = image;
  }

  public void main(IWContext iwc){
    if(iwc.isParameterSet(prmInfo))
      info = iwc.getParameter(prmInfo);
    if(iwc.isParameterSet(prmImageId)){
      try {
        int id = Integer.parseInt(iwc.getParameter(prmImageId));
        image = new Image(id);
      }
      catch (Exception ex) {

      }
    }
    Table T = new Table(1,2);
    if(image !=null){
      T.add(image,1,1);
      if(info!=null)
        T.add(info,1,2);
    }
    add(T);


  }


}
