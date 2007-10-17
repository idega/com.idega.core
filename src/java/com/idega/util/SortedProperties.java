/*
 * Created on 20.2.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;


/**
 * Title:        SortedProperties
 * Description:  A class to read and write property files but stores them ordered by key in the property file.
 * Copyright:  (C) 2002 idega software All Rights Reserved.
 * Company:      idega software
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0  
 */
public class SortedProperties extends Properties //implements SortedMap
{
	private SortedMap internalSortedMap;
	/**
	 * 
	 */
	public SortedProperties()
	{
		super();
	}
	/**
	 * @param defaults
	 */
	public SortedProperties(Properties defaults)
	{
		super(defaults);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#comparator()
	 */
	 
	 private SortedMap<Object, Object> getInternalSortedMap(){
	 	if(this.internalSortedMap==null){
			this.internalSortedMap=new TreeMap();
	 	}
	 	return this.internalSortedMap;
	 }
	 
	public Comparator comparator()
	{
		return getInternalSortedMap().comparator();
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
	 */
	public SortedMap subMap(Object fromKey, Object toKey)
	{
		return getInternalSortedMap().subMap(fromKey,toKey);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#headMap(java.lang.Object)
	 */
	public SortedMap headMap(Object toKey)
	{
		return getInternalSortedMap().headMap(toKey);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#tailMap(java.lang.Object)
	 */
	public SortedMap tailMap(Object fromKey)
	{
		return getInternalSortedMap().tailMap(fromKey);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#firstKey()
	 */
	public Object firstKey()
	{
		return getInternalSortedMap().firstKey();
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#lastKey()
	 */
	public Object lastKey()
	{
		return getInternalSortedMap().lastKey();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public synchronized void clear()
	{
		getInternalSortedMap().clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#contains(java.lang.Object)
	 */
	public synchronized boolean contains(Object value)
	{
		//return super.contains(value);
		return getInternalSortedMap().containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(Object key)
	{
		return getInternalSortedMap().containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value)
	{
		return getInternalSortedMap().containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#elements()
	 */
	public synchronized Enumeration elements()
	{
		Vector v = new Vector(getInternalSortedMap().values());
		return v.elements();
		//return getInternalSortedMap().elements();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet()
	{
		return getInternalSortedMap().entrySet();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public synchronized boolean equals(Object o)
	{
		return getInternalSortedMap().equals(o);
	}

	public String getProperty(String key){
		return (String)get(key);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Dictionary#get(java.lang.Object)
	 */
	public synchronized Object get(Object key)
	{
		return getInternalSortedMap().get(key);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public synchronized int hashCode()
	{
		return getInternalSortedMap().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#isEmpty()
	 */
	public boolean isEmpty()
	{
		return getInternalSortedMap().isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#keys()
	 */
	public synchronized Enumeration keys()
	{
		Vector v = new Vector(keySet());
		return v.elements();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet()
	{
		return getInternalSortedMap().keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized Object put(Object key, Object value)
	{
		return getInternalSortedMap().put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public synchronized void putAll(Map t)
	{
		getInternalSortedMap().putAll(t);
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#rehash()
	 */
	protected void rehash()
	{
		//getInternalSortedMap().rehash();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#remove(java.lang.Object)
	 */
	public synchronized Object remove(Object key)
	{
		return getInternalSortedMap().remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#size()
	 */
	public int size()
	{
		return getInternalSortedMap().size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public synchronized String toString()
	{
		return getInternalSortedMap().toString();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values()
	{
		return getInternalSortedMap().values();
	}

	
	
	
	//BORROWED CODE FROM THE JDK LIBRARY java.util.Properties

	private static final String specialSaveChars = "=: \t\r\n\f#!";
	
	private boolean addDateHeader=false;
	
	
	/**
	 * ATTENTION THIS METHOD NOW USES UTF-8 NOT ISO 8859-1 LIKE THE JAVADOC SAYS
	 * <p>
	 * Writes this property list (key and element pairs) in this
	 * <code>Properties</code> table to the output stream in a format suitable
	 * for loading into a <code>Properties</code> table using the
	 * <code>load</code> method.
	 * The stream is written using the ISO 8859-1 character encoding.
	 * <p>
	 * Properties from the defaults table of this <code>Properties</code>
	 * table (if any) are <i>not</i> written out by this method.
	 * <p>
	 * If the header argument is not null, then an ASCII <code>#</code>
	 * character, the header string, and a line separator are first written
	 * to the output stream. Thus, the <code>header</code> can serve as an
	 * identifying comment.
	 * <p>
	 * Next, a comment line is always written, consisting of an ASCII
	 * <code>#</code> character, the current date and time (as if produced
	 * by the <code>toString</code> method of <code>Date</code> for the
	 * current time), and a line separator as generated by the Writer.
	 * <p>
	 * Then every entry in this <code>Properties</code> table is written out,
	 * one per line. For each entry the key string is written, then an ASCII
	 * <code>=</code>, then the associated element string. Each character of
	 * the element string is examined to see whether it should be rendered as
	 * an escape sequence. The ASCII characters <code>\</code>, tab, newline,
	 * and carriage return are written as <code>\\</code>, <code>\t</code>,
	 * <code>\n</code>, and <code>\r</code>, respectively. Characters less
	 * than <code>&#92;u0020</code> and characters greater than
	 * <code>&#92;u007E</code> are written as <code>&#92;u</code><i>xxxx</i> for
	 * the appropriate hexadecimal value <i>xxxx</i>. Leading space characters,
	 * but not embedded or trailing space characters, are written with a
	 * preceding <code>\</code>. The key and value characters <code>#</code>,
	 * <code>!</code>, <code>=</code>, and <code>:</code> are written with a
	 * preceding slash to ensure that they are properly loaded.
	 * <p>
	 * After the entries have been written, the output stream is flushed.  The
	 * output stream remains open after this method returns.
	 *
	 * @param   out      an output stream.
	 * @param   header   a description of the property list.
	 * @exception  IOException if writing this property list to the specified
	 *             output stream throws an <tt>IOException</tt>.
	 * @exception  ClassCastException  if this <code>Properties</code> object
	 *             contains any keys or values that are not <code>Strings</code>.
	 * @exception  NullPointerException  if <code>out</code> is null.
	 * @since 1.2
	 */
	public synchronized void store(OutputStream out, String header)
	throws IOException
	{
		BufferedWriter awriter;
		//awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
		//TODO change javadoc comment
		awriter = new BufferedWriter(new OutputStreamWriter(out, CoreConstants.ENCODING_UTF8));
		
		if (header != null) {
			writeln(awriter, "#" + header);
		}
		if(getAddDateHeader()) {
			writeln(awriter, "#" + new Date().toString());
		}
		for (Enumeration e = keys(); e.hasMoreElements();) {
			String key = (String)e.nextElement();
			String val = (String)get(key);
			key = saveConvert(key, true);

		/* No need to escape embedded and trailing spaces for value, hence
		 * pass false to flag.
		 */
			val = saveConvert(val, false);
			writeln(awriter, key + "=" + val);
		}
		awriter.flush();
	}
	
	/**
	 * Gets if to add the date header to the file in store().
	 * Default value is false.
	 */
	public boolean getAddDateHeader(){
		return this.addDateHeader;
	}

	/**
	 * Sets if to add the date header to the file in store().
	 * Default value is false.
	 */
	public void setAddDateHeader(boolean addHeader){
		this.addDateHeader=addHeader;
	}
	
	private static void writeln(BufferedWriter bw, String s) throws IOException {
		bw.write(s);
		bw.newLine();
	}

	/*
	 * Converts unicodes to encoded &#92;uxxxx
	 * and writes out any of the characters in specialSaveChars
	 * with a preceding slash
	 */
	private String saveConvert(String theString, boolean escapeSpace) {
		if(theString == null) {theString="";}
		
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len*2);

		for(int x=0; x<len; x++) {
			char aChar = theString.charAt(x);
			switch(aChar) {
		case ' ':
			if (x == 0 || escapeSpace) {
				outBuffer.append('\\');
			}

			outBuffer.append(' ');
			break;
				case '\\':outBuffer.append('\\'); outBuffer.append('\\');
						  break;
				case '\t':outBuffer.append('\\'); outBuffer.append('t');
						  break;
				case '\n':outBuffer.append('\\'); outBuffer.append('n');
						  break;
				case '\r':outBuffer.append('\\'); outBuffer.append('r');
						  break;
				case '\f':outBuffer.append('\\'); outBuffer.append('f');
						  break;
				default:
					if ((aChar < 0x0020) || (aChar > 0x007e)) {
						outBuffer.append('\\');
						outBuffer.append('u');
						outBuffer.append(toHex((aChar >> 12) & 0xF));
						outBuffer.append(toHex((aChar >>  8) & 0xF));
						outBuffer.append(toHex((aChar >>  4) & 0xF));
						outBuffer.append(toHex( aChar        & 0xF));
					} else {
						if (specialSaveChars.indexOf(aChar) != -1) {
							outBuffer.append('\\');
						}
						outBuffer.append(aChar);
					}
			}
		}
		return outBuffer.toString();
	}
	
	/**
	 * Convert a nibble to a hex character
	 * @param	nibble	the nibble to convert.
	 */
	private static char toHex(int nibble) {
	return hexDigit[(nibble & 0xF)];
	}

	/** A table of hex digits */
	private static final char[] hexDigit = {
	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	};
	
	
}
