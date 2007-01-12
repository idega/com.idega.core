package com.idega.data;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.0
 */

public class IDODependencyList {

  private List startClasses;
  private List dependencyClassesList;

  private IDODependencyList() {
  }

  private IDODependencyList(Class entityClass) {
    this.addEntityClass(entityClass);
  }


  List getStartClasses(){
    if(this.startClasses==null){
      this.startClasses = new Vector();
    }
    return this.startClasses;
  }



  public void addEntityClass(Class startingEntityClass){
    if(startingEntityClass!=null){
      Class interfaceClass = IDODependencyList.getInterfaceClassForClass(startingEntityClass);
      if(!IDODependencyList.listContainsClass(getStartClasses(),interfaceClass)){
        this.getStartClasses().add(interfaceClass);
      }
    }
  }


  /**
   * Takes in a collection of either Class of IDOLegacyEntity Objects
   */
  public void addAllEntityClasses(Collection startingEntityClasses){
    Iterator iter = startingEntityClasses.iterator();
    while (iter.hasNext()) {
      Object item = iter.next();
      Class c = null;
      if(item instanceof Class){
        c = (Class)item;
      }
      else if(item instanceof IDOLegacyEntity){
        c = ((IDOLegacyEntity)item).getClass();
      }
      addEntityClass(c);
    }
  }

  /**
   * Returns a Set that contains Class Entity objects that are dependent on entityClass
   * The depencencyList must be compiled "i.e. compile() must have been called" before the call to this method.
   * - Ordered so: the element entityClass itself first and the least dependent Class last
   */
  public List getDependencyListAsClasses(){
    return this.dependencyClassesList;
  }

  /**
   * Compiles the dependencylist and finds all entityClasses that the startingEntityClasses are dependent upon.
   */
  public void compile(){
    List l = compileDependencyList();
    this.dependencyClassesList=l;
    //compileDependencyList((Class)getStartClasses().get(0),getStartClasses());
  }



  public static IDODependencyList createDependencyList(Class startingEntityClass){
    IDODependencyList instance = createDependencyList();
    instance.addEntityClass(startingEntityClass);
    return instance;
  }


  public static IDODependencyList createDependencyList(){
    IDODependencyList instance = new IDODependencyList();
    return instance;
  }


  /**
   * Returns a Set that contains Class Entity objects that are dependent on entityClass
   * - Ordered so: the element entityClass itself first and the least dependent Class last
   */
  private List compileDependencyList(){
    List theReturn = new Vector();
    List entityClasses = this.getStartClasses();
    int size = entityClasses.size();
    //Iterator iter = entityClasses.iterator();
    //while (iter.hasNext()) {
      //Class entityClass = (Class)iter.next();
    for (int i = 0; i < size; i++) {
      Class entityClass = (Class)entityClasses.get(i);
      compileDependencyList(entityClass,theReturn);
    }
    return theReturn;
  }

  private static void compileDependencyList(Class entityClass,List theReturn){

    boolean alreadyInList =  listContainsClass(theReturn,entityClass);

    Class interfaceClass = getInterfaceClassForClass(entityClass);
    if(alreadyInList){
      theReturn.remove(entityClass);
      theReturn.remove(interfaceClass);
    }
    theReturn.add(interfaceClass);


    List manyToManies = EntityControl.getManyToManyRelationShipClasses(entityClass);
    List nToOnes = EntityControl.getNToOneRelatedClasses(entityClass);

    if(manyToManies!=null){
      //Iterator iter = manyToManies.i();
      //while (iter.hasNext()) {
      int size = manyToManies.size();
      for (int i = 0; i < size; i++) {
        Class item = (Class)manyToManies.get(i);
        //Class item = (Class)iter.next();
        if(!listContainsClass(theReturn,item)){
          compileDependencyList(item,theReturn);
          //System.out.println(item.getName());
        }
        else{
          reshuffleDependencyList(item,theReturn);
        }
      }
    }


    if(nToOnes!=null){
      Iterator iter2 = nToOnes.iterator();
      while (iter2.hasNext()) {
        Class item = (Class)iter2.next();
        if(!listContainsClass(theReturn,item)){
          compileDependencyList(item,theReturn);
        }
        else{
          reshuffleDependencyList(item,theReturn);
        }
      }
    }

  }

  private static void reshuffleDependencyList(Class entityClass,List theReturn){
    List checkList = new Vector();
    reshuffleDependencyList(entityClass,theReturn,checkList);
  }

  private static void reshuffleDependencyList(Class entityClass,List theReturn,List checkList){
    System.out.println("[idoDependencyList] Reshuffling for entityClass = "+entityClass.getName());
    if(listContainsClass(checkList,entityClass)) {
		return;
	}

    Class interfaceClass = getInterfaceClassForClass(entityClass);
    checkList.add(interfaceClass);

    theReturn.remove(entityClass);
    theReturn.remove(interfaceClass);
    theReturn.add(interfaceClass);

    //List manyToManies = EntityControl.getManyToManyRelationShipClasses(entityClass);
    List nToOnes = EntityControl.getNToOneRelatedClasses(entityClass);

    /*if(manyToManies!=null){
      Iterator iter = manyToManies.iterator();
      while (iter.hasNext()) {
        Class item = (Class)iter.next();
          reshuffleDependencyList(item,theReturn,checkList);
      }
    }*/

    if(nToOnes!=null){
      Iterator iter2 = nToOnes.iterator();
      while (iter2.hasNext()) {
        Class item = (Class)iter2.next();
        Class newItem = getInterfaceClassForClass(item);
          reshuffleDependencyList(newItem,theReturn,checkList);
      }
    }

  }


  private static Class getInterfaceClassForClass(Class entityClass){
    return IDOLookup.getInterfaceClassFor(entityClass);
  }

  private static boolean listContainsClass(List list,Class entityClass){
    if(entityClass.isInterface()){
      return list.contains(entityClass);
    }
    else{
      Class newClass = getInterfaceClassForClass(entityClass);
      return list.contains(newClass);
    }
  }

}
