/*
 * $Id: Stripper.java,v 1.3.8.1 2007/03/06 22:38:28 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 *  A class to strip out \CVS\ lines from files to assist in compilation of the
 *  whole com tree.
 */

public class Stripper {

  public Stripper() {
  }

  public static void main(String[] args) {
    //Stripper stripper1 = new Stripper();

    if (args.length != 2) {
    	System.err.println("You moron. You should have two parameters with this, login and logout");
      return;
    }

    BufferedReader in = null;
    BufferedWriter out = null;

    try {
      in = new BufferedReader(new FileReader(args[0]));
    }
    catch (java.io.FileNotFoundException e) {
      System.err.println("You moron. Error : " + e.toString());

      return;
    }

    try {
      out = new BufferedWriter(new FileWriter(args[1]));
    }
    catch (java.io.IOException e) {
      System.err.println("You moron. Error : " + e.toString());

      return;
    }

    try {
      String input = in.readLine();
      int count = 0;
      while (input != null) {
        int index = input.indexOf("\\CVS\\");
        if (index > -1){
          System.out.println("Skipping : " + input);
          count++;
        }
        else {
          out.write(input);
          out.newLine();
        }

        input = in.readLine();
      }
      System.out.println("Skipped : " + count);
    }
    catch (java.io.IOException e) {
      System.err.println("Error reading or writing file : " + e.toString());
    }

    try {
      in.close();
      out.close();
    }
    catch (java.io.IOException e) {
      System.err.println("Error closing files : " + e.toString());
    }
  }


}
