
//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import com.idega.util.database.*;
import javax.sql.*;
import com.idega.jmodule.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.idegaweb.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class WindowOpener extends JSPModule
{

      //public static int maxNumberOfWindows=4;
      //private static String windowsHashtableSessionStorageParameter = "idega_special_windows_hashtable";
      //private static String windowsListSessionStorageParameter = "idega_special_windows_list";

      /*public Page getPage(){
        Window theReturn = null;
        IWContext iwc = getIWContext();
        try{
         theReturn = fetchWindow(iwc);
        }
        catch(NullPointerException ex){
          theReturn = new Window();
          theReturn.add("<H2>Síða útrunnin, vinsamlegast endurhlaðið síðurnar</H2>");
        }

        return theReturn;
        //String sessionStorageName = getIWContext().getParameter(IdegaWebApplication.windowOpenerParameter);
        //Page thePage = (Page)getIWContext().getSessionAttribute(sessionStorageName);
        //return thePage;
      }*/

      public Window getWindow(){
        return (Window)getPage();
      }


      /*public static String storeWindow(IWContext iwc,Window window){
        String parameter=window.getID();
        Map table = getHashtable(iwc);
        LinkedList list = getLinkedList(table);
        if(list.size()>=maxNumberOfWindows){
            String s = (String)list.removeLast();
            table.remove(s);
        }
        //else{
            table.put(parameter,window);
            list.addFirst(parameter);
        //}
        return parameter;
      }


      private static Map getHashtable(IWContext iwc){
        Hashtable table = (Hashtable)iwc.getSessionAttribute(windowsHashtableSessionStorageParameter);
        if(table==null){
          table = new Hashtable();
          iwc.setSessionAttribute(windowsHashtableSessionStorageParameter,table);
          LinkedList list = new LinkedList();
          table.put(windowsListSessionStorageParameter,list);
        }
        return table;
      }

      private static LinkedList getLinkedList(Map table){
          LinkedList list = (LinkedList)table.get(windowsListSessionStorageParameter);
          return list;
      }


      public static Window fetchWindow(IWContext iwc){
        String sessionStorageName = iwc.getParameter(IWMainApplication.windowOpenerParameter);
        Map table = getHashtable(iwc);
        return (Window)table.get(sessionStorageName);
        //Page thePage = (Page)getIWContext().getSessionAttribute(sessionStorageName);
        //return thePage;
      }*/

      public static String storeWindow(IWContext iwc,Window window){
        Page.storePage(window,iwc);
        return window.getID();
      }



}

//-------------
//- End of file
//-------------
