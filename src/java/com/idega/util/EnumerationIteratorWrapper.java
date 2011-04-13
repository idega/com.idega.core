/*
 * Created on 28.10.2003 by  tryggvil in project com.project
 */
package com.idega.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class is a simple wrapper around an Iterator to form an Enumeration.
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 * @param <T>
 */
public class EnumerationIteratorWrapper<T> implements Enumeration<T> {
	
	private Iterator<T> iterator;

	public EnumerationIteratorWrapper(Iterator<T> iter) {
		this.iterator = iter;
	}

	public boolean hasMoreElements() {
		return this.iterator.hasNext();
	}

	public T nextElement() {
		return this.iterator.next();
	}
}