package com.idega.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoreConstants {
	
	public static final String CORE_IW_BUNDLE_IDENTIFIER = "com.idega.core";
	
	private static final String[] _HEXIDECIMAL_LETTERS = new String[] {"a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"};
	public static final List <String> HEXIDECIMAL_LETTERS = Collections.unmodifiableList(Arrays.asList(_HEXIDECIMAL_LETTERS));
	
	public static final String NUMBER_SIGN = "#";
	public static final String SEMICOLON = ";";

}