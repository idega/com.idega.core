package com.idega.presentation;

import java.sql.SQLException;
import java.util.*;
import java.util.Iterator;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICMimeType;
import com.idega.presentation.Block;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.IWContext;
import com.idega.presentation.Script;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;

public class ImageSlideShow extends Block {

private static final String IMAGE_SLIDE_SHOW = "image_slide_show";
private ICFile fileFolder;
private String width;
private String height;
private String alt;
private String preloadImages = "";
private int fileId = -1;
private List listOfFiles = null;
private boolean useJavascript = true;

  public void main(IWContext iwc) {
    IWBundle iwb = getBundle(iwc);
    if(fileFolder == null && fileId > 1){
      try {
        fileFolder = new ICFile(fileId);
      }
      catch (Exception ex) {

      }

    }
    else if(listOfFiles !=null){
      fileFolder = (ICFile)listOfFiles.remove(0);
    }

    if(fileFolder !=null){
    Table T = new Table(2,2);
    String name = "";
    Vector urls = new Vector();

        try {
          name = fileFolder.getName();
          if(name.indexOf(".")> 0)
            name = name.substring(0,name.indexOf("."));
          name = "p"+name;

          Image image = new Image(fileFolder.getID());
          if ( image != null ) {
	          if ( width != null )
              image.setWidth(width);
            if ( height != null )
              image.setHeight(height);
	          if ( alt != null )
              image.setName(alt);
          }
          urls.add(image.getServletURL(fileFolder.getID()));
          image.setName(name);
	        T.add(image);

        }
        catch (Exception ex) {
          ex.printStackTrace();
        }

      Iterator iter = null;
      if ( fileFolder.getChildCount() > 0 && fileFolder.getChildren() != null ) {
        iter = fileFolder.getChildren();
      }
      else if(listOfFiles!=null){
        iter = listOfFiles.iterator();
      }

      if(iter !=null && iter.hasNext()){
        ICFile image ;
        while (iter.hasNext()) {
          image = (ICFile)iter.next();
          try{
            Image img = new Image(image.getID());
            urls.add(img.getServletURL(image.getID()));
          }
          catch(SQLException ex){
            ex.printStackTrace(System.err);

          }
        }
        T.mergeCells(1,1,2,1);
        Link left = new Link(iwb.getImage("arrowleft.gif"));
          left.setURL("javascript://");
          left.setOnClick(getCallingScript(name,-1));
          Link right = new Link(iwb.getImage("arrowright.gif"));
          right.setURL("javascript://");
          right.setOnClick(getCallingScript(name,+1));
          if(getParentPage()!=null)
          getParentPage().getAssociatedScript().addFunction("slide"+name,getSlideScript(name,urls));
          T.add(left,1,2);
          T.add(right,2,2);
          T.setCellpadding(0);
          T.setCellspacing(0);
          T.setAlignment(2,2,"right");

      }
      else
        T.mergeCells(1,2,2,2);

      add(T);
    }
  }

  public void setFileFolder(ICFile imagefile) {
    fileFolder = imagefile;
  }

  public void setFileId(int iImageFileId){
    fileId = iImageFileId;
  }

  public void setFiles(List listOfImageFiles){
    listOfFiles = listOfImageFiles;
  }

  public void setWidth(String width) {
    width = width;
  }

  public void setWidth(int imagewidth) {
    width = String.valueOf(imagewidth);
  }

  public void setHeight(String height) {
    height = height;
  }

  public void setAlt(String alt) {
    alt = alt;
  }

  private String getCallingScript(String name,int step){

    return "Check"+name+"("+step+")";
  }

  private String getSlideScript(String name,List urls){

    String sCurrent = "Current_"+name;
    String sPicArray = "Pics_"+name;
    String sAddPic = "addPic"+name;
    String sCheck = "Check"+name;


    StringBuffer addPics = new StringBuffer("var ").append(sCurrent).append(" = 0;\n");
    addPics.append("var ").append(sPicArray).append(" = new Array();\n\n");

    addPics.append("function ").append(sAddPic).append("(_p) {\n\t");
    addPics.append(sPicArray).append("[").append(sPicArray).append(".length?");
    addPics.append(sPicArray).append(".length:0] = new Image();\n\t");
    addPics.append(sPicArray).append("[").append(sPicArray).append(".length-1].src=_p;\n}\n");

    if(urls !=null){
      for (int i = 0; i < urls.size(); i++) {
        String url = (String) urls.get(i);
        addPics.append(sAddPic).append("(\"").append(url).append("\");\n");
      }
    }

    addPics.append("\n");
    addPics.append("function ").append(sCheck).append("(val) {\n\t");
    addPics.append(sCurrent).append(" = Math.abs((").append(sCurrent).append("+parseInt(val))%");
    addPics.append(sPicArray).append(".length);\n\t");
    addPics.append("document.").append(name).append(".src = ");
    addPics.append(sPicArray).append("[").append(sCurrent).append("].src;\n");
    addPics.append("}");

    return addPics.toString();
  /*

   <script type=text/javascript>
    var current = 0;
    var myPics = new Array();
    function addPic(_p) {
      myPics[myPics.length?myPics.length:0] = new Image();
      myPics[myPics.length-1].src=_p;
    }

    addPic("http://www.js-examples.com/js/pic1.gif");
    addPic("http://www.js-examples.com/js/pic2.gif");

    function checkIt(val) {
      current = Math.abs((current+parseInt(val))%myPics.length);
      document.myimg.src = myPics[current].src;
    }*/
  }

}// class ImageRotater