package com.idega.util;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ListUtil {

  private static final Vector emptyVector = new EmptyList();

  private ListUtil() {
  }

  public static List getEmptyList(){
    return getEmptyVector();
  }

  public static Vector getEmptyVector(){
    return emptyVector;
  }


  private static class EmptyList extends Vector{


    public boolean add(Object o){
      throw new RuntimeException("This empty list is final and cannot be added to");
    }

    public void add(int index,Object o){
      throw new RuntimeException("This empty list is final and cannot be added to");
    }

    public boolean addAll(Collection o){
      throw new RuntimeException("This empty list is final and cannot be added to");
    }

    public boolean addAll(int index, Collection o){
      throw new RuntimeException("This empty list is final and cannot be added to");
    }



  }

}