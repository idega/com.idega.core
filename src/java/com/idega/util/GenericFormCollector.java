package com.idega.util;

import java.util.Vector;
import com.idega.presentation.IWContext;
import com.idega.util.datastructures.Collectable;

/**
 * Title:        IWTabbedPane
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class GenericFormCollector {

  private int selectedIndex = -1;
  private Vector addedCollectableObjects = null;

  public GenericFormCollector() {
    this.addedCollectableObjects = new Vector();
  }

  public void addCollectable(Collectable obj, int index){
    this.addedCollectableObjects.insertElementAt(obj,index);
    if (this.addedCollectableObjects.size() == 1) {
      this.selectedIndex = 0;
    }
  }

  public int getSelectedIndex(){
    return this.selectedIndex;
  }

  /**
   * collects the selected index also
   * @param index
   * @param iwc
   * @return
   */
  public boolean setSelectedIndex(int index, IWContext iwc){
    boolean collected = true;
    if(this.selectedIndex > -1 && this.selectedIndex < this.addedCollectableObjects.size()){
      if(this.selectedIndex != index ){
        collected = ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).collect(iwc);
      }
    }
    if(collected){
      this.selectedIndex = index;
      return true;
    } else {
      return false;
    }
  }

/*
  public boolean collectAll(IWContext iwc){
    boolean collected = true;
    boolean returned = true;
    for (int i = 0; i < this.addedCollectableObjects.size(); i++) {
      collected = ((Collectable)this.addedCollectableObjects.get(i)).collect(iwc);
      if(!collected){
        returned = false;
      }
    }
    return returned;
  }
*/

  public boolean storeAll(IWContext iwc){
    boolean stored = true;
    boolean returned = true;
    boolean collected = true;

    // collect current object
    if(this.selectedIndex > -1 && this.selectedIndex < this.addedCollectableObjects.size()){
      collected = ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).collect(iwc);
      if(!collected){
        return false;
      }
    }
    // storeAll
    for (int i = 0; i < this.addedCollectableObjects.size(); i++) {
      stored = ((Collectable)this.addedCollectableObjects.get(i)).store(iwc);
      if(!stored){
        returned = false;
      }
    }
    return returned;
  }



}// Class ends
