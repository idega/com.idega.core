package com.idega.util;

import com.idega.jmodule.object.ModuleObjectContainer;
import javax.swing.DefaultSingleSelectionModel;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import com.idega.util.datastructures.Collectable;
import com.idega.jmodule.object.ModuleInfo;

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


  public void setSelectedIndex(int index, ModuleInfo modinfo, boolean collect, boolean store){
    if(this.selectedIndex > -1 && this.selectedIndex < this.addedCollectableObjects.size()){
      if(collect && selectedIndex != index ){
        ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).collect(modinfo);
      }
      if(store && selectedIndex != index ){
        ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).store(modinfo);
      }
    }
    this.selectedIndex = index;
  }

  /**
   * collect : true <br>
   * store : false
   */
  public void setSelectedIndex(int index, ModuleInfo modinfo){
    this.setSelectedIndex(index,modinfo,true,false);
  }

/*
  public boolean collectAll(ModuleInfo modinfo){
    boolean collected = true;
    boolean returned = true;
    for (int i = 0; i < this.addedCollectableObjects.size(); i++) {
      collected = ((Collectable)this.addedCollectableObjects.get(i)).collect(modinfo);
      if(!collected){
        returned = false;
      }
    }
    return returned;
  }
*/

  public boolean storeAll(ModuleInfo modinfo){
    boolean stored = true;
    boolean returned = true;

    // collect current object
    if(this.selectedIndex > -1 && this.selectedIndex < this.addedCollectableObjects.size()){
      ((Collectable)this.addedCollectableObjects.get(this.selectedIndex)).collect(modinfo);
    }
    // storeAll
    for (int i = 0; i < this.addedCollectableObjects.size(); i++) {
      stored = ((Collectable)this.addedCollectableObjects.get(i)).store(modinfo);
      if(!stored){
        returned = false;
      }
    }
    return returned;
  }



}// Class ends