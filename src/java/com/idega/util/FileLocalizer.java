/*
 * $Id: FileLocalizer.java,v 1.3 2006/04/09 12:13:13 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

/**
 * A class to find getLocalizedString method in source file and save keys and
 * values to file in a propiate way
 */

public class FileLocalizer {

	public static String stringToFind = "getLocalizedString(";

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Wimp. I need two parameters, input file og directory and output file");
			System.err.println("Usage java FileLocalizer input output");

			return;
		}

		File in = null;
		BufferedWriter out = null;

		Properties props = new Properties();

		try {
			in = new File(args[0]);
		}
		catch (Exception e) {
			System.err.println("Auli. Error : " + e.toString());

			return;
		}

		try {
			out = new BufferedWriter(new FileWriter(args[1]));
		}
		catch (java.io.IOException e) {
			System.err.println("Auli. Error : " + e.toString());

			return;
		}

		try {
			findRecursive(in, props);
			props.list(new PrintWriter(out));
		}
		catch (Exception e) {
			System.err.println("Error reading or writing file : " + e.toString());
		}

		try {
			out.close();
		}
		catch (java.io.IOException e) {
			System.err.println("Error closing files : " + e.toString());
		}
	}

	public static void findRecursive(File fileToRead, Properties props) {
		if (fileToRead.isDirectory()) {
			int index = fileToRead.getName().toUpperCase().indexOf("CVS");
			File[] F = fileToRead.listFiles();
			if (index == -1) {
				for (int i = 0; i < F.length; i++) {
					findRecursive(F[i], props);
				}
			}
		}
		else if (fileToRead.isFile()) {
			int index = fileToRead.getName().toUpperCase().indexOf(".JAVA");
			if (index != -1) {
				readFile(fileToRead, props);
			}
		}
		else {
			return;
		}
	}

	public static void readFile(File fileToRead, Properties props) {
		BufferedReader in = null;
		try {
			if (fileToRead.isFile()) {
				in = new BufferedReader(new FileReader(fileToRead));
				String input = in.readLine();
				StringTokenizer st;
				String a, b;
				while (input != null) {
					int index = input.indexOf(stringToFind);
					if (index > -1) {
						int i1 = input.indexOf("(", index);
						int i2 = input.indexOf(")", index);
						if (i2 > -1) {
							a = input.substring(i1 + 2, i2 - 1);
							b = "";
							st = new StringTokenizer(a, "\",");
							if (st.hasMoreTokens()) {
								a = st.nextToken();
								if (st.hasMoreTokens()) {
									b = st.nextToken();
								}
								if (!props.containsKey(a)) {
									props.setProperty(a, b);

									// System.err.println(a+"="+b);
								}
							}
						}
					}
					input = in.readLine();
				}// while ends
			}
			else {
				return;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

}
