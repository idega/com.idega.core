package com.idega.util;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ListUtil {

  private static final Vector emptyVector = new Vector();

  private ListUtil() {
  }

  public static List getEmptyList(){
    return getEmptyVector();
  }

  public static Vector getEmptyVector(){
    return emptyVector;
  }
}