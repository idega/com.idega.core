/*
 * Created on 13.7.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import dori.jasper.engine.JRDataSource;
import dori.jasper.engine.JRException;
import dori.jasper.engine.JRField;

/**
 * Title:		IDOReportableCollection
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class IDOReportableCollection extends Vector implements JRDataSource {
	
	private Iterator _reportIterator = null;
	private IDOReportableEntity _currentJRDataSource = null;
	
	/**
	 * @param initialCapacity
	 * @param capacityIncrement
	 */
	public IDOReportableCollection(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

	/**
	 * @param initialCapacity
	 */
	public IDOReportableCollection(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 
	 */
	public IDOReportableCollection() {
		super();
	}

	/**
	 * @param c
	 */
	public IDOReportableCollection(Collection c) {
		super(c);
	}

	/* (non-Javadoc)
	 * @see dori.jasper.engine.JRDataSource#next()
	 */
	public boolean next() throws JRException {
		if(_reportIterator == null){
			_reportIterator = this.iterator();
		}
		
		try {
			if(_reportIterator.hasNext()){
				_currentJRDataSource = (IDOReportableEntity)_reportIterator.next();
				return true;
			} else {
				_currentJRDataSource = null;
				return false;
			} 
		} catch (ClassCastException e) {
			_currentJRDataSource=null;
			e.printStackTrace();
			throw new JRException(e);
		}
	}

	/* (non-Javadoc)
	 * @see dori.jasper.engine.JRDataSource#getFieldValue(dori.jasper.engine.JRField)
	 */
	public Object getFieldValue(JRField field) throws JRException {
		if(_reportIterator == null){
			next();
		}
		return _currentJRDataSource.getFieldValue(field);
	}

}
