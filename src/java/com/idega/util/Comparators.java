package com.idega.util;

import java.util.Comparator;
import java.beans.MethodDescriptor;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class Comparators{

  private Comparators() {
  }

  public static Comparator getMethodDescriptorComparator(){
    return new MethodDescriptorComparator();
  }

  private static class MethodDescriptorComparator implements Comparator{

    public int compare(Object parm1, Object parm2) {
        MethodDescriptor methodDesc1 = (MethodDescriptor)parm1;
        MethodDescriptor methodDesc2 = (MethodDescriptor)parm2;
        String s1 = methodDesc1.getMethod().getName();
        String s2 = methodDesc2.getMethod().getName();
        return s1.compareTo(s2);
    }
  }




}