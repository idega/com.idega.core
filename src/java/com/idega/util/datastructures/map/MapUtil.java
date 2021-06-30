package com.idega.util.datastructures.map;

import java.util.Map;

/**
 * <p>Class for managing {@link Map}s.</p>
 * <p>You can report about problems to: <a href="mailto:martynas@idega.com">
 * Martynas Stakė</a></p>
 * <p>You can expect to find some test cases notice in the end of the file.</p>
 *
 * @version 1.0.0 2011.10.31
 * @author martynas
 */
public class MapUtil {

	private MapUtil() {}

	/**
	 * <p>Checks if {@link Map} is empty.</p>
	 * @param map {@link Map} object.
	 * @return True, if empty, false otherwise.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}

}