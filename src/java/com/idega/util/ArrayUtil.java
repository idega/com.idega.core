package com.idega.util;

import java.lang.reflect.Array;
import java.util.Collection;


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

	@SuppressWarnings("unchecked")
	public static final <T>T[] convertListToArray(Collection<T> collection) {
		if (ListUtil.isEmpty(collection)) {
			return null;
		}

		int index = 0;
		T[] array = (T[]) Array.newInstance(collection.iterator().next().getClass(), collection.size());
		for (T collectionItem: collection) {
			if (collectionItem == null) {
				continue;
			}

			array[index] = collectionItem;
			index++;
		}

		return array;
	}

	public static final boolean isEmpty(Object[] array) {
		if (array == null || array.length == 0) {
			return true;
		}

		return false;
	}
}
