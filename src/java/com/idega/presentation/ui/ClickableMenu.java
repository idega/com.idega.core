//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.presentation.ui;





import java.util.*;

import com.idega.presentation.*;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*/

public class ClickableMenu extends InterfaceObjectContainer{



  private String clickedMenuItemName;

  private List items;

  private Hashtable notClickedItems;

  private Hashtable clickedItems;



  public void addMenuItem(String menuItemName,PresentationObject ItemDisplayedIfNotClicked,PresentationObject ItemDisplayedIfClicked){

    if(getClickedMenuItemName()==null){

      setClickedMenuItem(menuItemName);

      notClickedItems = new Hashtable();

      clickedItems = new Hashtable();

      items=new Vector();

    }

    items.add(menuItemName);

    notClickedItems.put(menuItemName,ItemDisplayedIfNotClicked);

    clickedItems.put(menuItemName,ItemDisplayedIfClicked);

  }



  public void setClickedMenuItem(String menuItemName){

      clickedMenuItemName=menuItemName;

  }



  public String getClickedMenuItemName(){

    return clickedMenuItemName;

  }





  public void main(IWContext iwc)throws Exception{

    Iterator iterator = this.getItemNameList().iterator();

    while(iterator.hasNext()){

      String currentItem = (String)iterator.next();

      add(getMenuItem(currentItem));

    }

  }





  public PresentationObject getMenuItem(String menuItemName){

      if(menuItemName.equals(this.getClickedMenuItemName())){

        return (PresentationObject)clickedItems.get(menuItemName);

      }

      else{

        return (PresentationObject)notClickedItems.get(menuItemName);

      }

  }



    public boolean isPreviousItemClicked(String itemInQuestion){

        String clickedItem = getClickedMenuItemName();

        String leftItem = getPreviousItemName(itemInQuestion);

        if(leftItem!=null){

          if(leftItem.equals(clickedItem)) return true;

        }

        return false;

    }





    public boolean isNextItemClicked(String itemInQuestion){

        String clickedItem = getClickedMenuItemName();

        String rightItem = getNextItemName(itemInQuestion);

        if(rightItem!=null){

          if(rightItem.equals(clickedItem)) return true;

        }

        return false;

    }



    public String getPreviousItemName(String itemName){

        Iterator iterator = getItemNameList().iterator();

        String previousElement=null;

        while(iterator.hasNext()){

          String currentElement = (String)iterator.next();

          if(currentElement.equals(itemName)){

            return previousElement;

          }

          previousElement=currentElement;

        }

        return null;

    }



    public String getNextItemName(String itemName){

        Iterator iterator = getItemNameList().iterator();



        while(iterator.hasNext()){

          String currentElement = (String)iterator.next();

          if(currentElement.equals(itemName)){

              try{

                  return (String)iterator.next();

              }

              catch(NoSuchElementException ex){

                  return null;

              }

          }

        }

        return null;

    }



    /**

     * Returns a list of menu item names in the correct order

     */

    public List getItemNameList(){

      return items;

    }



    /**

     * Returns a list of menu item names in a specific and correct order

     */

    public void setItemNameList(List items){

      this.items=items;

    }



}

