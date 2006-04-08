package com.idega.util;

import java.util.Comparator;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import com.idega.presentation.IWContext;

import com.idega.idegaweb.IWProperty;

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

  /**
   * @todo Modify so it sorts by locale
   * Compares IWProperty Objects
   */
  public static Comparator getMethodDescriptionComparator(IWContext iwc){
    return new MethodDescriptionComparator(iwc);
  }

  public static Comparator getMethodComparator(){
    return new MethodComparator();
  }

  public static Comparator getMethodDescriptorComparator(){
    return new MethodDescriptorComparator();
  }

  protected static class MethodDescriptorComparator implements Comparator{

    public int compare(Object parm1, Object parm2) {
        MethodDescriptor methodDesc1 = (MethodDescriptor)parm1;
        MethodDescriptor methodDesc2 = (MethodDescriptor)parm2;
        String s1 = methodDesc1.getMethod().getName();
        String s2 = methodDesc2.getMethod().getName();
        return s1.compareTo(s2);
    }
  }


  protected static class MethodDescriptionComparator implements Comparator{

    MethodDescriptionComparator(IWContext iwc){

    }

    public int compare(Object parm1, Object parm2) {
        IWProperty methodDesc1 = (IWProperty)parm1;
        IWProperty methodDesc2 = (IWProperty)parm2;
        String s1 = methodDesc1.getValue();
        String s2 = methodDesc2.getValue();
        return s1.compareTo(s2);
    }
  }


  protected static class MethodComparator implements Comparator{

    MethodComparator(){

    }

    public int compare(Object parm1, Object parm2) {
        Method method1 = (Method)parm1;
        Method method2 = (Method)parm2;
        String s1 = method1.getName();
        String s2 = method2.getName();
        return s1.compareTo(s2);
    }
  }

}
