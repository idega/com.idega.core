package com.idega.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoreConstants {
	
	private static final String[] _HEXIDECIMAL_LETTERS = new String[] {"a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"};
	public static final List <String> HEXIDECIMAL_LETTERS = Collections.unmodifiableList(Arrays.asList(_HEXIDECIMAL_LETTERS));
	
	public static final String NUMBER_SIGN = "#";
	public static final String SEMICOLON = ";";

}
