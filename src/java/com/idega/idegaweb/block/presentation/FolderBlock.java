package com.idega.idegaweb.block.presentation;

import com.idega.core.data.ICLocale;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.presentation.InformationCategory;
import com.idega.core.presentation.InformationFolder;
import com.idega.idegaweb.block.business.FolderBlockBusiness;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class FolderBlock extends Block {

  private InformationFolder _workingFolder = null;
  private InformationFolder[] _viewFolders = null;
  private InformationCategory[] _categoriesForInstance = null;
  private boolean _autocreate = true;
  private String _contentLocaleIdentifier = null;

  public FolderBlock() {

  }

  public int getWorkingFolderId(){
    return _workingFolder.getID();
  }

  public InformationFolder getWorkingFolder(){
    return _workingFolder;
  }

/*
  public InformationFolder[] getFoldersToView(){
    return _viewFolders;
  }
  */
  public InformationCategory[] getCategoriesToView(){
    return _categoriesForInstance;
  }


  public void setFolder(InformationFolder folder){
     _workingFolder = folder;
  }

  public void setAutoCreate(boolean autocreate){
    _autocreate = autocreate;
  }

  public void setContentLocaleIdentifier(String identifier){
    _contentLocaleIdentifier = identifier;
  }

  public String getContentLocaleIdentifier(){
    return _contentLocaleIdentifier;
  }

  public void setContentLocaleDependent(){
    _contentLocaleIdentifier = null;
  }


  public void _main(IWContext iwc) throws Exception{
    if(getICObjectInstanceID() > 0){
      FolderBlockBusiness business = FolderBlockBusiness.getInstance();
      int localeId = -1;
      if(getContentLocaleIdentifier() != null){
        ICLocale locale = ICLocaleBusiness.getICLocale(getContentLocaleIdentifier());//getContentLocaleIdentifier();
        if(locale != null){
           localeId = locale.getID();
        }
      }
      if(localeId == -1){
        localeId = iwc.getCurrentLocaleId();
      }

      InformationFolder folder = business.getInstanceWorkeFolder(getICObjectInstanceID(), getICObjectID(), localeId,_autocreate);
      if(folder != null){
        setFolder(folder);
      }

      List infoCategories = business.getInstanceCategories(getICObjectInstanceID());
      if(infoCategories != null && infoCategories.size() > 0){
        _categoriesForInstance = new InformationCategory[infoCategories.size()];
        int pos = 0;
        Iterator iter = infoCategories.iterator();
        while (iter.hasNext()) {
          InformationCategory item = (InformationCategory)iter.next();
          _categoriesForInstance[pos] = item;
          pos++;
        }
      } else {
        _categoriesForInstance = new InformationCategory[0];
      }

    }
    super._main(iwc);
  }

  public synchronized Object clone() {
    FolderBlock obj = null;
    try {
      obj = (FolderBlock)super.clone();
      obj._workingFolder = this._workingFolder;
      obj._viewFolders = this._viewFolders;
      obj._categoriesForInstance = this._categoriesForInstance;
      obj._autocreate = this._autocreate;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}