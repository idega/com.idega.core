package com.idega.util;
/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	Copyright (c) Non, Inc. 2002 -- All Rights Reserved

PACKAGE:	JavaWorld Tips & Tricks
FILE:		StackTrace.java

AUTHOR:		John D. Mitchell, Feb 20, 2002

REVISION HISTORY:
	Name	Date		Description
	----	----		-----------
	JDM	2002.02.20   	Initial version.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
// (Un)Comment the following package declaration to try the code with a
// package (un)defined.  Remember to setup your class path and the location
// of the file appropriately.
// package boo.hoo;
// Java core packages:



/**
 ** Using the new Java v1.4 facilities in the Throwable class
 ** to pull information out of a stack trace.
<br><br>
 **
 ** @author	John D. Mitchell, Non, Inc., Feb 20, 2002
 **
 ** @version 0.5
 **
 **/
public class StackTrace {
	public static void main(String[] args) {
		// Create a throwable so we can pick it apart.
		// Note clearly that the line number information will be noted at
		// the point where the exception is created...
		Throwable ex = new Throwable();
		// Display what the stack trace looks like directly from here.
		System.out.println("Dumping from directly in main():");
		displayStackTraceInformation(ex, true);
		// Now, what's it look like deeper in a call stack...
		// Note how the stack elements are LIFO order.
		System.out.println("Dumping from somewhere inside foo():");
		foo();
		// Let's create an instance to see if that looks any different from
		// the static case...
		System.out.println("Dumping from inside StackTrace constructor():");
		StackTrace st = new StackTrace();
		System.out.println("Dumping from inside crashAndBurnout():");
		st.crashAndBurnout();
		// Now, let's see what happens with an inner class...
		 new StackTrace(true);
	} // End of main().
	public static boolean displayStackTraceInformation(Throwable ex) {
		// For the default case, just show the top element in the trace.
		return displayStackTraceInformation(ex, false);
	}
	public static boolean displayStackTraceInformation(Throwable ex, boolean displayAll) {
		/*
		 if (null == ex) {
			System.out.println("Null stack trace reference! Bailing...");
			return false;
		}
		// Display the entire blob using printStackTrace().
		System.out.println("The stack according to printStackTrace():\n");
		ex.printStackTrace();
		System.out.println("");
		// Get the list of StackTraceElements from the throwable.
		StackTraceElement[] stackElements = ex.getStackTrace();
		// Display each of the elements using the various capabilitiies of
		// the StackTraceElements class.
		// Or, just display the top element of the stack (to cut down on
		// all of the repetition.
		if (displayAll) {
			System.out.println(
				"The "
					+ stackElements.length
					+ " element"
					+ ((stackElements.length == 1) ? "" : "s")
					+ " of the stack trace:\n");
		}
		else {
			System.out.println("The top element of a " + stackElements.length + " element stack trace:\n");
		}
		for (int lcv = 0; lcv < stackElements.length; lcv++) {
			System.out.println("File name: " + stackElements[lcv].getFileName());
			System.out.println("Line number: " + stackElements[lcv].getLineNumber());
			String className = stackElements[lcv].getClassName();
			String packageName = extractPackageName(className);
			String simpleClassName = extractSimpleClassName(className);
			System.out.println("Package name: " + ("".equals(packageName) ? "[default package]" : packageName));
			System.out.println("Full class name: " + className);
			System.out.println("Simple class name: " + simpleClassName);
			System.out.println("Unmunged class name: " + unmungeSimpleClassName(simpleClassName));
			System.out.println("Direct class name: " + extractDirectClassName(simpleClassName));
			System.out.println("Method name: " + stackElements[lcv].getMethodName());
			System.out.println("Native method?: " + stackElements[lcv].isNativeMethod());
			System.out.println("toString(): " + stackElements[lcv].toString());
			System.out.println("");
			// Only continue if the caller really wanted all of the
			// elements displayed.
			if (!displayAll)
				return true;
		}
		System.out.println("");
		*/
		return true;
	} // End of displayStackTraceInformation().
	public static String extractPackageName(String fullClassName) {
		if ((null == fullClassName) || ("".equals(fullClassName)))
			return "";
		// The package name is everything preceding the last dot.
		// Is there a dot in the name?
		int lastDot = fullClassName.lastIndexOf('.');
		// Note that by fiat, I declare that any class name that has been
		// passed in which starts with a dot doesn't have a package name.
		if (0 >= lastDot)
			return "";
		// Otherwise, extract the package name.
		return fullClassName.substring(0, lastDot);
	}
	public static String extractSimpleClassName(String fullClassName) {
		if ((null == fullClassName) || ("".equals(fullClassName)))
			return "";
		// The simple class name is everything after the last dot.
		// If there's no dot then the whole thing is the class name.
		int lastDot = fullClassName.lastIndexOf('.');
		if (0 > lastDot)
			return fullClassName;
		// Otherwise, extract the class name.
		return fullClassName.substring(++lastDot);
	}
	public static String extractDirectClassName(String simpleClassName) {
		if ((null == simpleClassName) || ("".equals(simpleClassName)))
			return "";
		// The direct class name is everything after the last '$', if there
		// are any '$'s in the simple class name.  Otherwise, it's just
		// the simple class name.
		int lastSign = simpleClassName.lastIndexOf('$');
		if (0 > lastSign)
			return simpleClassName;
		// Otherwise, extract the last class name.
		// Note that if you have a multiply-nested class, that this
		// will only extract the very last one.  Extracting the stack of
		// nestings is left as an exercise for the reader.
		return simpleClassName.substring(++lastSign);
	}
	public static String unmungeSimpleClassName(String simpleClassName) {
		if ((null == simpleClassName) || ("".equals(simpleClassName)))
			return "";
		// Nested classes are set apart from top-level classes by using
		// the dollar sign '$' instead of a period '.' as the separator
		// between them and the top-level class that they sit
		// underneath. Let's undo that.
		return simpleClassName.replace('$', '.');
	}
	public static void foo() {
		bar();
	}
	public static void bar() {
		displayStackTraceInformation(new Throwable(), true);
	}
	public StackTrace() {
		// Notice how the constructors are named "<init>".
		displayStackTraceInformation(new Throwable());
	}
	public void crashAndBurnout() {
		displayStackTraceInformation(new Throwable());
	}
	public StackTrace(boolean na) {
		// Let's create some inner classes and abuse them a bit...
		// Notice how we can't directly tell which constructor we're in
		// given the information that we've been given.
		StackTrace.FirstNested nested = new StackTrace.FirstNested();
	}
	public class FirstNested {
		public FirstNested() {
			StackTrace.displayStackTraceInformation(new Throwable());
			// Let's go hog wild with another inner class.
			StackTrace.FirstNested.SecondNested yan = new StackTrace.FirstNested.SecondNested();
			System.out.println("Dumping from inside hogwash():");
			yan.hogwash();
		}
		public class SecondNested {
			public SecondNested() {
				StackTrace.displayStackTraceInformation(new Throwable());
			}
			public void hogwash() {
				StackTrace.displayStackTraceInformation(new Throwable());
				// Let's add in an anonymous inner class into the mix...
				Whackable whacked = new Whackable() {
					public void whack() {
							// Note how this anonymous class is actually
		// directly a child of the top-level class and
		// how it's given a numerical name.
	StackTrace.displayStackTraceInformation(new Throwable());
					}
				}; // End of anonymous member class.
				whacked.whack();
			} // End of hogwash().
		} // End of FirstNested.SecondNexted member class.
	} // End of FirstNested member class.
	public interface Whackable {
		public void whack();
	}
} // End of class StackTrace.
