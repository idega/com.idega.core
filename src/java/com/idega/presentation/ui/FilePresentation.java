package com.idega.presentation.ui;

import com.idega.presentation.text.Link;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.idegaweb.IWMainApplication;

import com.idega.core.data.ICFile;
import com.idega.core.data.ICFileHome;

import com.idega.block.media.business.MediaBusiness;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class FilePresentation extends Link {

  private ICFile _file;

  public FilePresentation() {
  }

  public void setFileID(int file_id){
    try{
      setFile(getFileHome().findByPrimaryKey(file_id));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * @deprecated
   */
  public static String getFileURL(int file_id){
    return IWMainApplication.FILE_SERVLET_URL+"?file"+file_id+"&file_id="+file_id;
  }

  private String getFileURL(ICFile file){
    return MediaBusiness.getMediaURL(file,this.getIWApplicationContext().getApplication());
  }

  public FilePresentation(ICFile file){
      //try{
          //this(new Text(file_name),"/servlet/FileModule?file_id="+file_id);
          this(file,new Text(file.getName()));
      /*}
      catch(Exception e){
        e.printStackTrace();
      }*/
  }

  public FilePresentation(ICFile file,PresentationObject po){
      try{
        setPresentationObject(po);
        setFile(file);
      }
      catch(Exception e){
        e.printStackTrace();
      }

  }


  //for files
  public FilePresentation(int file_id){
          //this(new Text("File"),"/servlet/FileModule?file_id="+file_id);
              super(new Text("File"),getFileURL(file_id));
  }

  public FilePresentation(int file_id,String fileName){
          //this(new Text(file_name),"/servlet/FileModule?file_id="+file_id);
          super(new Text(fileName),getFileURL(file_id));
  }

  public FilePresentation(PresentationObject mo,ICFile file){
        super();
        setPresentationObject(mo);
        //setURL("/servlet/FileModule?file_id="+file_id);
        setFile(file);
  }


  public FilePresentation(PresentationObject mo,int file_id){
          super();
          setPresentationObject(mo);
          //setURL("/servlet/FileModule?file_id="+file_id);
          setFileID(file_id);

  }

  public FilePresentation(int file_id, Window myWindow){
          //super();
          setFileID(file_id);
          this.setWindow(myWindow);
          //this.myWindow = myWindow;
  }


  public void setFile(ICFile file){
    super.setURL(this.getFileURL(file));
    this._file=file;
  }

  public ICFile getFile(){
    return _file;
  }

  protected ICFileHome getFileHome(){
    try{
      return (ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class);
    }
    catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }

}
