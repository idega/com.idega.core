package com.idega.util;

import com.idega.presentation.PresentationObjectContainer;
import javax.swing.DefaultSingleSelectionModel;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import com.idega.util.datastructures.Collectable;
import com.idega.presentation.IWContext;

/**
 * Title:        IWTabbedPane
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
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


  public void setSelectedIndex(int index, IWContext iwc, boolean collect, boolean store){
    if(this.selectedIndex > -1 && this.selectedIndex < this.addedCollectableObjects.size()){
      if(collect && selectedIndex != index ){
        ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).collect(iwc);
      }
      if(store && selectedIndex != index ){
        ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).store(iwc);
      }
    }
    this.selectedIndex = index;
  }

  /**
   * collect : true <br>
   * store : false
   */
  public void setSelectedIndex(int index, IWContext iwc){
    this.setSelectedIndex(index,iwc,true,false);
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

    // collect current object
    if(this.selectedIndex > -1 && this.selectedIndex < this.addedCollectableObjects.size()){
      ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).collect(iwc);
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