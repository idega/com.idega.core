package com.idega.util;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description:
 * Collection of some useful methods regarding arrays. 
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 16, 2004
 */
public class ArrayUtil  {

	private ArrayUtil() {
		// private contructor
	}
		
	public static boolean contains(Object[] array, Object object) {
		if (array == null) {
			return false;
		}
		for (int i = 0; i < array.length; i++) {
			Object element = array[i];
			if (element.equals(object)) {
				return true;
			}
		}
		return false;
	}
}
