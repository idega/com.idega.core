package com.idega.presentation;

import java.sql.SQLException;
import java.util.Vector;
import java.util.Iterator;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICMimeType;
import com.idega.presentation.Block;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;

public class ImageRotater extends Block {

private ICFile _imageFile;
private String _width;
private String _height;
private String _alt;

  public void main(IWContext iwc) {
    if ( _imageFile != null ) {
      boolean folder = true;
      Vector imageVector = new Vector();


      if ( _imageFile.getChildCount() > 0 ) {
        Iterator iter = _imageFile.getChildren();
        while (iter.hasNext()) {
          imageVector.add((ICFile)iter.next());
        }
      }
      else
        folder = false;

      int num = (int) (Math.random() * imageVector.size());

      Image image = null;

      try {
        if ( folder )
          image = new Image(((ICFile)(imageVector.elementAt(num))).getID());
        else
          image = new Image(_imageFile.getID());
      }
      catch (SQLException e) {
        e.printStackTrace(System.err);
      }

      if ( image != null ) {
        if ( _width != null ) image.setWidth(_width);
        if ( _height != null ) image.setHeight(_height);
        if ( _alt != null ) image.setName(_alt);
        add(image);
      }
    }
  }

  public void setFileFolder(ICFile file) {
    _imageFile = file;
  }

  public void setWidth(String width) {
    _width = width;
  }

  public void setHeight(String height) {
    _height = height;
  }

  public void setAlt(String alt) {
    _alt = alt;
  }

}// class ImageRotater