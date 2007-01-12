package com.idega.presentation;

import java.sql.SQLException;
import java.util.Vector;
import java.util.Iterator;

import com.idega.core.file.data.ICFile;

public class ImageRotater extends Block {

private static final String IMAGE_ROTATER = "image_rotater";
private ICFile _imageFile;
private String _width;
private String _height;
private String _alt;

  public void main(IWContext iwc) {
    if ( this._imageFile != null ) {
      boolean folder = true;
      int icObjectInstanceID = getICObjectInstanceID();
      Vector imageVector = (Vector) iwc.getApplicationAttribute(IMAGE_ROTATER+"_"+Integer.toString(icObjectInstanceID));

      if ( imageVector == null ) {
	imageVector = new Vector();
	if ( this._imageFile.getChildCount() > 0 && this._imageFile.getChildrenIterator() != null ) {
	  Iterator iter = this._imageFile.getChildrenIterator();
	  while (iter.hasNext()) {
	    imageVector.add(iter.next());
	  }
	}
	else {
		folder = false;
	}
	iwc.setApplicationAttribute(IMAGE_ROTATER+"_"+Integer.toString(icObjectInstanceID),imageVector);
      }

      int num = (int) (Math.random() * imageVector.size());

      Image image = null;

      try {
	if ( folder ) {
		image = new Image(((Integer)((ICFile)(imageVector.elementAt(num))).getPrimaryKey()).intValue());
	}
	else {
		image = new Image(((Integer)this._imageFile.getPrimaryKey()).intValue());
	}
      }
      catch (SQLException e) {
	e.printStackTrace(System.err);
      }

      if ( image != null ) {
	if ( this._width != null ) {
		image.setWidth(this._width);
	}
	if ( this._height != null ) {
		image.setHeight(this._height);
	}
	if ( this._alt != null ) {
		image.setName(this._alt);
	}
	add(image);
      }
    }
  }

  public void setFileFolder(ICFile file) {
    this._imageFile = file;
    getIWApplicationContext().removeApplicationAttribute(IMAGE_ROTATER+"_"+Integer.toString(getICObjectInstanceID()));
  }

  public void setWidth(String width) {
    this._width = width;
  }

  public void setHeight(String height) {
    this._height = height;
  }

  public void setAlt(String alt) {
    this._alt = alt;
  }
}// class ImageRotater